package io.funfun.redbook;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public abstract class Stream<T> {

    private final static Stream NONE = new None();

    abstract T head();
    abstract Stream<T> tail();
    abstract boolean isEmpty();

    public static <T> Supplier<Stream<T>> none() {
        return (() -> NONE);
    }

    public static <T> Stream<T> seq(Supplier<T> head, Supplier<Stream<T>> tail) {
        return new Seq<>(head, tail);
    }

    public static <T> Stream<T> stream(T... args) {
        Stream<T> result = NONE;
        for (int i = args.length - 1; i >= 0; i--) {
            final Stream<T> tail = result;
            final T head = args[i];
            result = seq(() -> head, () -> tail);
        }
        return result;
    }

    public static <T> Stream<T> stream(Supplier<T>... args) {
        Stream<T> result = NONE;
        for (int i = args.length - 1; i >= 0; i--) {
            final Stream<T> tail = result;
            result = seq(args[i], () -> tail);
        }
        return result;
    }

    public static class Seq<T> extends Stream<T> {

        private final Supplier<T> head;
        private final Supplier<Stream<T>> tail;
        private T _head;
        private Stream<T> _tail;

        public Seq(Supplier<T> head, Supplier<Stream<T>> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public T head() {
            if (_head == null) {
                _head = head.get();
            }
            return _head;
        }

        @Override
        public Stream<T> tail() {
            if (_tail == null) {
                _tail = tail.get();
            }
            return _tail;
        }

        @Override
        boolean isEmpty() {
            return false;
        }
    }

    public static class None extends Stream<Object> {

        @Override
        public Object head() {
            throw new NoSuchElementException();
        }

        @Override
        public Stream<Object> tail() {
            throw new NoSuchElementException();
        }

        @Override
        boolean isEmpty() {
            return true;
        }
    }
}
