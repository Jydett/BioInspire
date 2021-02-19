package fr.polytech.jydet.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.BiFunction;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class Tuple<T> {
    private T _1;
    private T _2;

    public <U> Tuple<U> map(Function<T, U> transformer) {
        return new Tuple<>(transformer.apply(_1), transformer.apply(_2));
    }

    public <R> R compute(BiFunction<T, T, R> computer) {
        return computer.apply(_1, _2);
    }
}
