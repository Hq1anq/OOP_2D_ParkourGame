package objects;

import main.Game;
import static utilz.Constants.ObjectConstants.SHOOTER_HEIGHT;
import static utilz.Constants.ObjectConstants.SHOOTER_WIDTH;

public class Shooter extends GameObject{
    public Shooter(int x, int y, int objectType) {
        super(x, y, objectType);
        initHitbox(SHOOTER_WIDTH, SHOOTER_HEIGHT);
        xDrawOffset = (int) (3 * Game.SCALE);
        yDrawOffset = (int) (0 * Game.SCALE);
        hitbox.x += xDrawOffset;
    }

    public void update() {
        if (doAnimation)
            updateAnimationTick();
    }
    
}
