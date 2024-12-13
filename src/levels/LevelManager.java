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

    private float chainsawIndex = 266;
    private float fireIndex = 228;
    private float spikeIndex = 285;
    private float spike2Index = 289;
    private float swordtrap1Index = 133;
    private float swordtrap2Index = 152;
    private float brownsawIndex = 247;

    private int trapFPU = 40; // 1 frame for 40 updates

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        level = new Level(currentLevel);
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[684];
        for (int i = 0; i < 36; i++)
            for (int j = 0; j < 19; j++) {
                int index = i * 19 + j;
                levelSprite[index] = img.getSubimage(j*32, i*32, 32, 32);
            }
    }

    public void draw(Graphics g, int xLevelOffset, int yLevelOffset) {
        for (int i = 0; i < level.getLevelData().length; i++)
            for (int j = 0; j < level.getLevelData()[0].length; j++) {
                int index = level.getSpriteIndex(j, i);
                if (index == 266) {
                    g.drawImage(levelSprite[(int) chainsawIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    chainsawIndex += 1.0/trapFPU;
                    if (chainsawIndex > 274) chainsawIndex = 266;
                } else if (index == 228) {
                    g.drawImage(levelSprite[(int) fireIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    fireIndex += 1.0/trapFPU;
                    if (fireIndex > 232) fireIndex = 228;
                } else if (index == 285) {
                    g.drawImage(levelSprite[(int) spikeIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    spikeIndex += 1.0/(10 * trapFPU);
                    if (spikeIndex > 287) spikeIndex = 285;
                } else if (index == 289) {
                    g.drawImage(levelSprite[(int) spike2Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    spike2Index += 5.0/trapFPU;
                    if (spike2Index > 295) spike2Index = 289;
                } else if (index == 133) {
                    g.drawImage(levelSprite[(int) swordtrap1Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    swordtrap1Index += 1.0/trapFPU;
                    if (swordtrap1Index > 138) swordtrap1Index = 133;
                } else if (index == 152) {
                    g.drawImage(levelSprite[(int) swordtrap2Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    swordtrap2Index += 2.0/trapFPU;
                    if (swordtrap2Index > 156) swordtrap2Index = 152;
                } else if (index == 247) {
                    g.drawImage(levelSprite[(int) brownsawIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    brownsawIndex += 1.0/(5 * trapFPU);
                    if (brownsawIndex > 249) brownsawIndex = 247;
                } else if (index != -1) {
                    g.drawImage(levelSprite[index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                }

            }
    }
    
    public void loadLevel(int levelNumber) {
        currentLevel = levelNumber;
        level = new Level(currentLevel);
        game.getPlayer().loadLevelData(level.getLevelData());
        if(currentLevel == 2)
        game.getPlayer().resetLevel2Statistics();
        else if(currentLevel == 1)
        game.getPlayer().resetLevel1Statistics();
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
