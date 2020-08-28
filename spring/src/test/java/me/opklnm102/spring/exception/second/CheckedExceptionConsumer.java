package me.opklnm102.spring.exception.second;

import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedExceptionConsumer<T> {
    void accept(T t) throws Exception;

    static <T> Consumer<T> wrap(CheckedExceptionConsumer<T> consumer) {
        return input -> {
            try {
                consumer.accept(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
