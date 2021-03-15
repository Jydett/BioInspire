package fr.polytech.jydet.ofvr._1;

import lombok.AllArgsConstructor;

import java.util.Random;
import java.util.Vector;
import java.util.function.ToDoubleFunction;

@AllArgsConstructor
public class F0 implements F {

    private final int n;

    @Override
    public double applyAsDouble(Vector<Double> v) {
        assert v.size() == n;
        double res = 0;
        for (int i = 0; i < n; i++) {
            double value = v.get(i);
            res = res + value * value;
        }
        return res;
    }
}
