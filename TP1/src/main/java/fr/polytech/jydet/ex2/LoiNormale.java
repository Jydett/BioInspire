package fr.polytech.jydet.ex2;

import fr.polytech.jydet.lib.NormalLaw;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

import java.io.File;

public class LoiNormale {

    public static void main(String[] args) throws Exception {
        double[] values = new double[100000];
        NormalLaw normalLaw = new NormalLaw(0, 1);
        for (int i = 0; i < values.length; i++) {
            values[i] = normalLaw.nextValue();
        }

        var dataset = new HistogramDataset();
        dataset.addSeries("key", values, 100);

        JFreeChart histogram = ChartFactory.createHistogram("",
            "classes", "instances", dataset);

        ChartUtils.saveChartAsPNG(new File("histogram2.png"), histogram, 2000, 400);

    }
}
