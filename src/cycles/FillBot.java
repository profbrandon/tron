import java.util.List;

class FillBot extends BasicCycle {

    private Point direction;

    private Point previousDir = null;

    public String getName () { return "Fill Bot"; }

    public void init (int width, int height, boolean board[][]) {
        if (this.position.y > height / 2) direction = Point.NORTH;
        else                         direction = Point.SOUTH;
    }

    public Point move (boolean board[][]) {
        List<Point> available = getAvailable (this.position, board);

        Point predicted = Point.add (this.position, this.direction);

        if (available.size () == 0) {
            this.previousDir = null;
            return predicted;
        }

        // Last resort
        Point point = available.get ((int) (Math.random () * available.size ()));

        // If forward is available, update
        for (Point p : available) {
            if (p.equals (predicted)) {
                if (this.direction == this.previousDir) this.previousDir = null;
                point = predicted;
            }
        }

        if (previousDir != null) {
            int rotations = Point.getRotations (this.previousDir, this.direction);

            //if (rotations == -1) point = available.get ((int) (Math.random () * available.size ()));
            Point target = Point.rotate (this.direction, rotations);

            for (Point p : available) {
                if (p.equals (Point.add (this.position, target))) point = p;
            }
        }



        this.previousDir = this.direction;
        this.direction   = Point.sub (point, this.position);
        return point;
    }


}