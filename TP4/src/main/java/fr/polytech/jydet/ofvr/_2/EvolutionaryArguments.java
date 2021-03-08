package fr.polytech.jydet.ofvr._2;

import lombok.Getter;
import org.kohsuke.args4j.Option;

@Getter
public class EvolutionaryArguments {

    @Option(name = "-d", aliases = "--dimension", usage = "Dimension n du problème", required = true)
    private int n;

    @Option(name = "-f", aliases = "--function", usage = "Fonction", required = true)
    private boolean function;

    @Option(name = "-s", aliases = "-stop", usage = "Condition d'arrêt", required = true)
    private int condition;

    @Option(name = "-l", aliases = "--lambda", required = true)
    private int lambda;

    @Option(name = "-m", aliases = "--mu", required = true)
    private int mu;

}
