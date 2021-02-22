package fr.polytech.jydet.ofvr;

import lombok.RequiredArgsConstructor;

import java.util.Vector;
import java.util.function.ToDoubleFunction;

@RequiredArgsConstructor
public class F1 implements ToDoubleFunction<Vector<Double>> {

    private final int n;
    private final static int A = 10;

    @Override
    public double applyAsDouble(Vector<Double> v) {
        double res = 0;
        for (int i = 0; i < n; i++) {
            double value = v.get(i);
            res = res + (value * value) - A * Math.cos(2 * Math.PI * value);
        }
        return A * n + res;
    }
}
