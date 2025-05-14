package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

import java.util.List;

public class BossEnemy extends Enemy {

    private static final double SHOOT_PROBABILITY = 0.01;

    private int health;

    private static final int WIDTH = 60;
    static final int HEIGHT = 60;

    private double horizontalSpeed;

    // Để đảo chiều di chuyển ngang
    private boolean movingRight = true;
    private Image bossImage;

    public BossEnemy(double x, double y) {
        super(x, y);
        this.health = 50; // Tăng máu của boss
        this.horizontalSpeed = 2;
        this.width = WIDTH;
        this.height = HEIGHT;

        // Tải ảnh boss từ thư mục res
        try {
            this.bossImage = new Image("boss.png");
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh boss: " + e.getMessage());
            this.bossImage = null; // Đảm bảo không bị lỗi nếu ảnh không tải được
        }
    }

    @Override
    public void update() {
        y += SPEED / 2;  // Boss di chuyển chậm dọc

        // Giới hạn di chuyển dọc trong khung hình
        if (y + HEIGHT > SpaceShooter.HEIGHT) {
            y = SpaceShooter.HEIGHT - HEIGHT; // Giới hạn phía dưới
        }

        // Di chuyển ngang qua lại
        if (movingRight) {
            x += horizontalSpeed;
            if (x + WIDTH / 2 > SpaceShooter.WIDTH) {  // Giới hạn bên phải
                x = SpaceShooter.WIDTH - WIDTH / 2;
                movingRight = false; // Đổi hướng
            }
        } else {
            x -= horizontalSpeed;
            if (x - WIDTH / 2 < 0) {  // Giới hạn bên trái
                x = WIDTH / 2;
                movingRight = true; // Đổi hướng
            }
        }

        // Xác suất bắn đạn cao hơn
        if (Math.random() < SHOOT_PROBABILITY) {
            shoot(SpaceShooter.getGameObjects());
        }

        // Kiểm tra nếu máu <= 0
        if (health <= 0) {
            setDead(true);
        }
    }

    public void takeDamage(int damage) {
        health -= damage; // Trừ máu theo lượng sát thương
        if (health <= 0) {
            setDead(true);
        }
    }

    public void shoot(List<GameObject> newObjects) {
        // Bắn 3 viên đạn cùng lúc
        newObjects.add(new EnemyBullet(x, y + HEIGHT / 2));
        newObjects.add(new EnemyBullet(x - 10, y + HEIGHT / 2));
        newObjects.add(new EnemyBullet(x + 10, y + HEIGHT / 2));
    }

    @Override
    public void render(GraphicsContext gc) {
        if (bossImage != null) {
            // Vẽ ảnh boss
            gc.drawImage(bossImage, x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        } else {
            // Nếu không tải được ảnh, vẽ hình chữ nhật thay thế
            gc.setFill(Color.DARKMAGENTA);
            gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        }

        // Vẽ thanh máu bên trên Boss
        gc.setFill(Color.RED);
        gc.fillRect(x - 50, y - 60, 100, 10); // Thanh máu đỏ (nền)
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(x - 50, y - 60, 100 * ((double) health / 50), 10); // Thanh máu xanh (tỷ lệ máu)
    }
}
