package me.opklnm102.spring.exception.second;

import me.opklnm102.spring.exception.Item;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;


class StreamCheckedExceptionHandleTest {

    private List<Item> items = List.of(new Item(1), new Item(2), new Item(3), new Item(4));

    @Test
    public void checked() throws Exception {

        // consumer interface
        System.out.println(1);
        items.stream()
             .filter(Objects::nonNull)
             .forEach(exceptionHandler(s -> {
                 try {
                     raiseChecked(s);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             }));

        // consumer interface로 checked exception을 unchecked exception으로 변환
        System.out.println(2);
        items.stream()
             .filter(Objects::nonNull)
             .forEach(CheckedExceptionConsumer.wrap(item -> raiseChecked(item)));

        // function interface로 checked exception을 unchecked exception으로 변환
        System.out.println(3);
        items.stream()
             .filter(Objects::nonNull)
             .map(CheckedExceptionFunction.wrap(item -> raiseChecked(item)))
             .forEach(System.out::println);


        // functional interface로 checked exception을 unchecked exception으로 변환
        // 변환된 unchecked exception을 처리하는 method 구현
        System.out.println(4);
        items.stream()
             .filter(Objects::nonNull)
             .forEach(exceptionHandler(CheckedExceptionConsumer.wrap(item -> raiseChecked(item))));

        System.out.println(5);
        items.stream()
             .filter(Objects::nonNull)
             .forEach(exceptionHandler(CheckedExceptionConsumer.wrap(item -> raiseChecked(item)), RuntimeException.class));
    }

    private Item raiseChecked(Item item) throws IOException {
        throw new IOException("error");
    }

    /**
     * 모든 exception을 처리
     */
    private <T> Consumer<T> exceptionHandler(Consumer<T> unhandledConsumer) {
        return input -> {
            try {
                unhandledConsumer.accept(input);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }

    /**
     * 정의된 exception만을 처리
     */
    private <T, E extends Exception> Consumer<T> exceptionHandler(Consumer<T> targetConsumer, Class<E> exceptionClass) {
        return input -> {
            try {
                targetConsumer.accept(input);
            } catch (Exception e) {
                try {
                    E exCast = exceptionClass.cast(e);
                    System.out.println("error " + exCast.getMessage());
                } catch (ClassCastException classCastException) {
                    System.out.println("class cast error " + classCastException);
                    throw e;
                }
            }
        };
    }
}
