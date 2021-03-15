package fr.polytech.jydet.ofvr._1;

import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.ProgressiveSurfaceModel;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Random;
import java.util.Vector;

public class EX1 {

    public static void main(String[] args) {
        JSurfacePanel jsp = new JSurfacePanel();
        jsp.setTitleText("Hello");

        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().add(jsp, BorderLayout.CENTER);
        jf.pack();
        jf.setVisible(true);

        Random rand = new Random();
        int max = 10;
        float[][] z1 = new float[max][max];
        float[][] z2 = new float[max][max];
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                z1[i][j] = rand.nextFloat() * 20 - 10;
                z2[i][j] = rand.nextFloat() * 20 - 10;
            }
        }

        F f = new F0(2);
//        F f = new F1(2);

        ProgressiveSurfaceModel sm = new ProgressiveSurfaceModel();
        sm.setMapper(new Mapper() {
            public  float f1(float x, float y)
            {
                Vector<Double> doubles = new Vector<>();
                doubles.add((double)x);
                doubles.add((double)y);
                return (float)f.applyAsDouble(doubles);
            }

            public  float f2( float x, float y)
            {
                return 0;
            }
        });
        sm.setXMin(-5);
        sm.setXMax(5);
        sm.setYMin(-5);
        sm.setYMax(5);
        sm.setDisplayXY(true);
        sm.setDisplayZ(true);
        sm.plot().execute();
        jsp.setModel(sm);
    }
}
