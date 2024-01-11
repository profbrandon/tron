import java.util.List;
import java.util.ArrayList;

abstract class BasicCycle implements Cycle
{
    protected Point position;

    public void setPos (Point p) {
        this.position = p;
    }

    protected List<Point> getAvailable (Point center, boolean board[][]) {
        List<Point> points = new ArrayList<> ();

        for (int i = 0; i < 4; ++i) {
            Point temp = Point.add (center, Point.CARDINAL[i]);

            if (!board[temp.x][temp.y]) points.add (temp);
        }

        return points;
    }

    protected int countDir (Point dir, boolean board[][]) {
        Point point = Point.add (position, dir);
        int   count = 0;

        while (!board[point.x][point.y]) {
            point = Point.add (point, dir);
            ++count;
        }

        return count; 
    }
}