package fr.polytech.jydet.evol;

import com.google.gson.GsonBuilder;
import fr.polytech.jydet.reactdiffuse.ReactDiffuseArguments;
import fr.polytech.jydet.reactdiffuse.ReactDiffuseImageProxy;
import fr.polytech.jydet.reactdiffuse.ReactDiffuseModel;
import fr.polytech.jydet.utils.Tuple;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Evolutionary {

    private static final int COL = 5;
    private static final int HEI = 5;

    private static final int POP_SIZE = COL * HEI;
    private static final int IMAGE_SIZE = 100;
    public static final int TURING_ITERATION = 10;
    private EvaluationFrame evaluationFrame;
    private static CountDownLatch initLatch;
    private CountDownLatch evalLatch;
    private List<Integer> toDelete = null;

    public static void main(String[] args) throws InterruptedException {
        initLatch = new CountDownLatch(1);
        Evolutionary evolutionary = new Evolutionary();
        SwingUtilities.invokeLater(() -> {
            EvaluationFrame evaluationFrame = new EvaluationFrame(COL, HEI, IMAGE_SIZE, () -> evolutionary.evalLatch.countDown(), () -> {
                File parametersFile = new File("parameters.txt");
                try {
                    if (! parametersFile.exists()) {
                        parametersFile.createNewFile();
                    }
                    FileWriter writer = new FileWriter(parametersFile);
                    new GsonBuilder().setPrettyPrinting().create()
                        .toJson(evolutionary.population.stream().map(a -> a.arguments).collect(Collectors.toList()), writer);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            evolutionary.evaluationFrame = evaluationFrame;
            initLatch.countDown();
        });
        initLatch.await();
        evolutionary.start();
    }

    public List<ReactDiffuseArgumentsWrapper> population = new ArrayList<>(POP_SIZE);
    public Map<Integer, Integer> notesById = new HashMap<>();

    public ExecutorService executorService = Executors.newFixedThreadPool(POP_SIZE);


    public void start() {
        int i = 0;
        try {
            init();
            while (true) {
                evaluationFrame.setTitle("Génération " + i++);
                evalLatch = new CountDownLatch(1);
                compute();
                refreshFrame(evaluationFrame);
                evaluate(evaluationFrame);
                reproduce(); //mutation incluse
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void refreshFrame(EvaluationFrame evaluationFrame) {
        if (evaluationFrame != null) {
            evaluationFrame.panels.forEach(EvaluationFrame.IndividualEvaluationPanel :: updateImage);
        }
    }

    private void init() {
        population.clear();
        evaluationFrame.reset();
        //FIXME initiallisation tous identique
        for (int i = 0; i < POP_SIZE; i++) {
            population.add(new ReactDiffuseArgumentsWrapper(new ReactDiffuseArguments()));
        }
    }

    private void compute() {
        try {
            ArrayList<Callable<Void>> callables = new ArrayList<>();
            if (toDelete == null) {
                for (int i = 0; i < population.size(); i++) {
                    computeCallableForImage(callables, i);
                }
            } else {
                for (Integer i : toDelete) {
                    computeCallableForImage(callables, i);
                }
            }
            executorService.invokeAll(callables).forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void computeCallableForImage(ArrayList<Callable<Void>> callables, int i) {
        ReactDiffuseArgumentsWrapper argumentsWrapper = population.get(i);
        int finalI = i;
        callables.add(() -> {
            ReactDiffuseImageProxy proxy = new ReactDiffuseImageProxy(new ReactDiffuseModel(argumentsWrapper.arguments, IMAGE_SIZE), TURING_ITERATION);
            proxy.tickFullAndSave(finalI);
            return null;
        });
    }

    private void evaluate(EvaluationFrame evaluationFrame) throws InterruptedException {
        evaluationFrame.beginEvaluate();
        evalLatch.await();
        notesById = evaluationFrame.endEvaluate();
    }

    private void reproduce() {
        Map<Boolean, List<Map.Entry<Integer, Integer>>> results = notesById.entrySet().stream()
            .collect(Collectors.partitioningBy(e -> e.getValue() >= 1));
        List<Map.Entry<Integer, Integer>> toDelete = results.get(Boolean.FALSE);
        List<Map.Entry<Integer, Integer>> toKeep = results.get(Boolean.TRUE);

        //on trie par ordre décroissant d'id
        //on supprime les mauvais résultats
        toDelete.sort((e1, e2) -> Integer.compare(e2.getKey(), e1.getKey()));
        toDelete.forEach(e -> population.set(e.getKey(), null));

        if (toKeep.size() == 1) { /* plus que 1 vivant */
            //reproduction par mutation
            //sinon -> on modifie
            Map.Entry<Integer, Integer> keepedEvaluation = toKeep.get(0);
            ReactDiffuseArgumentsWrapper keepedArg = population.get(keepedEvaluation.getKey());
            toDelete.forEach(d -> {
                ReactDiffuseArgumentsWrapper mutated = keepedArg.copy();
                ReactDiffuseArgumentsWrapper.mutate(mutated);
                population.set(d.getKey(), mutated);
            });
        } else if (toKeep.size() > 1) { /* au moins 2 vivants */
            //on supprime les autres
            //on reproduits
            toDelete.stream().map(e -> {
                ReactDiffuseArgumentsWrapper newEl = getRandomCouple(toKeep).map(x -> population.get(x.getKey()))
                    .compute(ReactDiffuseArgumentsWrapper :: merge);
                population.set(e.getKey(), newEl);
                return newEl;
            }).forEach(ReactDiffuseArgumentsWrapper::mutate);
        } else { /* aucun individu retenue */
            //on recreer tout
            init();
        }
        this.toDelete = toDelete.stream().map(e -> e.getKey()).collect(Collectors.toList());
        notesById.clear();
    }

    public <T> Tuple<T> getRandomCouple(List<T> list) {
        ArrayList<T> arrayList = new ArrayList<>(list);
        Collections.shuffle(arrayList);
        return new Tuple<>(arrayList.get(0), arrayList.get(1));
    }
}
