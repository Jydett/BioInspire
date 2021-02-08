package fr.polytech.jydet.ex3;

import fr.polytech.jydet.lib.NormalLaw;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

import java.io.File;

public class MelangeDeLoi {

    public static void main(String[] args) throws Exception {
        NormalLaw law1 = new NormalLaw(0, 1);
        NormalLaw law2 = new NormalLaw(0.4, 2);
        NormalLaw law3 = new NormalLaw(4, 2);

        int nbrValue = 100000;

        double[] values = new double[nbrValue];

        for (int i = 0; i < nbrValue; i++) {
            double random = Math.random();
            NormalLaw choosenLaw;
            if (random < 1.0/3) {
                choosenLaw = law1;
            } else if (random < 2.0/3) {
                choosenLaw = law2;
            } else {
                choosenLaw = law3;
            }
            values[i] = choosenLaw.nextValue();
        }

        var dataset = new HistogramDataset();
        dataset.addSeries("key", values, 50);

        JFreeChart histogram = ChartFactory.createHistogram("",
            "classes", "instances", dataset);

        ChartUtils.saveChartAsPNG(new File("histogram3.png"), histogram, 2000, 400);
    }
}
