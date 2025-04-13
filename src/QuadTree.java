
class QuadTree{
    double threshold=10;
    QuadTree NW,NE,SW,SE;
    Body body=null;
    boolean divided=false;
    Quad quad;
    QuadTree(Quad q)
    {
        this.quad=q;
    }
    void insert(Body b)
    {
        if(body==null)
        {
            body=b;
        }else{
            if(!divided)
            {
                divide();
                insertInside(body);
                body=Body.combine(body, b);

            }else{
                body=Body.combine(body, b);
            }
            insertInside(b);
        }
    }

    void insertInside(Body b)
    {
        if (quad.NW().contains(b.x, b.y)) NW.insert(b);
        else if (quad.NE().contains(b.x, b.y)) NE.insert(b);
        else if (quad.SW().contains(b.x, b.y)) SW.insert(b);
        else if (quad.SE().contains(b.x, b.y)) SE.insert(b);
    }
    void divide()
    {
        NW = new QuadTree(quad.NW());
        NE = new QuadTree(quad.NE());
        SW = new QuadTree(quad.SW());
        SE = new QuadTree(quad.SE());
        divided = true;
    }
    void Force(Body b)
    {
        if(this.body==null || this.body==b) return;
        double dx = this.body.x - b.x;
        double dy = this.body.y - b.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        if(quad.size/dist <threshold)
        {
            b.addForce(this.body);

        }else{
            if (NW != null) NW.Force(b);
            if (NE != null) NE.Force(b);
            if (SW != null) SW.Force(b);
            if (SE != null) SE.Force(b);
        }
    }
}