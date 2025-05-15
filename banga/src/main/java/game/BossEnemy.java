package game;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class BossEnemy extends Enemy {

    private static final double SHOOT_PROBABILITY = 0.005;

    private int health;
    private int hitCount = 0; // Số lần boss bị trúng đạn
    private static final int MAX_HITS = 3; // Số lần trúng đạn để boss chết

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
        hitCount++; // Tăng số lần boss bị trúng đạn
        System.out.println("Boss bị trúng đạn! Số lần trúng: " + hitCount); // Debug để kiểm tra số lần trúng đạn

        if (hitCount >= MAX_HITS) {
            setDead(true); // Boss chỉ chết khi bị trúng đạn đủ 3 lần
            System.out.println("Boss đã chết!");
        }
    }

    public void shoot(List<GameObject> newObjects) {
        // Bắn 1 viên đạn ở giữa
        newObjects.add(new EnemyBullet(x, y + HEIGHT / 2));
        
        // Bắn 2 viên đạn tỏa ra 2 bên
        EnemyBullet leftBullet = new EnemyBullet(x - 10, y + HEIGHT / 2);
        leftBullet.setDirection(-0.5, 2); // Đạn bên trái bay chéo sang trái
        newObjects.add(leftBullet);

        EnemyBullet rightBullet = new EnemyBullet(x + 10, y + HEIGHT / 2);
        rightBullet.setDirection(0.5, 2); // Đạn bên phải bay chéo sang phải
        newObjects.add(rightBullet);
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
