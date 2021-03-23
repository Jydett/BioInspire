package fr.polytech.jydet.ant;

import fr.polytech.jydet.ofvr._1.F;
import fr.polytech.jydet.ofvr._1.F0;
import fr.polytech.jydet.ofvr._1.F1;
import fr.polytech.jydet.utils.Tuple;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class API {

    Vector<Ant.HuntResult> abandonnedSite = new Vector<>();
    Vector<Double> nest;
    Vector<Ant> colony;
    //par dimension borne min, borne max
    private Double nestRadius;

    public static Vector<Tuple<Double>> borne;
    public static F function;

    public API(APIArguments apiArguments) {
        nestRadius = apiArguments.getInitialNestRadius();
        int n = apiArguments.getD();
        function = apiArguments.getFunction() == 0 ? new F0(n) : new F1(n);
        nest = new Vector<>(n);
        colony = new Vector<>(n);
        borne = new Vector<>(n);
        for (int i = 0; i < n; i++) {
            borne.add(new Tuple<>(-5d, 5d));
            nest.add(borne.get(i).get_1() + borne.get(i).get_2());//le niz spawn au milieu
        }

        for (int i = 0; i < n; i++) {
            var ant = new Ant( apiArguments.getAntHuntRadius());
            ant.init(randomLocInRange());
            colony.add(ant);
        }

        int antBoredom = apiArguments.getAntBoredom();
        for (int i = 0; i < apiArguments.getCondition(); i = i + colony.size()) {
            colony.forEach(ant -> {
                ant.routine();
                if (ant.getFailCounter().get() >= antBoredom) {
                    abandonnedSite.add(ant.getHuntRes());
                    ant.init(randomLocInRange());
                }
            });
        }
    }

    public Vector<Double> randomLocInRange() {
        Vector<Double> res = new Vector<>(borne.size());
        for (int i = 0; i < borne.size(); i++) {
            Tuple<Double> borneI = borne.get(i);
            res.add(ThreadLocalRandom.current().nextDouble(
                Math.max(borneI.get_1(), nest.get(i) - nestRadius),
                Math.min(borneI.get_2(), nest.get(i) + nestRadius)
                )
            );
        }
        return res;
    }

    public Double getBest() {
        return
            Stream.concat(
                colony.stream()
                    .map(Ant :: getHuntRes)
                    .filter(Objects ::nonNull),
                abandonnedSite.stream())
            .mapToDouble(Ant.HuntResult :: getBestRes)
            .max()
            .orElse(Double.NaN);
    }

    public static void main(String[] args) {
        var arguments = new APIArguments();
        var parser = new CmdLineParser(arguments);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException clEx) {
            System.err.println("ERROR: Unable to parse command-line options: " + clEx);
        }

        Vector<Double> res = new Vector<>(arguments.getRepeat());
        Vector<Long> timeT = new Vector<>(arguments.getRepeat());

        for (int i = 0; i < arguments.getRepeat(); i++) {
            System.out.println("Iteration " + i);
            long time = System.currentTimeMillis();
            Double best = new API(arguments).getBest();
            res.add(best);
            long endTime = System.currentTimeMillis();
            long took = endTime - time;
            timeT.add(took);
            System.out.println("Took " + took + "ms -> " + best);
        }

        double mean = res.stream().mapToDouble(f -> f).average().getAsDouble();
        double standartDev = Math.sqrt(res.stream()
            .map(i -> i - mean)
            .map(i -> i*i)
            .mapToDouble(i -> i).average().getAsDouble());

        System.out.println("Ecart type : " + standartDev + " - Moyenne : " + mean);
        System.out.println("Durée moyenne : " + timeT.stream().mapToLong(l -> l).average().getAsDouble() + "ms");
    }
}
