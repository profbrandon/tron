import java.util.List;

class LineBot extends BasicCycle {

    private Point direction;

    public String getName () { return "Line Bot"; }

    public void init (int width, int height, boolean board[][]) {
        if (this.position.y > height / 2) direction = Point.NORTH;
        else                         direction = Point.SOUTH;
    }

    public Point move (boolean board[][]) {
        List<Point> available = getAvailable (this.position, board);

        Point predicted = Point.add (this.position, this.direction);

        if (available.size () == 0) return predicted;

        for (Point p : available)
            if (p.equals (predicted)) return predicted;

        Point point = available.get ((int) (Math.random () * available.size ()));

        this.direction = Point.sub (point, this.position);
        return point;
    }
}