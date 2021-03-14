package fr.polytech.jydet.ofvr._2;

import fr.polytech.jydet.lib.NormalLaw;
import fr.polytech.jydet.lib.UniformLaw;
import fr.polytech.jydet.ofvr._1.F;
import fr.polytech.jydet.ofvr._1.F0;
import fr.polytech.jydet.ofvr._1.F1;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

public class EX4 {

    public CollectionElement launch(EvolutionaryArguments args) {
        int n = args.getD();
        Vector<Double> b = new Vector<>(n);
        Vector<Double> B = new Vector<>(n);
        for (int i = 0; i < n; i++) {
            b.add(-5d);
            B.add(5d);
        }
        return work(args.getFunction() == 0 ? new F0(n) : new F1(n),
            b,
            B,
            args.getLambda(),
            args.getMu(),
            args.getCondition());
    }

    public CollectionElement work(F toMinimize, Vector<Double> b, Vector<Double> B, int lambda, int mu, int maxEval) {
        int d = b.size();
        assert b.size() == B.size();
        var normalLaw = new NormalLaw(0, 1);
        var m = IntStream.range(0, b.size())
                .mapToObj(i -> new UniformLaw(b.get(i), B.get(i)).nextValue())
                .collect(toCollection(Vector::new));
        var sigma = 0.01d; //step_size
        var w = getW(mu);
        var X = new Vector<Vector<Double>>(mu);
        for (int i = 0; i < mu; i++) {
            X.add(m.stream().map(v -> v + sigma * normalLaw.nextValue()).collect(toCollection(Vector::new)));
        }
        var f = new Vector<CollectionElement>(mu);
        for (int i = 0; i < mu; i++) {
            f.add(new CollectionElement("parent init ",i, toMinimize.applyAsDouble(X.get(i)), X.get(i)));
        }

        log(f, false);

        var t = mu;
        CollectionElement best = null;
        while (t < maxEval) {
            var xprime = new Vector<Vector<Double>>(lambda); //children
            for (int i = 0; i < lambda; i++) {
                xprime.add(m.stream().map(v -> v + sigma * normalLaw.nextValue()).collect(toCollection(Vector::new)));
            }
            var fprime = new Vector<CollectionElement>(lambda);
            for (int i = 0; i < lambda; i++) {
                fprime.add(new CollectionElement("enfant " + t, i,toMinimize.applyAsDouble(xprime.get(i)), xprime.get(i)));
            }

            log(fprime, true);

            fprime.addAll(f);

            fprime.sort(Comparator.comparingDouble(e -> e.value));
            m.clear();
            for (int i = 0; i < d; i++) {
                double sum = 0;
                Vector<Double> x = fprime.get(i).x;
                for (int j = 0; j < x.size(); j++) {
                    sum = sum + w[j] * x.get(j);
                }
                m.add(sum);
            }
            fprime.subList(lambda, fprime.size()).clear();//remove overflow
            best = fprime.get(0);
            t = t + lambda;
        }
        return best;
    }

    private void log(Vector<CollectionElement> f, boolean append) {
        try(FileWriter fileWriter = new FileWriter("log.txt", append)) {
            if (append) fileWriter.write(System.lineSeparator());
            fileWriter.write(f.stream().map(e ->
                e.x.stream().map(Objects::toString).collect(Collectors.joining(":")) + ":" + e.value
            ).collect(Collectors.joining("#")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ToString
    @Data
    @AllArgsConstructor
    private static class CollectionElement {
        String id;
        int position;
        double value;
        Vector<Double> x;
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
        Arrays.sort(res, Collections.reverseOrder());

        return res;
    }


    public static void main(String[] args) {
        var arguments = new EvolutionaryArguments();
        var parser = new CmdLineParser(arguments);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException clEx) {
            System.err.println("ERROR: Unable to parse command-line options: " + clEx);
        }

        Vector<Double> res = new Vector<>(arguments.getRepeat());
        Vector<Long> timeT = new Vector<>(arguments.getRepeat());

        EX4 ex4 = new EX4();
        for (int i = 0; i < arguments.getRepeat(); i++) {
            System.out.println("Iteration " + i);
            long time = System.currentTimeMillis();
            CollectionElement resI = ex4.launch(arguments);
            res.add(resI.value);
            long endTime = System.currentTimeMillis();
            long took = endTime - time;
            timeT.add(took);
            System.out.println("Took " + took + "ms -> " + resI);
        }

        double squareSum = 0;
        double sum = 0;

        for (double r : res) {
            squareSum += r * r;
            sum += r;
        }

        double average = sum / res.size();
        double standDev = Math.sqrt(squareSum / res.size() - average * average);

        System.out.println("Ecart type : " + standDev + " - Moyenne : " + average);
        System.out.println("Durée moyenne : " + timeT.stream().mapToLong(l -> l).average());
    }
}
