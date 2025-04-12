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

class Quad{
    double x,y,size;
    Quad(double x,double y,double size)
    {   
        this.x=x;
        this.y=y;
        this.size=size;
    }
    boolean contains(double xc,double yc)
    {
        return xc>=x-size/2&&xc<=x+size/2 && yc>=y-size/2&&yc<=y+size/2;
    }
    Quad NW() { return new Quad(x - size/4, y - size/4, size / 2); }
    Quad NE() { return new Quad(x + size/4, y - size/4, size/ 2); }
    Quad SW() { return new Quad(x - size/4, y + size/4, size / 2); }
    Quad SE() { return new Quad(x + size/4, y + size/4, size/ 2); }

}
class QuadTree{
    double threshold=10;
    QuadTree NW,NE,SW,SE;
    Body body=null;
    boolean divided=false;
    Quad quad;
    QuadTree(Quad q)
    {
        this.quad=q;
    }
    void insert(Body b)
    {
        if(body==null)
        {
            body=b;
        }else{
            if(!divided)
            {
                divide();
                insertInside(body);
                body=Body.combine(body, b);

            }else{
                body=Body.combine(body, b);
            }
            insertInside(b);
        }
    }

    void insertInside(Body b)
    {
        if (quad.NW().contains(b.x, b.y)) NW.insert(b);
        else if (quad.NE().contains(b.x, b.y)) NE.insert(b);
        else if (quad.SW().contains(b.x, b.y)) SW.insert(b);
        else if (quad.SE().contains(b.x, b.y)) SE.insert(b);
    }
    void divide()
    {
        NW = new QuadTree(quad.NW());
        NE = new QuadTree(quad.NE());
        SW = new QuadTree(quad.SW());
        SE = new QuadTree(quad.SE());
        divided = true;
    }
    void Force(Body b)
    {
        if(this.body==null || this.body==b) return;
        double dx = this.body.x - b.x;
        double dy = this.body.y - b.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        if(quad.size/dist <threshold)
        {
            b.addForce(this.body);

        }else{
            if (NW != null) NW.Force(b);
            if (NE != null) NE.Force(b);
            if (SW != null) SW.Force(b);
            if (SE != null) SE.Force(b);
        }
    }
}

