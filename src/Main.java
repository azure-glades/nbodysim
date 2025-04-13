import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Exception;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.*;

import java.awt.Graphics;

public class Main {
    public static void main(String[] args) {
        // int numBodies = 2;
        // Sim sim = new Sim(numBodies);

        // double[] initialState = {

        //     380,380,  0,  0, 40000, 10,
        //     480,380,0,20,10,10
        // };

        double radius_uni;
        try {
            File initFile = new File("init.txt");
            Scanner sc = new Scanner(initFile);
            sc.useDelimiter(Pattern.compile(" |\\n"));

            int numBodies = Integer.parseInt(sc.next());
            System.out.println(numBodies);
            radius_uni = Double.parseDouble(sc.next());
            Sim sim = new Sim(numBodies);
            int index=0;
            double[] initialState=new double[numBodies*5];
            while(index!=numBodies*5)
            {   System.out.println(index);

                initialState[index++]=Double.parseDouble(sc.next());
            }
            sim.initialize(initialState,radius_uni);

            JFrame frame = new JFrame("N-Body Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);
            frame.add(sim);
            frame.setVisible(true);

            sim.start();
            sc.close();
        }catch (Exception e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



    }
}



