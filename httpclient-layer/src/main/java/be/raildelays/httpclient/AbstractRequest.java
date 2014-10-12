package be.raildelays.httpclient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Almex
 * @since 2.0
 */
public class AbstractRequest implements Request {

    private Map<String, Object> parameters = new HashMap<>();

    private Map<String, Class<?>> parameterTypes = new HashMap<>();

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public Map<String, Class<?>> getParameterTypes() {
        return Collections.unmodifiableMap(parameterTypes);
    }

    @Override
    public Class<?> getType(String key) {
        return parameterTypes.get(key);
    }

    @Override
    public <T> T getValue(String key) {
        return (T) parameters.get(key);
    }

    @Override
    public <T> void setValue(T value, String key, Class<? extends T> type) {
        parameters.put(key, value);
        parameterTypes.put(key, type);
    }
}
