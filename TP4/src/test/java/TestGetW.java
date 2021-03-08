import fr.polytech.jydet.lib.UniformLaw;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class TestGetW {

    @Test
    public void testImpl() {
        assertSumEquals(getW(1), 1);
        assertSumEquals(getW(10), 1);
        assertSumEquals(getW(100), 1);
    }

    private void assertSumEquals(Double[] w, double i) {
        double sum = 0;
        for (Double aDouble : w) {
            sum = sum + aDouble;
        }
        Assert.assertEquals(i, sum, 0);
    }

    private Double[] getW(int mu) {
        //based on https://stackoverflow.com/questions/2640053/getting-n-random-numbers-whose-sum-is-m
        var doubles = new HashSet<Double>(mu);
        var uniformLaw = new UniformLaw(0, 1);
        doubles.add(0d);
        doubles.add(1d);
        while (doubles.size() < mu + 1) {
            doubles.add(uniformLaw.nextValue());
        }
        var d = new ArrayList<>(doubles);
        d.sort(Double::compareTo);

        Double[] res = new Double[mu];
        for (int i = 1; i < d.size(); i++) {
            res[i - 1] = d.get(i) - d.get(i - 1);
        }

        return res;
    }
}
