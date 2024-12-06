package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import main.Game;
import static main.Game.TILE_SIZE;
import utilz.LoadSave;

// ***IMPORTANT

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level level1;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        level1 = new Level(LoadSave.getLevelData());
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[48];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 12; j++) {
                int index = i * 12 + j;
                levelSprite[index] = img.getSubimage(j*32, i*32, 32, 32);
            }
    }
    public void draw(Graphics g, int xLevelOffset) {
        for (int i = 0; i < Game.TILES_IN_HEIGHT; i++)
            for (int j = 0; j < level1.getLevelData()[0].length; j++) {
                int index = level1.getSpriteIndex(j, i);
                g.drawImage(levelSprite[index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i, TILE_SIZE, TILE_SIZE, null);

            }
    }
    public void update() {

    }
    public Level getCurrentLevel() {
        return level1;
    }
}
