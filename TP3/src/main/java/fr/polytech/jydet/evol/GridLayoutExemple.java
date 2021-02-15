package fr.polytech.jydet.evol;

import javax.swing.*;
import java.awt.*;

public class GridLayoutExemple extends JFrame {

    GridLayout grid = new GridLayout(3, 2);

    public GridLayoutExemple(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250,250);
        frame.setVisible(true);
        JPanel panel = new JPanel();
        frame.setContentPane(panel);
        panel.setLayout(grid);

        panel.add(new JLabel("1"));
        panel.add(new JLabel("2"));
        panel.add(new JLabel("3"));
        panel.add(new JLabel("4"));
        panel.add(new JLabel("5"));
        panel.add(new JLabel("6"));
    }
    public static void main(String[] args) {
        new GridLayoutExemple();
    }
}