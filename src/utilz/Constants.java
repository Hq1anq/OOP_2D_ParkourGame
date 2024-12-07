package utilz;

public class Constants {
    public static class PlayerConstants {
        public static final int IDLE = 0;
        public static final int JUMP = 1;
        public static final int FALL = 2;
        public static final int WALKING = 3;
        public static final int RUNNING = 4;
        public static final int CROUCH = 6;
        public static final int LEDGE_CLIMB = 11;
        // public static final int GROUND = 3;
        // public static final int HIT = 5;

        public static int GetSpriteAmount(int player_action) {
            return switch (player_action) {
                case IDLE -> 5;
                case JUMP -> 2;
                case FALL -> 4;
                case WALKING -> 8;
                case RUNNING -> 8;
                case CROUCH -> 2;
                case LEDGE_CLIMB -> 8;
                default -> 1;
            }; // case GROUND: return 8;
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
