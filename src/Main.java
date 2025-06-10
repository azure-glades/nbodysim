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
class Body {
    double x, y;
    double vx, vy;
    double mass;
    double radius;
    double fx, fy;
    double G = 1;
    int gridX, gridY; // For spatial hashing

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

    boolean collidesWith(Body other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (this.radius + other.radius);
    }

    void addForce(Body b,double G) {
        double dx = b.x - this.x;
        double dy = b.y - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy + 1e-3);
        double force = (G * this.mass * b.mass) / (dist * dist);
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

// Spatial Hash Grid for efficient collision detection
class SpatialHashGrid {
    private Map<Long, List<Body>> grid;
    private double cellSize;
    private int width, height;

    public SpatialHashGrid(double cellSize, int width, int height) {
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;
        this.grid = new HashMap<>();
    }

    private long getHash(int x, int y) {
        return ((long)x << 32) | (y & 0xFFFFFFFFL);
    }

    private int getGridX(double x) {
        return (int)(x / cellSize);
    }

    private int getGridY(double y) {
        return (int)(y / cellSize);
    }

    public void clear() {
        grid.clear();
    }

    public void insert(Body body) {
        int gridX = getGridX(body.x);
        int gridY = getGridY(body.y);
        body.gridX = gridX;
        body.gridY = gridY;
        
        long hash = getHash(gridX, gridY);
        grid.computeIfAbsent(hash, k -> new ArrayList<>()).add(body);
    }

    public List<Body> getNearbyBodies(Body body) {
        List<Body> nearby = new ArrayList<>();
        int gridX = body.gridX;
        int gridY = body.gridY;

        // Check current cell and 8 neighboring cells
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                long hash = getHash(gridX + dx, gridY + dy);
                List<Body> cellBodies = grid.get(hash);
                if (cellBodies != null) {
                    nearby.addAll(cellBodies);
                }
            }
        }
        return nearby;
    }

    public void handleCollisions() {
        Set<Body> processed = new HashSet<>();
        
        for (List<Body> cellBodies : grid.values()) {
            for (Body body : cellBodies) {
                if (processed.contains(body)) continue;
                
                List<Body> nearby = getNearbyBodies(body);
                for (Body other : nearby) {
                    if (body == other || processed.contains(other)) continue;
                    
                    if (body.collidesWith(other)) {
                        handleCollision(body, other);
                    }
                }
                processed.add(body);
            }
        }
    }

    private void handleCollision(Body b1, Body b2) {
        // Elastic collision response
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
        
        // Only resolve if objects are approaching
        if (dvn > 0) return;
        
        // Collision impulse
        double impulse = 2 * dvn / (b1.mass + b2.mass);
        
        b1.vx += impulse * b2.mass * dx;
        b1.vy += impulse * b2.mass * dy;
        b2.vx -= impulse * b1.mass * dx;
        b2.vy -= impulse * b1.mass * dy;
        
        // Separate overlapping bodies
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
}

class Sim extends JPanel {
    int n;
    long lastTime = 0;
    Body[] arr;
    double G = 1;
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
        spatialGrid = new SpatialHashGrid(cellSize, 800, 800);
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
            tree.insert(b);
        }

        for (Body b : arr) {
            tree.Force(b,this.G);
        }
        
        for (Body b : arr) {
            b.vx += b.fx / b.mass * dt;
            b.vy += b.fy / b.mass * dt;
            b.x += b.vx * dt;
            b.y += b.vy * dt;
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
    }

    void collision() {
        for (Body b : arr) {
            if (b.x + b.radius >= getWidth()) {
                b.vx = -Math.abs(b.vx);
            }
            if (b.x - b.radius < 0) {
                b.vx = Math.abs(b.vx);
            }
            
            if (b.y + b.radius >= getHeight()) {
                b.vy = -Math.abs(b.vy);
            }
            if (b.y - b.radius < 0) {
                b.vy = Math.abs(b.vy);
            }
        }
    }

    public void start() {
        lastTime = System.nanoTime();
        new javax.swing.Timer(2, e -> {
            System.out.println("frame complete");
            update();
            handleCollisions(); // Use efficient collision detection
            collision();
            repaint();
        }).start();
    }
}

class Quad {
    double x, y, size;
    
    Quad(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    boolean contains(double xc, double yc) {
        return xc >= x - size/2 && xc <= x + size/2 && yc >= y - size/2 && yc <= y + size/2;
    }
    
    Quad NW() { return new Quad(x - size/4, y - size/4, size / 2); }
    Quad NE() { return new Quad(x + size/4, y - size/4, size / 2); }
    Quad SW() { return new Quad(x - size/4, y + size/4, size / 2); }
    Quad SE() { return new Quad(x + size/4, y + size/4, size / 2); }
}

class QuadTree {
    double threshold = 0.5;
    QuadTree NW, NE, SW, SE;
    Body body = null;
    boolean divided = false;
    Quad quad;
    
    QuadTree(Quad q) {
        this.quad = q;
    }
    
    void insert(Body b) {
        if (body == null) {
            body = b;
        } else {
            if (!divided) {
                divide();
                insertInside(body);
                body = Body.combine(body, b);
            } else {
                body = Body.combine(body, b);
            }
            insertInside(b);
        }
    }

    void insertInside(Body b) {
        if (quad.NW().contains(b.x, b.y)) NW.insert(b);
        else if (quad.NE().contains(b.x, b.y)) NE.insert(b);
        else if (quad.SW().contains(b.x, b.y)) SW.insert(b);
        else if (quad.SE().contains(b.x, b.y)) SE.insert(b);
    }
    
    void divide() {
        NW = new QuadTree(quad.NW());
        NE = new QuadTree(quad.NE());
        SW = new QuadTree(quad.SW());
        SE = new QuadTree(quad.SE());
        divided = true;
    }
    
    void Force(Body b,double G) {
        if (this.body == null || this.body == b) return;
        double dx = this.body.x - b.x;
        double dy = this.body.y - b.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        
        if (NW == null && NE == null && SW == null && SE == null) {
            b.addForce(this.body,G);  
            return;
        }
        
        if (quad.size/dist <= threshold) {
            b.addForce(this.body,G);
        } else {
            if (NW != null) NW.Force(b,G);
            if (NE != null) NE.Force(b,G);
            if (SW != null) SW.Force(b,G);
            if (SE != null) SE.Force(b,G);
        }
    }
}

public class Main {
    private static void generateInitFile(int numBodies, double G, double radius) throws IOException {
    Random rand = new Random();
    PrintWriter writer = new PrintWriter(new FileWriter("./init.txt"));
    
    // First line: number of bodies
    writer.println(numBodies);
    
    // Second line: radius
    writer.println(radius);
    
    // Generate random bodies
    for (int i = 0; i < numBodies; i++) {
        double x = 50 + rand.nextDouble() * 700;  
        double y = 50 + rand.nextDouble() * 700;  
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

    Scanner userInput = new Scanner(System.in);
    
    // uncomment when in automatic mode
    System.out.print("Enter number of bodies: ");
    int numBodies = userInput.nextInt();
    
    System.out.print("Enter value of G (gravitational constant): ");
    double G = userInput.nextDouble();
    
    System.out.print("Enter radius for all bodies: ");
    double radius = userInput.nextDouble();
    
    try {
      
        generateInitFile(numBodies, G, radius); //uncomment for init file generation
        
     
        File initFile = new File("./init.txt");
        Scanner sc = new Scanner(initFile);
        sc.useDelimiter(Pattern.compile(" |\\n"));

        int fileNumBodies = Integer.parseInt(sc.next());
        System.out.println("Number of bodies: " + fileNumBodies);
        double radius_uni = Double.parseDouble(sc.next());
        
        Sim sim = new Sim(fileNumBodies);
        sim.G = G; 
        int index = 0;
        double[] initialState = new double[fileNumBodies * 5];
        
        while (index != fileNumBodies * 5) {
            initialState[index++] = Double.parseDouble(sc.next());
        }
        
        sim.initialize(initialState, radius_uni);
        
        JFrame frame = new JFrame("N-Body Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(sim);
        frame.setVisible(true);

        sim.start();
        sc.close();
        userInput.close();
        
    } catch (Exception e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
}
}