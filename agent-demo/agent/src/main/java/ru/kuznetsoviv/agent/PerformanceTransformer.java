package ru.kuznetsoviv.agent;

import javassist.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class PerformanceTransformer implements ClassFileTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(PerformanceTransformer.class);

    private final String className;
    private final String methodName;
    private final ClassLoader classLoader;

    public PerformanceTransformer(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
        this.classLoader = null;
    }

    public PerformanceTransformer(Class<?> clazz, String methodName) {
        this.className = clazz.getName();
        this.methodName = methodName;
        this.classLoader = clazz.getClassLoader();
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer
    ) {
        var byteCode = classfileBuffer;
        var name = this.className.replaceAll("\\.", "/");
        if (!className.equals(name)) {
            return byteCode;
        }
        if (classLoader == null || classLoader.equals(loader)) {
            LOG.info("Transforming the class: {}", this.className);
            try {
                LOG.info("Original class's byte code \n{}", getByteCodeAsString(classfileBuffer));
                ClassPool cp = ClassPool.getDefault();
                cp.appendClassPath(new LoaderClassPath(loader));
                LOG.info("Getting class {}", this.className);
                CtClass ctClass = cp.get(this.className);
                CtMethod ctMethod = ctClass.getDeclaredMethod(this.methodName);
                ctMethod.addLocalVariable("startTime", CtClass.longType);
                ctMethod.insertBefore("startTime = System.nanoTime();");
                ctMethod.addLocalVariable("endTime", CtClass.longType);
                ctMethod.addLocalVariable("opTime", CtClass.longType);
                ctMethod.insertAfter("""
                            endTime = System.nanoTime();
                            opTime = (endTime - startTime)/1000;
                            LOG.info("[AGENT] Withdrawal operation completed in: {} microseconds!", Long.valueOf(opTime));
                        """);
                byteCode = ctClass.toBytecode();
                ctClass.detach();
                LOG.info("Instrumented class's byte code: \n {}", getByteCodeAsString(byteCode));
            } catch (NotFoundException | CannotCompileException | IOException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stackTrace = sw.toString();
                LOG.info(stackTrace);
            }
        }
        return byteCode;
    }

    public String getByteCodeAsString(byte[] byteCode) throws IOException {
        ClassReader reader = new ClassReader(byteCode);
        StringWriter stringWriter = new StringWriter();
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(new PrintWriter(stringWriter));
        reader.accept(traceClassVisitor, 0);
        return stringWriter.toString();
    }

}
