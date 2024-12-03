package utilz;

public class Constants {
    public static class PlayerConstants {
        public static final int WALKING = 1;
        public static final int RUNNING = 3;
        public static final int IDLE = 0;
        public static final int JUMP = 15;
        // public static final int JUMPING = 2;
        // public static final int GROUND = 3;
        // public static final int HIT = 5;

        public static int GetSpriteAmount(int player_action) {
            switch(player_action) {
                case WALKING: return 8;
                case RUNNING: return 8;
                case IDLE: return 5;
                case JUMP: return 6;
                // case JUMPING: return 8;
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
