class Quad{
    double x,y,size;
    Quad(double x,double y,double size)
    {
        this.x=x;
        this.y=y;
        this.size=size;
    }
    boolean contains(double xc,double yc)
    {
        return xc>=x-size/2&&xc<=x+size/2 && yc>=y-size/2&&yc<=y+size/2;
    }
    Quad NW() { return new Quad(x - size/4, y - size/4, size / 2); }
    Quad NE() { return new Quad(x + size/4, y - size/4, size/ 2); }
    Quad SW() { return new Quad(x - size/4, y + size/4, size / 2); }
    Quad SE() { return new Quad(x + size/4, y + size/4, size/ 2); }

}