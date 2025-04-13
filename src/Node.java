import java.util.List;

public class Node {
    //each node should have center of mass
    //it should have the size of the quad
    //either store a reference to the Body inside it or store its children (Quad Tree object)

    // stored by tree
    Body CoM;
    double size; // stores the size of a quadrant
    double x, y; //this is the quadrant origin itself. will be center of quadrant

    // tree parameters;
    List<Node> children; //if children is empty , it is a leaf node
    // stored as {quad 1, quad 2, quad 3, quad 4}

    boolean isLeaf(){
        return this.children.isEmpty();
    }

    Node insert(Body input, double size, double qx, double qy){
        this.size = size;
        if (this.isLeaf()) {
            CoM = input;
            this.size = size;
            this.x = qx; this.y = qy;
        } else {
            double nx = input.position.get(0);
            double ny = input.position.get(1);
            CoM.mass += input.mass;

            CoM.position.set(0, (input.position.get(0)*input.mass + CoM.mass*CoM.position.get(0))/(input.mass)+ CoM.mass);
            CoM.position.set(1, (input.position.get(1)*input.mass + CoM.mass*CoM.position.get(1))/(input.mass)+ CoM.mass);

            if(nx >= x && ny >= y) {
                children.set(0, insert(input, size/2, qx+size/2, qy+size/2));
            } else if (nx < x && ny > y){
                children.set(1, insert(input, size/2, qx-size/2, qy+size/2));
            } else if (nx <= x && ny <= y) {
                children.set(2, insert(input, size/2, qx-size/2, qy-size/2));
            } else if(nx > x && ny < y){
                children.set(3, insert(input, size/2, qx+size/2, qy-size/2));
            }
        }
        return this;
    }


}
