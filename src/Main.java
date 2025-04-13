import java.util.Scanner;
import java.lang.Exception;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int numBodies = 0;
        double radius_uni = 0.0;
        double[] initialState = null;

        try {
            File initFile = new File("init.txt");
            Scanner sc = new Scanner(initFile);
            sc.useDelimiter(Pattern.compile(" |\\n"));

            numBodies = Integer.parseInt(sc.next());
            System.out.println(numBodies);
            radius_uni = Double.parseDouble(sc.next());

            initialState=new double[numBodies*5];

            int index=0;
            while(index!=numBodies*5)
            {
                System.out.println(index);
                initialState[index++]=Double.parseDouble(sc.next());
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        PhysicsEngine physicsEngine = new PhysicsEngine(numBodies);
        physicsEngine.initialize(initialState,radius_uni);

        JFrame frame = new JFrame("N-Body Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(physicsEngine);
        frame.setVisible(true);

        physicsEngine.start();
    }
}



