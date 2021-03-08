package fr.polytech.jydet.lib;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class UniformLaw implements Law {

    private final double inf;
    private final double sup;

    @Override
    public double nextValue() {
        return ThreadLocalRandom.current().nextDouble(inf, sup);
    }
}
