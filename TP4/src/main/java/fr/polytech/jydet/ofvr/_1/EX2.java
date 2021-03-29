package fr.polytech.jydet.ofvr._1;

import fr.polytech.jydet.ofvr.ColorUtils;
import fr.polytech.jydet.ofvr.Triplet;
import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.ProgressiveSurfaceModel;
import net.ericaro.surfaceplotter.surface.Projector;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import net.ericaro.surfaceplotter.surface.VerticalConfigurationPanel;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class EX2 {

    private JSurfacePanel jsp;
    private List<List<Triplet<Float, Float, Float>>> points;
    private AtomicInteger lineIndex = new AtomicInteger(0);

    public void testSomething() {
        points = new ArrayList<>();
        jsp = new JSurfacePanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                paintPoints(g);
            }
        };
        jsp.setTitleText("Hello");

        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().add(jsp, BorderLayout.CENTER);
        jf.pack();
        jf.setVisible(true);

        Random rand = new Random();
        int max = 100;
        float[][] z1 = new float[max][max];
        float[][] z2 = new float[max][max];
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                z1[i][j] = rand.nextFloat() * 20 - 10;
                z2[i][j] = rand.nextFloat() * 20 - 10;
            }
        }

        F[] fs = new F[] {
            new F0(2), new F1(2), new F2(2)
        };
        F f = null;
        try {
            String data = Files.readString(new File("logant.txt").toPath());
            boolean first = true;
            for (String s : data.split(System.lineSeparator())) {
                if (first) {
                    f = fs[Integer.parseInt(s)];
                    first = false;
                    continue;
                }
                ArrayList<Triplet<Float, Float, Float>> list = new ArrayList<>();
                for (String couple : s.split("#")) {
                    String[] split = couple.split(":");
                    if (split.length == 3) {
                        list.add(new Triplet<>(
                                (float) Double.parseDouble(split[0]),
                                (float) Double.parseDouble(split[1]),
                                (float) Double.parseDouble(split[2])
                            )
                        );
                    } else if (split.length == 4) {
                        list.add(new Triplet<>(
                                (float) Double.parseDouble(split[1]),
                                (float) Double.parseDouble(split[2]),
                                (float) Double.parseDouble(split[3])
                            )
                        );
                    }
                }
                points.add(list);
            }
            System.out.println(points.get(lineIndex.get()));
        } catch (IOException e) {
            points.clear();
            points.add(Collections.emptyList());
            f = fs[0];
        }

        ProgressiveSurfaceModel sm = new ProgressiveSurfaceModel();
        F finalF = f;
        sm.setMapper(new Mapper() {
            public  float f1(float x, float y)
            {
                Vector<Double> doubles = new Vector<>();
                doubles.add((double)x);
                doubles.add((double)y);
                return (float) finalF.applyAsDouble(doubles);
//                float r = x*x+y*y;
//                if (r == 0 ) return 1f;
//                return (float)( Math.sin(r)/(r));
            }

            public  float f2( float x, float y)
            {
                Vector<Double> doubles = new Vector<>();
                doubles.add((double)x);
                doubles.add((double)y);
                return (float) fs[2].applyAsDouble(doubles);
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
        JButton test = new JButton("0");

        try {
            Field configurationPanel = JSurfacePanel.class.getDeclaredField("configurationPanel");
            configurationPanel.setAccessible(true);
            VerticalConfigurationPanel configPanel = (VerticalConfigurationPanel) configurationPanel.get(jsp);
            test.addActionListener(a -> {
                test.setText(lineIndex.incrementAndGet() + "");
                jsp.repaint();
                if (lineIndex.get() == points.size() - 1) {
                    test.setEnabled(false);
                }
            });
            DefaultListModel<String> model = new DefaultListModel<>();
            JList<String> stringJList = new JList<>(model);
            points.get(0).forEach(p -> model.addElement(p.getA() + " | " + p.getB()));
            configPanel.add(new JScrollPane(stringJList));
            configPanel.add(test);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void paintPoints(Graphics g) {
        Color color = Color.RED;
        g.setColor(color);
        SurfaceModel model = jsp.getSurface().getModel();
        if (! model.isDataAvailable()) return;

        ((Graphics2D) g).setStroke(new BasicStroke(0.5f));
        float offsetXY = 0.2f;

//        points.clear();
//        points.add(List.of(
//            new Triplet<>(0f, 0f, 0f),
//            new Triplet<>(0f, 0f, 8.9f),
//            new Triplet<>(0f, 4f, 0f)
//        ));

        for (int i = 0, pointsSize = points.size(); i < pointsSize; i++) {
            color = new Color(ColorUtils.interpolation(((float) i) / pointsSize, Color.WHITE.getRGB(), Color.BLACK.getRGB()));
            g.setColor(color);
            List<Triplet<Float, Float, Float>> ps = points.get(i);
            for (Triplet<Float, Float, Float> point : ps) {
                point = scale(point, model);
                Projector projector = jsp.getSurface().getModel().getProjector();
                Point projectedTopA = projector.project(point.getA(), point.getB(), point.getC() - offsetXY);
                Point projectedBottomA = projector.project(point.getA(), point.getB(), point.getC() + offsetXY);

                g.drawLine(projectedTopA.x, projectedTopA.y, projectedBottomA.x, projectedBottomA.y);

                projectedTopA = projector.project(point.getA(), point.getB() + offsetXY, point.getC());
                projectedBottomA = projector.project(point.getA(), point.getB() - offsetXY, point.getC());

                g.drawLine(projectedTopA.x, projectedTopA.y, projectedBottomA.x, projectedBottomA.y);

                projectedTopA = projector.project(point.getA() + offsetXY, point.getB(), point.getC());
                projectedBottomA = projector.project(point.getA() - offsetXY, point.getB(), point.getC());

                g.drawLine(projectedTopA.x, projectedTopA.y, projectedBottomA.x, projectedBottomA.y);
            }
        }

//        for (Triplet<Float, Float, Float> point : points.get(lineIndex.get())) {
//            point = scale(point, model);
//            Projector projector = jsp.getSurface().getModel().getProjector();
//            Point projectedTopA = projector.project(point.getA(), point.getB(), point.getC() - offsetXY);
//            Point projectedBottomA = projector.project(point.getA(), point.getB(), point.getC() + offsetXY);
//
//            g.drawLine(projectedTopA.x, projectedTopA.y, projectedBottomA.x, projectedBottomA.y);
//
//            projectedTopA = projector.project(point.getA(), point.getB() + offsetXY, point.getC());
//            projectedBottomA = projector.project(point.getA(), point.getB() - offsetXY, point.getC());
//
//            g.drawLine(projectedTopA.x, projectedTopA.y, projectedBottomA.x, projectedBottomA.y);
//
//            projectedTopA = projector.project(point.getA() + offsetXY, point.getB(), point.getC());
//            projectedBottomA = projector.project(point.getA() - offsetXY, point.getB(), point.getC());
//
//            g.drawLine(projectedTopA.x, projectedTopA.y, projectedBottomA.x, projectedBottomA.y);
//        }
    }

    public Triplet<Float, Float, Float> scale(Triplet<Float, Float, Float> point, SurfaceModel model) {
        float sizeX = model.getXMax() - model.getXMin();
        float sizeY = model.getYMax() - model.getYMin();
        float sizeZ = model.getZMax() - model.getZMin();

        return new Triplet<>(
            (((-model.getXMin() + point.getA()) /sizeX) * 20 - 10),
            (((-model.getYMin() + point.getB()) /sizeY) * 20 - 10),
            (((-model.getZMin() + point.getC()) /sizeZ) * 20 - 10)
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EX2().testSomething());
    }

}
