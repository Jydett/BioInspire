package fr.polytech.jydet.evol;

import fr.polytech.jydet.reactdiffuse.ReactDiffuse;
import fr.polytech.jydet.reactdiffuse.ReactDiffuseArguments;
import fr.polytech.jydet.reactdiffuse.ReactDiffuseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Evolutionary {

    private static final int POP_SIZE = 9;

    public static void main(String[] args) {
        new Evolutionary();
    }

    public List<ReactDiffuseArgumentsWrapper> population = new ArrayList<>(POP_SIZE);
    public Map<Integer, Integer> notesById = new HashMap<>();

    public ExecutorService executorService = Executors.newFixedThreadPool(POP_SIZE);


    public Evolutionary() {
        init();
        compute();
        evaluate();
        reproduce();
        mutate();
    }

    private void init() {

    }

    private void compute() {
        try {
            executorService.invokeAll(population.stream().map(a -> (Callable<Void>) () -> {
                ReactDiffuse.main(a.toMainArgs());
                return null;
            }).collect(Collectors.toList()));
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final EvaluationFrame evaluationFrame = new EvaluationFrame(9);

    private void evaluate() {
        notesById = evaluationFrame.evaluate();
    }

    private void reproduce() {
        List<Map.Entry<Integer, Integer>> survivors = notesById.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .collect(Collectors.toList());
        if (survivors.size() == 1) { /* plus que 1 vivant */
            //reproduction par mutation
            //sinon -> on modifie
        } else if (survivors.size() > 1) { /* au moins 2 vivants */
            //on supprime les autres
            //on reproduits
        } else { /* aucun individu selectionné */
            //on recreer tout
            population.clear();
            init();
        }
    }

    private void mutate() {
        //on modifie le vecteur avec un gossien de moy = 1
    }
}
