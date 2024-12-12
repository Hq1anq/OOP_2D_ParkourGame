package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static main.Game.TILE_SIZE;
import static utilz.Constants.Menu.DARKEN_BACKGROUND_COLOR;

// MAIN USE: DRAWING MENU STATES
public class Menu {
    private GamePanel gamePanel;
    private BufferedImage mainBackground;
    public String[] startingMenuTexts = {"New Game", "Setting", "Guides", "Exit"};
    private Font mainFont;
    private File file;
    private BasicStroke mainStroke;

    // COLOR
    private Color miniFrameBackgroundColor = Color.decode("#638A55");   // background of the mini window
    private Color miniFrameColor = Color.decode("#C48D60");             // outline of the mini window
    private Color normalTextColor = Color.decode("#CBE54E");            // normal text (not being selected)
    private Color selectedTextColor = Color.decode("#FFE3B3");          // selected text (but not of the Key Bind being changed by user)
    private Color changingTextColor = Color.decode("#E1B083");          // text of the Key Bind being changed (Choosing key of Key Adjust)
    
    public Menu(GamePanel gamePanel){
        this.gamePanel = gamePanel;
        loadFont();
        setStroke();
        loadImage();
    }

    private void loadFont(){
        try {
            file = new File("res/font/ThaleahFat.ttf");
            mainFont = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(48f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStroke(){
        mainStroke = new BasicStroke(3);
    }

    private void loadImage() {
        try{
            file = new File("res/starting_menu_background_main_project.png");
            mainBackground = ImageIO.read(file);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2){
        // MAIN DRAWING METHOD
        if(gamePanel.getGame().gameState == gamePanel.getGame().startingMenuState){
            drawStartingMenuScreen(g2);
        } 
        else if(gamePanel.getGame().gameState == gamePanel.getGame().settingState){
            drawSettingScreen(g2);
        } 
        else if(gamePanel.getGame().gameState == gamePanel.getGame().guidesState){
            drawGuidesScreen(g2);
        } 
        else if(gamePanel.getGame().gameState == gamePanel.getGame().exitState){
            drawExitScreen(g2);
        }
        else if(gamePanel.getGame().gameState == gamePanel.getGame().choosingLevelState){
            drawChoosingLevelScreen(g2);
        }
    }

    private void drawStartingMenuScreen(Graphics2D g2){
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);

        int x, y = 8 * TILE_SIZE;
        for(int i = 0 ; i < startingMenuTexts.length; i++){
            x = Game.GAME_WIDTH/2 - getTextLenght(g2, startingMenuTexts[i])/2;
            g2.setColor(Color.gray);
            g2.drawString(startingMenuTexts[i], x + 3, y + 3);
            if(i == gamePanel.getGame().selectedOptions)
                g2.setColor(selectedTextColor);
            else
                g2.setColor(normalTextColor);
            g2.drawString(startingMenuTexts[i], x, y);
            y += 55;
        }
    }

    private void drawSettingScreen(Graphics2D g2){
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);
        drawMiniWindow(g2, 8 * TILE_SIZE, 3 * TILE_SIZE, 10 * TILE_SIZE, 8 * TILE_SIZE);

        int x = GAME_WIDTH / 2 - getTextLenght(g2, "Settings") / 2;
        int y = TILE_SIZE * 4;
        g2.setColor(normalTextColor);
        g2.drawString("Settings", x, y);

        g2.setFont(g2.getFont().deriveFont(32f));
        g2.setStroke(mainStroke);
        x = 9 * TILE_SIZE;

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 0)   g2.setColor(selectedTextColor);
        else g2.setColor(normalTextColor);
        g2.drawString("Show FPS", x, y);
        // g2.setColor(normalTextColor);
        if(gamePanel.getGame().showFPS == true)
            g2.fillRect(GAME_WIDTH - 9 * TILE_SIZE - 32, y - 32, 32, 32);
        else
            g2.drawRect(GAME_WIDTH - 9 * TILE_SIZE - 32, y - 32, 32, 32);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 1)   g2.setColor(selectedTextColor);
        else g2.setColor(normalTextColor);
        g2.drawString("Volume", x, y);
        // g2.setColor(normalTextColor);
        g2.drawRect(GAME_WIDTH - (int)(9 * TILE_SIZE) - 32 * 5, y - 32, 32 * 5, 32);
        g2.fillRect(GAME_WIDTH - (int)(9 * TILE_SIZE) - 32 * 5, y - 32, gamePanel.getGame().volume * 32, 32);

        y += 15;
        g2.setColor(normalTextColor);
        g2.fillRect(9 * TILE_SIZE, y, 8 * TILE_SIZE, 3);

        y += TILE_SIZE + 5;
        g2.setColor(normalTextColor);
        g2.setFont(mainFont);
        g2.drawString("Key Adjust", GAME_WIDTH / 2 - getTextLenght(g2, "Key Adjust") / 2, y);
        
        g2.fillRect(GAME_WIDTH / 2 - 1, y + 24, 3, 3 * TILE_SIZE - 20);

        y += TILE_SIZE;
        g2.setFont(mainFont.deriveFont(32f));
        if (gamePanel.getGame().selectedOptions == 2){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Up", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.upButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 3){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Left", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.leftButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 4){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Dash", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.dashButton), x + 2 * TILE_SIZE, y);

        y -= 2 * TILE_SIZE;
        x += (int)(4.5 * TILE_SIZE);
        if (gamePanel.getGame().selectedOptions == 5){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Down", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.downButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 6){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Right", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.rightButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 7){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Climb", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.climbButton), x + 2 * TILE_SIZE, y);

    }

    private void drawGuidesScreen(Graphics2D g2){
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);
        drawMiniWindow(g2, 4 * TILE_SIZE, 2 * TILE_SIZE, 18 * TILE_SIZE, 10 * TILE_SIZE);

        int x = GAME_WIDTH / 2 - getTextLenght(g2, "Guides") / 2;
        int y = 150;
        g2.setColor(normalTextColor);
        g2.drawString("Guides", x, y);
        g2.setFont(g2.getFont().deriveFont(30f));
    }

    private void drawExitScreen(Graphics2D g2){
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);
        drawMiniWindow(g2, 5 * TILE_SIZE, 5 * TILE_SIZE, 16 * TILE_SIZE, 4 * TILE_SIZE);

        int x = GAME_WIDTH / 2 - getTextLenght(g2, "Are you sure you want to exit?") / 2;
        int y = (int)(6.2 * TILE_SIZE);
        g2.setColor(normalTextColor);
        g2.drawString("Are you sure you want to exit?", x, y);

        g2.setFont(g2.getFont().deriveFont(32f));
        x = GAME_WIDTH / 2 - getTextLenght(g2, "Your progress won't be saved if you exit") / 2;
        y += TILE_SIZE;
        g2.drawString("Your progress won't be saved if you exit", x, y);

        x = GAME_WIDTH / 2 - getTextLenght(g2, "Press enter again to exit") / 2;
        y += TILE_SIZE;
        g2.drawString("Press enter again to exit", x, y);
    }

    private void drawChoosingLevelScreen(Graphics2D g2){
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        drawMiniWindow(g2, 9 * TILE_SIZE, (int)(4.75 * TILE_SIZE), 8 * TILE_SIZE, 4 * TILE_SIZE);

        g2.setFont(mainFont);
        g2.setColor(normalTextColor);
        String text = "Choose Level";
        int x = GAME_WIDTH / 2 - getTextLenght(g2, text) / 2;
        int y = 6 * TILE_SIZE;
        g2.drawString(text, x, y);

        g2.setFont(mainFont.deriveFont(32f));

        y += TILE_SIZE;
        text = "Level 1";
        x = GAME_WIDTH / 2 - getTextLenght(g2, text) / 2;
        if(gamePanel.getGame().selectedOptions == 0)    g2.setColor(selectedTextColor);
        else                                            g2.setColor(normalTextColor);
        g2.drawString(text, x, y);

        y += TILE_SIZE;
        text = "Level 2";
        x = GAME_WIDTH / 2 - getTextLenght(g2, text) / 2;
        if(gamePanel.getGame().selectedOptions == 1)    g2.setColor(selectedTextColor);
        else                                            g2.setColor(normalTextColor);
        if(!gamePanel.getGame().finishedLevel1)         g2.setColor(Color.gray);
        g2.drawString(text, x, y);
    }

    public void drawPausePanel(Graphics2D g2){
        drawMiniWindow(g2, 8 * TILE_SIZE, (int)(3.5 * TILE_SIZE), 10 * TILE_SIZE, 7 * TILE_SIZE);
        g2.setFont(mainFont);
        g2.setStroke(mainStroke);

        int x = GAME_WIDTH / 2 - getTextLenght(g2, "Game Paused") / 2;
        int y = (int)(TILE_SIZE * 4.5) + 10;

        g2.setColor(normalTextColor);
        g2.drawString("Game Paused", x, y);

        g2.setFont(g2.getFont().deriveFont(32f));
        
        x = 9 * TILE_SIZE;
        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 0)   g2.setColor(selectedTextColor);
        else g2.setColor(normalTextColor);
        g2.drawString("Continue", x, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 1)   g2.setColor(selectedTextColor);
        else g2.setColor(normalTextColor);
        g2.drawString("Show FPS", x, y);
        // g2.setColor(normalTextColor);
        if(gamePanel.getGame().showFPS == true)
            g2.fillRect(GAME_WIDTH - 9 * TILE_SIZE - 32, y - 32, 32, 32);
        else
            g2.drawRect(GAME_WIDTH - 9 * TILE_SIZE - 32, y - 32, 32, 32);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 2)   g2.setColor(selectedTextColor);
        else g2.setColor(normalTextColor);
        g2.drawString("Volume", x, y);
        // g2.setColor(normalTextColor);
        g2.drawRect(GAME_WIDTH - 9 * TILE_SIZE - 32 * 5, y - 32, 32 * 5, 32);
        g2.fillRect(GAME_WIDTH - 9 * TILE_SIZE - 32 * 5, y - 32, gamePanel.getGame().volume * 32, 32);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 3)   g2.setColor(selectedTextColor);
        else g2.setColor(normalTextColor);
        g2.drawString("Key Adjust", x, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 4)   g2.setColor(selectedTextColor);
        else g2.setColor(normalTextColor);
        g2.drawString("Main Menu", x, y);
    }

    public void drawInGameKeyAdjustPanel(Graphics2D g2){
        drawMiniWindow(g2, 8 * TILE_SIZE, 5 * TILE_SIZE, 10 * TILE_SIZE, 5 * TILE_SIZE);
        g2.setColor(normalTextColor);
        g2.setFont(mainFont);

        String text = "Key Adjust";
        int x = GAME_WIDTH / 2 - getTextLenght(g2, text) / 2;
        int y = (int)(6.25 * TILE_SIZE);
        g2.drawString(text, x, y);

        // g2.fillRect(9 * TILE_SIZE, y + 10, 8 * TILE_SIZE, 3);

        g2.fillRect(GAME_WIDTH / 2 - 1, y + 30, 3, 3 * TILE_SIZE - 30);

        y += TILE_SIZE;
        x = 9 * TILE_SIZE;
        g2.setFont(mainFont.deriveFont(32f));
        if (gamePanel.getGame().selectedOptions == 0){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Up", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.upButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 1){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Left", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.leftButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 2){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Dash", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.dashButton), x + 2 * TILE_SIZE, y);

        y -= 2 * TILE_SIZE;
        x += (int)(4.5 * TILE_SIZE);
        if (gamePanel.getGame().selectedOptions == 3){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Down", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.downButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 4){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Right", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.rightButton), x + 2 * TILE_SIZE, y);

        y += TILE_SIZE;
        if (gamePanel.getGame().selectedOptions == 5){
            if(gamePanel.getGame().changingButton)
                g2.setColor(changingTextColor);
            else
                g2.setColor(selectedTextColor);
        }
        else g2.setColor(normalTextColor);
        g2.drawString("Climb", x, y);
        g2.drawString(KeyEvent.getKeyText(gamePanel.keyboard.climbButton), x + 2 * TILE_SIZE, y);
    }

    public void drawGameOverPanel(Graphics2D g2){
        g2.setColor(DARKEN_BACKGROUND_COLOR);
        g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        drawMiniWindow(g2, 6 * TILE_SIZE, 5 * TILE_SIZE, 14 * TILE_SIZE, 4 * TILE_SIZE);

        g2.setFont(mainFont);
        g2.setColor(normalTextColor);
        String text = "Game Over!";
        int x = GAME_WIDTH / 2 - getTextLenght(g2, text) / 2;
        int y = (int)(6.25 * TILE_SIZE);
        g2.drawString(text, x, y);

        text = "You cannot give up just yet!";
        x = GAME_WIDTH / 2 - getTextLenght(g2, text) / 2;
        y += TILE_SIZE;
        g2.drawString(text, x, y);

        text = "Press Enter to return";
        x = GAME_WIDTH / 2 - getTextLenght(g2, text) / 2;
        y += TILE_SIZE;
        g2.drawString(text, x, y);
    }

    private void drawMiniWindow(Graphics2D g2, int x, int y, int width, int height){
        g2.setColor(miniFrameColor);
        g2.fillRoundRect(x, y, width, height, 20, 20);
        g2.setColor(miniFrameBackgroundColor);
        g2.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 20, 20);
    }

    public void drawFPS(Graphics2D g2){
        String text = "FPS: " + gamePanel.getGame().currentFPS;
        g2.setFont(mainFont.deriveFont(20f));
        g2.setStroke(mainStroke);
        g2.setColor(miniFrameBackgroundColor);
        g2.fillRect(GAME_WIDTH - getTextLenght(g2, text) - 20, 0, getTextLenght(g2, text) + 20, 40);
        g2.setColor(selectedTextColor);
        g2.drawString(text, GAME_WIDTH - getTextLenght(g2, text) - 10, 30);
    }

    private int getTextLenght(Graphics2D g2, String text){
        return (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
    }
}
