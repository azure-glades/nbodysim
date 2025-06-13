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

public class Quad {
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
