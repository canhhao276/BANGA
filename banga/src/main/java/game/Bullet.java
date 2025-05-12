package game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet extends GameObject {

    public static final int WIDTH = 4;
    public static final int HEIGHT = 15;

    private static final double SPEED = 6;

    private boolean dead;
    private int damage = 17;


    public Bullet(double x, double y, int damage) {
        super(x, y, WIDTH, HEIGHT);
        this.dead = false;
        this.damage = damage; // Đảm bảo giá trị damage được truyền vào
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void update() {
        y -= SPEED;

        // Nếu vượt ra khỏi màn hình phía trên, đánh dấu là "dead"
        if (y + HEIGHT / 2 < 0) {
            this.dead = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
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

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean isDead() {
        return dead;
    }
}
