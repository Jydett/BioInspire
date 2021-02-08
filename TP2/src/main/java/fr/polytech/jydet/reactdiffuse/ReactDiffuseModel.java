package fr.polytech.jydet.reactdiffuse;

import fr.polytech.jydet.utils.MathRandom;
import lombok.Getter;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ReactDiffuseModel {

    private final ReactDiffuseArguments arguments;
    private int size;
    @Getter
    private final BufferedImage model;
    private final double[][] a;
    private final double[][] i;
    private final double[][] at;
    private final double[][] it;
    private final int colorBelowThres;
    private final int colorOverThres;
    private final boolean gray;

    public ReactDiffuseModel(ReactDiffuseArguments arguments, int size) {
        this.arguments = arguments;
        this.size = size;
        gray = false;
        colorBelowThres = new Color(255, 255, 255).getRGB();
        colorOverThres = new Color(17, 74, 35).getRGB();
        this.model = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        a = new double[size][];
        i = new double[size][];
        at = new double[size][];
        it = new double[size][];
        for (int x = 0; x < size; x++) {
            a[x] = new double[size];
            i[x] = new double[size];
            at[x] = new double[size];
            it[x] = new double[size];
            for (int y = 0; y < size; y++) {
                a[x][y] = MathRandom.randomBetweenInclusive(1, 100);
                i[x][y] = MathRandom.randomBetweenInclusive(1, 100);
            }
        }
        thresh();
        copy(a, at); copy(i, it);
    }

    public void update() {
        react();
        copy(a, at); copy(i, it);
        diffuseParam();
        copy(at, a); copy(it, i);
        resorb();
        thresh();
    }

    private void copy(double[][] oldT, double[][] newT) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                newT[x][y] = oldT[x][y];
            }
        }
    }

    private void react() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double ancienI = i[x][y];
                double a_v = a[x][y];
                i[x][y] = i[x][y] + (arguments.getTX_REACTION_I() * a_v * a_v);
                a[x][y] = a_v + (arguments.getTX_REACTION_A() * a_v * a_v / ancienI);
            }
        }
    }

    private void diffuseParam() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int i1 = 0; i1 < arguments.getSPEED_DIFF_A(); i1++) {
                    diffuseParam(x, y, a, at);
                }
                for (int i1 = 0; i1 < arguments.getSPEED_DIFF_I(); i1++) {
                    diffuseParam(x, y, i, it);
                }
            }
        }
    }

    private void diffuseParam(int x, int y, double[][] table, double[][] temp) {
        double v = table[x][y] / 10 / 8;
        int roundDownY = roundDown(y - 1);
        int roundUpY = roundUp(y + 1);
        int roundDownX = roundDown(x - 1);
        int roundUpX = roundUp(x + 1);

        temp[x][roundUpX] = table[x][roundUpX] + v;
        temp[x][roundDownY] = table[x][roundDownY] + v;
        temp[roundUpX][y] = table[roundUpX][y] + v;
        temp[roundDownX][y] = table[roundDownX][y] + v;
        temp[roundUpX][roundDownY] = table[roundUpX][roundDownY] + v;
        temp[roundUpX][roundUpY] = table[roundUpX][roundUpY] + v;
        temp[roundDownX][roundDownY] = table[roundDownX][roundDownY] + v;
        temp[roundDownX][roundUpY] = table[roundDownX][roundUpY] + v;

        temp[x][y] = table[x][y] * 0.9;
    }

    private void resorb() {
        double tx = 1 - arguments.getTX_RESO();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                i[x][y] = i[x][y] * tx;
                a[x][y] = a[x][y] * tx;
            }
        }
    }

    private void thresh() {
        if (gray) {
            double max = 0;
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if (a[x][y] > max) {
                        max = a[x][y];
                    }
                }
            }
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                float grey = (float) ((max - a[x][y]) / max);
                model.setRGB(x, y, new Color(grey, grey, grey).getRGB());
                }
            }
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (arguments.getTHRESHOLD_ACTIVATION() > a[x][y]) {
                    model.setRGB(x, y, colorBelowThres);
                } else {
                    model.setRGB(x, y, colorOverThres);
                }
            }
        }
    }

    public void printStats() {
        double sum = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                sum += a[x][y];
            }
        }
        System.out.println("avg " + (sum/a.length));
    }

    private int roundUp(int a) {
        if (a >= size) {
            return a - size;
        }
        return a;
    }

    private int roundDown(int a) {
        if (a < 0) {
            return a + size;
        }
        return a;
    }
}
