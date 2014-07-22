@echo off
setlocal

java -jar -Dlog4j.configurationFile=./conf/log4j2.xml -Dorg.jboss.logging.provider=slf4j ./lib/raildelays-batch.jar 
PAUSE
