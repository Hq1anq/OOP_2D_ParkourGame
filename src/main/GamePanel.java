package main;

import inputs.Keyboard;
import inputs.Mouse;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

@SuppressWarnings({"FieldMayBeFinal", "unused"})

public class GamePanel extends JPanel {
    private Mouse mouse;
    private Game game;
    public Keyboard keyboard;

    // testing
    public JLabel tempLabel;

    public GamePanel(Game game) {
        mouse = new Mouse(this);
        this.game = game;
        keyboard = new Keyboard(this);

        setLayout(null);

        setUpPanel();
        add(tempLabel);

        setPanelSize();
        addKeyListener(keyboard);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setFocusable(true);
        requestFocusInWindow();
    }

    // testing
    private void setUpPanel(){

        ImageIcon gif = new ImageIcon("res/control4.gif");
        int gifWidth = gif.getIconWidth();
        int gifHeight = gif.getIconHeight();

        // Calculate the top-left corner to center the label
        int x = (GAME_WIDTH - gifWidth) / 2;
        int y = (GAME_HEIGHT - gifHeight) / 2;
        tempLabel = new JLabel(gif);
        tempLabel.setLocation(x, y);
        tempLabel.setBounds(x, y, gifWidth, gifHeight);
        tempLabel.setVisible(false);
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
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
