@echo off
set AGENT=../agent/target/agent.jar

java -javaagent:%AGENT%=ru.kuznetsoviv.application.Application#process ^
     -jar ..\application\target\application.jar
