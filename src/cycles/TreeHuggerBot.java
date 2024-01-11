import java.util.List;
import java.util.ArrayList;

class TreeHuggerBot extends BasicCycle {

    private Point direction;
    private Point oldDir;
    private List<Point> previous;

    public String getName () { return "Tree Hugger Bot"; }

    public void init (int width, int height, boolean board[][]) {

        int index = 0;
        int min   = Math.max (width, height);

        for (int i = 0; i < 4; ++i) {
            int temp = countDir (Point.CARDINAL[i], board);

            if (temp > min) {
                min   = temp;
                index = i;
            }
        }

        this.previous  = new ArrayList<> ();
        this.direction = Point.CARDINAL[index];
        this.oldDir    = null;
    }

    public Point move (boolean board[][]) {
        List<Point> available = getAvailable (this.position, board);
        List<Point> current   = new ArrayList<> ();
        List<Point> potential = new ArrayList<> ();
        Point       predicted = Point.add (this.position, this.direction);

        // Obtain current directions and determine if any are not
        // included in the previous directions (This determines a
        // wall turning)
        for (Point p : available) {
            Point dir = Point.sub (p, this.position);
            current.add (dir);

            boolean found = false;

            for (Point old : this.previous) {
                if (old.equals (dir))
                    found = true;
            }

            if (!found) potential.add (p);
        }

        // If we were just in a corner, figure out if we should turn
        // or just continue.
        if (this.previous.size () == 1 && current.size () == 2) {
            for (Point p : available) {
                if (p.equals (predicted)) {
                    this.previous = current;
                    this.oldDir   = this.direction;
                    return predicted;
                }
            }
        }
        /*
        else if (this.previous.size () == 1 && current.size () == 3) {
            Point point = Point.add (this.position, this.oldDir);

            this.previous  = current;
            this.oldDir    = this.direction;
            this.direction = this.oldDir;
            return point;
        }*/

        this.previous = current;
        this.oldDir   = this.direction;

        if (available.size () == 0) return predicted;
        if (available.size () == 1) {
            this.direction = Point.sub (available.get (0), this.position);
            return available.get (0);
        }

        Point point = null;

        if (potential.size () == 0) {
            for (Point p : available) {
                if (p.equals (predicted))
                    return predicted;
            }

            point = available.get ((int) (Math.random () * available.size ()));
        }
        else {
            point = potential.get ((int) (Math.random () * potential.size ()));
        }

        this.direction = Point.sub (point, this.position);
        return point;
    }
}