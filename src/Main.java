import java.util.Scanner;
import java.lang.Exception;
import java.io.File;
import java.util.regex.Pattern;

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
            File initFile = new File("init.txt");
            Scanner sc = new Scanner(initFile);
            sc.useDelimiter(Pattern.compile(" |\\n"));

            N = Integer.parseInt(sc.next());
            radius_uni = Double.parseDouble(sc.next());

            particles = new Body[N];
            for(int i = 0; i<N; i++){
                double[] inital_state = new double[5];
                for(int j = 0; j<5; j++){
                    String initstream = sc.next();
                    inital_state[j] = Double.parseDouble(initstream);
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
        PhysicsEngine runner = new PhysicsEngine(N,radius_uni, particles);
        runner.simulate();
        for(int j = 0; j<5; j++){
            System.out.printf("Body %d (%f , %f) (%f , %f) %f \n",j,particles[j].position.get(0),particles[j].position.get(0),particles[j].velocity.get(0),particles[j].velocity.get(0),particles[j].mass);
        }
    }
}