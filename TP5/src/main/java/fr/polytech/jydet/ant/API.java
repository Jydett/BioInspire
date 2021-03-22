package fr.polytech.jydet.ant;

import fr.polytech.jydet.ofvr._1.F;
import fr.polytech.jydet.ofvr._1.F0;
import fr.polytech.jydet.ofvr._1.F1;
import fr.polytech.jydet.ofvr._2.EX4;
import fr.polytech.jydet.utils.Tuple;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class API {

    Vector<Double> nest;
    Vector<Ant> colony;
    //par dimention borne min, borne max
    Vector<Tuple<Double>> borne;
    private Double nestRadius;
    private F function;

    public API(APIArguments apiArguments) {
        int n = apiArguments.getD();
        function = apiArguments.getFunction() == 0 ? new F0(n) : new F1(n)
        nest = new Vector<>(n);
        colony = new Vector<>(n);
        for (int i = 0; i < n; i++) {
            borne.add(new Tuple<>(-5d, 5d));
            nest.add(borne.get(i).get_1() + borne.get(i).get_2());//le niz spawn au milieu
            colony.add(new Ant(apiArguments));
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

    public void moveNest() {
        //TODO
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

        API api = new API();
        for (int i = 0; i < arguments.getRepeat(); i++) {
            System.out.println("Iteration " + i);
            long time = System.currentTimeMillis();
            CollectionElement resI = api.launch(arguments);
            res.add(resI.value);
            long endTime = System.currentTimeMillis();
            long took = endTime - time;
            timeT.add(took);
            System.out.println("Took " + took + "ms -> " + resI);
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
