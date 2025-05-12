package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnemyBullet extends GameObject {

    public static final int WIDTH = 4;
    public static final int HEIGHT = 20;

    private static final double SPEED = 3;

    private boolean dead;

    public EnemyBullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;
    }

    @Override
    public void update() {
        y += SPEED; // Di chuyển viên đạn xuống dưới

        // Kiểm tra nếu viên đạn ra khỏi khung hình
        if (y > SpaceShooter.HEIGHT) {
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
