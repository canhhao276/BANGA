package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

/**
 * Lớp trừu tượng GameObject - đại diện cho mọi đối tượng trong game (người chơi, đạn, kẻ địch...).
 * Các lớp con phải cài đặt các phương thức abstract để cập nhật, vẽ, và kiểm tra trạng thái.
 */
public abstract class GameObject {
    // Tọa độ và kích thước
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // Trạng thái "dead" của đối tượng
    protected boolean isDead;

    /**
     * Tạo một đối tượng GameObject với vị trí và kích thước ban đầu.
     * @param x Tọa độ X
     * @param y Tọa độ Y
     * @param width Chiều rộng
     * @param height Chiều cao
     */
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isDead = false; // Mặc định đối tượng chưa "chết"
    }

    /**
     * Cập nhật trạng thái đối tượng mỗi khung hình (frame).
     */
    public abstract void update();

    /**
     * Vẽ đối tượng lên màn hình.
     * @param gc Đối tượng GraphicsContext để vẽ
     */
    public abstract void render(GraphicsContext gc);

    /**
     * Kiểm tra xem đối tượng có "chết" chưa (để loại bỏ).
     * @return true nếu đối tượng đã bị tiêu diệt hoặc cần xóa
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Đặt trạng thái "dead" cho đối tượng.
     * @param isDead true nếu đối tượng cần bị xóa
     */
    protected void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    /**
     * Lấy tọa độ X hiện tại.
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Lấy tọa độ Y hiện tại.
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Lấy bounding box để dùng cho phát hiện va chạm.
     * @return vùng bao của đối tượng
     */
    public Bounds getBounds() {
        return new Rectangle(
            x - width / 2,
            y - height / 2,
            width,
            height
        ).getBoundsInLocal();
    }

    /**
     * Lấy chiều rộng.
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Lấy chiều cao.
     * @return height
     */
    public double getHeight() {
        return height;
    }
}