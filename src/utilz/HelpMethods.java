package utilz;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.Game;
import static main.Game.TILE_SIZE;
import objects.Arrow;
import objects.BreakablePlatform;
import objects.Shooter;
import static utilz.Constants.ObjectConstants.BREAKABLE_PLATFORM;
import static utilz.Constants.ObjectConstants.BROWNSAW;
import static utilz.Constants.ObjectConstants.CHAINSAW;
import static utilz.Constants.ObjectConstants.FIRE;
import static utilz.Constants.ObjectConstants.GetSpriteAmount;
import static utilz.Constants.ObjectConstants.SHOOTER;
import static utilz.Constants.ObjectConstants.SPIKE;
import static utilz.Constants.ObjectConstants.SWORDTRAP1;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] levelData) {
        return !IsSolid(x, y, levelData)  && !IsSolid(x + width, y + height, levelData) &&
            !IsSolid(x + width, y, levelData) && !IsSolid(x, y + height, levelData) &&
            !IsSolid(x, y + height/2, levelData) && !IsSolid(x + width, y + height/2, levelData) &&
            !IsSolid(x + width/2, y, levelData) && !IsSolid(x + width/2, y + height, levelData);
    }

    public static boolean IsSolid(float x, float y, int[][] levelData) {
        int maxWidth = levelData[0].length * Game.TILE_SIZE;
        int maxHeight = levelData.length * Game.TILE_SIZE;

        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight)
            return true;

        int xIndex = (int) (x / Game.TILE_SIZE);
        int yIndex = (int) (y / Game.TILE_SIZE);

        int value = levelData[yIndex][xIndex];
        return (value != -1 && (value >= 0 && value < 130));
    }

    public static boolean IsLadder(float x, float y, int[][] levelData) {
        int xIndex = (int) (x / Game.TILE_SIZE);
        int yIndex = (int) (y / Game.TILE_SIZE);
        return (levelData[yIndex][xIndex] == 209 || levelData[yIndex][xIndex] == 211 || levelData[yIndex][xIndex] == 213);
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / Game.TILE_SIZE);
        if (xSpeed > 0) { // Move to Right
            int tileXPos = currentTile * Game.TILE_SIZE;
            int xOffSet = (int) (2 * Game.TILE_SIZE - hitbox.width);
            return tileXPos + xOffSet - 1; // -1 For not overlapping
        } else { // Move to Left
            return currentTile * Game.TILE_SIZE + 1;
        }
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] levelData) {
        // Check pixels below the bottom-left and bottom-right corners of the hitbox
        return (IsSolid(hitbox.x, hitbox.y + hitbox.height + 4, levelData) ||
                IsSolid(hitbox.x + hitbox.width / 3, hitbox.y + hitbox.height + 4, levelData) ||
                IsSolid(hitbox.x + 2/3 * hitbox.width, hitbox.y + hitbox.height + 4, levelData) ||
                IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 4, levelData));
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        if (airSpeed > 0) { // Falling -> Touching Floor
            int floorTile = (int) ((hitbox.y + airSpeed) / Game.TILE_SIZE);
            return floorTile * Game.TILE_SIZE;
        } else { // Jumping -> Hit the roof
            return ((int) (hitbox.y / Game.TILE_SIZE)) * Game.TILE_SIZE;
        }
    }

    public static Point GetEntityWhenLedgeClimb(Rectangle2D.Float hitbox, int[][] levelData, boolean isFacingLeft, float ledgeClimbXOffset, float ledgeClimbYOffset) {
        if (isFacingLeft) {
            int ledgeX = (int) ((hitbox.x - ledgeClimbXOffset) / Game.TILE_SIZE);
            int ledgeY = (int) ((hitbox.y + ledgeClimbYOffset) / Game.TILE_SIZE);
            if (IsSolid(ledgeX * Game.TILE_SIZE, hitbox.y + Game.TILE_SIZE / 2, levelData) &&
                !IsSolid(ledgeX * Game.TILE_SIZE, ledgeY * Game.TILE_SIZE, levelData))
                return new Point(ledgeX * Game.TILE_SIZE + Game.TILE_SIZE - (int)hitbox.width,
                                ledgeY * Game.TILE_SIZE - Game.TILE_SIZE);
            else return null;
        } else {
            int ledgeX = (int) ((hitbox.x + hitbox.width + ledgeClimbXOffset) / Game.TILE_SIZE);
            int ledgeY = (int) ((hitbox.y + ledgeClimbYOffset) / Game.TILE_SIZE);
            if (IsSolid(ledgeX * Game.TILE_SIZE, hitbox.y + Game.TILE_SIZE / 2, levelData) &&
                !IsSolid(ledgeX * Game.TILE_SIZE, ledgeY * Game.TILE_SIZE, levelData))
                return new Point(ledgeX * Game.TILE_SIZE,
                                ledgeY * Game.TILE_SIZE - Game.TILE_SIZE);
            else return null;
        }
    }

    public static boolean IsOnIce(Rectangle2D.Float hitbox, int[][] levelData){
        float player_bot_left_corner_in_map_X = hitbox.x;
        float player_bot_right_corner_in_map_X = hitbox.x + hitbox.width;
        float player_bot_in_map_Y = hitbox.y + hitbox.height + 3;

        int xIndex1 = (int)(player_bot_left_corner_in_map_X / Game.TILE_SIZE);
        int xIndex2 = (int)(player_bot_right_corner_in_map_X / Game.TILE_SIZE);
        int yIndex = (int)(player_bot_in_map_Y / Game.TILE_SIZE);

        return levelData[yIndex][xIndex1] >= 114 && levelData[yIndex][xIndex1] <= 118 
            && levelData[yIndex][xIndex2] >= 114 && levelData[yIndex][xIndex2] <= 118;
    }

    public static boolean IsOnMud(Rectangle2D.Float hitbox, int[][] levelData){
        float player_bot_left_corner_in_map_X = hitbox.x;
        float player_bot_right_corner_in_map_X = hitbox.x + hitbox.width;
        float player_bot_in_map_Y = hitbox.y + hitbox.height + 3;

        int xIndex1 = (int)(player_bot_left_corner_in_map_X / Game.TILE_SIZE);
        int xIndex2 = (int)(player_bot_right_corner_in_map_X / Game.TILE_SIZE);
        int yIndex = (int)(player_bot_in_map_Y / Game.TILE_SIZE);

        return IsMud(xIndex1, yIndex, levelData) && IsMud(xIndex2, yIndex, levelData);
    }

    public static boolean IsInLadder(Rectangle2D.Float hitbox, int[][] levelData) {
        return IsLadder(hitbox.x, hitbox.y, levelData) && IsLadder(hitbox.x, hitbox.y + hitbox.height, levelData);
    }
    public static boolean HitTrap(Rectangle2D.Float hitbox, int[][] levelData){
        float top_Y = hitbox.y;
        float bottom_Y = hitbox.y + hitbox.height;
        float left_X = hitbox.x;
        float right_X = hitbox.x + hitbox.width;

        int map_left_X = (int)(left_X / TILE_SIZE);
        int map_right_X = (int)(right_X / TILE_SIZE);
        int map_top_Y = (int)(top_Y / TILE_SIZE);
        int map_bottom_Y = (int)(bottom_Y / TILE_SIZE);

        // Chainsaw
        if((levelData[map_bottom_Y][map_left_X] >= CHAINSAW && levelData[map_bottom_Y][map_left_X] < CHAINSAW + GetSpriteAmount(CHAINSAW))
        || (levelData[map_top_Y][map_left_X] >= CHAINSAW && levelData[map_top_Y][map_left_X] < CHAINSAW + GetSpriteAmount(CHAINSAW))
        || (levelData[map_bottom_Y][map_right_X] >= CHAINSAW && levelData[map_bottom_Y][map_right_X] < CHAINSAW + GetSpriteAmount(CHAINSAW))
        || (levelData[map_top_Y][map_right_X] >= CHAINSAW && levelData[map_top_Y][map_right_X] < CHAINSAW + GetSpriteAmount(CHAINSAW)))
            return true;

        // Brown-saw
        if(levelData[map_bottom_Y][map_right_X] >= BROWNSAW && levelData[map_bottom_Y][map_right_X] < BROWNSAW){
            return true;
        }

        if((levelData[map_top_Y][map_left_X] >= BROWNSAW && levelData[map_top_Y][map_left_X] < BROWNSAW + GetSpriteAmount(BROWNSAW))
        || (levelData[map_bottom_Y][map_left_X] >= BROWNSAW && levelData[map_bottom_Y][map_left_X] < BROWNSAW + GetSpriteAmount(BROWNSAW))
        || (levelData[map_top_Y][map_right_X] >= BROWNSAW && levelData[map_top_Y][map_right_X] < BROWNSAW + GetSpriteAmount(BROWNSAW))){
            if(left_X > map_left_X * TILE_SIZE + 28) return false;
            return top_Y <= map_top_Y * TILE_SIZE + 28;
        }

        // Spike
        if((levelData[map_bottom_Y][map_left_X] >= SPIKE && levelData[map_bottom_Y][map_left_X] < SPIKE + GetSpriteAmount(SPIKE))
        || (levelData[map_bottom_Y][map_right_X] >= SPIKE && levelData[map_bottom_Y][map_right_X] < SPIKE + GetSpriteAmount(SPIKE))){
            return bottom_Y >= map_bottom_Y * TILE_SIZE + 19;
        }

        if((levelData[map_top_Y][map_left_X] >= SPIKE && levelData[map_top_Y][map_left_X] < SPIKE + GetSpriteAmount(SPIKE))
        || (levelData[map_top_Y][map_right_X] >= SPIKE && levelData[map_top_Y][map_right_X] < SPIKE + GetSpriteAmount(SPIKE))){
            return true;
        }

        // if((levelData[map_top_Y][map_left_X] >= CELL_SPIKE && levelData[map_top_Y][map_left_X] < CELL_SPIKE + GetSpriteAmount(CELL_SPIKE))
        // || (levelData[map_top_Y][map_right_X] >= CELL_SPIKE && levelData[map_top_Y][map_right_X] < CELL_SPIKE + GetSpriteAmount(CELL_SPIKE))){
        //     return true;
        // }

        // Fire
        if((levelData[map_bottom_Y][map_left_X] >= FIRE && levelData[map_bottom_Y][map_left_X] < FIRE + GetSpriteAmount(FIRE))
        || (levelData[map_top_Y][map_left_X] >= FIRE && levelData[map_top_Y][map_left_X] < FIRE + GetSpriteAmount(FIRE))) {
            return left_X >= map_left_X * TILE_SIZE + 7;
        }

        if((levelData[map_bottom_Y][map_right_X] >= FIRE && levelData[map_bottom_Y][map_right_X] < FIRE + GetSpriteAmount(FIRE))
        || (levelData[map_top_Y][map_right_X] >= FIRE && levelData[map_top_Y][map_right_X] < FIRE + GetSpriteAmount(FIRE))) {
            return (left_X <= map_left_X * TILE_SIZE + 26);
        }
        
        // Sword trap
        if((levelData[map_bottom_Y][map_left_X] >= SWORDTRAP1 && levelData[map_bottom_Y][map_left_X] < SWORDTRAP1 + GetSpriteAmount(SWORDTRAP1))
        || (levelData[map_bottom_Y][map_right_X] >= SWORDTRAP1 && levelData[map_bottom_Y][map_right_X] < SWORDTRAP1 + GetSpriteAmount(SWORDTRAP1))){
            return (bottom_Y >= map_bottom_Y * TILE_SIZE + 8);
        }

        return (levelData[map_top_Y][map_left_X] >= SWORDTRAP1 && levelData[map_top_Y][map_left_X] < SWORDTRAP1 + GetSpriteAmount(SWORDTRAP1))
            || (levelData[map_top_Y][map_right_X] >= SWORDTRAP1 && levelData[map_top_Y][map_right_X] < SWORDTRAP1 + GetSpriteAmount(SWORDTRAP1));
    }

    public static boolean IsMud(int x, int y, int[][] levelData) {
        return levelData[y][x] == 9 || levelData[y][x] == 28 || levelData[y][x] == 47 || levelData[y][x] == 66
            || levelData[y][x] == 85;
    }

    
    public static BufferedImage turnWhite(BufferedImage inputImage) {
        // Create a new image with the same dimensions and type as the input
        BufferedImage outputImage = new BufferedImage(
            inputImage.getWidth(),
            inputImage.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
    
        // Process each pixel
        for (int x = 0; x < inputImage.getWidth(); x++) {
            for (int y = 0; y < inputImage.getHeight(); y++) {
                int pixelColor = inputImage.getRGB(x, y);
                // Check if the pixel is not fully transparent (alpha != 0)
                if ((pixelColor >> 24) != 0) {
                    // Set the pixel to white with full opacity
                    outputImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    // Keep the transparent pixel as-is
                    outputImage.setRGB(x, y, pixelColor);
                }
            }
        }
        return outputImage;
    }

    
    public static BufferedImage syncWithUnvulerable(BufferedImage input, Graphics2D g2, boolean unvulerable, long timeSinceLastUnvulerable){
        if(unvulerable && System.currentTimeMillis() - timeSinceLastUnvulerable <= 150){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            return turnWhite(input);
        }
        else return input;
    }
    
    public static ArrayList<BreakablePlatform> GetBreakablePlatforms(int[][] levelData){
        ArrayList<BreakablePlatform> breakablePlatforms = new ArrayList<>();
        for (int i = 0; i < levelData.length; i++)
            for (int j = 0; j < levelData[0].length; j++) {
                if (levelData[i][j] == BREAKABLE_PLATFORM) {
                    breakablePlatforms.add(new BreakablePlatform(j * TILE_SIZE, i * TILE_SIZE, BREAKABLE_PLATFORM));
                }
            }
        return breakablePlatforms;
    }

    public static ArrayList<Shooter> GetShooters(int[][] levelData) {
        ArrayList<Shooter> shooters = new ArrayList<>();
        for (int i = 0; i < levelData.length; i++)
            for (int j = 0; j < levelData[0].length; j++) {
                if (levelData[i][j] == SHOOTER) {
                    shooters.add(new Shooter(j * TILE_SIZE, i * TILE_SIZE, SHOOTER));
                }
            }
        return shooters;
    }

    public static boolean IsArrowHitLevel(Arrow arrow, int[][] levelData) {
        return IsSolid(arrow.getHitbox().x + arrow.getHitbox().width/2, arrow.getHitbox().y + arrow.getHitbox().height/2, levelData);
    }
}