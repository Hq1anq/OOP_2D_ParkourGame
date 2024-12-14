package levels;

import utilz.LoadSave;
import utilz.Point;

@SuppressWarnings("FieldMayBeFinal")

public class Level {

    private int id;
    private int[][] levelData;
    private int levelTileHeight;
    private int levelTileWide;
    private Point playerSpawn;
    private Point winPos;

    public Level(int id) {
        this.id = id;
        loadLevel();
    }
    private void loadLevel() {
        if (id == 1) {
            levelTileHeight = 56;
            levelTileWide = 104;
            playerSpawn = new Point(4800, 288);
            // winPos = new Point(4730, 288);
            winPos = new Point(121, 2208);
        } else if (id == 2) {
            levelTileHeight = 56;
            levelTileWide = 104;
            // playerSpawn = new Point(4700, 1344);
            playerSpawn = new Point(126, 624);
            winPos = new Point(4795, 1344);
        }
        levelData = LoadSave.getLevelData(this);
    }
    public int getId() {
        return id;
    }
    public int getSpriteIndex(int x, int y) {
        return levelData[y][x];
    }
    public int getLevelTileHeight() {
        return levelTileHeight;
    }
    public int getLevelTileWide() {
        return levelTileWide;
    }
    public int[][] getLevelData() {
        return levelData;
    }
    public Point getPlayerSpawn() {
        return playerSpawn;
    }
    public Point getWinPos() {
        return winPos;
    }
}