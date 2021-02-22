package fr.polytech.jydet.reactdiffuse;

import lombok.Getter;
import org.kohsuke.args4j.Option;

@Getter
public class ReactDiffuseArguments {
    @Option(name = "-ra", aliases = "--reactionA", usage = "Taux de réaction A")
    private double TX_REACTION_A = 0.04;

    @Option(name = "-ri", aliases = "--reactionI", usage = "Taux de réaction I")
    private double TX_REACTION_I = 0.002;

    @Option(name = "-va", aliases = "--vitesseA", usage = "Vitesse de diffusion A")
    private int SPEED_DIFF_A = 8;

    @Option(name = "-vi", aliases = "--vitesseI", usage = "Vitesse de diffusion I")
    private int SPEED_DIFF_I = 25;

    @Option(name = "-tr", aliases = "--tauxResorption", usage = "Taux de resorption")
    private double TX_RESO = 0.1;

    @Option(name = "-sa", aliases = "--seilActivation", usage = "Seuil d'activation")
    private double THRESHOLD_ACTIVATION = 110;

    @Option(name = "-sau", aliases = "--seilActivationHaut", usage = "Seuil d'activation haut")
    private double THRESHOLD_ACTIVATION_UPPER = 125;
}
