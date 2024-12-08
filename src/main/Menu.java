package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static main.Game.TILE_SIZE;

// MAIN USE: DRAWING MENU STATES
public class Menu {
    private GamePanel gamePanel;
    private BufferedImage mainBackground;
    public String[] startingMenuTexts = {"New Game", "Setting", "Guides", "Exit"};
    public String[] settingTexts = {""};
    private Font mainFont;
    private File file;
    
    public Menu(GamePanel gamePanel){
        this.gamePanel = gamePanel;
        loadFont();
        loadImage();

        // *****************************************READ THIS PLEASE*************************************************
        // no background image was uploaded when these code were written so don't load image yet utill we have one
        // also don't draw background image at below yet to avoid exceptions
        // the line of code that draw background image has been turned into comment
        // for temperaly draw black space instead (2 lines of code before the comment)
    }

    private void loadFont(){
        try {
            file = new File("res/font/PixelifySans-VariableFont_wght.ttf");
            mainFont = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(48f);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    }

    private void drawStartingMenuScreen(Graphics2D g2){
        // g2.setColor(Color.black);
        // g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);

        int x, y = 10 * TILE_SIZE;
        for(int i = 0 ; i < startingMenuTexts.length; i++){
            x = Game.GAME_WIDTH/2 - getTextLenght(g2, startingMenuTexts[i])/2;
            g2.setColor(Color.gray);
            g2.drawString(startingMenuTexts[i], x + 3, y + 3);
            if(i == gamePanel.getGame().selectedOptions)
                g2.setColor(Color.yellow);
            else
                g2.setColor(Color.white);
            g2.drawString(startingMenuTexts[i], x, y);
            y += 55;
        }
    }

    private void drawSettingScreen(Graphics2D g2){
        // g2.setColor(Color.black);
        // g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);
        drawMiniWindow(g2, 8 * TILE_SIZE, 2 * TILE_SIZE, 10 * TILE_SIZE, 10 * TILE_SIZE);

        int x = GAME_WIDTH / 2 - getTextLenght(g2, "Settings") / 2;
        int y = 150;
        g2.setColor(Color.white);
        g2.drawString("Settings", x, y);
        g2.setFont(g2.getFont().deriveFont(30f));
    }

    private void drawGuidesScreen(Graphics2D g2){
        // g2.setColor(Color.black);
        // g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);
        drawMiniWindow(g2, 8 * TILE_SIZE, 2 * TILE_SIZE, 10 * TILE_SIZE, 10 * TILE_SIZE);

        int x = GAME_WIDTH / 2 - getTextLenght(g2, "Guides") / 2;
        int y = 150;
        g2.setColor(Color.white);
        g2.drawString("Guides", x, y);
        g2.setFont(g2.getFont().deriveFont(30f));
    }

    private void drawExitScreen(Graphics2D g2){
        // g2.setColor(Color.black);
        // g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2.drawImage(mainBackground, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        g2.setFont(mainFont);
        drawMiniWindow(g2, 4 * TILE_SIZE, 4 * TILE_SIZE, 18 * TILE_SIZE, 6 * TILE_SIZE);

        int x = GAME_WIDTH / 2 - getTextLenght(g2, "Are you sure you want to exit?") / 2;
        int y = (int)(6.4 * TILE_SIZE);
        g2.setColor(Color.white);
        g2.drawString("Are you sure you want to exit?", x, y);

        g2.setFont(g2.getFont().deriveFont(32f));
        x = GAME_WIDTH / 2 - getTextLenght(g2, "Your progress won't be saved if you exit") / 2;
        y += TILE_SIZE;
        g2.drawString("Your progress won't be saved if you exit", x, y);

        x = GAME_WIDTH / 2 - getTextLenght(g2, "Press enter again to exit") / 2;
        y += TILE_SIZE;
        g2.drawString("Press enter again to exit", x, y);
    }

    private void drawMiniWindow(Graphics2D g2, int x, int y, int width, int height){
        g2.setColor(Color.white);
        g2.fillRoundRect(x, y, width, height, 20, 20);
        g2.setColor(Color.black);
        g2.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 20, 20);
    }

    private int getTextLenght(Graphics2D g2, String text){
        return (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
    }
}
