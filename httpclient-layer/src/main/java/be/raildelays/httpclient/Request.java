package be.raildelays.httpclient;

import java.util.Map;

/**
 * @author Almex
 * @since 2.0
 */
public interface Request {

    Map<String, Object> getParameters();

    Map<String, Class<?>> getParameterTypes();

    Class<?> getType(String key);

    <T> T getValue(String key);

    <T> void setValue(T value, String key, Class<? extends T> type);
}
