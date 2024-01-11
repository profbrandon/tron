import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

class TronPanel extends JPanel implements TronListener {

    // Constants
    public static final int SQUARE_WIDTH = 16;

    // Game Variables
    private int               width;
    private int               height;
    private List<Point>       cycles;
    private List<List<Point>> trails;
    private List<Point>       walls;
    private List<Color>       colors;

    // Rendering Information
    private int                      pixelWidth;
    private int                      pixelHeight;
    private BufferedImage            cycleConstant;
    private BufferedImage            cycleShade;
    private BufferedImage            cycleShape;
    private Map<Color,BufferedImage> textureMap;

    public TronPanel (int width, int height) {

        this.width  = width;
        this.height = height;

        this.pixelWidth  = width * TronPanel.SQUARE_WIDTH;
        this.pixelHeight = height * TronPanel.SQUARE_WIDTH;

        this.cycles     = new ArrayList<> ();
        this.trails     = new ArrayList<> ();
        this.walls      = new ArrayList<> ();
        this.colors     = new ArrayList<> ();
        this.textureMap = new HashMap<> ();

        setVisible (true);
        setPreferredSize (new Dimension (TronPanel.SQUARE_WIDTH * width, TronPanel.SQUARE_WIDTH * height));
        setBackground (Color.BLACK);

        cycleConstant = ImageHandler.getImage ("../data/cycle-constant.png");
        cycleShade    = ImageHandler.getImage ("../data/cycle-shade.png");
        cycleShape    = ImageHandler.getImage ("../data/cycle-shape.png");
    }

    @Override
    public void paint (Graphics g) {
        super.paint (g);

        g.setColor (Color.BLACK);
        g.fillRect (0, 0, pixelWidth, pixelHeight);

        this.drawGrid (g);

        // Draw the walls
        g.setColor (new Color (40, 40, 40));

        for (Point wall : walls)
            g.fillRect (SQUARE_WIDTH * wall.x, SQUARE_WIDTH * wall.y, SQUARE_WIDTH, SQUARE_WIDTH);

        // Draw trails amd cycles
        for (int i = 0; i < trails.size (); ++i) {
            List<Point> trail = trails.get (i);

            for (int j = 0; j < trail.size () - 1; ++j)
                drawSegment (trail, j, colors.get (i), g);

            Point cycle = cycles.get (i);

            if (cycleConstant == null || cycleShade == null) {
                g.setColor (colors.get (i));
                g.fillRect (SQUARE_WIDTH * cycle.x, SQUARE_WIDTH * cycle.y, SQUARE_WIDTH, SQUARE_WIDTH);
            }
            else {
                int rotation = 0;

                if (trail.size () > 1) {
                    rotation = Point.getRotations ( Point.WEST, 
                                                    Point.sub (trail.get (trail.size () - 1), trail.get (trail.size () - 2)));
                }

                drawImage (g, SQUARE_WIDTH * cycle.x, SQUARE_WIDTH * cycle.y, cycleConstant, rotation);
                drawImage (g, SQUARE_WIDTH * cycle.x, SQUARE_WIDTH * cycle.y, this.textureMap.get (this.colors.get (i)), rotation);
            }
        }
    }

    private void drawGrid (Graphics g) {
        // Draw the grid
        g.setColor (new Color (20, 20, 20));

        for (int i = 1; i < this.width; ++i) {
            g.drawLine (TronPanel.SQUARE_WIDTH * i, 0, TronPanel.SQUARE_WIDTH * i, pixelHeight);
        }

        for (int j = 1; j < this.height; ++j) {
            g.drawLine (0, TronPanel.SQUARE_WIDTH * j, pixelWidth, TronPanel.SQUARE_WIDTH * j);
        }
    }

    private void drawSegment (List<Point> trail, int index, Color color, Graphics g) {
        Point pos = trail.get (index);

        g.setColor (color);

        if (index != 0) {
            Point behind = Point.normalize (Point.sub (pos, trail.get (index - 1)));

            if (!behind.equals (Point.ZERO)) {

                int width  = (behind.x == 0) ? 5 : 11;
                int height = (behind.y == 0) ? 5 : 11;

                int dx = (behind.x + behind.y < 0) ? 6 : (behind.x > behind.y) ? 0 : 6;
                int dy = (behind.x + behind.y < 0) ? 6 : (behind.x > behind.y) ? 6 : 0;

                g.fillRect (SQUARE_WIDTH * pos.x + dx, SQUARE_WIDTH * pos.y + dy, width, height);
            }
        }

        if (index != trail.size () - 1) {
            Point forward = Point.normalize (Point.sub (pos, trail.get (index + 1)));

            if (!forward.equals (Point.ZERO)) {

                int width  = (forward.x == 0) ? 5 : 11;
                int height = (forward.y == 0) ? 5 : 11;

                int dx = (forward.x + forward.y < 0) ? 6 : (forward.x > forward.y) ? 0 : 6;
                int dy = (forward.x + forward.y < 0) ? 6 : (forward.x > forward.y) ? 6 : 0;

                g.fillRect (SQUARE_WIDTH * pos.x + dx, SQUARE_WIDTH * pos.y + dy, width, height);
            }
        }
    }

    private void drawImage (Graphics g, int x, int y, BufferedImage img, int rotation) {
        
        rotation = rotation % 4;

        double centerX = img.getWidth () / 2;
        double centerY = img.getHeight () / 2;

        AffineTransform   transform = AffineTransform.getQuadrantRotateInstance (rotation, centerX, centerY);
        AffineTransformOp op        = new AffineTransformOp (transform, AffineTransformOp.TYPE_BICUBIC);

        BufferedImage temp = op.filter (img, null);

        int diameter = Math.max (img.getWidth (), img.getHeight ());

        int offsetX = (diameter - temp.getWidth ()) / 2;
        int offsetY = (diameter - temp.getHeight ()) / 2;

        if (rotation == 1 || rotation == 2) ++offsetX;
        if (rotation == 2 || rotation == 3) ++offsetY;

        g.drawImage (temp, x + offsetX, y + offsetY, temp.getWidth (), temp.getHeight (), null);
    }

    private Color scaleColor (Color init, double scalar) {
        if (scalar < 0 || scalar > 1) return null;

        double s = (scalar < 0.5) ? 2 * scalar : 2 * scalar - 1;

        if (scalar < 0.5)
            return new Color  ((int) (init.getRed () * s)
                             , (int) (init.getGreen () * s)
                             , (int) (init.getBlue () * s)
                             , init.getAlpha ());
        else {
            return new Color ( (int) (init.getRed () * (1 - s) + s * 255)
                             , (int) (init.getGreen () * (1 - s) + s * 255)
                             , (int) (init.getBlue () * (1 - s) + s * 255)
                             , init.getAlpha ());
        }
    }

    @Override
    public void tick (TronData data) {
        this.cycles = data.cycles;
        this.trails = data.trails;
        this.walls  = data.walls;
        this.colors = data.colors;

        for (int i = 0; i < cycles.size (); ++i)
            this.trails.get (i).add (cycles.get (i));
        repaint ();
    }

    public void init (TronData data) {
        this.colors = data.colors;

        for (Color c : this.colors) {

            BufferedImage copy = new BufferedImage (
                  cycleShade.getColorModel ()
                , cycleShade.copyData (null)
                , cycleShade.getColorModel ().isAlphaPremultiplied ()
                , null
                );

            for (int i = 0; i < copy.getWidth (); ++i) {
                for (int j = 0; j < copy.getHeight (); ++j) {

                    if (new Color (cycleShape.getRGB (i, j)).getRed () == 0) {
                        copy.setRGB (i, j, scaleColor (c, (double) (new Color (cycleShade.getRGB (i, j)).getRed ()) / 255.0).getRGB ());
                    }
                }
            }

            this.textureMap.put (c, copy);
        }
    }
}