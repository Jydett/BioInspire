package fr.polytech.jydet.ofvr._1;

import lombok.RequiredArgsConstructor;

import java.util.Vector;

@RequiredArgsConstructor
public class F2 implements F {

    private final int n;
    private final static int M = 10;

    @Override
    public double applyAsDouble(Vector<Double> v) {
        double res = 0;
        for (int i = 0; i < n; i++) {
            double value = v.get(i);
            res = res + Math.sin(value) * Math.pow(Math.sin((i * value * value) / Math.PI), 2 * M);
        }
        return -res;
    }
}
