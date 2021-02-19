import java.text.DecimalFormat;

public class Test {

    public static void main(String[] args) {
        System.out.println(new DecimalFormat("#.##").format(Double.MAX_VALUE));
    }
}
