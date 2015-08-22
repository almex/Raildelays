package be.raildelays.delays;

import java.time.LocalTime;

/**
 * @author Almex
 * @since 2.0
 */
public class DelayMatcher<T> implements Matcher<T> {

    public static OperatorMatcher<Long> is(OperatorMatcher<Long> matcher) {
        return matcher;
    }

    public static OperatorMatcher<Long> is(Long value) {
        return is(equalTo(value));
    }

    public static OperatorMatcher<Long> equalTo(Long value) {
        return OperatorMatcher.operator(Operator.EQUAL, ValueMatcher.value(value));
    }

    public static OperatorMatcher<Long> equal() {
        return OperatorMatcher.operator(Operator.EQUAL, ValueMatcher.value(0L));
    }

    public static OperatorMatcher<Long> greaterThan(Long value) {
        return OperatorMatcher.operator(Operator.GREATER, ValueMatcher.value(value));
    }

    public static OperatorMatcher<Long> after() {
        return greaterThan(0L);
    }

    public static OperatorMatcher<Long> lessThan(Long value) {
        return OperatorMatcher.operator(Operator.LESS, ValueMatcher.value(value));
    }

    public static OperatorMatcher<Long> before() {
        return lessThan(0L);
    }

    public static boolean difference(OrderingComparison comparison,
                                     OperatorMatcher<Long> matcher) {
        comparison.setOperator(matcher.getOperator());

        return comparison.match(matcher.getValueMatcher().getValue());
    }

    public static boolean duration(OrderingComparison comparison,
                                   OperatorMatcher<Long> matcher) {
        // A duration is the opposite of a difference
        return difference(comparison, opposite(matcher));
    }

    /**
     * Create a {@link OperatorMatcher} containing the {@link ValueMatcher} with an opposite {@code value} and the
     * opposite {@link be.raildelays.delays.DelayMatcher.Operator}.
     *
     * @param matcher the {@link OperatorMatcher} to clone
     * @return a {@link OperatorMatcher} containing the {@link ValueMatcher} with an opposite {@code value} and the
     * opposite {@link be.raildelays.delays.DelayMatcher.Operator}.
     */
    private static OperatorMatcher<Long> opposite(OperatorMatcher<Long> matcher) {
        Operator operator = matcher.getOperator();
        ValueMatcher<Long> valueMatcher = ValueMatcher.value(-matcher.getValueMatcher().getValue());

        switch (operator) {
            case GREATER:
                operator = Operator.LESS;
                break;
            case LESS:
                operator = Operator.GREATER;
                break;
            case GREATER_OR_EQUAL:
                operator = Operator.LESS_OR_EQUAL;
                break;
            case LESS_OR_EQUAL:
                operator = Operator.GREATER_OR_EQUAL;
                break;
        }

        return OperatorMatcher.operator(operator, valueMatcher);
    }

    public static OrderingComparison between(TimeDelay from) {
        return new OrderingComparison(from);
    }

    public static OrderingComparison between(LocalTime from) {
        return new OrderingComparison(TimeDelay.of(from));
    }

    @Override
    public boolean match(T object) {
        return false;
    }


    enum Operator {
        GREATER, LESS, EQUAL, GREATER_OR_EQUAL, LESS_OR_EQUAL
    }

    public static class OrderingComparison implements Matcher<Long> {

        private TimeDelay from;
        private TimeDelay to;
        private Operator operator;

        protected OrderingComparison(TimeDelay from) {
            this.from = from;
        }

        @Override
        public boolean match(Long value) {
            boolean result = false;

            switch (operator) {
                case EQUAL:
                    result = Delays.compareTimeAndDelay(from, to) == value;
                    break;
                case GREATER:
                    result = Delays.compareTimeAndDelay(from, to) > value;
                    break;
                case GREATER_OR_EQUAL:
                    result = Delays.compareTimeAndDelay(from, to) >= value;
                    break;
                case LESS:
                    result = Delays.compareTimeAndDelay(from, to) < value;
                    break;
                case LESS_OR_EQUAL:
                    result = Delays.compareTimeAndDelay(from, to) <= value;
                    break;

            }

            return result;
        }

        protected TimeDelay getFrom() {
            return from;
        }

        protected void setFrom(TimeDelay from) {
            this.from = from;
        }

        protected TimeDelay getTo() {
            return to;
        }

        protected void setTo(TimeDelay to) {
            this.to = to;
        }

        protected void setOperator(Operator operator) {
            this.operator = operator;
        }

        public OrderingComparison and(TimeDelay to) {
            this.setTo(to);

            return this;
        }

        public OrderingComparison and(LocalTime to) {
            this.setTo(TimeDelay.of(to));

            return this;
        }
    }

    private static class ValueMatcher<V> implements Matcher<V> {

        private V value;

        private ValueMatcher(V value) {
            this.value = value;
        }

        public static <V> ValueMatcher<V> value(V value) {
            return new ValueMatcher<>(value);
        }

        @Override
        public boolean match(V target) {
            boolean result = false;

            if (value != null) {
                result = value.equals(target);
            } else if (target == null) {
                result = true;
            }

            return result;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

    private static class OperatorMatcher<V> implements Matcher<Operator> {

        private Operator operator;
        private ValueMatcher<V> valueMatcher;

        private OperatorMatcher(Operator operator, ValueMatcher<V> valueMatcher) {
            this.operator = operator;
            this.valueMatcher = valueMatcher;
        }

        public static <V> OperatorMatcher<V> operator(Operator operator, ValueMatcher<V> matcher) {
            return new OperatorMatcher<>(operator, matcher);
        }

        @Override
        public boolean match(Operator target) {
            return operator.equals(target);
        }

        public ValueMatcher<V> getValueMatcher() {
            return valueMatcher;
        }

        public Operator getOperator() {
            return operator;
        }
    }
}
