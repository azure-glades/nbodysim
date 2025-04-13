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
        for(int i = 0; i < this.particles.length; i++){
            for(int j = i; j < this.particles.length; j++){
                if(i != j){
                    updateForce(particles[i], particles[j]);
                }
            }
        }

        for(int i = 0; i < this.particles.length; i++){
            particles[i].update(1);
        }
    }

    void updateForce(Body p1, Body p2){
        double x1 = p1.position.get(0); double y1 = p1.position.get(1);
        double x2 = p2.position.get(0); double y2 = p2.position.get(1);
        double r_sqr = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
        double G = 6.6743;
        double force = G*p1.mass*p2.mass/r_sqr;
        double angle = Math.atan((y2-y1)/(x2-x1));

        p1.force.set(0, p1.force.get(0)+force*Math.cos(angle));
        p1.force.set(1, p1.force.get(1)+force*Math.sin(angle));
    }
}
