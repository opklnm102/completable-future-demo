package me.opklnm102.spring.exception.second;

import java.util.function.Predicate;

@FunctionalInterface
public interface CheckedExceptionPredicate<T> {
    boolean test(T t) throws Exception;

    static <T> Predicate<T> wrap(CheckedExceptionPredicate<T> predicate) {
        return input -> {
            try {
                return predicate.test(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
