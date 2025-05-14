package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PowerUpEffect extends GameObject {
    private double radius;
    private double maxRadius;
    private double opacity;
    private boolean expanding;

    public PowerUpEffect(double x, double y) {
        super(x, y, y, y); // Explicitly call the constructor of GameObject with parameters
        this.radius = 10; // Bán kính ban đầu
        this.maxRadius = 50; // Bán kính tối đa
        this.opacity = 1.0; // Độ trong suốt ban đầu
        this.expanding = true; // Hiệu ứng bắt đầu mở rộng
    }

    @Override
    public void update() {
        if (expanding) {
            radius += 2; // Tăng bán kính
            opacity -= 0.05; // Giảm độ trong suốt
            if (radius >= maxRadius) {
                expanding = false; // Dừng mở rộng
                setDead(true); // Đánh dấu hiệu ứng đã hoàn thành
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN.deriveColor(0, 1, 1, opacity)); // Màu xanh nhạt với độ trong suốt
        gc.fillOval(x - radius / 2, y - radius / 2, radius, radius); // Vẽ hình tròn
    }
}