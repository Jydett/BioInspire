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
    private final int colorMiddle;
    private final boolean gray;

    public ReactDiffuseModel(ReactDiffuseArguments arguments, int size) {
        this.arguments = arguments;
        this.size = size;
        gray = false;
        colorBelowThres = new Color(112, 56, 4, 255).getRGB();
        colorMiddle = new Color(238, 124, 45).getRGB();
        colorOverThres = new Color(255, 223, 0, 255).getRGB();
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
//        for (int j = 0; j < 10; j++) {
            react();
            copy(a, at); copy(i, it);
            diffuseParam();
            copy(at, a); copy(it, i);
            resorb();
            thresh();
//        }
    }

    //copy oldT dans newT
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

    //Diffuse la valeur table[x][y] aux 8 cases autour
    //j'utilise deux tableau pour faire la mise a jour en une fois
    private void diffuseParam(int x, int y, double[][] table, double[][] temp) {
        double tx_diff = 0.1;
        double v = table[x][y] * tx_diff;
        double vPerNeighbour = v / 8;
        int roundDownY = roundDown(y - 1);
        int roundUpY = roundUp(y + 1);
        int roundDownX = roundDown(x - 1);
        int roundUpX = roundUp(x + 1);

        temp[x][roundUpX] = table[x][roundUpX] + vPerNeighbour;
        temp[x][roundDownY] = table[x][roundDownY] + vPerNeighbour;
        temp[roundUpX][y] = table[roundUpX][y] + vPerNeighbour;
        temp[roundDownX][y] = table[roundDownX][y] + vPerNeighbour;
        temp[roundUpX][roundDownY] = table[roundUpX][roundDownY] + vPerNeighbour;
        temp[roundUpX][roundUpY] = table[roundUpX][roundUpY] + vPerNeighbour;
        temp[roundDownX][roundDownY] = table[roundDownX][roundDownY] + vPerNeighbour;
        temp[roundDownX][roundUpY] = table[roundDownX][roundUpY] + vPerNeighbour;

        temp[x][y] = table[x][y] - v;


        //on vérifie que le taux global reste identique
       assert
            temp[x][y] +
            temp[x][roundUpX] +
            temp[x][roundDownY] +
            temp[roundUpX][y] +
            temp[roundDownX][y] +
            temp[roundUpX][roundDownY] +
            temp[roundUpX][roundUpY] +
            temp[roundDownX][roundDownY] +
            temp[roundDownX][roundUpY]
           ==
            table[x][y] +
            table[x][roundUpX] +
            table[x][roundDownY] +
            table[roundUpX][y] +
            table[roundDownX][y] +
            table[roundUpX][roundDownY] +
            table[roundUpX][roundUpY] +
            table[roundDownX][roundDownY] +
            table[roundDownX][roundUpY];
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

    //seuillage
    private void thresh() {
        //En mode niveau de gris sur A
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
        } else {//Mode avec seuil
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if (arguments.getTHRESHOLD_ACTIVATION_UPPER() > a[x][y]) {
                        model.setRGB(x, y, colorOverThres);
                    } else if (arguments.getTHRESHOLD_ACTIVATION() > a[x][y]) {
                        model.setRGB(x, y, colorMiddle);
                    } else {
                        model.setRGB(x, y, colorBelowThres);
                    }
                }
            }
        }
    }

    //affiche le niveau moyen de A
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
