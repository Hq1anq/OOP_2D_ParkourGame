package main;

import entities.Player;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import levels.LevelManager;
import utilz.Constants.Level1;
import utilz.Constants.Level2;
import utilz.LoadSave;
import utilz.Point;

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
    public static Point camera;
    private float cameraSpeed = 4f;
    private int xLevelOffset;
    private int yLevelOffset;
    private int leftBorder = (int) (0.45 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.55 * Game.GAME_WIDTH);
    private int topBorder = (int) (0.45 * Game.GAME_HEIGHT);
    private int bottomBorder = (int) (0.55 * Game.GAME_HEIGHT);
    private int maxLevelOffsetX, maxLevelOffsetY;

    // GAME STATES
    public final int startingMenuState = -1;    // for easier code handling than to remember which state to which number
    public final int playingState = 0;
    public final int settingState = 1;
    public final int guidesState = 2;
    public final int exitState = 3;
    public final int choosingLevelState = 4;
    public int gameState = -1;      // current game state : default when start game : starting menu state
    public int selectedOptions = 0; // current selecting option : to choose option of the next game state
    public boolean paused = false;              // DO NOT TOUCH THIS
    public boolean changingButton = false;      // DO NOT TOUCH THIS EITHER
    public boolean adjustingKeyInGame = false;  // DO NOT TOUCH THIS EITHER
    public boolean gameOver = false;            // AND ALSO THIS
    public boolean winning = false;
    public boolean playingLevel1 = false;
    public boolean playingLevel2 = false;
    public boolean finishedLevel1 = false;
    public boolean finishedLevel2 = true;
    public boolean warning = false;

    // STATES DRAWER
    public Menu menu;
    public int currentFPS = 120;

    // SETTING
    public boolean showFPS = false;
    public int volume = 1;
    public int currentLevel = 1;

    private BufferedImage frontTree, behindTree, frontRock, behindRock;

    // Camera shake variables
    private boolean shaking = false;
    private long shakeStartTime;
    private long shakeDuration = 500; // Shake duration in milliseconds
    private int shakeIntensity = 10; // Shake intensity in pixels

    public Game() {
        // GENERATE GAME WINDOW AND PANEL AND MENU DRAWER
        initWindowAndPanel();
        initClasses();

        calcOffset();
        
        startGameLoop();
    }

    private void calcOffset() {
        int maxTileX = levelManager.getCurrentLevel().getLevelTileWide() - Game.TILES_IN_WIDTH;
        int maxTileY = levelManager.getCurrentLevel().getLevelTileHeight() - Game.TILES_IN_HEIGHT;
        maxLevelOffsetX = maxTileX * Game.TILE_SIZE;
        maxLevelOffsetY = maxTileY * Game.TILE_SIZE;
    }

    private void initClasses() {
        // INITIATE LEVEL AND PLAYER

        levelManager = new LevelManager(this);
        player = new Player(4800, 100, (int) (32 * 2 * SCALE), (int) (32 * 2 * SCALE));
        camera = new Point(4800, 100);
        player.loadLevelData(levelManager.getCurrentLevel().getLevelData());
        menu = new Menu(gamePanel);
        frontTree = LoadSave.GetSpriteAtlas(LoadSave.FRONT_TREE);
        behindTree = LoadSave.GetSpriteAtlas(LoadSave.BEHIND_TREE);
        frontRock = LoadSave.GetSpriteAtlas(LoadSave.FRONT_ROCK);
        behindRock = LoadSave.GetSpriteAtlas(LoadSave.BEHIND_ROCK);
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

    public void loadLevel() {
        levelManager.loadLevel(currentLevel);
        player.loadLevelData(levelManager.getCurrentLevel().getLevelData());
    }

    public void update() {
        // UPDATE STATISTICS EVERY FRAME

        if(gameState == playingState){
            if(paused == false && gameOver == false) {
                player.update();
                levelManager.update();
                checkCloseToBorder();
                updateCameraShake();

                if (player.getHealth() <= 0){
                    gameOver = true;
                    return;
                }
                
                if (player.getHitbox().x > levelManager.getCurrentLevel().getWinPos().x - 10 &&
                    player.getHitbox().x < levelManager.getCurrentLevel().getWinPos().x + 10 &&
                    player.getHitbox().y > levelManager.getCurrentLevel().getWinPos().y - 10 &&
                    player.getHitbox().y < levelManager.getCurrentLevel().getWinPos().y + 10) {
                        winning = true;
                        player.activateWin();
                }
            }
        }
    }

    private void checkCloseToBorder() {
        // CHECK WHETHER IF PLAYER IS CLOSE ENOUGH TO GAME WINDOW BORDERS

        int targetXCamera = (int) (player.getHitbox().x);
        int targetYCamera = (int) (player.getHitbox().y);

        double angle = Math.atan2(targetYCamera - camera.y, targetXCamera - camera.x);
        int cameraXSpeed = (int) (Math.cos(angle) * cameraSpeed);
        int cameraYSpeed = (int) (Math.sin(angle) * cameraSpeed);

        camera.x += cameraXSpeed;
        camera.y += cameraYSpeed;

        int diffX = camera.x - xLevelOffset;
        int diffY = camera.y - yLevelOffset;

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
        g.setColor(Level1.BG_COLOR);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        drawEnvironment(g);

        if(gameState == playingState){
            levelManager.draw(g, xLevelOffset, yLevelOffset);
            player.drawHealth((Graphics2D)g);
            player.render((Graphics2D)g, xLevelOffset, yLevelOffset);

            if(winning){
                menu.drawCongratulationPanel((Graphics2D)g);
                return;
            }

            if(gameOver){
                menu.drawGameOverPanel((Graphics2D)g);
                return;
            }

            if(showFPS)
                menu.drawFPS((Graphics2D)g);

            if(adjustingKeyInGame == true)  menu.drawInGameKeyAdjustPanel((Graphics2D)g);
            else if(paused == true)  menu.drawPausePanel((Graphics2D)g);
        }
        else if (menu != null)
            menu.draw((Graphics2D)g);
    }

    private void drawEnvironment(Graphics g) {
        if (levelManager != null) {
            if (levelManager.getCurrentLevel().getId() == 1)
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++) {
                        g.drawImage(behindTree, - (int) (xLevelOffset * 0.3) + j * 3 * Level1.ENV_WIDTH_DEFAULT, - (int) (yLevelOffset * 0.3) + i * 3 * Level1.ENV_HEIGHT_DEFAULT, 3 * Level1.ENV_WIDTH_DEFAULT, 3 * Level1.ENV_HEIGHT_DEFAULT, null);
                        g.drawImage(frontTree, - (int) (xLevelOffset * 0.7) + j * 3 * Level1.ENV_WIDTH_DEFAULT, - (int) (yLevelOffset * 0.7) + i * 3 * Level1.ENV_HEIGHT_DEFAULT, 3 * Level1.ENV_WIDTH_DEFAULT, 3 * Level1.ENV_HEIGHT_DEFAULT, null);
                    }
            else if (levelManager.getCurrentLevel().getId() == 2)
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++) {
                        g.drawImage(behindRock, - (int) (xLevelOffset * 0.3) + j * 1 * Level2.ENV_WIDTH_DEFAULT - 5, - (int) (yLevelOffset * 0.7) + i * 2 * Level2.ENV_HEIGHT_DEFAULT - 5, 1 * Level2.ENV_WIDTH_DEFAULT, 2 * Level2.ENV_HEIGHT_DEFAULT, null);
                        g.drawImage(frontRock, - (int) (xLevelOffset * 0.7) + j * 1 * Level2.ENV_WIDTH_DEFAULT - 5, - (int) (yLevelOffset * 0.7) + i * 2 * Level2.ENV_HEIGHT_DEFAULT - 5, 1 * Level2.ENV_WIDTH_DEFAULT, 2 * Level2.ENV_HEIGHT_DEFAULT, null);
                    }
        }
    }

    public void startCameraShake() {
        shaking = true;
        shakeStartTime = System.currentTimeMillis();
    }

    private void updateCameraShake() {
        if (shaking) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - shakeStartTime < shakeDuration) {
                int shakeX = (int) (Math.random() * shakeIntensity * 2 - shakeIntensity);
                int shakeY = (int) (Math.random() * shakeIntensity * 2 - shakeIntensity);
                xLevelOffset += shakeX;
                yLevelOffset += shakeY;
            } else {
                shaking = false;
            }
        }
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
                currentFPS = frames;
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
