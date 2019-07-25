package io.funfun.redbook;

import java.util.NoSuchElementException;
import java.util.function.Function;

public abstract class FPList<T> {

    private static final FPList<Object> NONE = new None();

    abstract T head();
    abstract FPList<T> tail();
    abstract boolean isEmpty();

    static <T> FPList<T> cons(T head) {
        return cons(head, none());
    }

    static <T> FPList<T> cons(T head, FPList<T> tail) {
        return new Cons<>(head, tail);
    }

    static <T> FPList<T> none() {
        return (FPList<T>) NONE;
    }

    static <T> FPList<T> fpList(T... args) {
        FPList<T> results = none();
        for (int i = args.length - 1; i >= 0; i--) {
            results = cons(args[i], results);
        }
        return results;
    }

    public int length() {
        return length(0);
    }

    public int length(int acc) {
        if (this instanceof Cons) {
            return tail().length(acc + 1);
        } else {
            return acc;
        }
    }

    public FPList<T> reverse() {
        return reverse(none());
    }

    public FPList<T> reverse(FPList<T> acc) {
        if (this instanceof Cons) {
            return tail().reverse(cons(this.head(), acc));
        } else {
            return acc;
        }
    }

    public <R> FPList<R> map(Function<T, R> func) {
        return map(none(), func);
    }

    public <R> FPList<R> map(FPList<R> acc, Function<T, R> func) {
        if (this instanceof Cons) {
            return this.tail().map(cons(func.apply(head()), acc), func);
        } else {
            return acc.reverse();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        if (this instanceof Cons) {
            int result = 1;
            result = prime * result + head().hashCode();
            result = prime * result + tail().hashCode();

            return result;
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this.getClass() != obj.getClass())
            return false;

        if (this == obj)
            return true;

        if (this instanceof Cons) {
            Cons o1 = (Cons) this;
            if (obj instanceof None)
                return false;

            Cons o2 = (Cons) obj;
            if (!o1.head().equals(o2.head()))
                return false;
            if (!o1.tail().equals(o2.tail()))
                return false;
        }

        return true;
    }

    public static class Cons<T> extends FPList<T> {

        private T _head;
        private FPList<T> _tail;

        public Cons(T head, FPList<T> tail) {
            this._head = head;
            this._tail = tail;
        }

        @Override
        public T head() {
            return _head;
        }

        @Override
        public FPList<T> tail() {
            return _tail;
        }

        @Override
        boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return "Cons(\"" + _head + "\", " + _tail + ")";
        }
    }

    public static class None extends FPList<Object> {

        @Override
        public Object head() {
            throw new NoSuchElementException();
        }

        @Override
        public FPList<Object> tail() {
            throw new NoSuchElementException();
        }

        @Override
        boolean isEmpty() {
            return true;
        }

        @Override
        public String toString() {
            return "None";
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}

