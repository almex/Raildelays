package be.raildelays.delays;

import java.util.Date;

/**
 * @author Almex
 * @since 2.0
 */
public class DelayMatcher {

    public static <T> Matcher<T> is(Matcher<T> matcher) {
        return matcher;
    }

    public static <V> ValueMatcher<Class<V>> is(ValueMatcher<Class<V>> matcher) {
        return matcher;
    }

    public static <T extends Comparable<T>, V> boolean difference(OrderingComparison<T> comparison,
                                                                  ValueMatcher<Class<V>> matcher) {
        comparison.setOperator(OrderingComparison.Operator.EQUAL);
        if (matcher.match(Long.class)) {
            comparison.setValue((Long) matcher.getValue());
        }
        return comparison.match(null);
    }

//    public static <T> boolean is(Matcher<? extends T> matcher) {
//        return matcher.match(null);
//    }

    public static OrderingComparison<TimestampDelay> between(TimestampDelay from, TimestampDelay to) {
        return new OrderingComparison<>(from, to);
    }

    public static ValueMatcher<Class<Long>> equalTo(Long value) {
        return new ValueMatcher<>(value);
    }

    public static <T extends Comparable<T>> Matcher<T> greaterThan(Date date) {
        return new OrderingComparison<>(null, null);
    }

    private static class OrderingComparison<T extends Comparable<T>> implements Matcher<T> {

        private TimestampDelay from;

        ;
        private TimestampDelay to;
        private Operator operator;
        private Long value = 0L;
        public OrderingComparison(Long value) {
            this.value = value;
        }

        public OrderingComparison(Operator operator) {
            this.operator = operator;
        }

        public OrderingComparison(TimestampDelay from, TimestampDelay to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean match(T object) {
            boolean result = false;

            switch (operator) {
                case EQUAL:
                    result = DelayUtils.compareTimeAndDelay(from, to) == value;
                    break;
                case GREATER:
                    result = DelayUtils.compareTimeAndDelay(from, to) > value;
                    break;
                case GREATER_OR_EQUAL:
                    result = DelayUtils.compareTimeAndDelay(from, to) >= value;
                    break;
                case LESS:
                    result = DelayUtils.compareTimeAndDelay(from, to) < value;
                    break;
                case LESS_OR_EQUAL:
                    result = DelayUtils.compareTimeAndDelay(from, to) <= value;
                    break;

            }

            return result;
        }

        public TimestampDelay getFrom() {
            return from;
        }

        public void setFrom(TimestampDelay from) {
            this.from = from;
        }

        public TimestampDelay getTo() {
            return to;
        }

        public void setTo(TimestampDelay to) {
            this.to = to;
        }

        public Operator getOperator() {
            return operator;
        }

        public void setOperator(Operator operator) {
            this.operator = operator;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        enum Operator {
            GREATER, LESS, EQUAL, GREATER_OR_EQUAL, LESS_OR_EQUAL;
        }
    }

    private static class ValueMatcher<T> implements Matcher<Class<?>> {

        private Object value;

        private ValueMatcher(Object value) {
            this.value = value;
        }

        @Override
        public boolean match(Class<?> classe) {
            boolean result = false;

            if (classe != null) {
                result = classe.isInstance(value);
            }

            return result;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    private static class OperatorMatcher<T> implements Matcher<T> {

        @Override
        public boolean match(T object) {
            return false;
        }
    }
}
