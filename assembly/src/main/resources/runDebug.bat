@echo off
setlocal

java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 -Dlog4j.configurationFile=./conf/log4j2.xml ./lib/raildelays-batch-1.0.0-SNAPSHOT.jar 
PAUSE