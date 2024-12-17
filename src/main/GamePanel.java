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
import static main.Game.TILE_SIZE;

@SuppressWarnings({"FieldMayBeFinal"})

public class GamePanel extends JPanel {
    private Mouse mouse;
    private Game game;
    public Keyboard keyboard;

    public JLabel Dash;
    public JLabel DoubleJump;
    public JLabel JumpCutting;
    public JLabel LaddleClimb;
    public JLabel LedgeClimb;
    public JLabel WallClimb;
    public JLabel WallKick;

    public GamePanel(Game game) {
        mouse = new Mouse(this);
        this.game = game;
        keyboard = new Keyboard(this);

        setLayout(null);

        setUpPanels();
        addAllGif();

        setPanelSize();
        addKeyListener(keyboard);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setFocusable(true);
        requestFocusInWindow();
    }

    private void setUpPanels(){
        ImageIcon DashGif = new ImageIcon("res/tutorial_gif/Dash.gif");
        ImageIcon DoubleJumpGif = new ImageIcon("res/tutorial_gif/DoubleJump.gif");
        ImageIcon JumpCuttingGif = new ImageIcon("res/tutorial_gif/JumpCutting.gif");
        ImageIcon LaddleClimbGif = new ImageIcon("res/tutorial_gif/LaddleClimb.gif");
        ImageIcon LedgeClimbGif = new ImageIcon("res/tutorial_gif/LedgeClimb.gif");
        ImageIcon WallClimbGif = new ImageIcon("res/tutorial_gif/WallClimb.gif");
        ImageIcon WallKickGif = new ImageIcon("res/tutorial_gif/WallKick.gif");

        int x = GAME_WIDTH / 2 - (int)(1.5 * TILE_SIZE);
        int y = GAME_HEIGHT / 2 - DashGif.getIconHeight() / 2 + TILE_SIZE / 2;

        Dash = new JLabel(DashGif);
        DoubleJump = new JLabel(DoubleJumpGif);
        JumpCutting = new JLabel(JumpCuttingGif);
        LaddleClimb = new JLabel(LaddleClimbGif);
        LedgeClimb = new JLabel(LedgeClimbGif);
        WallClimb = new JLabel(WallClimbGif);
        WallKick = new JLabel(WallKickGif);

        Dash.setBounds(x, y, 480, 390);
        DoubleJump.setBounds(x, y, 480, 390);
        JumpCutting.setBounds(x, y, 480, 390);
        LaddleClimb.setBounds(x, y, 480, 390);
        LedgeClimb.setBounds(x, y, 480, 390);
        WallClimb.setBounds(x, y, 480, 390);
        WallKick.setBounds(x, y, 480, 390);

        setAllGifUnvisible();
    }

    private void addAllGif(){
        add(Dash);
        add(DoubleJump);
        add(JumpCutting);
        add(LaddleClimb);
        add(LedgeClimb);
        add(WallClimb);
        add(WallKick);
    }

    public void setAllGifUnvisible(){
        Dash.setVisible(false);
        DoubleJump.setVisible(false);
        JumpCutting.setVisible(false);
        LaddleClimb.setVisible(false);
        LedgeClimb.setVisible(false);
        WallClimb.setVisible(false);
        WallKick.setVisible(false);
    }

    public void setGifVisible(int gifNumber){
        setAllGifUnvisible();
        switch (gifNumber) {
            case 0 -> Dash.setVisible(true);
            case 1 -> DoubleJump.setVisible(true);
            case 2 -> JumpCutting.setVisible(true);
            case 3 -> LaddleClimb.setVisible(true);
            case 4 -> LedgeClimb.setVisible(true);
            case 5 -> WallClimb.setVisible(true);
            case 6 -> WallKick.setVisible(true);
            default -> {
            }
        }
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
