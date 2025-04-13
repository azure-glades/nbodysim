import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PhysicsEngine extends JPanel implements ActionListener { //changed from Sim to PhysicsEngine
    int n;
    long lastTime = 0;
    Body[] arr;
    double G =1;

    PhysicsEngine(int no) {
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
    public void start() {
        lastTime = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // sets up runnable methods that will be done by threads in the pool
        Runnable update_handler = this::update;
        Runnable collision_handler = this::collision;
        Runnable repainter = this::repaint;

        new javax.swing.Timer(2, e -> {
            pool.submit(update_handler);
            pool.submit(collision_handler);
            pool.submit(repainter);
            //update();
            //collision();
            //repaint();
        }).start();
    }

    // BRUTE FORCE
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
    protected void paintComponent(Graphics g) { // draws a body
        super.paintComponent(g);
        for (int i = 0; i < n; i++) {
            Body b = arr[i];
            int drawX = (int) (b.x - b.radius);
            int drawY = (int) (b.y - b.radius);
            int d = (int) (2 * b.radius);
            g.fillOval(drawX, drawY, d, d);
        }
    }


    void collision() //handles collisions with border
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


}