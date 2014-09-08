package be.raildelays.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class simplifying parsing.
 *
 * @author Almex
 */
public final class ParsingUtil {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "hh:mm";
    public static final String TIMESTAMP_FORMAT = DATE_FORMAT + TIME_FORMAT;
    /**
     * Default constructor.
     */
    private ParsingUtil() {
        // No instantiation is possible.
    }

    public static Date parseTimestamp(String value) {
        final SimpleDateFormat sdc = new SimpleDateFormat(TIMESTAMP_FORMAT);

        try {
            return sdc.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseTime(String value) {
        final SimpleDateFormat sdc = new SimpleDateFormat(TIME_FORMAT);

        try {
            return sdc.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDate(String value) {
        final SimpleDateFormat sdc = new SimpleDateFormat(DATE_FORMAT);

        try {
            return sdc.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatTimestamp(Date value) {
        final SimpleDateFormat sdc = new SimpleDateFormat(TIMESTAMP_FORMAT);

        return sdc.format(value);
    }

    public static String formatTime(Date value) {
        final SimpleDateFormat sdc = new SimpleDateFormat(TIME_FORMAT);

        return sdc.format(value);
    }

    public static String formatDate(Date value) {
        final SimpleDateFormat sdc = new SimpleDateFormat(DATE_FORMAT);

        return sdc.format(value);
    }
}