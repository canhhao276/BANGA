package game;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player extends GameObject {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 80;
    private static final double SPEED = 3;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveForward;
    private boolean moveBackward;

    private int health;
    private boolean dead;

    private Image playerImage;

    private int bulletCount = 1;
    private long powerUpEndTime = 0; // Thời gian kết thúc hiệu lực PowerUp
    private boolean powerUpActive = false; // Trạng thái PowerUp

    // Hiệu ứng nhấp nháy
    private boolean isBlinking; // Trạng thái nhấp nháy
    private double blinkOpacity; // Độ trong suốt khi nhấp nháy
    private int blinkTimer; // Bộ đếm thời gian nhấp nháy
    private static final int BLINK_DURATION = 60; // Thời gian nhấp nháy (60 frame ~ 1 giây)

    public Player(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.health = 3; // mặc định 3 mạng
        this.dead = false;

        // Tải ảnh lên
        this.playerImage = new Image("player.png");

        // Khởi tạo hiệu ứng nhấp nháy
        this.isBlinking = false;
        this.blinkOpacity = 1.0;
        this.blinkTimer = 0;
    }

    @Override
    public void update() {
        // Nếu đang nhấp nháy, giảm thời gian nhấp nháy
        if (isBlinking) {
            blinkTimer--;
            blinkOpacity = (blinkTimer / 5 % 2 == 0) ? 0.5 : 1.0; // Nhấp nháy mờ đi
            if (blinkTimer <= 0) {
                isBlinking = false; // Kết thúc nhấp nháy
                blinkOpacity = 1.0; // Trở lại trạng thái bình thường
            }
        }

        // Di chuyển Player
        if (moveLeft) x -= SPEED;
        if (moveRight) x += SPEED;
        if (moveForward) y -= SPEED;
        if (moveBackward) y += SPEED;

        // Giới hạn trong màn hình
        x = Math.max(0, Math.min(SpaceShooter.WIDTH - getWidth(), x));
        y = Math.max(0, Math.min(SpaceShooter.HEIGHT - getHeight(), y));

        // Kiểm tra nếu PowerUp hết hiệu lực
        if (powerUpActive && System.currentTimeMillis() > powerUpEndTime) {
            deactivatePowerUp(); // Hủy hiệu ứng PowerUp
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setGlobalAlpha(blinkOpacity); // Đặt độ trong suốt
        if (playerImage != null) {
            // Vẽ ảnh player từ góc trên bên trái
            gc.drawImage(playerImage, x, y, WIDTH, HEIGHT);
        } else {
            // Nếu không tải được ảnh, vẽ hình chữ nhật thay thế
            gc.setFill(Color.CYAN);
            gc.fillRect(x, y, WIDTH, HEIGHT);
        }
        gc.setGlobalAlpha(1.0); // Khôi phục độ trong suốt bình thường
    }

    public void triggerBlink() {
        isBlinking = true;
        blinkTimer = BLINK_DURATION; // Đặt thời gian nhấp nháy
    }

    public void activatePowerUp(long duration) {
        powerUpActive = true; // Kích hoạt PowerUp
        powerUpEndTime = System.currentTimeMillis() + duration; // Đặt thời gian kết thúc hiệu lực
        bulletCount++; // Tăng số lượng đạn bắn
    }

    private void deactivatePowerUp() {
        powerUpActive = false; // Hủy kích hoạt PowerUp
        bulletCount = Math.max(1, bulletCount - 1); // Giảm số lượng đạn bắn về mặc định
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
            double offset = (i - (bulletCount - 1) / 2.0) * 10; // Tính toán vị trí đạn lệch
            double bulletX = x + getWidth() / 2 + offset; // Tọa độ X (điều chỉnh không trừ 5)
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

    public void setX(double i) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setX'");
    }
}