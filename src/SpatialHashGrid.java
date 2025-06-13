
// Spatial Hash Grid for efficient collision detection
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

public class SpatialHashGrid {
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