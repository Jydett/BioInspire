package fr.polytech.jydet.ofvr;

import java.util.Vector;

public class EX1 {

    public static void main(String[] args) {
        F1 f1 = new F1(2);
        Vector<Double> doubles = new Vector<>();
        doubles.add(Math.random());
        doubles.add(Math.random());
        System.out.println(f1.applyAsDouble(doubles));

//        var dataset = new ScalableXYDataset(1000);
//        dataset.addSeries("key", values, 100, 0, 1);
//
//        JFreeChart histogram = ChartFactory.createHistogram("",
//                "classes", "instances / 1000", dataset);
//
//        ChartUtils.saveChartAsPNG(new File("histogram.png"), histogram, 1000, 400);
    }
}
