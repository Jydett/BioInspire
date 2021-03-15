package fr.polytech.jydet.ofvr._2;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kohsuke.args4j.Option;

@Getter
public class EvolutionaryArguments {

    @Option(name = "-d", aliases = "--dimension", usage = "Dimension n du problème", required = true)
    private int d;

    @Option(name = "-f", aliases = "--function", usage = "Fonction", required = true)
    private int function;

    @Option(name = "-s", aliases = "-stop", usage = "Condition d'arrêt", required = true)
    private int condition;

    @Option(name = "-l", aliases = "--lambda", required = true)
    private int lambda;

    @Option(name = "-m", aliases = "--mu", required = true)
    private int mu;

    @Option(name = "-r", aliases = "--repeat")
    private int repeat = 1;
}
