
class Point {

    public static final Point ZERO  = new Point ();
    public static final Point NORTH = new Point (0, -1);
    public static final Point EAST  = new Point (1, 0);
    public static final Point SOUTH = new Point (0, 1);
    public static final Point WEST  = new Point (-1, 0);
    public static final Point CARDINAL[] = new Point[]{NORTH, EAST, SOUTH, WEST};
    
    public int x;
    public int y;

    public Point () {
        this.x = 0;
        this.y = 0;
    }

    public Point (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point normalize (Point p) {
        Point rval = new Point ();

        if (p.x > 0)
            rval.x = 1;
        else if (p.x < 0)
            rval.x = -1;

        if (p.y > 0)
            rval.y = 1;
        else if (p.y < 0)
            rval.y = -1;

        return rval;
    }

    public static Point sub (Point p1, Point p2) {
        return new Point (p1.x - p2.x, p1.y - p2.y);
    }

    public static Point add (Point p1, Point p2) {
        return new Point (p1.x + p2.x, p1.y + p2.y);
    }

    public static Point rotate (Point p, int r) {
        r %= 4;

        if (r == 0) return new Point (p.x, p.y);
        if (r == 1) return new Point (-p.y, p.x);
        if (r == 2) return new Point (-p.x, -p.y);
        
        return new Point (p.y, -p.x);
    }

    public static int getRotations (Point base, Point offset) {
        for (int i = 0; i < 4; ++i) {
            if (rotate (base, i).equals (offset)) return i;
        }

        return -1;
    }

    public boolean equals (Point p) {
        return (this.x == p.x) && (this.y == p.y);
    }
}