package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import main.Game;
import static main.Game.TILE_SIZE;
import utilz.LoadSave;

@SuppressWarnings("FieldMayBeFinal")

// ***IMPORTANT

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level level;
    private int currentLevel = 1;

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        level = new Level(currentLevel);
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[576];
        for (int i = 0; i < 36; i++)
            for (int j = 0; j < 16; j++) {
                int index = i * 16 + j;
                levelSprite[index] = img.getSubimage(j*32, i*32, 32, 32);
            }
    }
    public void draw(Graphics g, int xLevelOffset, int yLevelOffset) {
        for (int i = 0; i < level.getLevelData().length; i++)
            for (int j = 0; j < level.getLevelData()[0].length; j++) {
                int index = level.getSpriteIndex(j, i);
                if (index != -1)
                    g.drawImage(levelSprite[index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);

            }
    }
    public void loadNextLevel() {
        currentLevel = 2;
        level = new Level(currentLevel);
        game.getPlayer().loadLevelData(level.getLevelData());
        game.getPlayer().resetLevel2Statistics();
    }
    public void update() {

    }

    public void setLevelIndex(int levelIndex) {
        currentLevel = levelIndex;
    }

    public Level getCurrentLevel() {
        return level;
    }
}
