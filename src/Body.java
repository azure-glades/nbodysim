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