import java.util.Vector;

class Bodies{
    Vector<Double> state = new Vector<>(); //stores pos and vel
    double mass;

    Bodies(double[] input){
        for(int i = 0; i<4; i++){
            state.add(input[i]);
        }
        mass = input[4];
    }
}