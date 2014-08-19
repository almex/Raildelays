#!/bin/bash
java -Dlog4j.configurationFile=./conf/log4j2.xml -Dorg.jboss.logging.provider=slf4j -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -jar ./lib/raildelays-batch.jar
