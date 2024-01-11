
interface Cycle {

    public String getName ();

    public void init (int width, int height, boolean board[][]);

    public Point move (boolean board[][]);

    public void setPos (Point p);
}