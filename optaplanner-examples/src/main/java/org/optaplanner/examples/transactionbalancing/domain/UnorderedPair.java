package org.optaplanner.examples.transactionbalancing.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
/**
 * this implementation of pair, ignores the order and (x,y) is equal to (y,x)
 */
public class UnorderedPair<T> {
    private T i;
    private T j;

    public UnorderedPair(T i, T j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString() {
        return "(" + i + ", " + j + ')';
    }

    // Override equals to ignore order
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnorderedPair<?> unorderedPair = (UnorderedPair<?>) o;

        // Compare both ways to ignore order
        return (Objects.equals(i, unorderedPair.i) && Objects.equals(j, unorderedPair.j)) ||
                (Objects.equals(i, unorderedPair.j) && Objects.equals(j, unorderedPair.i));
    }

    //ignore order
    @Override
    public int hashCode() {
        int hashFirst = Objects.hash(i);
        int hashSecond = Objects.hash(j);

        // Combine the hash codes using a combination of sum and product
        return hashFirst + hashSecond + 31 * hashFirst * hashSecond;
    }

    public void setValues(T i, T j) {
        this.i = i;
        this.j = j;
    }
}
