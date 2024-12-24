package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import main.Game;
import static utilz.Constants.ARROW.*;

@SuppressWarnings("FieldMayBeFinal")

public class Arrow {
    private Rectangle2D.Float hitbox;
    private boolean active = true;
    
    private int xDrawOffset;
    private int yDrawOffset;
    
    public Arrow(int x, int y) {
        hitbox = new Rectangle2D.Float(x, y, 10 * Game.SCALE, 14 * Game.SCALE);
        xDrawOffset = (int) (11 * Game.SCALE);
        yDrawOffset = (int) (14 * Game.SCALE);
        hitbox.x += xDrawOffset;
        hitbox.y += yDrawOffset;
    }

    public void drawHitbox(Graphics g, int xLevelOffset, int yLevelOffset) {
        g.setColor(Color.PINK);
        g.drawRect((int) (hitbox.x - xLevelOffset), (int) (hitbox.y - yLevelOffset), (int) hitbox.width, (int) hitbox.height);
    }

    public void updatePos() {
        hitbox.y += SPEED;
    }

    public void setPos(int x, int y) {
        hitbox.x = x; hitbox.y = y;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public int getxDrawOffset() {
        return xDrawOffset;
    }

    public int getyDrawOffset() {
        return yDrawOffset;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
