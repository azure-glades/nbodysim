import java.util.Scanner;
import java.lang.Exception;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.*;
import java.awt.*;

// Main just calls functions
// Vector is used to store position of each object
//INPUT: x,y coord ; x,y velocity; mass
public class Main {
    public static void main(String[] args) {
        double radius_uni = 0;
        int N = 0;
        Body[] particles = null;

        //creating objects for each particle
        try {
            File initFile = new File("debug.txt");
            Scanner sc = new Scanner(initFile);
            sc.useDelimiter(Pattern.compile(" |\\n"));
            /*
            N = Integer.parseInt(sc.next());
            radius_uni = Double.parseDouble(sc.next());
            */
            N = sc.nextInt();
            radius_uni = sc.nextDouble();

            particles = new Body[N];
            for(int i = 0; i<N; i++){
                double[] inital_state = new double[5];
                for(int j = 0; j<5; j++){
                    //String initstream = sc.next();
                    inital_state[j] = sc.nextDouble();
                }
                particles[i] = new Body(inital_state);
                System.out.printf("Body " + i + ": ");
                for(int j = 0; j<5; j++){
                    System.out.printf(inital_state[j]+" ");
                }
                System.out.printf("\n");
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        System.out.println(" ");
        for(int j = 0; j<N; j++){
            System.out.printf("Body %d (%f , %f) (%f , %f) %f \n",j,particles[j].position.get(0),particles[j].position.get(0),particles[j].velocity.get(0),particles[j].velocity.get(0),particles[j].mass);
        }
        System.out.println(" ");

        JFrame frame = new JFrame("N-Body Simulation");
        UniversePanel panel = new UniversePanel(particles, radius_uni);
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        PhysicsEngine runner = new PhysicsEngine(N,radius_uni, particles);

        Timer timer = new Timer(10, e -> {
            runner.simulate(); // tweak time delta
            panel.repaint();
        });
        timer.start();

        for(int j = 0; j<N; j++){
            System.out.printf("Body %d (%f , %f) (%f , %f) %f \n",j,particles[j].position.get(0),particles[j].position.get(0),particles[j].velocity.get(0),particles[j].velocity.get(0),particles[j].mass);
        }
    }
}