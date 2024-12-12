package utilz;

// import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class LoadSave {

    public static final String PLAYER_ATLAS = "player_spirites.png";
    public static final String LEVEL_ATLAS = "outside_sprites.png";
    public static final String LEVEL_ONE_CSV = "Level1.csv";
    public static final String FRONT_TREE = "front_tree.png";
    public static final String BEHIND_TREE = "behind_tree.png";
    public static final String EMPTY_HEART = "singleEmptyHeart.png";
    public static final String FULL_HEART = "singleFullHeart.png";

    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                System.out.println("Error: " + e);
            }
        }
        return img;
    }
    public static int[][] getLevelData() {
        InputStream is = LoadSave.class.getResourceAsStream("/" + LEVEL_ONE_CSV);
        int ROWS = 56, COLLUMS = 104;
        int[][] levelData = new int[ROWS][COLLUMS];

        try (Scanner inputStream = new Scanner(is)) {
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