package utilz;

import java.awt.geom.Rectangle2D;
import main.Game;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] levelData) {
        if (!IsSolid(x, y, levelData))
            if (!IsSolid(x + width, y + height, levelData))
                if (!IsSolid(x + width, y, levelData))
                    if (!IsSolid(x, y + height, levelData))
                        return true;
        return false;
    }

    private static boolean IsSolid(float x, float y, int[][] levelData) {
        int maxWidth = levelData[0].length * Game.TILE_SIZE;
        int maxHeight = levelData.length * Game.TILE_SIZE;

        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight)
            return true;

        float xIndex = x / Game.TILE_SIZE;
        float yIndex = y / Game.TILE_SIZE;

        int value = levelData[(int) yIndex][(int) xIndex];
        return (value >= 48 || value < 0 || value != 11);
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / Game.TILE_SIZE);
        if (xSpeed > 0) { // Move to Right
            int tileXPos = currentTile * Game.TILE_SIZE;
            int xOffSet = (int) (Game.TILE_SIZE - hitbox.width);
            return tileXPos + xOffSet - 1; // -1 For not overlapping
        } else { // Move to Left
            return currentTile * Game.TILE_SIZE + 1;
        }
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] levelData) {
        // Check pixels below the bottom-left and bottom-right corners of the hitbox
        float xStart = hitbox.x;
        float xEnd = hitbox.x + hitbox.width;
        float y = hitbox.y + hitbox.height + 3;

        for (float x = xStart; x < xEnd; x += Game.TILE_SIZE / 2) {
            if (IsSolid(x, y, levelData)) {
                return true;
            }
        }
        return IsSolid(xEnd, y, levelData);
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        int currentTile = (int) (hitbox.y / Game.TILE_SIZE);
        if (airSpeed > 0) { // Falling -> Touching Floor
            int tileYPos = currentTile * Game.TILE_SIZE;
            int yOffSet = (int) (Game.TILE_SIZE - hitbox.height);
            return tileYPos + yOffSet - 1;
        } else { // Jumping -> Hit the roof
            return currentTile * Game.TILE_SIZE;
        }
    }
}
