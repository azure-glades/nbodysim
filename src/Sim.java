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
import java.awt.Color;
import java.awt.Font;

public class Sim extends JPanel {
    int n;
    long lastTime = 0;
    Body[] arr;
    double G = 1;
    int frame_count=0;
    long frame_time=0;
    int fps=0;
    private SpatialHashGrid spatialGrid;

    Sim(int no) {
        n = no;
        arr = new Body[n];
    }

    public void initialize(double[] initialState, double rad) {
        for (int i = 0; i < n; i++) {
            double x = initialState[i * 5 + 0];
            double y = initialState[i * 5 + 1];
            double vx = initialState[i * 5 + 2];
            double vy = initialState[i * 5 + 3];
            double mass = initialState[i * 5 + 4];
            double radius = rad;
            arr[i] = new Body(x, y, vx, vy, mass, radius);
        }
        
        // Initialize spatial grid with cell size based on average body radius
        double cellSize = rad * 4; // Cell size should be larger than body diameter
        spatialGrid = new SpatialHashGrid(cellSize, 1850, 1053);
    }

    // Efficient collision detection using spatial hashing - O(n) average case
    void handleCollisions() {
        spatialGrid.clear();
        
        // Insert all bodies into spatial grid
        for (Body body : arr) {
            spatialGrid.insert(body);
        }
        
        // Handle collisions efficiently
        spatialGrid.handleCollisions();
    }

    // private void update() {
    //     long now = System.nanoTime();
    //     if((now-frame_time)<1e+9)
    //     {
    //         frame_count+=1;
    //         // System.out.println((now-lastTime));
    //     }else{
    //         fps=frame_count;
    //         // System.out.println(frame_count);
    //         frame_count=0;
    //         frame_time=System.nanoTime();
    //     }
    //     double dt = (now - lastTime) * 1e-9;
    //     lastTime = now;

    //     double minX = 0, minY = 0, maxX = getWidth(), maxY = getHeight();
    //     double size = Math.max(maxX - minX, maxY - minY);

    //     Quad rootQuad = new Quad(minX + size / 2, minY + size / 2, size);
    //     QuadTree tree = new QuadTree(rootQuad);

    //     for (Body b : arr) {
    //         b.fx = b.fy = 0;
    //         tree.insert(b);
    //     }

    //     for (Body b : arr) {
    //         tree.Force(b,this.G);
    //     }
        
    //     for (Body b : arr) {
    //         b.vx += b.fx / b.mass * dt;
    //         b.vy += b.fy / b.mass * dt;
    //         b.x += b.vx * dt;
    //         b.y += b.vy * dt;
    //     }
    // }
    private void update() {
        
         long now = System.nanoTime();
        if((now-frame_time)<1e+9)
        {
            frame_count+=1;
            // System.out.println((now-lastTime));
        }else{
            fps=frame_count;
            // System.out.println(frame_count);
            frame_count=0;
            frame_time=System.nanoTime();
        }
    double dt = (now - lastTime) * 1e-9;
    lastTime = now;
if(Main.mode){
    // Reset forces
    for (Body b : arr) {
        b.fx = 0;
        b.fy = 0;
    }

    // Brute-force pairwise interaction: gravity + elastic collisions
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            Body bi = arr[i];
            Body bj = arr[j];

            // Add gravitational forces
            bi.addForce(bj, this.G);
            bj.addForce(bi, this.G); // Newton's 3rd law

            // Check and handle collisions
            if (bi.collidesWith(bj)) {
                handleElasticCollision(bi, bj);
            }
        }
    }
}else{

     double minX = 0, minY = 0, maxX = getWidth(), maxY = getHeight();
        double size = Math.max(maxX - minX, maxY - minY);

        Quad rootQuad = new Quad(minX + size / 2, minY + size / 2, size);
        QuadTree tree = new QuadTree(rootQuad);

        for (Body b : arr) {
            b.fx = b.fy = 0;
            tree.insert(b);
        }

        for (Body b : arr) {
            tree.Force(b,this.G);
        }

         handleCollisions();


}
    // Update positions and velocities
    for (Body b : arr) {
        b.vx += b.fx / b.mass * dt;
        b.vy += b.fy / b.mass * dt;
        b.x += b.vx * dt;
        b.y += b.vy * dt;
    }
}
private void handleElasticCollision(Body b1, Body b2) {
    double dx = b2.x - b1.x;
    double dy = b2.y - b1.y;
    double distance = Math.sqrt(dx * dx + dy * dy);
    
    if (distance == 0) return; // Prevent division by zero

    // Normalize collision vector
    dx /= distance;
    dy /= distance;

    // Relative velocity
    double dvx = b2.vx - b1.vx;
    double dvy = b2.vy - b1.vy;
    double dvn = dvx * dx + dvy * dy;

    if (dvn > 0) return; // Bodies moving apart

    // Elastic collision impulse
    double impulse = 2 * dvn / (b1.mass + b2.mass);

    b1.vx += impulse * b2.mass * dx;
    b1.vy += impulse * b2.mass * dy;
    b2.vx -= impulse * b1.mass * dx;
    b2.vy -= impulse * b1.mass * dy;

    // Resolve overlap
    double overlap = (b1.radius + b2.radius) - distance;
    if (overlap > 0) {
        double separationX = dx * overlap * 0.5;
        double separationY = dy * overlap * 0.5;
        b1.x -= separationX;
        b1.y -= separationY;
        b2.x += separationX;
        b2.y += separationY;
    }
}

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
        // g.setColor(Color.BLACK);
        // g.setFont(new Font("SansSerif", Font.BOLD, 18));
        // g.drawString("FPS: " +fps, 10, 20); 
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setTitle("N-Body Simulator rendering with FPS: " + fps);


    }

    void collision() {
        // System.out.print(getHeight());
        // System.out.print(getWidth());
        for (Body b : arr) {
            if (b.x + b.radius >= 1850) {
                b.vx = -Math.abs(b.vx);
            }
            if (b.x - b.radius < 0) {
                b.vx = Math.abs(b.vx);
            }
            
            if (b.y + b.radius >= 1016) {
                b.vy = -Math.abs(b.vy);
            }
            if (b.y - b.radius < 0) {
                b.vy = Math.abs(b.vy);
            }
        }
    }

    public void start() {
        lastTime = System.nanoTime();
        frame_time= System.nanoTime();
        new javax.swing.Timer(2, e -> {
            // System.out.println("frame complete");
            update();
            // Use efficient collision detection
            collision();
            repaint();
        }).start();
    }
//     public void start() {
//     lastTime = System.nanoTime();

//     // Thread 1: Handles updates
//     Thread updateThread = new Thread(() -> {
//         while (true) {
//             update();
//             // try {
//             //     Thread.sleep(2); // Optional tuning
//             // } catch (InterruptedException e) {
//             //     e.printStackTrace();
//             // }
//         }
//     });

//     // Thread 2: Handles collision detection
//     Thread collisionDetectionThread = new Thread(() -> {
//         while (true) {
//             handleCollisions();
//             // try {
//             //     Thread.sleep(2);
//             // } catch (InterruptedException e) {
//             //     e.printStackTrace();
//             // }
//         }
//     });

//     // Thread 3: Handles post-collision logic
//     Thread collisionResponseThread = new Thread(() -> {
//         while (true) {
//             collision();
//             // try {
//             //     Thread.sleep(2);
//             // } catch (InterruptedException e) {
//             //     e.printStackTrace();
//             // }
//         }
//     });

//     // Thread 4: Handles UI repaint on EDT
//     Thread renderThread = new Thread(() -> {
//         while (true) {
//             SwingUtilities.invokeLater(() -> {
//                 System.out.println("frame complete");
//                 repaint();
//             });
//             // try {
//             //     Thread.sleep(16); // ~60 FPS
//             // } catch (InterruptedException e) {
//             //     e.printStackTrace();
//             // }
//         }
//     });

//     // Start all threads
//     updateThread.start();
//     collisionDetectionThread.start();
//     collisionResponseThread.start();
//     renderThread.start();
// }

}