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


    }
}
