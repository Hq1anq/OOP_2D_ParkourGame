package objects;

import main.Game;

public class BreakablePlatform extends GameObject {
    public BreakablePlatform(int x, int y, int objectType) {
        super(x, y, objectType);
        // doAnimation = true;
        initHitbox((int) (22 * Game.SCALE), (int) (22 * Game.SCALE));
        xDrawOffset = (int) (16 * Game.SCALE);
        yDrawOffset = (int) (16 * Game.SCALE);
        // hitbox.x += xDrawOffset;
        // hitbox.y += yDrawOffset;
    }

    public void update() {
        if (doAnimation) {
            updateAnimationTick();
        }
    }

}
