import java.util.Vector;

class Bodies{
    Vector<Double> position = new Vector<>();
    Vector<Double> velocity = new Vector<>();
    Vector<Double> acceleration = new Vector<>(); //stores pos and vel
    double mass;

    Bodies(double[] input){
        for(int i = 0; i<2; i++){
            position.add(input[i]);
        }
        for(int i = 2; i<4; i++){
            velocity.add(input[i]);
        }
        mass = input[4];
        acceleration.add(0.0);
        acceleration.add(0.0);
    }
}