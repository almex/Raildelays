/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */.raildelays.httpclient;

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
