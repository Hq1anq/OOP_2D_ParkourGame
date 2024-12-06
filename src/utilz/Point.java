package utilz;

public class Point {
    public float x; // Sử dụng public để truy cập trực tiếp (giảm overhead getter/setter)
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
}