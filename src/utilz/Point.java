package utilz;

public class Point {
    public int x; // Sử dụng public để truy cập trực tiếp (giảm overhead getter/setter)
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}