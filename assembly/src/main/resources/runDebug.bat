@echo off
setlocal

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000 -jar  -Dlog4j.configurationFile=./conf/log4j2.xml ./lib/raildelays-batch.jar 
PAUSE
