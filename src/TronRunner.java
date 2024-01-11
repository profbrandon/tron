import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class TronRunner extends JFrame {

    private TronPanel panel;
    private Tron      game;

    public TronRunner () {
        super ("TRON");

        this.game  = new Tron (41, 40, 0.1);
        this.panel = new TronPanel (this.game.getWidth (), this.game.getHeight ());

        // Setup the game
        List<Cycle> cycles = new ArrayList<> ();
        Player player = new Player ();

        cycles.add (new LineBot2());
        cycles.add (new FillBot());
        cycles.add (new TreeHuggerBot ());

        this.game.addTronListener (this.panel);
        this.game.loadCycles (cycles);

        addKeyListener (player);
        addKeyListener (new KeyListener () {
            public void keyTyped (KeyEvent e) {}
            public void keyReleased (KeyEvent e) {}
            public void keyPressed (KeyEvent e) {
                if (e.getKeyCode () == KeyEvent.VK_SPACE) {
                    if (game.getStatus () == Tron.Status.PAUSED) game.play ();
                    else if (game.getStatus () == Tron.Status.IN_GAME) game.pause ();
                }
            }
        });

        // Setup the window
        setResizable (false);
        setVisible (true);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        add (panel);
        pack ();
        repaint ();

        // Play
        this.game.play ();
    }

    public static void main (String args[]) {
        SwingUtilities.invokeLater (() -> {
            new TronRunner ();
        });
    }
}