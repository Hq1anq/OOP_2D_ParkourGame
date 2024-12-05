package main;

import inputs.Keyboard;
import inputs.Mouse;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

public class GamePanel extends JPanel {
    private Mouse mouse;
    private Game game;

    public GamePanel(Game game) {
        mouse = new Mouse(this);
        this.game = game;

        setPanelSize();
        addKeyListener(new Keyboard(this));
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setFocusable(true);
        requestFocusInWindow();
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println(GAME_WIDTH + ", " + GAME_HEIGHT);
    }

    public void updateGame() {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        game.render(g);
    }
    
    public Game getGame() {
        return game;
    }
}
