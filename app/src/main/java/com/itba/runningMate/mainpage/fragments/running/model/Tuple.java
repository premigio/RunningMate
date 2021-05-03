package com.itba.runningMate.mainpage.fragments.running.model;

public class Tuple<T, K> {

    private final T a;
    private final K b;

    private Tuple(T a, K b) {
        this.a = a;
        this.b = b;
    }

    public static <T, K> Tuple<T, K> from(final T a, final K b) {
        return new Tuple<>(a, b);
    }

    public T getA() {
        return a;
    }

    public K getB() {
        return b;
    }
}
