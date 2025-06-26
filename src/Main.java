import java.util.*;
import java.util.Timer;
import java.lang.Exception;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.*;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.*;




public class Main {
    private static void generateInitFile(int numBodies, double G, double radius) throws IOException {
    Random rand = new Random();
    PrintWriter writer = new PrintWriter(new FileWriter("./random.txt"));
    
    // First line: number of bodies
    writer.println(numBodies);
    
    // Second line: radius
    writer.println(radius);
    
    // Generate random bodies
    for (int i = 0; i < numBodies; i++) {
        double x = 50 + rand.nextDouble() * 1750;  
        double y = 50 + rand.nextDouble() * 900;  
        double vx = (rand.nextDouble() - 0.5) * 100; 
        double vy = (rand.nextDouble() - 0.5) * 100;  
        double mass = 10 + rand.nextDouble() * 150;   
        
        writer.println(x + " " + y + " " + vx + " " + vy + " " + mass);
    }
    
    writer.close();
    System.out.println("Generated init.txt with " + numBodies + " bodies");
}
    public static void main(String[] args) 
    {

    // Scanner userInput = new Scanner(System.in);
    
    // // uncomment when in automatic mode
    // System.out.print("Enter number of bodies: ");
    // int numBodies = userInput.nextInt();
    
    // System.out.print("Enter value of G (gravitational constant): ");
    // double G = userInput.nextDouble();
    
    // System.out.print("Enter radius for all bodies: ");
    // double radius = userInput.nextDouble();
    
    try {
      
        // generateInitFile(numBodies, G, radius); //uncomment for init file generation
        
     
        File initFile = new File("./init_manual2.txt");
        Scanner sc = new Scanner(initFile);
        sc.useDelimiter(Pattern.compile(" |\\n"));

        int fileNumBodies = Integer.parseInt(sc.next());
        System.out.println("Number of bodies: " + fileNumBodies);
        double radius_uni = Double.parseDouble(sc.next());
        
        Sim sim = new Sim(fileNumBodies);
        sim.G = 1; 
        int index = 0;
        double[] initialState = new double[fileNumBodies * 5];
        
        while (index != fileNumBodies * 5) {
            initialState[index++] = Double.parseDouble(sc.next());
        }
        
        sim.initialize(initialState, radius_uni);
        
        JFrame frame = new JFrame("N-Body Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1850,1053);
        // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
       
        frame.add(sim);
        frame.setVisible(true);
        // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        // Rectangle usableBounds = ge.getMaximumWindowBounds();

        // int maxWidth = usableBounds.width;
        // int maxHeight = usableBounds.height;

        // System.out.println("Maximized width: " + maxWidth);
        // System.out.println("Maximized height: " + maxHeight);
        sim.start();
        sc.close();
        // userInput.close();
        
        
    } catch (Exception e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
}

}