package main;

import entities.Player;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import levels.LevelManager;
import static utilz.Constants.Environment.BG_COLOR;
import static utilz.Constants.Environment.ENV_HEIGHT_DEFAULT;
import static utilz.Constants.Environment.ENV_WIDTH_DEFAULT;
import utilz.LoadSave;

@SuppressWarnings({"FieldMayBeFinal", "unused"})

// ***IMPORTANT

public class Game implements Runnable {
    // SYSTEM
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 60;
    public final static int UPS_SET = 150;

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
    // Camera feature
    private int xLevelOffset;
    private int yLevelOffset;
    private int leftBorder = (int) (0.3 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.7 * Game.GAME_WIDTH);
    private int topBorder = (int) (0.3 * Game.GAME_HEIGHT);
    private int bottomBorder = (int) (0.7 * Game.GAME_HEIGHT);
    private int levelTileWide = LoadSave.getLevelData()[0].length;
    private int levelTileHeight = LoadSave.getLevelData().length;
    private int maxTileX = levelTileWide - Game.TILES_IN_WIDTH;
    private int maxTileY = levelTileHeight - Game.TILES_IN_HEIGHT;
    private int maxLevelOffsetX = maxTileX * Game.TILE_SIZE;
    private int maxLevelOffsetY = maxTileY * Game.TILE_SIZE;

    // GAME STATES
    public final int startingMenuState = -1;    // for easier code handling than to remember which state to which number
    public final int playingState = 0;
    public final int settingState = 1;
    public final int guidesState = 2;
    public final int exitState = 3;
    public int gameState = -1;      // current game state : default when start game : starting menu state
    public int selectedOptions = 0; // current selecting option : to choose option of the next game state
    public boolean paused = false;          // DO NOT TOUCH THIS
    public boolean changingButton = false;  // DO NOT TOUCH THIS EITHER

    // STATES DRAWER
    public Menu menu;

    // SETTING
    public boolean showFPS = false;
    public int volume = 1;

    private BufferedImage frontTree, behindTree;

    public Game() {
        // GENERATE GAME WINDOW AND PANEL AND MENU DRAWER

        initWindowAndPanel();
        initClasses();
        startGameLoop();
    }

    private void initClasses() {
        // INITIATE LEVEL AND PLAYER

        levelManager = new LevelManager(this);
        player = new Player(100, 350, (int) (32 * 2 * SCALE), (int) (32 * 2 * SCALE));
        player.loadLevelData(levelManager.getCurrentLevel().getLevelData());
        menu = new Menu(gamePanel);
        frontTree = LoadSave.GetSpriteAtlas(LoadSave.FRONT_TREE);
        behindTree = LoadSave.GetSpriteAtlas(LoadSave.BEHIND_TREE);
    }

    private void initWindowAndPanel() {
        // INITIATE GAME WINDOW AND PANEL

        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
    }

    private void startGameLoop() {
        // START GAME LOOP
        // LOOP INCLUDES UPDATE AND DRAW EVERY GAME FRAME

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        // UPDATE STATISTICS EVERY FRAME

        if(gameState == playingState){
            player.update();
            levelManager.update();
            checkCloseToBorder();
        }
    }

    private void checkCloseToBorder() {
        // CHECK WHETHER IF PLAYER IS CLOSE ENOUGH TO GAME WINDOW BORDERS

        int playerX = (int) player.getHitbox().x;
        int playerY = (int) player.getHitbox().y;
        int diffX = playerX - xLevelOffset;
        int diffY = playerY - yLevelOffset;

        if (diffX > rightBorder)
            xLevelOffset += diffX - rightBorder;
        else if (diffX < leftBorder)
            xLevelOffset += diffX - leftBorder;
        if (diffY > bottomBorder)
            yLevelOffset += diffY - bottomBorder;
        else if (diffY < topBorder)
            yLevelOffset += diffY - topBorder;

        if (xLevelOffset > maxLevelOffsetX)
            xLevelOffset = maxLevelOffsetX;
        else if (xLevelOffset < 0)
            xLevelOffset = 0;
        if (yLevelOffset > maxLevelOffsetY)
            yLevelOffset = maxLevelOffsetY;
        else if (yLevelOffset < 0)
            yLevelOffset = 0;
    }

    public void render(Graphics g) {
        // RE-DRAW GAME FRAME EVERY GAME FRAME
        // DRAWING METHODS

        // DRAW BACKGROUND
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        drawEnvironment(g);

        if(gameState == playingState){
            levelManager.draw(g, xLevelOffset, yLevelOffset);
            player.render(g, xLevelOffset, yLevelOffset);

            if(paused == true)  menu.drawPausePanel((Graphics2D)g);
        }
        else if (menu != null)
            menu.draw((Graphics2D)g);
    }

    private void drawEnvironment(Graphics g) {
        g.drawImage(behindTree, - (int) (xLevelOffset * 0.3), - (int) (yLevelOffset * 0.3), TILE_SIZE * levelTileHeight * ENV_WIDTH_DEFAULT / ENV_HEIGHT_DEFAULT,TILE_SIZE * levelTileHeight, null);
        g.drawImage(frontTree, - (int) (xLevelOffset * 0.7), - (int) (yLevelOffset * 0.7), TILE_SIZE * levelTileHeight * ENV_WIDTH_DEFAULT / ENV_HEIGHT_DEFAULT,TILE_SIZE * levelTileHeight, null);
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
