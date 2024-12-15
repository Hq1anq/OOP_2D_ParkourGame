package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import main.Game;
import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.ObjectConstants.BREAKABLE_PLATFORM;
import static utilz.Constants.ObjectConstants.GetSpriteAmount;

public class GameObject {
    protected int x, y, objectType;
    protected Rectangle2D.Float hitbox;
    protected boolean doAnimation, active = true;
    protected int aniTick, aniIndex;
    protected int xDrawOffset, yDrawOffset;

    public GameObject(int x, int y, int objectType) {
        this.x = x;
        this.y = y;
        this.objectType = objectType;
    }

    protected void initHitbox(int width, int height) {
        hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
    }

    public void drawHitbox(Graphics g, int xLevelOffset, int yLevelOffset) {
        g.setColor(Color.PINK);
        g.drawRect((int) (hitbox.x - xLevelOffset), (int) (hitbox.y - yLevelOffset), (int) hitbox.width, (int) hitbox.height);
    }
    protected void updateAnimationTick() {
		aniTick++;
		if (aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(objectType)) {
				aniIndex = 0;
				if (objectType == BREAKABLE_PLATFORM) {
					doAnimation = false;
					active = false;
				}
			}
		}
	}
    public void reset() {
        aniTick = 0;
        aniTick = 0;
        active = true;
        doAnimation = (objectType != BREAKABLE_PLATFORM);
    }

    public int getObjectType() {
        return objectType;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAnimation(boolean doAnimation) {
        this.doAnimation = doAnimation;
    }

    public int getxDrawOffset() {
        return xDrawOffset;
    }

    public int getyDrawOffset() {
        return yDrawOffset;
    }

    public int getAniIndex() {
        return aniIndex;
    }

}
