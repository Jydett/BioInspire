package fr.polytech.jydet.ofvr._2;

import fr.polytech.jydet.ex2.LoiNormale;
import fr.polytech.jydet.lib.NormalLaw;
import fr.polytech.jydet.lib.UniformLaw;
import fr.polytech.jydet.ofvr._1.F;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class EX4 {

    private NormalLaw normalLaw;

    public EX4(EvolutionaryArguments args) {

    }

    public void work(F toMinimize, Vector<Double> b, Vector<Double> B, int lambda, int mu, int maxEval) {
        assert b.size() == B.size();
        var normalLaw = new NormalLaw(0, 1);
        var m = IntStream.range(0, b.size())
                .mapToObj(i -> new UniformLaw(b.get(i), B.get(i)).nextValue())
                .collect(toCollection(Vector::new));
        var sigma = 0.01d; //step_size
        var w = getW(mu);
        var X = new Vector<Vector<Double>>(mu);
        for (int i = 0; i < mu; i++) {
            X.set(i, m.stream().map(v -> v + sigma * normalLaw.nextValue()).collect(toCollection(Vector::new)));
        }
        var f = new Vector<CollectionElement>(mu);
        for (int i = 0; i < mu; i++) {
            f.set(i, new CollectionElement(i, toMinimize.applyAsDouble(X.get(i))));
        }
        var t = mu;
        while (t < maxEval) {
            var xprime = new Vector<Vector<Double>>(lambda); //children
            for (int i = 0; i < lambda; i++) {
                xprime.set(i, m.stream().map(v -> v + sigma * normalLaw.nextValue()).collect(toCollection(Vector::new)));
            }
            var fprime = new Vector<CollectionElement>(lambda);
            for (int i = 0; i < lambda; i++) {
                fprime.set(i, new CollectionElement(i, toMinimize.applyAsDouble(xprime.get(i))));
            }

            fprime.addAll(f);

            fprime.sort(Comparator.comparingDouble(e -> e.value));
            fprime.subList(lambda, fprime.size()).clear();//remove overflow


        // TODO
        //            for (int i = 0; i < lambda; i++) {
        //                m = wi * X.get(i)
        //                w[i] * fprime.get(i).value
        //            }

            t = t + lambda;
        }
        return ;
    }

    @Data
    @AllArgsConstructor
    private class CollectionElement {
        int position;
        double value;
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


    public static void main(String[] args) {
        var arguments = new EvolutionaryArguments();
        var parser = new CmdLineParser(arguments);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException clEx) {
            System.err.println("ERROR: Unable to parse command-line options: " + clEx);
            return;
        }
    }
}
