import java.util.List;

import java.awt.Color;

class TronData {

    // Game State
    public final int         turn;
    public final Tron.Status status;

    // Rendering Information
    public final List<Color>       colors;
    public final List<Point>       walls;
    public final List<Point>       cycles;
    public final List<List<Point>> trails;

    public final List<Point>       deadCycles;
    public final List<List<Point>> deadTrails;

    
    public TronData (int turn, Tron.Status status, List<Color> colors,
                     List<Point> cycles, List<List<Point>> trails, List<Point> walls,
                     List<Point> deadCycles, List<List<Point>> deadTrails) {
        
        this.turn   = turn;
        this.status = status;
        this.colors = colors;
        this.cycles = cycles;
        this.trails = trails;
        this.walls  = walls;

        this.deadCycles = deadCycles;
        this.deadTrails = deadTrails;
    }
}