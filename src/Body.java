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