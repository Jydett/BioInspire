package fr.polytech.jydet.ex4;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

import java.io.File;

public class Discret {

    public static void main(String[] args) throws Exception {
        var p = new double[] {
            0.5/8,
            2.5/8,
            0.75/8,
            2.0/8,
            0.5/8,
            1.0/8,
            0.25/8,
            0.5/8
        };

        int nbr = 8000;
        double[] values = new double[nbr];

        for (int x = 0; x < nbr; x++) {
            var t = p[0];
            var a = Math.random();
            var i = 0;
            //faire dicotomie
            while (t < a) {
                i = i + 1;
                t = t + p[i];
            }
            values[x] = i;
        }
        var dataset = new HistogramDataset();
        dataset.addSeries("key", values, 8);
        JFreeChart histogram = ChartFactory.createHistogram("",
            "classes", "instances", dataset);
        ChartUtils.saveChartAsPNG(new File("histogram4.png"), histogram, 1000, 400);
    }
}
