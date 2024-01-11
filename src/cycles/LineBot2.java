import java.util.List;

class LineBot2 extends BasicCycle {

    private Point direction;

    public String getName () { return "Line Bot V2"; }

    public void init (int width, int height, boolean board[][]) {
        int index = 0;
        int max   = 0;

        for (int i = 0; i < 4; ++i) {
            int temp = countDir (Point.CARDINAL[i], board);

            if (temp > max) {
                max   = temp;
                index = i;
            }
        }

        this.direction = Point.CARDINAL[index];
    }

    public Point move (boolean board[][]) {
        List<Point> available = getAvailable (this.position, board);

        Point predicted = Point.add (this.position, this.direction);

        if (available.size () == 0) return predicted;

        for (Point p : available)
            if (p.equals (predicted)) return predicted;

        available.sort ((Point p1, Point p2) -> {
            Point dir1 = Point.sub (p1, this.position);
            Point dir2 = Point.sub (p2, this.position);

            int d1 = countDir (dir1, board);
            int d2 = countDir (dir2, board);

            if (d1 < d2) return 1;
            if (d1 > d2) return -1;
            
            return 2 * (int) (2 * Math.random ()) - 1;
        });

        Point point = available.get (0);

        this.direction = Point.sub (point, this.position);
        return point;
    }
}