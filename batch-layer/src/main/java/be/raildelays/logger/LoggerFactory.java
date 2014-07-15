package be.raildelays.logger;

/**
 * @author Almex
 */
public class LoggerFactory {

    public static Logger getLogger(String prefix, Class<?> classe) {
        return new RaildelaysLogger(prefix, org.slf4j.LoggerFactory.getLogger(classe));
    }
}
