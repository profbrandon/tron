import java.util.List;

class RandomBot extends BasicCycle {

    public RandomBot () {}

    public String getName () { return "Random Bot"; }

    public void init (int width, int height, boolean board[][]) { }

    public Point move (boolean board[][]) {
        List<Point> available = getAvailable (this.position, board);

        if (available.size () == 0) return position;
        
        int index = (int) (Math.random () * available.size ());

        return available.get (index);
    }
}