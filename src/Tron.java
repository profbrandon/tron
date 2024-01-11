import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Tron implements ActionListener {

    public static enum Status {
        GAME_OVER,
        IN_GAME,
        DRAW,
        PAUSED,
        UNINITIALIZED,
        NO_CYCLES
    }

    // Class Constants
    public static final int    DEFAULT_WIDTH  = 30;
    public static final int    DEFAULT_HEIGHT = 20;
    public static final double DEFAULT_SPEED  = 0.5;

    // Variables
    private int                    turn;
    private int                    width;
    private int                    height;
    private double                 speed;

    private boolean[][]            board;
    private int                    cycleCount;
    private List<Cycle>            cycles;
    private List<Cycle>            deadCycles;
    private Map<Cycle,Point>       positions;
    private Map<Cycle,List<Point>> trails;
    private List<Point>            walls;
    private Map<Cycle,Color>       colorMap;
    private Map<Cycle,String>      names;

    private List<TronListener>     listeners;
    private Timer                  timer;
    private Status                 status;

    // Flags
    private boolean ready = false;

    // Constructors

    public Tron () {
        this (Tron.DEFAULT_WIDTH, Tron.DEFAULT_HEIGHT, Tron.DEFAULT_SPEED);
    }

    public Tron (double speed) {
        this (Tron.DEFAULT_WIDTH, Tron.DEFAULT_HEIGHT, speed);
    }

    public Tron (int width, int height) {
        this (width, height, Tron.DEFAULT_SPEED);
    }

    public Tron (int width, int height, double speed) {
        if (width < 10 || height < 10 || speed < 0.01 || speed > 2.0) {
            this.width  = Tron.DEFAULT_WIDTH;
            this.height = Tron.DEFAULT_HEIGHT;
            this.speed  = Tron.DEFAULT_SPEED;
        }
        else {
            this.width  = width;
            this.height = height;
            this.speed  = speed;
        }

        this.status = Status.UNINITIALIZED;

        init ();
    }


    // Member Functions

    private void init ()
    {
        //if (status != Status.UNINITIALIZED) return;

        this.turn       = 0;
        this.cycleCount = 0;
        this.board      = new boolean[this.width][this.height];
        this.cycles     = new ArrayList<> ();
        this.deadCycles = new ArrayList<> ();
        this.trails     = new HashMap<> ();
        this.positions  = new HashMap<> ();
        this.colorMap   = new HashMap<> ();
        this.names      = new HashMap<> ();
        this.walls      = new ArrayList<> ();
        this.listeners  = new ArrayList<> ();
        this.timer      = new Timer ((int) (1000 * this.speed), this);

        // Init Board
        for (int i = 0; i < this.width; ++i)
            for (int j = 0; j < this.height; ++j)
                this.board[i][j] = false;

        // Init boarder walls
        for (int i = 0; i < this.width; ++i) {
            walls.add (new Point (i, 0));
            walls.add (new Point (i, this.height - 1));

            this.board[i][0] = true;
            this.board[i][this.height - 1] = true;
        }

        for (int j = 1; j < this.height - 1; ++j) {
            walls.add (new Point (0, j));
            walls.add (new Point (this.width - 1, j));

            this.board[0][j] = true;
            this.board[this.width - 1][j] = true;
        }

        // Update status
        this.status = Status.NO_CYCLES;
    }

    public void addTronListener (TronListener r) {
        this.listeners.add (r);
    }

    public void loadCycles (List<Cycle> cycles) {
        if (this.status != Status.NO_CYCLES) return;

        for (int i = 0; i < cycles.size (); ++i) {
            Cycle c = cycles.get (i);

            this.cycles.add (c);

            // Initialize Position Information
            Point pos = new Point (this.width / 2, (this.height - 1) / 2);
            
            if (cycles.size () == 1) {
                colorMap.put (c, new Color (255, 255, 255));
            }
            else if (cycles.size () == 2) {
                switch (i) {
                    case 0:
                        pos = new Point (this.width / 2, 4);
                        colorMap.put (c, new Color (255, 0, 0));
                        break;
                    case 1:
                        pos = new Point (this.width / 2, this.height - 5);
                        colorMap.put (c, new Color (0, 255, 255));
                        break;
                }
            }
            else if (cycles.size () == 3) {
                switch (i) {
                    case 0:
                        pos = new Point (this.width / 2, 4);
                        colorMap.put (c, new Color (255, 0, 0));
                        break;
                    case 1:
                        pos = new Point (4, 3 * (this.height - 1) / 4);
                        colorMap.put (c, new Color (0, 255, 255));
                        break;
                    case 2:
                        pos = new Point (this.width - 5, 3 * (this.height - 1) / 4);
                        colorMap.put (c, new Color (255, 255, 0));
                        break;
                }
            }

            c.setPos(pos);
            this.positions.put (c, pos);
            this.trails.put (c, new LinkedList<>());

            this.board[pos.x][pos.y] = true;

            c.init (this.width, this.height, this.board);
        }

        this.cycleCount = this.cycles.size ();
        this.status = Status.PAUSED;

        initListeners ();

        callListeners ();
    }

    private void initListeners () {

        List<Color> colors = new ArrayList<> ();

        for (Cycle c : cycles)
            colors.add (this.colorMap.get (c));

        for (TronListener listener : this.listeners)
            listener.init (new TronData (this.turn, this.status, colors, null, null, null, null, null));
    }

    private void callListeners () {
        // Render with current information
        List<Point>       cs  = new ArrayList<> ();
        List<List<Point>> ts  = new ArrayList<> ();
        List<Point>       dcs = new ArrayList<> ();
        List<List<Point>> dts = new ArrayList<> ();
        List<Color>       colors = new ArrayList<> ();

        for (Cycle c : cycles) {
            cs.add (this.positions.get (c));
            ts.add (this.trails.get (c));
            colors.add (this.colorMap.get (c));
        }

        TronData data = new TronData (this.turn, this.status, colors, cs, ts, this.walls, dcs, dts);

        for (TronListener listener : listeners)
            listener.tick (data);
    }

    private void update () {
        List<Cycle>      toDestroy = new ArrayList<> ();
        Map<Cycle,Point> moves     = new HashMap<> ();

        // Collect Requests
        for (Cycle c : cycles) {
            Point request = c.move (board);

            if (!valid (c, request)) toDestroy.add (c);
            
            moves.put (c, request);
        }

        // Find Request Conflicts
        for (Cycle c1 : cycles) {
            for (Cycle c2 : cycles) {
                if (c1 != c2 && moves.get (c1).equals (moves.get (c2))) {
                    if (!toDestroy.contains (c1)) toDestroy.add (c1);
                    if (!toDestroy.contains (c2)) toDestroy.add (c2);
                }
            }
        }
        
        // Destroy invalid cycles
        for (Cycle c : toDestroy) destroy (c);

        // Move Cycles
        for (Cycle c : cycles) {
            if (!toDestroy.contains (c)) {
                trails.get (c).add (positions.get (c));

                Point request = moves.get (c);

                c.setPos (request);
                positions.replace (c, request);
                board[request.x][request.y] = true;
            }
        }

        if (this.cycleCount != 1) {
            if (this.cycles.size () == 1) 
                this.status = Status.GAME_OVER;
            else if (this.cycles.size () == 0)
                this.status = Status.DRAW;
            else
                ++this.turn;
        }
        else {
            if (this.cycles.size () == 0)
                this.status = Status.GAME_OVER;
            else
                ++this.turn;
        }

        callListeners ();
    }

    private void destroy (Cycle c) {
        for (Point p : trails.get (c))
            board[p.x][p.y] = false;

        Point head = positions.get (c);
        board[head.x][head.y] = false;

        // Update Maps
        trails.remove (c);
        positions.remove (c);
        deadCycles.add (c);
        cycles.remove (c);
    }

    private boolean valid (Cycle c, Point request) {
        for (Point w : this.walls)
            if (w.equals (request)) return false;

        for (Cycle other : this.cycles) {
            if (positions.get (other).equals (request)) return false;

            for (Point p : trails.get (other))
                if (p.equals (request)) return false;
        }
        
        return true;
    }

    public void play () {
        if (this.status != Status.PAUSED) return;

        this.status = Status.IN_GAME;
        timer.start ();
    }

    public void pause () {
        if (this.status != Status.IN_GAME) return;

        this.status = Status.PAUSED;
        this.timer.stop ();
    }

    public void restart () {

        List<Cycle> temp = new ArrayList<> ();

        temp.addAll (cycles);
        temp.addAll (deadCycles);

        this.init ();
        this.loadCycles (temp);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (this.status != Status.IN_GAME) {
            this.timer.stop ();

            switch (this.status) {
                case GAME_OVER:
                    System.out.println ("Turn " + turn + ": GAME OVER " + cycles.get (0).getName ());
                    break;
                case DRAW:
                    System.out.println ("Turns " + turn + ": DRAW");
                    break;
            }

            return;
        }

        update ();
    }

    public int getWidth () { return this.width; }

    public int getHeight () { return this.height; }

    public Status getStatus () { return this.status; }
}