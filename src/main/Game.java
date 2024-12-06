package main;

import entities.Player;
import java.awt.Graphics;
import levels.LevelManager;
import utilz.LoadSave;

@SuppressWarnings("unused")

// ***IMPORTANT

public class Game implements Runnable {
    // SYSTEM
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 150;

    private Player player;
    private LevelManager levelManager;

    // STATISTICS
    // CONST
    public final static int TILE_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILE_SIZE = (int) (TILE_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILE_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILE_SIZE * TILES_IN_HEIGHT;

    // STATISTICS
    // VARIABLE
    private int xLevelOffset;
    private int leftBorder = (int) (0.4 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.6 * Game.GAME_WIDTH);
    private int levelTileWide = LoadSave.getLevelData()[0].length;
    private int maxTileOffset = levelTileWide - Game.TILES_IN_WIDTH;
    private int maxLevelOffsetX = maxTileOffset * Game.TILE_SIZE;

    public Game() {
        // GENERATE GAME WINDOW AND PANEL

        initClasses();

        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        
        startGameLoop();
    }

    private void initClasses() {
        //  INITIATE LEVEL AND PLAYER

        levelManager = new LevelManager(this);
        player = new Player(100, 350, (int) (32 * 2 * SCALE), (int) (32 * 2 * SCALE));
        player.loadLevelData(levelManager.getCurrentLevel().getLevelData());
    }

    private void startGameLoop() {
        // START GAME LOOP
        // LOOP INCLUDES UPDATE AND DRAW EVERY GAME FRAME

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        // UPDATE STATISTICS EVERY FRAME

        player.update();
        levelManager.update();
        checkCloseToBorder();
    }

    private void checkCloseToBorder() {
        // CHECK WHETHER IF PLAYER IS CLOSE ENOUGH TO GAME WINDOW BORDERS

        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLevelOffset;

        if (diff > rightBorder)
            xLevelOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLevelOffset += diff - leftBorder;

        if (xLevelOffset > maxLevelOffsetX)
            xLevelOffset = maxLevelOffsetX;
        else if (xLevelOffset < 0)
            xLevelOffset = 0;
    }

    public void render(Graphics g) {
        // RE-DRAW GAME FRAME EVERY GAME FRAME
        // DRAWING METHODS
        
        levelManager.draw(g, xLevelOffset);
        player.render(g, xLevelOffset);
    }

    @Override
    public void run() {
        // RUN WHEN START THREAD
        // RE-UPDATE AND RE-RENDER EVERY FRAME TIME: 1s / FPS

        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();
        double deltaU = 0;
        double deltaF = 0;

        while (true) { 
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                // System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    public void windowFocusLost() {
        // RESET STATISTICS WHEN GAME IS NOT FOCUSED ANYMORE

        player.resetDirectionBooleans();
    }

    public Player getPlayer() {
        // GETTER METHOD

        return player;
    }
}
