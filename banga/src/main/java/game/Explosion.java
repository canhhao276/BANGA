package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Explosion extends GameObject {

    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;
    private static final long DURATION = 500; // 500ms = 0.5 giây

    private Image explosionImage;
    private long startTime;
    private boolean isDead = false;

    public Explosion(double x, double y) {
        super(x, y, WIDTH, HEIGHT);

        try {
            explosionImage = new Image(getClass().getResourceAsStream("/explosion.png"));
            if (explosionImage.isError()) {
                System.out.println("❌ Không tải được ảnh explosion.png!");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi load ảnh explosion.png: " + e.getMessage());
        }

        startTime = System.currentTimeMillis();
    }

    @Override
    public void update() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > DURATION) {
            isDead = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        long elapsed = System.currentTimeMillis() - startTime;
        double progress = Math.min((double) elapsed / DURATION, 1.0);

        double scale = 1.0 + 2.0 * progress; // nổ to dần
        double opacity = 1.0 - progress;     // mờ dần

        double drawWidth = WIDTH * scale;
        double drawHeight = HEIGHT * scale;

        // Kiểm tra ảnh có hợp lệ không
        if (explosionImage == null || explosionImage.getWidth() == 0) {
            System.out.println("⚠️ Ảnh explosion.png chưa được load đúng.");
            return;
        }

        gc.save();
        gc.setGlobalAlpha(opacity);

        gc.drawImage(
            explosionImage,
            x - drawWidth / 2,
            y - drawHeight / 2,
            drawWidth,
            drawHeight
        );

        gc.setGlobalAlpha(1.0);
        gc.restore();
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
    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }
}
