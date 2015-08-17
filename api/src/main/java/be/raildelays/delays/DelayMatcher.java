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

    public static OrderingComparison<TimestampDelay> between(TimestampDelay from) {
        return new OrderingComparison<>(from);
    }

    public static OrderingComparison<TimestampDelay> between(Date from) {
        return new OrderingComparison<>(new TimestampDelay(from));
    }

    public static ValueMatcher<Class<Long>> equalTo(Long value) {
        return new ValueMatcher<>(value);
    }

//    public static <T extends Comparable<T>> Matcher<T> greaterThan(Date date) {
//        return new OrderingComparison<>(null);
//    }

    public static class OrderingComparison<T extends Comparable<T>> implements Matcher<T> {

        private TimestampDelay from;
        private TimestampDelay to;
        private Operator operator;
        private Long value = 0L;

        protected OrderingComparison(Long value) {
            this.value = value;
        }

        protected OrderingComparison(Operator operator) {
            this.operator = operator;
        }

        protected OrderingComparison(TimestampDelay from) {
            this.from = from;
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

        protected TimestampDelay getFrom() {
            return from;
        }

        protected void setFrom(TimestampDelay from) {
            this.from = from;
        }

        protected TimestampDelay getTo() {
            return to;
        }

        protected void setTo(TimestampDelay to) {
            this.to = to;
        }

        protected Operator getOperator() {
            return operator;
        }

        protected void setOperator(Operator operator) {
            this.operator = operator;
        }

        protected Long getValue() {
            return value;
        }

        protected void setValue(Long value) {
            this.value = value;
        }

        enum Operator {
            GREATER, LESS, EQUAL, GREATER_OR_EQUAL, LESS_OR_EQUAL;
        }

        public OrderingComparison<T> and(TimestampDelay to) {
            this.setTo(to);

            return this;
        }

        public OrderingComparison<T> and(Date to) {
            this.setTo(new TimestampDelay(to));

            return this;
        }
    }

    private static class ValueMatcher<T> implements Matcher<Class<?>> {

        private Object value;

        private ValueMatcher(Object value) {
            this.value = value;
        }

        @Override
        public boolean match(Class<?> clazz) {
            boolean result = false;

            if (clazz != null) {
                result = clazz.isInstance(value);
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
