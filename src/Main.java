import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Exception;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.*;

import java.awt.Graphics;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
class Body {
    double x, y;
    double vx, vy;
    double mass;
    double radius;
    double fx, fy;
    double G = 1;
    Body(double x, double y, double vx, double vy, double mass, double radius) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
        this.fx = 0;
        this.fy = 0;
    }


void addForce(Body b) {
    double dx = b.x - this.x;
    double dy = b.y - this.y;
    double dist = Math.sqrt(dx * dx + dy * dy + 1e-3);
    double force = (G * this.mass * b.mass) / (dist * dist);
    System.out.println(force);
    this.fx += force * dx / dist;
    this.fy += force * dy / dist;
}

static Body combine(Body a, Body b) {
    double m = a.mass + b.mass;
    double x = (a.x * a.mass + b.x * b.mass) / m;
    double y = (a.y * a.mass + b.y * b.mass) / m;
    return new Body(x, y, 0, 0, m, 0);
}
}

class Sim extends JPanel implements ActionListener {
    int n;
    long lastTime = 0;
    Body[] arr;
    double G =1;

    Sim(int no) {
        n = no;
        arr = new Body[n];
    }

    public void initialize(double[] initialState,double rad) {
        for (int i = 0; i < n; i++) {
            double x = initialState[i * 5 + 0];
            double y = initialState[i * 5 + 1];
            double vx = initialState[i * 5 + 2];
            double vy = initialState[i * 5 + 3];
            double mass = initialState[i * 5 + 4];
            double radius = rad;
            arr[i] = new Body(x, y, vx, vy, mass, radius);
        }
    }
  
    // private void update() {
        // long now = System.nanoTime();
        // double dt = (now - lastTime) * 1e-9;
        // lastTime = now;
        // for (int i = 0; i < n; i++) {
        //     Body b1 = arr[i];
        //     for (int j = i + 1; j < n; j++) {
        //         Body b2 = arr[j];
        //         double dx = b2.x - b1.x;
        //         double dy = b2.y - b1.y;
        //         double dist = Math.sqrt(dx * dx + dy * dy);
        //         if (dist == 0) continue;
        //         double force = G * b1.mass * b2.mass / (dist * dist);
        //         double fx = force * dx / dist;
        //         double fy = force * dy / dist;
        //         b1.vx += fx / b1.mass * dt;
        //         b1.vy += fy / b1.mass * dt;
        //         b2.vx -= fx / b2.mass * dt;
        //         b2.vy -= fy / b2.mass * dt;
        //     }
        // }
        // for (int i = 0; i < n; i++) {
        //     Body b = arr[i];
        //     b.x += b.vx * dt;
        //     b.y += b.vy * dt;
        // }
        private void update() {
            long now = System.nanoTime();
            double dt = (now - lastTime) * 1e-9;
            lastTime = now;
        
            double minX = 0, minY = 0, maxX = getWidth(), maxY = getHeight();
            double size = Math.max(maxX - minX, maxY - minY);
        
            Quad rootQuad = new Quad(minX + size / 2, minY + size / 2, size);
            QuadTree tree = new QuadTree(rootQuad);
        
            for (Body b : arr) {
                b.fx = b.fy = 0;
                // if (rootQuad.contains(b.x, b.y))
                 tree.insert(b);
            }
        
            for (Body b : arr) {
                tree.Force(b);
            }
        
            for (Body b : arr) {

                b.vx += b.fx / b.mass * dt;
                b.vy += b.fy / b.mass * dt;
                b.x += b.vx * dt;
                b.y += b.vy * dt;
                
              
            }
        }
        
        
    //}

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < n; i++) {
            Body b = arr[i];
            int drawX = (int) (b.x - b.radius);
            int drawY = (int) (b.y - b.radius);
            int d = (int) (2 * b.radius);
            g.fillOval(drawX, drawY, d, d);
        }
    }

    
    void collision()
    {   
        for(Body b: arr)
        {
            if(b.x+b.radius>=getWidth())
            {
                b.vx=-Math.abs(b.vx);
            }
            if(b.x-b.radius<0)
            {
                b.vx=Math.abs(b.vx);
            }
            
            if(b.y+b.radius>=getHeight())
            {
                b.vy=-Math.abs(b.vy);
            }
            if(b.y-b.radius<0)
            {
                b.vy=Math.abs(b.vy);
            }
        }
    }
    public void start() {
        lastTime = System.nanoTime();
        new javax.swing.Timer(2, e -> {
            update();
           collision();
            repaint();
        }).start();
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
            File initFile = new File("src/init.txt");
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
