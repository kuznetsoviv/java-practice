package ru.kuznetsoviv.agentloader;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AgentLoader {

    private static final Logger LOG = LoggerFactory.getLogger(AgentLoader.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            LOG.error("Usage: java AgentLoader <target-jvm-pid> <agent-file-path>");
            System.exit(-1);
        }
        String targetJvmPid = args[0];
        String agentFilePath = args[1];

        String argsToAgent = null;
        if (args.length > 2) {
            argsToAgent = args[2];
        }
        if (targetJvmPid == null || targetJvmPid.isBlank()) {
            LOG.error("JVM PID is not specified");
        }
        if (agentFilePath == null || agentFilePath.isBlank()) {
            LOG.error("Path to agent lib is not specified");
        }
        LOG.info("targetJvmPid={}, agentFilePath={}", targetJvmPid, agentFilePath);
        run(targetJvmPid, agentFilePath, argsToAgent);
    }

    private static void run(String jvmId, String agentFilePath, String agentArgs) throws Exception {
        boolean hasJvmWithId = VirtualMachine.list().stream()
                .anyMatch(descriptor -> descriptor.id().equals(jvmId));
        if (!hasJvmWithId) {
            LOG.error("Application with id {} not found", jvmId);
            return;
        }

        File agentFile = new File(agentFilePath);
        LOG.info("Attaching to target JVM with PID: {}", jvmId);

        VirtualMachine jvm = VirtualMachine.attach(jvmId);
        jvm.loadAgent(agentFile.getAbsolutePath(), agentArgs);
        jvm.detach();
        LOG.info("Attached to target JVM {} and loaded Java agent successfully", jvmId);
    }

}
