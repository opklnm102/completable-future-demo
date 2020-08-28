package me.opklnm102.spring.exception.forth;

import io.vavr.API;
import io.vavr.CheckedFunction1;
import io.vavr.Value;
import io.vavr.control.Either;
import io.vavr.control.Try;
import me.opklnm102.spring.exception.Item;
import me.opklnm102.spring.exception.third.PredicateSplitterConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


class StreamCheckedExceptionHandleTest {

    private List<Item> items = List.of(new Item(1), new Item(2), new Item(3), new Item(4));

    @Test
    public void _1_using_CheckedFunction() throws Exception {
        System.out.println("1. Using CheckedFunction");

        CheckedFunction1<Item, Item> raisedCheckedFunction = x -> raiseChecked(x);

        // unchecked exception 발생시 stream이 끊기므로 주의
        items.stream()
             .filter(Objects::nonNull)
             .map(raisedCheckedFunction.unchecked())  // wrapping with unchecked exception
             .forEach(output -> System.out.println("do something " + output));
    }

    // 1의 방식을 위한 helper method 지원
    @Test
    public void _2_using_helper_methods() throws Exception {
        System.out.println("2. using helper methods");

        items.stream()
             .filter(Objects::nonNull)
             .map(API.unchecked(this::raiseChecked))
             .forEach(output -> System.out.println("do something " + output));
    }

    /* unchecked exception 처리를 위해 lambda block에 try-catch 사용 가능하나 lambda의 간결함이 손상
     Vavr lifting 사용
     Lifting
        concept from functional programming
        Option을 return하는 total function으로 partial function을 lift할 수 있다
        partial function
            전체 domain에 정의된 total function이 아닌 subset domain에 정의되는 function
            범위를 벗어난 input으로 partial function을 호출하면 일반적으로 exception 발생
    */
    @Test
    public void _3_using_lifting() throws Exception {
        System.out.println("3. Using Lifting");

        items.stream()
             .filter(Objects::nonNull)
             .map(CheckedFunction1.lift(this::raiseChecked))
             .map(x -> x.getOrElse(Item.NOT_FOUND))
             .forEach(output -> System.out.println("do something " + output));
    }

    /*
        lift는 exception을 해결하지만, 실제로는 삼킨다
        -> consumer는 default value를 모른다
        -> Try 사용
        Try
            exception을 묶을 수 있는 container
        자세한 것은 https://www.baeldung.com/vavr-try 참고
     */
    @Test
    public void _4_using_try() throws Exception {
        System.out.println("4. Using Try");

        items.stream()
             .filter(Objects::nonNull)
             .map(CheckedFunction1.liftTry(this::raiseChecked))
             .flatMap(Value::toJavaStream)
             .forEach(output -> System.out.println("do something " + output));
    }

    @Test
    public void multiTask() throws Exception {
        items.stream()
             .filter(Objects::nonNull)
             .map(item -> Try.of(() -> task1(item))
                             .andThen(() -> task2(item))
                             .andThen(() -> task3(item))
                             .filter(item1 -> item1.getId() != 2)
                             .onFailure(throwable -> System.out.println("fatal error " + throwable + "origin value: " + item)))  // try 자체로는 origin value를 알 수 없지만, 같은 scope에 있어서 가능
             .flatMap(Value::toJavaStream)  // Try.Success -> return origin value
             .forEach(output -> System.out.println("do something " + output));
    }

    @Test
    public void success_fail_양쪽_다_처리하고_싶을_때() throws Exception {
        items.stream()
             .map(item -> Try.of(() -> raiseChecked(item))
                             .filter(Objects::nonNull)
                             .andThen(item1 -> System.out.println("andThen " + item1))
                             .onFailure(throwable -> System.out.println("loggging " + throwable))
                             .filter(item1 -> item1.getId() != 2))
             .forEach(x -> PredicateSplitterConsumer.<Try<Item>>of(Try::isFailure,
                     y -> System.out.println("fail " + y),
                     z -> System.out.println("success " + z)));
    }

    @Test
    public void multiTaskEither() throws Exception {
        items.stream()
             .filter(Objects::nonNull)
             .map(item -> taskEither1(item).peekLeft(x -> System.out.println("task1 fatal error " + x.getFirst() + " origin value: " + x.getSecond())))
             .filter(Either::isRight)
             .map(either -> taskEither2(either.get()).peekLeft(x -> System.out.println("task2 fatal error " + x.getFirst() + " origin value: " + x.getSecond())))
             .filter(Either::isRight)
             .map(x -> taskEither3(x.get()))
             .flatMap(Value::toJavaStream)
             .forEach(x -> System.out.println("final: " + x));
    }

    private Either<Pair<Exception, Item>, Item> taskEither1(Item item) {
        System.out.println("task1 " + item);
        if (item.getId() % 2 == 0) {
            try {
                throw new IOException("fail task1");
            } catch (Exception e) {
                return Either.left(Pair.of(e, item));
            }
        }
        return Either.right(item);
    }

    private Either<Pair<Exception, Item>, Item> taskEither2(Item item) {
        System.out.println("task2 " + item);

        if (item.getId() == 3) {
            return Either.left(Pair.of(new RuntimeException("error"), item));
        }
        return Either.right(item);
    }

    private Either<Pair<Exception, Item>, Item> taskEither3(Item item) {
        System.out.println("task3 " + item);
        return Either.right(item);
    }

    private Item task1(Item item) throws IOException {
        System.out.println("task1 " + item);
        if (item.getId() % 2 == 0) {
            throw new IOException("fail task1");
        }
        return item;
    }

    private Item task2(Item item) {
        System.out.println("task2 " + item);
        return item;
    }

    private Item task3(Item item) {
        System.out.println("task3 " + item);
        if (item.getId() == 3) {
            throw new RuntimeException("fail task3");
        }
        return item;
    }

    private Item raiseChecked(Item item) throws IOException {
        if (item.getId() % 2 == 0) {
            return item;
        }
        throw new IOException("error");
    }
}
