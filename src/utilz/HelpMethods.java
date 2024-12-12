package utilz;

import static main.Game.TILE_SIZE;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import main.Game;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] levelData) {
        if (!IsSolid(x, y, levelData)  && !IsSolid(x + width, y + height, levelData) && !IsSolid(x + width, y, levelData) 
        && !IsSolid(x, y + height, levelData) && !IsSolid(x, y + height/2, levelData) && !IsSolid(x + width, y + height/2, levelData))
            return true;
        return false;
    }

    public static boolean IsSolid(float x, float y, int[][] levelData) {
        int maxWidth = levelData[0].length * Game.TILE_SIZE;
        int maxHeight = levelData.length * Game.TILE_SIZE;

        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight)
            return true;

        int xIndex = (int) (x / Game.TILE_SIZE);
        int yIndex = (int) (y / Game.TILE_SIZE);

        int value = levelData[yIndex][xIndex];
        return (value != -1 && (value >= 0 && value < 101));
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
        return (IsSolid(hitbox.x, hitbox.y + hitbox.height + 3, levelData) ||
                IsSolid(hitbox.x + hitbox.width / 2, hitbox.y + hitbox.height + 3, levelData) ||
                IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 3, levelData));
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

        return levelData[yIndex][xIndex1] >= 96 && levelData[yIndex][xIndex1] <= 100 
            && levelData[yIndex][xIndex2] >= 96 && levelData[yIndex][xIndex2] <= 100;
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
        if((levelData[map_bottom_Y][map_left_X] >= 224 && levelData[map_bottom_Y][map_left_X] <= 231)
        || (levelData[map_top_Y][map_left_X] >= 224 && levelData[map_top_Y][map_left_X] <= 231)
        || (levelData[map_bottom_Y][map_right_X] >= 224 && levelData[map_bottom_Y][map_right_X] <= 231)
        || (levelData[map_top_Y][map_right_X] >= 224 && levelData[map_top_Y][map_right_X] <= 231))
            return true;

        // Brown-saw
        if(levelData[map_bottom_Y][map_right_X] >= 208 && levelData[map_bottom_Y][map_right_X] <= 209){
            return true;
        }

        if((levelData[map_top_Y][map_left_X] >= 208 && levelData[map_top_Y][map_left_X] <= 209)
        || (levelData[map_bottom_Y][map_left_X] >= 208 && levelData[map_bottom_Y][map_left_X] <= 209)
        || (levelData[map_top_Y][map_right_X] >= 208 && levelData[map_top_Y][map_right_X] <= 209)){
            if(left_X > map_left_X * TILE_SIZE + 28) return false;
            if(top_Y > map_top_Y * TILE_SIZE + 28) return false;
            return true;
        }

        // Spike
        if((levelData[map_bottom_Y][map_left_X] >= 240 && levelData[map_bottom_Y][map_left_X] <= 241)
        || (levelData[map_bottom_Y][map_right_X] >= 240 && levelData[map_bottom_Y][map_right_X] <= 241)){
            if(bottom_Y < map_bottom_Y * TILE_SIZE + 26) return false;
            return true;
        }
        if((levelData[map_bottom_Y][map_left_X] >= 242 && levelData[map_bottom_Y][map_left_X] <= 249)
        || (levelData[map_bottom_Y][map_right_X] >= 242 && levelData[map_bottom_Y][map_right_X] <= 249)){
            if(bottom_Y < map_bottom_Y * TILE_SIZE + 19) return false;
            return true;
        }

        if((levelData[map_top_Y][map_left_X] >= 240 && levelData[map_top_Y][map_left_X] <= 241)
        || (levelData[map_top_Y][map_right_X] >= 240 && levelData[map_top_Y][map_right_X] <= 241)
        || (levelData[map_top_Y][map_left_X] >= 242 && levelData[map_top_Y][map_left_X] <= 249)
        || (levelData[map_top_Y][map_right_X] >= 242 && levelData[map_top_Y][map_right_X] <= 249)){
            return true;
        }

        // Fire
        if((levelData[map_bottom_Y][map_left_X] >= 192 && levelData[map_bottom_Y][map_left_X] <= 195)
        || (levelData[map_top_Y][map_left_X] >= 192 && levelData[map_top_Y][map_left_X] <= 195)){
            if(left_X < map_left_X * TILE_SIZE + 7) return false;
            return true;
        }

        if((levelData[map_bottom_Y][map_right_X] >= 192 && levelData[map_bottom_Y][map_right_X] <= 195)
        || (levelData[map_top_Y][map_right_X] >= 192 && levelData[map_top_Y][map_right_X] <= 195)){
            if(left_X > map_left_X * TILE_SIZE + 26) return false;
            return true;
        }
        
        // Sword trap
        if((levelData[map_bottom_Y][map_left_X] >= 112 && levelData[map_bottom_Y][map_left_X] <= 116)
        || (levelData[map_bottom_Y][map_right_X] >= 112 && levelData[map_bottom_Y][map_right_X] <= 116)){
            if(bottom_Y < map_bottom_Y * TILE_SIZE + 8) return false;
            return true;
        }

        if((levelData[map_top_Y][map_left_X] >= 112 && levelData[map_top_Y][map_left_X] <= 116)
        || (levelData[map_top_Y][map_right_X] >= 112 && levelData[map_top_Y][map_right_X] <= 116))
            return true;

        return false;
    }

    public static boolean IsMud(int x, int y, int[][] levelData){
        return levelData[y][x] == 9 || levelData[y][x] == 25 || levelData[y][x] == 41 || levelData[y][x] == 57
            || levelData[y][x] == 73 || levelData[y][x] == 89;
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
    
}