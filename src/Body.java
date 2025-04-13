import java.util.Vector;

class Body {
    Vector<Double> position = new Vector<>();
    Vector<Double> velocity = new Vector<>();
    Vector<Double> force = new Vector<>(); //stores pos and vel
    double mass;

    Body(double[] input){
        position.add(0,input[0]);
        position.add(1,input[1]);
        velocity.add(0, input[2]);
        velocity.add(1, input[3]);
        mass = input[4];
        force.add(0.0);
        force.add(0.0);
    }

    void update(double dt){
        velocity.set(0,velocity.get(0)+(force.get(0)/mass)*dt);
        velocity.set(1,velocity.get(1)+(force.get(1)/mass)*dt);

        position.set(0,velocity.get(0)+position.get(0));
        position.set(1,velocity.get(1)+position.get(1));
    }

    void resetForce(){
        this.force.set(0,0.0);
        this.force.set(1,0.0);
    }
}