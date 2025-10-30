package cs451;

import java.util.Objects;

public class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getKey() {
        return first;
    }

    public B getValue() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                     // same reference
        if (!(o instanceof Pair)) return false;          // not a Pair
        Pair<?, ?> other = (Pair<?, ?>) o;
        return Objects.equals(first, other.first)        // compare by value
                && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);              // consistent with equals
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}