package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PowerUpEffect extends GameObject {
    private double radius;
    private double maxRadius;
    private double opacity;
    private boolean expanding;
    private List<Double> expirationTimes; // Danh sách thời điểm hết hạn của từng power-up
    private boolean defaultState; // Trạng thái mặc định (ví dụ: 1 viên đạn)

    public PowerUpEffect(double x, double y) {
        super(x, y, y, y); // Explicitly call the constructor of GameObject with parameters
        this.radius = 10; // Bán kính ban đầu
        this.maxRadius = 50; // Bán kính tối đa
        this.opacity = 1.0; // Độ trong suốt ban đầu
        this.expanding = true; // Hiệu ứng bắt đầu mở rộng
        this.expirationTimes = new ArrayList<>(); // Khởi tạo danh sách thời điểm hết hạn
        this.defaultState = true; // Bắt đầu ở trạng thái mặc định
        addPowerUp(10.0); // Thêm power-up với thời gian tồn tại 10 giây
    }

    public void addPowerUp(double duration) {
        expirationTimes.add(System.currentTimeMillis() / 1000.0 + duration); // Thêm thời điểm hết hạn mới
        defaultState = false; // Khi có power-up, không còn ở trạng thái mặc định
    }

    @Override
    public void update() {
        if (expanding) {
            radius += 2; // Tăng bán kính
            opacity -= 0.05; // Giảm độ trong suốt
            if (radius >= maxRadius) {
                expanding = false; // Dừng mở rộng
            }
        }

        // Kiểm tra và loại bỏ các power-up đã hết hạn
        double currentTime = System.currentTimeMillis() / 1000.0;
        Iterator<Double> iterator = expirationTimes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() <= currentTime) {
                iterator.remove(); // Loại bỏ power-up đã hết hạn
            }
        }

        // Nếu không còn power-up nào, trở về trạng thái mặc định
        if (expirationTimes.isEmpty()) {
            defaultState = true; // Trở về trạng thái mặc định
            setDead(true); // Đánh dấu là "dead" nếu cần
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN.deriveColor(0, 1, 1, opacity)); // Màu xanh nhạt với độ trong suốt
        gc.fillOval(x - radius / 2, y - radius / 2, radius, radius); // Vẽ hình tròn
    }

    public boolean isDefaultState() {
        return defaultState; // Trả về trạng thái mặc định
    }
}