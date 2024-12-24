package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import main.Game;
import static main.Game.TILE_SIZE;
import static utilz.Constants.ObjectConstants.BREAKABLE_PLATFORM;
import static utilz.Constants.ObjectConstants.BROWNSAW;
import static utilz.Constants.ObjectConstants.CHAINSAW;
import static utilz.Constants.ObjectConstants.FIRE;
import static utilz.Constants.ObjectConstants.GetSpriteAmount;
import static utilz.Constants.ObjectConstants.INVISIBLE_TILE;
import static utilz.Constants.ObjectConstants.SHOOTER;
import static utilz.Constants.ObjectConstants.SPIKE;
import static utilz.Constants.ObjectConstants.SWORDTRAP1;
import static utilz.Constants.ObjectConstants.SWORDTRAP2;
import utilz.LoadSave;

@SuppressWarnings("FieldMayBeFinal")

// ***IMPORTANT

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private Level level;
    private int currentLevel = 1;

    private float chainsawIndex = CHAINSAW;
    private float fireIndex = FIRE;
    private float spikeIndex = SPIKE;
    private float swordtrap1Index = SWORDTRAP1;
    private float swordtrap2Index = SWORDTRAP2;
    private float brownsawIndex = BROWNSAW;
    // private float cellSpikeIndex = CELL_SPIKE;

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
                if (index == CHAINSAW) {
                    g.drawImage(levelSprite[(int) chainsawIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    chainsawIndex += 1.0/trapFPU;
                    if (chainsawIndex > CHAINSAW + GetSpriteAmount(CHAINSAW)) chainsawIndex = CHAINSAW;
                } else if (index == FIRE) {
                    g.drawImage(levelSprite[(int) fireIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    fireIndex += 1.0/trapFPU;
                    if (fireIndex > FIRE + GetSpriteAmount(FIRE)) fireIndex = FIRE;
                } else if (index == SPIKE) {
                    g.drawImage(levelSprite[(int) spikeIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    spikeIndex += 1.0/(2 * trapFPU);
                    if (spikeIndex > SPIKE + GetSpriteAmount(SPIKE)) spikeIndex = SPIKE;
                } else if (index == SWORDTRAP1) {
                    g.drawImage(levelSprite[(int) swordtrap1Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    swordtrap1Index += 1.0/trapFPU;
                    if (swordtrap1Index > SWORDTRAP1 + GetSpriteAmount(SWORDTRAP1)) swordtrap1Index = SWORDTRAP1;
                } else if (index == SWORDTRAP2) {
                    g.drawImage(levelSprite[(int) swordtrap2Index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    swordtrap2Index += 2.0/trapFPU;
                    if (swordtrap2Index > SWORDTRAP2 + GetSpriteAmount(SWORDTRAP2)) swordtrap2Index = SWORDTRAP2;
                } else if (index == BROWNSAW) {
                    g.drawImage(levelSprite[(int) brownsawIndex], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                    brownsawIndex += 1.0/(5 * trapFPU);
                    if (brownsawIndex > BROWNSAW + GetSpriteAmount(BROWNSAW)) brownsawIndex = BROWNSAW;
                } else if (index == BREAKABLE_PLATFORM) {
                    g.drawImage(levelSprite[INVISIBLE_TILE], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                } else if (index != -1 && index != SHOOTER && (index < 304 || index > 336)) {
                    g.drawImage(levelSprite[index], TILE_SIZE * j - xLevelOffset, TILE_SIZE * i - yLevelOffset, TILE_SIZE, TILE_SIZE, null);
                }

            }
    }
    
    public void loadLevel(int levelNumber) {
        currentLevel = levelNumber;
        level = new Level(currentLevel);
        game.getPlayer().loadLevelData(level.getLevelData());
        game.getObjectManager().resetAllObjects();
        game.getObjectManager().loadObjects(level);
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
