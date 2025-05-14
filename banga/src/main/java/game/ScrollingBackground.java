package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ScrollingBackground {
    private Image backgroundImage;
    private double backgroundY = 0; // Vị trí Y của ảnh nền
    private double backgroundSpeed = 1; // Tốc độ di chuyển của ảnh nền
    private final int width;
    private final int height;

    public ScrollingBackground(String imagePath, int width, int height) {
        this.backgroundImage = new Image(imagePath);
        this.width = width;
        this.height = height;
    }

    public void update() {
        // Cập nhật vị trí ảnh nền
        backgroundY += backgroundSpeed;
        if (backgroundY >= height) {
            backgroundY = 0; // Lặp lại ảnh nền
        }
    }

    public void render(GraphicsContext gc) {
        // Vẽ ảnh nền
        gc.drawImage(backgroundImage, 0, backgroundY, width, height); // Vẽ ảnh nền ở vị trí hiện tại
        gc.drawImage(backgroundImage, 0, backgroundY - height, width, height); // Vẽ ảnh nền phía trên
    }
}