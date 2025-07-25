package utilz;

import java.awt.Color;
import main.Game;

public class Constants {

    public static final String GAME_NAME = "Squirrel 's adventure";
    public static final int ANI_SPEED = 15;

    public static class ARROW {
        public static final int ARROW_DEFAULT_WIDTH = 10;
        public static final int ARROW_DEFAULT_HEIGHT = 14;
        public static final int ARROW_WIDTH = (int) (ARROW_DEFAULT_WIDTH * Game.SCALE);
        public static final int ARROW_HEIGHT = (int) (ARROW_DEFAULT_HEIGHT * Game.SCALE);
        public static final float SPEED = 1.5f * Game.SCALE;
    }
    public static class ObjectConstants {
        public static final int BREAKABLE_PLATFORM = 119;
        public static final int INVISIBLE_TILE = 120;

        public static final int CHAINSAW = 266;
        public static final int FIRE = 228;
        public static final int SPIKE = 285;
        public static final int SWORDTRAP1 = 133;
        public static final int SWORDTRAP2 = 152;
        public static final int BROWNSAW = 247;
        // public static final int CELL_SPIKE = 190;

        public static final int SHOOTER = 190;

        public static final int SHOOTER_DEFAULT_WIDTH = 26;
        public static final int SHOOTER_DEFAULT_HEIGHT = 14;
        public static final int SHOOTER_WIDTH = (int) (SHOOTER_DEFAULT_WIDTH * Game.SCALE);
        public static final int SHOOTER_HEIGHT = (int) (SHOOTER_DEFAULT_HEIGHT * Game.SCALE);
        public static int GetSpriteAmount(int objType) {
            return switch (objType) {
                case BREAKABLE_PLATFORM -> 15;
                case CHAINSAW -> 8;
                case FIRE -> 4;
                case SPIKE -> 10;
                case SWORDTRAP1 -> 5;
                case SWORDTRAP2 -> 4;
                case BROWNSAW -> 2;
                // case CELL_SPIKE -> 2;
                case SHOOTER -> 13;
                default -> 1;
            };
        }
    }

    public static class GameState {
        public static final int START_MENU = -1;
        public static final int PLAYING = 0;
        public static final int SETTING = 1;
        public static final int GUIDES = 2;
        public static final int EXIT = 3;
        public static final int CHOOSING_LEVEL = 4;
    }
    public static class Menu {
        public static final Color DARKEN_BACKGROUND_COLOR = new Color(0, 0, 0, 0.5f);
    }

    public static class Level1 {
        public static final int ENV_WIDTH_DEFAULT = 1280;
        public static final int ENV_HEIGHT_DEFAULT = 750;
        public static final Color BG_COLOR = new Color(86, 88, 123);
    }

    public static class Level2 {
        public static final int ENV_WIDTH_DEFAULT = 1280;
        public static final int ENV_HEIGHT_DEFAULT = 750;
        public static final Color BG_COLOR = new Color(166, 172, 186);
    }

    public static class PlayerConstants {
        public static final int IDLE = 0;
        public static final int JUMP = 1;
        public static final int FALL = 2;
        public static final int WALKING = 3;
        public static final int START_DASH = 4;
        public static final int FINISH_DASH = 5;
        public static final int RUNNING = 6;
        public static final int CROUCH = 7;
        public static final int AIR_FLIP = 9;
        public static final int FLOOR_SMASH = 10;
        public static final int LADDER_CLIMB = 11;
        public static final int WIN_POSE = 12;
        public static final int WALL_KICK = 13;
        public static final int WALL_CLIMB = 14;
        public static final int LEDGE_CLIMB = 15;
        // public static final int HIT = 5;

        public static int GetSpriteAmount(int player_action) {
            return switch (player_action) {
                case IDLE -> 5;
                case JUMP -> 2;
                case FALL -> 4;
                case WALKING -> 16;
                case START_DASH -> 6;
                case FINISH_DASH -> 4;
                case RUNNING -> 8;
                case CROUCH -> 2;
                case AIR_FLIP -> 8;
                case FLOOR_SMASH -> 3;
                case LADDER_CLIMB -> 6;
                case WIN_POSE -> 12;
                case WALL_KICK -> 1;
                case WALL_CLIMB -> 8;
                case LEDGE_CLIMB -> 8;
                default -> 1;
            };
            // case HIT: return 8;
        }
    }
    public static class Directions {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }
}
