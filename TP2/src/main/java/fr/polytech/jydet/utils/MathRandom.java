package fr.polytech.jydet.utils;

public final class MathRandom {

    public static double randomBetweenInclusive(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}
