package utilz;

import java.awt.geom.Rectangle2D;
import main.Game;

public class HelpMethods {

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] levelData) {
        if (!IsSolid(x, y, levelData))
            if (!IsSolid(x + width, y + height, levelData))
                if (!IsSolid(x + width, y, levelData))
                    if (!IsSolid(x, y + height, levelData))
                        if (!IsSolid(x, y + height/2, levelData))
                            if (!IsSolid(x + width, y + height/2, levelData))
                                return true;
        return false;
    }

    private static boolean IsSolid(float x, float y, int[][] levelData) {
        int maxWidth = levelData[0].length * Game.TILE_SIZE;
        int maxHeight = levelData.length * Game.TILE_SIZE;

        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight)
            return true;

        int xIndex = (int) (x / Game.TILE_SIZE);
        int yIndex = (int) (y / Game.TILE_SIZE);

        int value = levelData[yIndex][xIndex];
        return (value >= 48 || value < 0 || value != 11);
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
}