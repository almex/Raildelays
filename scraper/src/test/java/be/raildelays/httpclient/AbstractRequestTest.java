package be.raildelays.httpclient;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Almex
 */
public class AbstractRequestTest {

    public static final String VALUE = "value";
    public static final String KEY = "key";
    private Request request;

    @Before
    public void setUp() throws Exception {
        request = new AbstractRequest() {};
    }

    @Test
    public void getParameters() throws Exception {
        assertNotNull(request.getParameters());
    }

    @Test
    public void getParameterTypes() throws Exception {
        assertNotNull(request.getParameterTypes());
    }

    @Test
    public void getType() throws Exception {
        assertNull(request.getType(KEY));
    }

    @Test
    public void getValue() throws Exception {
        assertNull(request.getValue(KEY));
    }

    @Test
    public void setValue() throws Exception {
        request.setValue(VALUE, KEY, String.class);

        assertEquals(VALUE, request.getValue(KEY));
        assertEquals(1, request.getParameters().size());
        assertEquals(VALUE, request.getParameters().get(KEY));
        assertEquals(String.class, request.getParameterTypes().get(KEY));
    }

}