import javax.swing.*;
import java.awt.*;

class UniversePanel extends JPanel {
    Body[] particles;
    double radius;

    public UniversePanel(Body[] particles, double radius) {
        this.particles = particles;
        this.radius = radius;
        setPreferredSize(new Dimension(1000, 1000)); // canvas size
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        for (Body body : particles) {
            int x = (int) (width  / 2 + (body.position.get(0) / radius) * width / 2);
            int y = (int) (height / 2 - (body.position.get(1) / radius) * height / 2);

            g.setColor(Color.WHITE);
            g.fillOval(x - 2, y - 2, 4, 4); // draw body as a small dot
        }
    }
}
