package main;

import entities.Player;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import levels.LevelManager;
import objects.ObjectManager;
import sound.Sound;
import utilz.Constants.GameState;
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
    public Sound sound;
    private final int FPS_SET = 60;
    public final static int UPS_SET = 150;

    private Player player;
    private LevelManager levelManager;
    private ObjectManager objectManager;

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
    // Main game states
    public int gameState = GameState.START_MENU;      // current game state : default when start game : starting menu state
    public int selectedOptions = 0; // current selecting option : to choose option of the next game state
    // Sub game state
    public boolean paused = false;              // DO NOT TOUCH THIS
    public boolean changingButton = false;      // DO NOT TOUCH THIS EITHER
    public boolean adjustingKeyInGame = false;  // DO NOT TOUCH THIS EITHER
    public boolean gameOver = false;            // AND ALSO THIS
    public boolean winning = false;
    public boolean playingLevel1 = false;
    public boolean playingLevel2 = false;
    public boolean finishedLevel1 = false;
    public boolean finishedLevel2 = false;
    public boolean warning = false;

    // STATES DRAWER
    public Menu menu;
    public int currentFPS = 120;

    // SETTING
    public boolean showFPS = false;
    public int musicVolume = 1;
    public int soundEffectVolume = 1;
    public int currentLevel = 1;

    private BufferedImage frontTree, behindTree, frontRock, behindRock;

    // Camera shake variables
    private boolean shaking = false;
    public long shakeStartTime;
    private long shakeDuration = 250; // Shake duration in milliseconds
    private int shakeIntensity = 5; // Shake intensity in pixels

    public Game() {
        // GENERATE GAME WINDOW AND PANEL AND MENU DRAWER
        initWindowAndPanel();
        initClasses();

        calcOffset();
        
        startGameLoop();

        initializeMusic();

        objectManager.loadObjects(levelManager.getCurrentLevel());

        gameWindow.activateVisible();
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
        objectManager = new ObjectManager(this);
        player = new Player(4800, 100, (int) (32 * 2 * SCALE), (int) (32 * 2 * SCALE), this);
        camera = new Point(4800, 100);
        player.loadLevelData(levelManager.getCurrentLevel().getLevelData());
        menu = new Menu(gamePanel);
        sound = new Sound(this);
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

    private void initializeMusic() {
        playMusic(0);
    }

    public void loadLevel() {
        levelManager.loadLevel(currentLevel);
        player.loadLevelData(levelManager.getCurrentLevel().getLevelData());
    }

    public void update() {
        // UPDATE STATISTICS EVERY FRAME

        if(gameState == GameState.PLAYING){
            if(paused == false && gameOver == false) {
                player.update();
                levelManager.update();
                objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
                checkCloseToBorder();
                updateCameraShake();

                if (player.getHealth() <= 0){
                    if(gameOver == false){
                        stopMusic();
                        playSoundEffect(5);
                        playMusic(3);
                    }
                    gameOver = true;
                    return;
                }
                
                if (player.getHitbox().x > levelManager.getCurrentLevel().getWinPos().x - 10 &&
                    player.getHitbox().x < levelManager.getCurrentLevel().getWinPos().x + 10 &&
                    player.getHitbox().y > levelManager.getCurrentLevel().getWinPos().y - 10 &&
                    player.getHitbox().y < levelManager.getCurrentLevel().getWinPos().y + 10) {
                        if(winning == false){
                            gamePanel.getGame().stopMusic();
                            gamePanel.getGame().playMusic(4);
                        }
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


        drawEnvironment(g);

        if(gameState == GameState.PLAYING){
            levelManager.draw(g, xLevelOffset, yLevelOffset);
            player.drawHealth((Graphics2D)g);
            player.render((Graphics2D)g, xLevelOffset, yLevelOffset);

            objectManager.draw(g, xLevelOffset, yLevelOffset);

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
        else // if (menu != null)
            menu.draw((Graphics2D)g);
    }

    private void drawEnvironment(Graphics g) {
        int scaleEnv1 = 3;
        int scaleEnv2 = 3;
        if (levelManager != null) {
            if (levelManager.getCurrentLevel().getId() == 1) {
                g.setColor(Level1.BG_COLOR);
                g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
                g.drawImage(behindTree, - (int) (xLevelOffset * 0.3), - (int) (yLevelOffset * 0.3), scaleEnv1 * Level1.ENV_WIDTH_DEFAULT, scaleEnv1 * Level1.ENV_HEIGHT_DEFAULT, null);
                g.drawImage(frontTree, - (int) (xLevelOffset * 0.7), - (int) (yLevelOffset * 0.7), scaleEnv1 * Level1.ENV_WIDTH_DEFAULT, scaleEnv1 * Level1.ENV_HEIGHT_DEFAULT, null);
            }
            else if (levelManager.getCurrentLevel().getId() == 2) {
                g.setColor(Level2.BG_COLOR);
                g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
                g.drawImage(behindRock, - (int) (xLevelOffset * 0.3), - (int) (yLevelOffset * 0.7), scaleEnv2 * Level2.ENV_WIDTH_DEFAULT, scaleEnv2 * Level2.ENV_HEIGHT_DEFAULT, null);
                g.drawImage(frontRock, - (int) (xLevelOffset * 0.7), - (int) (yLevelOffset * 0.7), scaleEnv2 * Level2.ENV_WIDTH_DEFAULT, scaleEnv2 * Level2.ENV_HEIGHT_DEFAULT, null);
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

    public Point checkBreakablePlatformStepped(Rectangle2D.Float playerHitbox) {
        return objectManager.checkObjectStepped(playerHitbox);
    }

    @Override
    public void run() {
        // RUN WHEN START THREAD
        // RE-UPDATE AND RE-RENDER EVERY FRAME TIME: 1s / FPS

        double timePerFrame = 1000000000.0 / FPS_SET; // 10^9 nano seconds = 1 second
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

    public void playMusic(int soundNumber){
        sound.setMusic(soundNumber);
        sound.playMusic();
        sound.loop();
    }

    public void playSoundEffect(int soundNumber){
        sound.setSoundEffect(soundNumber);
        sound.playSoundEffect();
    }

    public void stopMusic(){
        sound.stopMusic();
    }

    public void stopSoundEffect(){
        sound.stopSoundEffect();
    }

    public void windowFocusLost() {
        // RESET STATISTICS WHEN GAME IS NOT FOCUSED ANYMORE

        player.resetDirectionBooleans();
    }

    public Player getPlayer() {
        // GETTER METHOD

        return player;
    }

    public ObjectManager getObjectManager() {
        // GETTER METHOD

        return objectManager;
    }
}
