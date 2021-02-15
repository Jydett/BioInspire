package fr.polytech.jydet.evol;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class EvaluationFrame extends JFrame {

    public List<IndividualEvaluationPanel> panels = new ArrayList<>();

    //FIXME initialize grid size with argument
    public EvaluationFrame(int pop_size) throws HeadlessException {
        super("evaluation");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,500);
        this.setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < pop_size; i++) {
            IndividualEvaluationPanel comp = new IndividualEvaluationPanel(i);
            panel.add(comp);
            panels.add(comp);
        }

        this.setContentPane(panel);
        this.setVisible(true);

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EvaluationFrame(9));
    }

    public Map<Integer, Integer> evaluate() {
        for (IndividualEvaluationPanel panel : panels) {
            panel.enterEvaluate();
        }
        Map<Integer, Integer> res = panels.stream().collect(Collectors.toMap(i -> i.id, i -> i.slider.getValue()));
        for (IndividualEvaluationPanel panel : panels) {
            panel.exitEvaluate();
        }
        return res;
    }

    public static class IndividualEvaluationPanel extends JPanel {

        private final JLabel imageResult;
        private final JSlider slider;
        private final int id;

        public IndividualEvaluationPanel(int id) {
            this.id = id;
            imageResult = new JLabel();
            this.add(imageResult);
            this.add(new JLabel("Individu " + id));
            slider = new JSlider(0, 5);
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setSnapToTicks(true);
            JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            sliderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            sliderPanel.add(slider);
            this.add(sliderPanel);
        }

        public void enterEvaluate() {
            slider.setValue(0);
            slider.setEnabled(true);
        }

        public void exitEvaluate() {
            slider.setEnabled(false);
        }
    }
}
