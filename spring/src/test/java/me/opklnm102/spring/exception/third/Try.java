package me.opklnm102.spring.exception.third;

import me.opklnm102.spring.exception.second.CheckedExceptionFunction;

import java.util.function.Function;

public class Try<E extends Exception, R> {
    private final E failure;
    private final R success;

    public Try(E failure, R success) {
        this.failure = failure;
        this.success = success;
    }

    public static <E extends Exception, R> Try<E, R> Failure(E failure) {
        return new Try<>(failure, null);
    }

    public static <E extends Exception, R> Try<E, R> Success(R success) {
        return new Try<>(null, success);
    }

    public static <T, R> Function<T, Try> lift(CheckedExceptionFunction<T, R> function) {
        return input -> {
            try {
                return Try.Success(function.apply(input));
            } catch (Exception e) {
                return Try.Failure(e);
            }
        };
    }

    @Override
    public String toString() {
        return "Try{" +
                "failure=" + failure +
                ", success=" + success +
                '}';
    }
}
