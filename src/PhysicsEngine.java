import java.lang.Math;

class PhysicsEngine{
    Body[] particles;
    int num_of_particles;
    double size_univ;

    PhysicsEngine(int n, double radius, Body[] particles){
        num_of_particles = n;
        size_univ = radius;
        this.particles = particles;
    }

    void simulate(){
        for (Body b : particles) {
            b.resetForce();
        }

        for(int i = 0; i < num_of_particles; i++){
            for(int j = i; j < num_of_particles; j++){
                if(i != j){
                    updateForce(particles[i], particles[j]);
                }
            }
        }

        for (Body b : particles) {
            b.update(25000000); // small time-step (seconds)
        }
    }

    void updateForce(Body p1, Body p2){
        double dx = p2.position.get(0) - p1.position.get(0);
        double dy = p2.position.get(1) - p1.position.get(1);
        double r2 = dx * dx + dy * dy;
        double dist = Math.sqrt(r2 + 1e9); // epsilon to avoid div by 0

        double G = 6.743;

        double force = (G * p1.mass * p2.mass) / (r2 + 1e9);
        double fx = force * dx / dist;
        double fy = force * dy / dist;

        p1.force.set(0, p1.force.get(0) + fx);
        p1.force.set(1, p1.force.get(1) + fy);
        p2.force.set(0, p2.force.get(0) - fx);
        p2.force.set(1, p2.force.get(1) - fy);
    }
}
