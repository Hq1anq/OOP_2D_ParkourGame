package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    // FOR DRAWING
    protected float x, y;
    protected int width, height;

    // FOR COLLISION DETECTING
    protected Rectangle2D.Float hitbox;

    public Entity(float x, float y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }

    protected void drawHitbox(Graphics g, int xLevelOffset, int yLevelOffset) {
        g.setColor(Color.GREEN);
        g.drawRect((int) (hitbox.x - xLevelOffset), (int) (hitbox.y - yLevelOffset), (int) hitbox.width, (int) hitbox.height);
    }

    protected void initHitbox(float x, float y, float width, float height) {
        // INITIATE HITBOX != IN IT HITBOX
        hitbox = new Rectangle2D.Float(x, y, width, height);
    }

    // protected void updateHitbox() {
    //     hitbox.x = (int) x;
    //     hitbox.y = (int) y;
    // }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
}
