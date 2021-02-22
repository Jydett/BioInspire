package fr.polytech.jydet.ofvr;

import lombok.AllArgsConstructor;

import java.util.Vector;
import java.util.function.ToDoubleFunction;

@AllArgsConstructor
public class F0 implements ToDoubleFunction<Vector<Double>> {

    private final int n;

    @Override
    public double applyAsDouble(Vector<Double> v) {
        double res = 0;
        for (int i = 0; i < n; i++) {
            double value = v.get(i);
            res = value * value;
        }
        return res;
    }
}
