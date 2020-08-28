package me.opklnm102.spring.exception.second;

import java.util.function.Function;

@FunctionalInterface
public interface CheckedExceptionFunction<T, R> {
    R apply(T t) throws Exception;

    static <T, R> Function<T, R> wrap(CheckedExceptionFunction<T, R> function) {
        return input -> {
            try {
                return function.apply(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
