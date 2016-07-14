package be.raildelays.delays;

/**
 * @author Almex
 */
public interface Matcher<T> {

    boolean match(T object);
}
