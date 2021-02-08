package fr.polytech.jydet.ex1;

import fr.polytech.jydet.lib.ScalableXYDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import java.io.File;
import java.io.IOException;

public class HistogramEx {

    public static void main(String[] args) throws IOException {

        double[] values = new double[100000];
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.random();
        }

        var dataset = new ScalableXYDataset(1000);
        dataset.addSeries("key", values, 100, 0, 1);

        JFreeChart histogram = ChartFactory.createHistogram("",
            "classes", "instances / 1000", dataset);

        ChartUtils.saveChartAsPNG(new File("histogram.png"), histogram, 1000, 400);
    }
}
