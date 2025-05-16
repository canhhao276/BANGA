package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnemyBullet extends GameObject {

    public static final int WIDTH = 4;
    public static final int HEIGHT = 20;

    private static final double SPEED = 5;

    private double dx = 0; // Hướng di chuyển theo trục X
    private double dy = SPEED; // Hướng di chuyển theo trục Y (mặc định là xuống dưới)

    private boolean dead;

    public EnemyBullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;
    }

    public void setDirection(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        x += dx; // Cập nhật vị trí theo trục X
        y += dy; // Cập nhật vị trí theo trục Y

        // Kiểm tra nếu viên đạn ra khỏi khung hình
        if (y > SpaceShooter.HEIGHT || x < 0 || x > SpaceShooter.WIDTH) {
            setDead(true); // Đánh dấu viên đạn là "dead"
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
