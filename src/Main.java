import java.util.Scanner;
import java.lang.Exception;
import java.io.File;
import java.util.Vector;
import java.util.regex.Pattern;

// Main just calls functions
// Vector is used to store position of each object
//INPUT: x,y coord ; x,y velocity; mass
public class Main {
    public static void main(String[] args) {
        double radius_uni;
        
        //creating objects for each particle
        try {
            File initFile = new File("init.txt");
            Scanner sc = new Scanner(initFile);
            sc.useDelimiter(Pattern.compile(" |\\n"));

            int N = Integer.parseInt(sc.next());
            radius_uni = Double.parseDouble(sc.next());

            Bodies[] particles = new Bodies[N];
            for(int i = 0; i<N; i++){
                double[] inital_state = new double[5];
                for(int j = 0; j<5; j++){
                    String initstream = sc.next();
                    inital_state[j] = Double.parseDouble(initstream);
                }
                particles[i] = new Bodies(inital_state);
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

        //to do: create a function to calculate the next position using physics
    }
}

