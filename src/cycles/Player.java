import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class Player extends BasicCycle implements KeyListener {

    private Point direction;

    public String getName () { return "Player"; }

    public void init (int width, int height, boolean board[][]) {
        if (this.position.y > height / 2) direction = Point.NORTH;
        else                              direction = Point.SOUTH;
    }

    public Point move (boolean board[][]) {
        return Point.add (this.position, this.direction);
    }

    public void keyReleased (KeyEvent event) {} 

    public void keyPressed (KeyEvent event) {

        Point temp = null;

        switch (event.getKeyCode ()) {
            case KeyEvent.VK_LEFT:
                temp = Point.WEST;
                break;
            case KeyEvent.VK_RIGHT:
                temp = Point.EAST;
                break;
            case KeyEvent.VK_UP:
                temp = Point.NORTH;
                break;
            case KeyEvent.VK_DOWN:
                temp = Point.SOUTH;
                break;
        }
        
        if (!temp.equals (Point.rotate (this.direction, 2)))
            this.direction = temp;
    }

    public void keyTyped (KeyEvent event) {}
}