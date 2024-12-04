package utilz;

public class Constants {
    public static class PlayerConstants {
        public static final int WALKING = 3;
        public static final int RUNNING = 4;
        public static final int IDLE = 0;
        public static final int JUMP = 1;
        public static final int FALL = 2;
        // public static final int GROUND = 3;
        // public static final int HIT = 5;

        public static int GetSpriteAmount(int player_action) {
            switch(player_action) {
                case WALKING: return 8;
                case RUNNING: return 8;
                case IDLE: return 5;
                case JUMP: return 2;
                case FALL: return 4;
                // case GROUND: return 8;
                // case HIT: return 8;
                default: return 1;
            }
        }
    }
    public static class Directions {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }
}
