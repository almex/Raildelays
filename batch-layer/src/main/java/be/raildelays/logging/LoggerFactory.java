package be.raildelays.logging;

/**
 * @author Almex
 */
public class LoggerFactory {

    public static Logger getLogger(String prefix, Class<?> classe) {
        return new RaildelaysLogger(prefix, org.slf4j.LoggerFactory.getLogger(classe));
    }

    public static Logger getLogger(String prefix, Class<?> classe, char separator) {
        RaildelaysLogger logger = new RaildelaysLogger(prefix, org.slf4j.LoggerFactory.getLogger(classe));

        logger.setSeparator(separator);

        return logger;
    }
}
