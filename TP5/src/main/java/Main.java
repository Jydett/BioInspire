import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main :: new);
    }

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }
}
