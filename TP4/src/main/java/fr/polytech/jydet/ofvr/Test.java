package fr.polytech.jydet.ofvr;

import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.ProgressiveSurfaceModel;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.Vector;

public class Test {

    public void testSomething() {
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


        F1 f1 = new F1(2);


        ProgressiveSurfaceModel sm = new ProgressiveSurfaceModel();
        sm.setMapper(new Mapper() {
            public  float f1(float x, float y)
            {
                Vector<Double> doubles = new Vector<>();
                doubles.add((double)x);
                doubles.add((double)y);
                return (float)f1.applyAsDouble(doubles);
//                float r = x*x+y*y;
//                if (r == 0 ) return 1f;
//                return (float)( Math.sin(r)/(r));
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
        sm.plot().execute();
        jsp.setModel(sm);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Test().testSomething());

    }

}
