package me.opklnm102.spring.exception.first;

import me.opklnm102.spring.exception.Item;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * lambda block을 다른 method로 wrapping
 */
class StreamCheckedExceptionHandleTest {

    private List<Item> items = List.of(new Item(1), new Item(2), new Item(3), new Item(4));

    @Test
    public void test() throws Exception {

        // 1 compile error
//        items.stream()
//             .filter(Objects::nonNull)
//             .forEach(item -> raiseChecked(item));

        // 2 lambda block
        items.stream()
             .filter(Objects::nonNull)
             .forEach(item -> {
                 try {
                     raiseChecked(item);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             });

        // 3 wrapping lambda block
        items.stream()
             .filter(Objects::nonNull)
             .forEach(this::wrapCheckedException);
    }

    private Item raiseChecked(Item item) throws IOException {
        throw new IOException("error");
    }

    private Item wrapCheckedException(Item item) {
        try {
            return raiseChecked(item);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
