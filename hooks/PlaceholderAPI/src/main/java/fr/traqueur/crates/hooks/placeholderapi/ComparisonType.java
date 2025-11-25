package fr.traqueur.crates.hooks.placeholderapi;

import java.util.function.BiPredicate;

public enum ComparisonType {
    EQUALS(Double::equals),
    NOT_EQUALS((a, b) -> !a.equals(b)),
    GREATER_THAN((a, b) -> a > b),
    LESS_THAN((a, b) -> a < b),
    GREATER_THAN_OR_EQUALS((a, b) -> a >= b),
    LESS_THAN_OR_EQUALS((a, b) -> a <= b);

    private final BiPredicate<Double, Double> comparator;

    ComparisonType(BiPredicate<Double, Double> comparator) {
        this.comparator = comparator;
    }

    public boolean apply(double actual, double expected) {
        return comparator.test(actual, expected);
    }

    public @interface Default {
        ComparisonType value() default EQUALS;
    }

}