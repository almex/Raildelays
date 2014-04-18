@echo off
setlocal

java -jar -Dlog4j.configurationFile=./conf/log4j2.xml ./lib/raildelays-batch-1.0.0-SNAPSHOT.jar 
PAUSE