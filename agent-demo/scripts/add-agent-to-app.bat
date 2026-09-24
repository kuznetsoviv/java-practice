@echo off

set TARGET_JVM_PID=%1
set AGENT=../agent/target/agent.jar
set AGENT_ARGS=ru.kuznetsoviv.application.Application#process

java -jar ../agent-loader/target/agent-loader.jar ^
     %TARGET_JVM_PID% %AGENT% %AGENT_ARGS%


