package me.opklnm102.spring.exception.third;

import me.opklnm102.spring.exception.Item;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

class StreamCheckedExceptionHandleTest {

    private List<Item> items = List.of(new Item(1), new Item(2), new Item(3), new Item(4));

    @Test
    public void checked() throws Exception {

        // consumer interface로 checked exception을
        System.out.println(1);
        items.stream()
             .filter(Objects::nonNull)
             .map(Either.lift(item -> raiseChecked(item)))
             .forEach(either -> System.out.println(either));

        System.out.println(2);
        items.stream()
             .filter(Objects::nonNull)
             .map(Either.liftWithValue(item -> raiseChecked(item)))
             .forEach(either -> System.out.println(either));

        // Try - origin value를 알 수 없다
        System.out.println(3);
        items.stream()
             .filter(Objects::nonNull)
             .map(Try.lift(item -> raiseChecked(item)))
             .forEach(either -> System.out.println(either));

        // 성공, 실패 양쪽다 처리하고 싶을 때
//        System.out.println(4);
//        items.stream()
//             .filter(Objects::nonNull)
//             .map(Either.liftWithValue(this::raiseChecked))
//             .forEach(new PredicateSplitterConsumer<>(Either::isLeft,
//                     either -> System.out.println(either.getLeft() + " left"),
//                     either -> System.out.println(either.getRight() + "right")));
    }

    private Item raiseChecked(Item item) throws IOException {
        if (item.getId() % 2 == 0) {
            return item;
        }
        throw new IOException("error");
    }
}
