@echo off
setlocal

java -jar -Dlog4j.configurationFile=./conf/log4j2.xml ./lib/raildelays-batch.jar 
PAUSE
