package be.raildelays.batch;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.*;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

/**
 * @author Almex
 */
@Plugin(category = "ConfigurationFactory", name = "Log4j2ConfigurationFactory")
@Order(0)
public class Log4j2ConfigurationFactory extends ConfigurationFactory {

    private static final String LOGGER_CONFIG_PATH = "./conf/log4j2.xml";

    @Override
    protected String[] getSupportedTypes() {
        return null;
    }

    @Override
    public Configuration getConfiguration(ConfigurationSource source) {
        return getConfiguration(null, null);
    }

    @Override
    public Configuration getConfiguration(String name, URI configLocation) {
        Configuration configuration = null;

        try {
            InputStream inputStream = new FileInputStream(new File(LOGGER_CONFIG_PATH));

            configuration = XMLConfigurationFactory.getInstance().getConfiguration(new ConfigurationSource(inputStream));
        } catch (IOException e) {
            System.err.printf("Error when loading logging configuration: %s", e.getMessage());
        }

        return configuration;
    }

}