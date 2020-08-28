package me.opklnm102.spring.exception.third;

import me.opklnm102.spring.exception.second.CheckedExceptionFunction;

import java.util.Optional;
import java.util.function.Function;

public class Either<L, R> {
    private final L left;
    private final R right;

    public Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> Left(L value) {
        return new Either<>(value, null);
    }

    public static <L, R> Either<L, R> Right(R value) {
        return new Either<>(null, value);
    }

    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        if (isRight()) {
            return Optional.of(mapper.apply(right));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        if (isLeft()) {
            return "Left(" + left + ")";
        }

        return "Right(" + right + ")";
    }

    public static <T, R> Function<T, Either<Exception, R>> lift(CheckedExceptionFunction<T, R> function) {
        return input -> {
            try {
                return Either.Right(function.apply(input));
            } catch (Exception e) {
                return Either.Left(e);
            }
        };
    }

    public static <T, R> Function<T, Either<Pair<Exception, T>, R>> liftWithValue(CheckedExceptionFunction<T, R> function) {
        return input -> {
            try {
                return Either.Right(function.apply(input));
            } catch (Exception e) {
                return Either.Left(Pair.of(e, input));
            }
        };
    }
}
