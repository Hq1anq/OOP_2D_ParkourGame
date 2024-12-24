package objects;

import entities.Player;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import levels.Level;
import main.Game;
import static utilz.Constants.ObjectConstants.GetSpriteAmount;
import static utilz.Constants.ObjectConstants.SHOOTER;
import static utilz.HelpMethods.IsArrowHitLevel;
import utilz.LoadSave;
import utilz.Point;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class ObjectManager {

    private Game game;
    private BufferedImage[] breakablePlatformImgs, shooterImgs;
    private BufferedImage arrowImg;
    private ArrayList<BreakablePlatform> breakablePlatforms;
    private ArrayList<Shooter> shooters;
    private ArrayList<Arrow> arrows = new ArrayList<>();

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
        shooters = level.getShooters();
        arrows.clear();
    }

    private void loadImgs() {
        BufferedImage outsideSprites = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        breakablePlatformImgs = new BufferedImage[15];
        for (int i = 0; i < 8; i++) {
            breakablePlatformImgs[i] = outsideSprites.getSubimage(0, 32 * 16, 64, 64);
        }
        for (int i = 8; i < 15; i++) {
            breakablePlatformImgs[i] = outsideSprites.getSubimage((i - 8) * 64, 32 * 16, 64, 64);
        }
        shooterImgs = new BufferedImage[GetSpriteAmount(SHOOTER)];
        for (int i = 0; i < shooterImgs.length - 7; i++) {
            shooterImgs[i] = outsideSprites.getSubimage(i * 32, 10 * 32, 32, 32);
        }
        for (int i = shooterImgs.length - 7; i < shooterImgs.length; i++) {
            shooterImgs[i] = outsideSprites.getSubimage(32 * (GetSpriteAmount(SHOOTER) - 7 - 1), 10 * 32, 32, 32);
        }
        arrowImg = outsideSprites.getSubimage(6 * 32, 10 * 32, 32, 32);
    }

    public void update(int[][] levelData, Player player) {
        for (BreakablePlatform bp : breakablePlatforms) {
            if (bp.isActive()) bp.update();
        }
        updateShooters();
        updateArrows(levelData, player);
    }

    private void updateShooters() {
        for (Shooter shooter : shooters) {
            if (!shooter.isAnimating()) {
                shooter.setAnimation(true);
            }
            shooter.update();
            if (shooter.getAniIndex() == 3 && shooter.getAniTick() == 0) {
                shootArrow(shooter);
            }
        }
    }

    private void shootArrow(Shooter shooter) {
        arrows.add(new Arrow((int) shooter.getHitbox().x - shooter.getxDrawOffset(), (int) shooter.getHitbox().y));
    }

    private void updateArrows(int[][] levelData, Player player) {
        for (Arrow arrow : arrows) {
            if (arrow.isActive()) {
                arrow.updatePos();
                if (arrow.getHitbox().intersects(player.getHitbox()) && !player.getUnvulerable()) {
                    player.gotHit();
                    arrow.setActive(false);
                } else if (IsArrowHitLevel(arrow, levelData)) {
                    arrow.setActive(false);
                }
            }
        }
    }

    public void draw(Graphics g, int xLevelOffset, int yLevelOffset) {
        drawBreakablePlatforms(g, xLevelOffset, yLevelOffset);
        drawShooters(g, xLevelOffset, yLevelOffset);
        drawArrows(g, xLevelOffset, yLevelOffset);
    }

    private void drawBreakablePlatforms(Graphics g, int xLevelOffset, int yLevelOffset) {
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

    private void drawShooters(Graphics g, int xLevelOffset, int yLevelOffset) {
        for (Shooter shooter : shooters) {
            g.drawImage(shooterImgs[shooter.getAniIndex()],
                            (int) (shooter.getHitbox().x - shooter.getxDrawOffset() - xLevelOffset),
                            (int) (shooter.getHitbox().y - yLevelOffset),
                            (int) (32 * Game.SCALE),
                            (int) (32 * Game.SCALE), null);
            // shooter.drawHitbox(g, xLevelOffset, yLevelOffset);
        }
    }

    private void drawArrows(Graphics g, int xLevelOffset, int yLevelOffset) {
        Iterator<Arrow> iterator = arrows.iterator();
        while (iterator.hasNext()) {
            Arrow arrow = iterator.next();
            if (arrow.isActive()) {
                g.drawImage(arrowImg,
                            (int) (arrow.getHitbox().x - arrow.getxDrawOffset() - xLevelOffset),
                            (int) (arrow.getHitbox().y - arrow.getyDrawOffset() - yLevelOffset),
                            (int) (32 * Game.SCALE),
                            (int) (32 * Game.SCALE), null);
                arrow.drawHitbox(g, xLevelOffset, yLevelOffset);
            } else {
                iterator.remove(); // Safely remove inactive arrows
            }
        }
        // for (Arrow arrow : arrows) {
        //     if (arrow.isActive()) {
        //         g.drawImage(arrowImg,
        //                     (int) (arrow.getHitbox().x - arrow.getxDrawOffset() - xLevelOffset),
        //                     (int) (arrow.getHitbox().y - arrow.getyDrawOffset() - yLevelOffset),
        //                     (int) (32 * Game.SCALE),
        //                     (int) (32 * Game.SCALE), null);
        //         // arrow.drawHitbox(g, xLevelOffset, yLevelOffset);
        //     }
        // }
    }

    public void resetAllObjects() {
        for (BreakablePlatform bp : breakablePlatforms) {
            bp.reset();
        }
        // for (Shooter shooter: shooters) {
        //     shooter.reset();
        // }
    }
}
