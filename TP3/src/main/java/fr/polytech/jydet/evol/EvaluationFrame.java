package fr.polytech.jydet.evol;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EvaluationFrame extends JFrame {

    public List<IndividualEvaluationPanel> panels = new ArrayList<>();
    private final JPanel bottom;
    private final JButton validate;

    public EvaluationFrame(int width, int height, int imgSize, Runnable notify, Runnable saveArgs) throws HeadlessException {
        super("evaluation");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(imgSize * width + width * 100,imgSize * height + height * 100);
        this.setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(height, width));
        for (int i = 0; i < (width * height); i++) {
            IndividualEvaluationPanel comp = new IndividualEvaluationPanel(i);
            panel.add(comp);
            panels.add(comp);
        }

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
        validate = new JButton("Validate");
        validate.addActionListener(a -> notify.run());
        bottom = new JPanel();
        bottom.add(validate);
        JButton saveArg = new JButton("Save");
        bottom.add(saveArg);
        saveArg.addActionListener(a -> saveArgs.run());
        contentPane.add(bottom, BorderLayout.SOUTH);
        this.setContentPane(contentPane);
        this.setVisible(true);

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EvaluationFrame(3, 3, 100, () -> {}, () -> {}));
    }

    public void beginEvaluate() {
        bottom.setEnabled(true);
        validate.setEnabled(true);
        for (IndividualEvaluationPanel panel : panels) {
            panel.enterEvaluate();
        }
    }

    public void reset() {
        panels.forEach(IndividualEvaluationPanel::reset);
    }

    public Map<Integer, Integer> endEvaluate() {
        bottom.setEnabled(false);
        validate.setEnabled(false);
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
            this.add(new JLabel( Integer.toString(id)));
            slider = new JSlider(0, 5);
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setSnapToTicks(true);
            slider.setValue(0);
            slider.addChangeListener(e -> {
                if (slider.getValue() > 0) {
                    this.setBackground(Color.GREEN);
                } else {
                    this.setBackground(null);
                }
            });
            JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            sliderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            sliderPanel.add(slider);
            this.add(sliderPanel);
            enterEvaluate();
            exitEvaluate();
            updateImage();
        }

        public void reset() {
            slider.setValue(0);
        }

        public void enterEvaluate() {
            slider.setEnabled(true);
        }

        public void exitEvaluate() {
            slider.setEnabled(false);
        }

        public void updateImage() {
            if (imageResult.getIcon() != null) {
                ((ImageIcon) imageResult.getIcon()).getImage().flush();
            }
            imageResult.setIcon(new ImageIcon(id + ".png"));
        }
    }
}
