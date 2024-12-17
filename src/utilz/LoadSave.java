package utilz;

// import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import levels.Level;

public class LoadSave {

    public static final String PLAYER_ATLAS = "images/player_spirites.png";
    public static final String LEVEL_ATLAS = "images/outside_sprites.png";
    public static final String FRONT_TREE = "images/front_tree.png";
    public static final String BEHIND_TREE = "images/behind_tree.png";
    public static final String FRONT_ROCK = "images/front_rock.png";
    public static final String BEHIND_ROCK = "images/behind_rock.png";
    public static final String EMPTY_HEART = "images/singleEmptyHeart.png";
    public static final String FULL_HEART = "images/singleFullHeart.png";

    public static final String LEVEL_ONE_CSV = "Level1.csv";
    public static final String LEVEL_TWO_CSV = "Level2.csv";

    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        File file = new File("res/" + fileName);

        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        return img;
    }
    public static int[][] getLevelData(Level level) {
        File file = null;
        if (level.getId() == 1) {
            file = new File("res/" + LEVEL_ONE_CSV);
        } else if (level.getId() == 2) {
            file = new File("res/" + LEVEL_TWO_CSV);
        }
        int ROWS = level.getLevelTileHeight();
        int COLLUMS = level.getLevelTileWide();
        int[][] levelData = new int[ROWS][COLLUMS];

        try (Scanner inputStream = new Scanner(file)) {
            int row = 0;
            while (inputStream.hasNextLine() && row < ROWS) {
                String data = inputStream.nextLine();
                String[] values = data.split(",");
                for (int col = 0; col < values.length && col < COLLUMS; col++) {
                    levelData[row][col] = Integer.parseInt(values[col]);
                }
                row++;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return levelData;
    }
}