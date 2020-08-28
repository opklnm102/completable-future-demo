package me.opklnm102.spring.exception.third;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PredicateSplitterConsumer<T> implements Consumer<T> {

    private Predicate<T> predicate;
    private Consumer<T> positiveConsumer;
    private Consumer<T> negativeConsumer;

    private PredicateSplitterConsumer(Predicate<T> predicate, Consumer<T> positiveConsumer, Consumer<T> negativeConsumer) {
        this.predicate = predicate;
        this.positiveConsumer = positiveConsumer;
        this.negativeConsumer = negativeConsumer;
    }

    public static <T> PredicateSplitterConsumer<T> of(Predicate<T> predicate, Consumer<T> positiveConsumer, Consumer<T> negativeConsumer) {
        return new PredicateSplitterConsumer<>(predicate, positiveConsumer, negativeConsumer);
    }

    @Override
    public void accept(T t) {
        if (predicate.test(t)) {
            positiveConsumer.accept(t);
            return;
        }
        negativeConsumer.accept(t);
    }
}
