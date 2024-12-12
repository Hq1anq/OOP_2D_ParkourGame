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

    private float chainsawIndex = 224;
    private float fireIndex = 192;
    private float spikeIndex = 240;
    private float spike2Index = 244;
    private float swordtrap1Index = 112;
    private float swordtrap2Index = 128;
    private float brownsawIndex = 208;

    private int trapFPU = 40; // 1 frame for 40 updates

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
                if (index == 224) {
                    g.drawImage(levelSprite[(int) chainsawIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    chainsawIndex += 1.0/trapFPU;
                    if (chainsawIndex > 232) chainsawIndex = 224;
                } else if (index == 192) {
                    g.drawImage(levelSprite[(int) fireIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    fireIndex += 1.0/trapFPU;
                    if (fireIndex > 196) fireIndex = 192;
                } else if (index == 240) {
                    g.drawImage(levelSprite[(int) spikeIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    spikeIndex += 1.0/(10 * trapFPU);
                    if (spikeIndex > 242) spikeIndex = 240;
                } else if (index == 244) {
                    g.drawImage(levelSprite[(int) spike2Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    spike2Index += 5.0/trapFPU;
                    if (spike2Index > 250) spike2Index = 244;
                } else if (index == 112) {
                    g.drawImage(levelSprite[(int) swordtrap1Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    swordtrap1Index += 1.0/trapFPU;
                    if (swordtrap1Index > 117) swordtrap1Index = 112;
                } else if (index == 128) {
                    g.drawImage(levelSprite[(int) swordtrap2Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    swordtrap2Index += 2.0/trapFPU;
                    if (swordtrap2Index > 132) swordtrap2Index = 128;
                } else if (index == 208) {
                    g.drawImage(levelSprite[(int) brownsawIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    brownsawIndex += 1.0/(5 * trapFPU);
                    if (brownsawIndex > 210) brownsawIndex = 208;
                } else if (index != -1) {
                    g.drawImage(levelSprite[index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                }

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
