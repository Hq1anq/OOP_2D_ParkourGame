package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import levels.Level;
import main.Game;
import utilz.LoadSave;
import utilz.Point;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class ObjectManager {

    private Game game;
    private BufferedImage[] breakablePlatformImgs;
    private ArrayList<BreakablePlatform> breakablePlatforms;

    public ObjectManager(Game game) {
        this.game = game;
        loadImgs();
        breakablePlatforms = new ArrayList<>();
    }

    public Point checkObjectStepped(Rectangle2D.Float playerHitbox) {
        Rectangle2D.Float footHitbox = new Rectangle2D.Float(playerHitbox.x, playerHitbox.y + playerHitbox.height + 2, playerHitbox.width, 10);
        for (BreakablePlatform bp : breakablePlatforms) {
            if (bp.isActive() && footHitbox.intersects(bp.getHitbox())) {
                bp.setAnimation(true);
                return new Point((int) bp.getHitbox().x, (int) bp.getHitbox().y);
            }
        }
        return null;
    }

    public void loadObjects(Level level) {
        breakablePlatforms = level.getBreakablePlatforms();
    }

    private void loadImgs() {
        BufferedImage breakablePlatformSprite = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        breakablePlatformImgs = new BufferedImage[15];
        for (int i = 0; i < 8; i++) {
            breakablePlatformImgs[i] = breakablePlatformSprite.getSubimage(0, 32 * 16, 64, 64);
        }
        for (int i = 8; i < 15; i++) {
            breakablePlatformImgs[i] = breakablePlatformSprite.getSubimage((i - 8) * 64, 32 * 16, 64, 64);
        }
    }

    public void update() {
        for (BreakablePlatform bp : breakablePlatforms) {
            if (bp.isActive()) bp.update();
        }
    }

    public void draw(Graphics g, int xLevelOffset, int yLevelOffset) {
        for (BreakablePlatform bp : breakablePlatforms) {
            if (bp.isActive()) {
                g.drawImage(breakablePlatformImgs[bp.getAniIndex()],
                            (int) (bp.getHitbox().x - bp.getxDrawOffset() - xLevelOffset),
                            (int) (bp.getHitbox().y - bp.getyDrawOffset() - yLevelOffset),
                            (int) (64 * Game.SCALE),
                            (int) (64 * Game.SCALE), null);
                // bp.drawHitbox(g, xLevelOffset, yLevelOffset);
            }
        }
    }

    public void resetAllObjects() {
        for (BreakablePlatform bp : breakablePlatforms) {
            bp.reset();
        }
    }
}
