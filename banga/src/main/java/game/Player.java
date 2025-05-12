package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;
import javafx.scene.image.Image;

public class Player extends GameObject {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 80;
    private static final double SPEED = 2;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveForward;
    private boolean moveBackward;

    private int health;
    private boolean dead;

    private Image playerImage;

    private int bulletCount = 1;

    public void increaseBulletCount() {
        bulletCount++; // Tăng số lượng đạn bắn
    } 

    public Player(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.health = 3; // mặc định 3 mạng
        this.dead = false;

        //tải ảnh lên
        this.playerImage = new Image("player.png");
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if (this.health <= 0) {
            this.dead = true;
        }
    }

    @Override
    public void update() {
        if (moveLeft) x -= SPEED;
        if (moveRight) x += SPEED;
        if (moveForward) y -= SPEED;
        if (moveBackward) y += SPEED;

        // Giới hạn trong màn hình (tính từ góc trên bên trái)
        x = Math.max(0, Math.min(SpaceShooter.WIDTH - getWidth(), x)); // Giới hạn theo chiều ngang
        y = Math.max(0, Math.min(SpaceShooter.HEIGHT - getHeight(), y)); // Giới hạn theo chiều dọc
    }

    @Override
    public void render(GraphicsContext gc) {
        if (playerImage != null) {
            // Vẽ ảnh player từ góc trên bên trái
            gc.drawImage(playerImage, x, y, WIDTH, HEIGHT);
        } else {
            // Nếu không tải được ảnh, vẽ hình chữ nhật thay thế
            gc.setFill(Color.CYAN);
            gc.fillRect(x, y, WIDTH, HEIGHT);
        }
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void setMoveForward(boolean moveForward) {
        this.moveForward = moveForward;
    }

    public void setMoveBackward(boolean moveBackward) {
        this.moveBackward = moveBackward;
    }

    public void shoot(List<GameObject> gameObjects) {
        for (int i = 0; i < bulletCount; i++) {
            double offset = (i - bulletCount / 2.0) * 10; // Tính toán vị trí đạn lệch
            double bulletX = x + getWidth() / 2 + offset; // Tọa độ X của viên đạn (giữa Player)
            double bulletY = y; // Tọa độ Y của viên đạn (phía trên Player)
            gameObjects.add(new Bullet(bulletX, bulletY, 1)); // Thêm tham số damage (ví dụ: 1)
        }
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean isDead() {
        return dead;
    }
}



