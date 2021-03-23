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

    @Option(name = "-nr", aliases = "-nestRadius", usage = "Rayon de spamn des sites de chasse")
    private double initialNestRadius = 3d;

    @Option(name = "-ar", aliases = "-antRadius", usage = "Rayon d'action des fourmis sur un site")
    private double antHuntRadius = 0.01d;

    @Option(name = "-b", aliases = "-boredom", usage = "Nombre d'echec consécutif avant de changer de site", required = true)
    private int antBoredom;

    @Option(name = "-r", aliases = "--repeat")
    private int repeat = 1;
}
