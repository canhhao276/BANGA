package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Skeleton for PowerUp. Students must implement movement,
 * rendering, and state management.
 */
public class PowerUp extends GameObject {

    // Dimensions of the power-up
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;

    // Fall speed of the power-up
    private static final double SPEED = 1;

    // Flag indicating whether the power-up should be removed
    private boolean dead;

    private Image powerUpImage;

    /**
     * Constructs a PowerUp at the given position.
     * @param x initial X position
     * @param y initial Y position
     */
    public PowerUp(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;

        // Load the power-up image
        try {
            this.powerUpImage = new Image("powerup.png");
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh powerup: " + e.getMessage());
            this.powerUpImage = null; // Fallback in case the image cannot be loaded
        }
    }

    /**
     * Updates power-up position each frame.
     */
    @Override
    public void update() {
        y += SPEED; // Di chuyển power-up xuống dưới

        // Kiểm tra nếu power-up ra khỏi khung hình
        if (y > SpaceShooter.HEIGHT) {
            setDead(true); // Đánh dấu power-up là "dead"
        }
    }

    /**
     * Renders the power-up on the canvas.
     * @param gc the GraphicsContext to draw on
     */
    @Override
    public void render(GraphicsContext gc) {
        if (powerUpImage != null) {
            // Draw the power-up image
            gc.drawImage(powerUpImage, x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        } else {
            // Fallback: draw a yellow rectangle if the image is not available
            gc.setFill(Color.YELLOW);
            gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        }
    }

    /**
     * Returns the width of the power-up.
     * @return WIDTH
     */
    @Override
    public double getWidth() {
        // TODO: return width
        return WIDTH;
    }

    /**
     * Returns the height of the power-up.
     * @return HEIGHT
     */
    @Override
    public double getHeight() {
        // TODO: return height
        return HEIGHT;
    }

    /**
     * Checks whether the power-up should be removed.
     * @return true if dead
     */
    @Override
    public boolean isDead() {
        // TODO: return dead flag
        return dead;
    }

    /**
     * Marks this power-up as dead (to be removed).
     * @param dead true if should be removed
     */
    public void setDead(boolean dead) {
        // TODO: update dead flag
        this.dead = dead;
    }
}