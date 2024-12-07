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
    // public static final String LEVEL_ONE_DATA = "level_one_data.png";
    public static final String LEVEL_ONE_CSV = "Level1.csv";

    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }
    // public static int[][] getLevelData() {
    //     BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
    //     int[][] levelData = new int[img.getHeight()][img.getWidth()];

    //     for (int i = 0; i < img.getHeight(); i++)
    //         for (int j = 0; j < img.getWidth(); j++) {
    //             Color color = new Color(img.getRGB(j, i));
    //             int value = color.getRed();
    //             if (value >= 48)
    //                 value = 0;
    //             levelData[i][j] = value;
    //         }
    //     return levelData;
    // }
    public static int[][] getLevelData() {
        InputStream is = LoadSave.class.getResourceAsStream("/" + LEVEL_ONE_CSV);
        int ROWS = 28, COLLUMS = 52;
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
            e.printStackTrace();
        }

        return levelData;
    }
}