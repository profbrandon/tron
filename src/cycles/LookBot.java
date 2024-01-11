import java.util.List;

class LookBot extends BasicCycle {

    private Point direction;

    public String getName () { return "Look Bot"; }

    public void init (int width, int height, boolean board[][]) {
        if (this.position.y > height / 2) direction = Point.NORTH;
        else                         direction = Point.SOUTH;
    }

    public Point move (boolean board[][]) {
        List<Point> available = getAvailable (this.position, board);

        Point predicted = Point.add (this.position, this.direction);

        if (available.size () == 0) return predicted;

        available.sort ((Point p1, Point p2) -> {
            Point dir1  = Point.sub (p1, this.position);
            Point dir2  = Point.sub (p2, this.position);
            int   dist1 = countDir (dir1, board);
            int   dist2 = countDir (dir2, board);

            if (dist1 < dist2) return 1;
            if (dist1 > dist2) return -1;

            int a1 = getAvailable (p1, board).size ();
            int a2 = getAvailable (p2, board).size ();

            if (a1 < a2) return 1;
            if (a1 > a2) return -1;
            else return 1 - 2 * (int) (Math.random () * 2);
        });

        Point point = available.get (0);

        this.direction = Point.sub (point, this.position);
        return point;
    }
}