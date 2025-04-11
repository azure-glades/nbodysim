import java.util.Scanner;
import java.lang.Exception;
import java.io.File;
import java.util.regex.Pattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            File initFile = new File("init.txt");
            Scanner sc = new Scanner(initFile);
            sc.useDelimiter(Pattern.compile(" |\\n"));

            double[] inital_state = new double[27];
            int i = 0;
            while (sc.hasNext()) {
                String initstream = sc.next();
                inital_state[i++] = Double.parseDouble(initstream);
                System.out.println(inital_state[i-1]);
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}