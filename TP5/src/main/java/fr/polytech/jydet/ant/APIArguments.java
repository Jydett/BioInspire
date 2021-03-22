package fr.polytech.jydet.ant;

import lombok.Getter;
import org.kohsuke.args4j.Option;

@Getter
public class APIArguments {
    @Option(name = "-d", aliases = "--dimension", usage = "Dimension n du problème", required = true)
    private int d;

    @Option(name = "-f", aliases = "--function", usage = "Fonction", required = true)
    private int function;

    @Option(name = "-s", aliases = "-stop", usage = "Condition d'arrêt", required = true)
    private int condition;



    @Option(name = "-r", aliases = "--repeat")
    private int repeat = 1;
}
