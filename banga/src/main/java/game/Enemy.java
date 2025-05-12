package game;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class Enemy extends GameObject {

    private static final double SHOOT_PROBABILITY = 0.001;

    protected static final int WIDTH = 42;
    protected static final int HEIGHT = 42;

    public static double SPEED = 0.5;

    private boolean dead;
    private Image enemyImage;

    public Enemy(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;

        //lấy ảnh lên
        this.enemyImage = new Image("enemy.png");
    }

    public void shoot(List<GameObject> gameObjects) {
        // Tạo một viên đạn mới tại vị trí của Enemy
        EnemyBullet bullet = new EnemyBullet(this.x, this.y + HEIGHT / 2);
        gameObjects.add(bullet);
    }
    
    @Override
    public void update() {
        y += SPEED; // Enemy di chuyển xuống dưới

        // Xác suất bắn đạn
        if (Math.random() < SHOOT_PROBABILITY) {
            shoot(SpaceShooter.getGameObjects());
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (enemyImage != null) {
            // Vẽ ảnh enemy
            gc.drawImage(enemyImage, x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        } else {
            // Nếu không tải được ảnh, vẽ hình chữ nhật thay thế
            gc.setFill(Color.RED);
            gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        }
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean isDead() {
        return dead;
    }
}
