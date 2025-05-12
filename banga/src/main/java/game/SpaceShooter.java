package game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpaceShooter extends Application {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;
    public static int numLives = 3;

    private int score;
    private boolean bossExists;
    private boolean gameRunning;

    private static List<GameObject> gameObjects = new ArrayList<>();
    private Player player;
    private Pane root;
    private Scene scene;
    private GraphicsContext gc;

    private Label scoreLabel;
    private Image heartImage;
    private Image backgroundImage;

    private AnimationTimer gameLoop;

    private int lastBossSpawnScore = 0;

    private  long lastShootTime = 0; // Thời gian lần bắn cuối cùng(sua)
    private  static final long SHOOT_DELAY = 200_000_000; // 300ms(đơn vị: nanoseconds)(sua)
    private boolean isShooting = false; // Biến theo dõi trạng thái bắn(sua)

    public static List<GameObject> getGameObjects() {
    return gameObjects;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Space Shooter");

        // Hiển thị màn hình hướng dẫn ngay khi chương trình khởi chạy
        showInstructionsScreen(primaryStage);
    }

    private void initGame() {
        gameObjects = new ArrayList<>();
        player = new Player(WIDTH / 2, HEIGHT - 50);
        gameObjects.add(player);
        score = 0;
        bossExists = false;
        gameRunning = true;
    }

    private void initEventHandlers() {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT : player.setMoveLeft(true); break;
                case RIGHT : player.setMoveRight(true); break;
                case UP : player.setMoveForward(true); break;
                case DOWN : player.setMoveBackward(true); break;
                case SPACE : 
                    isShooting = true; // Bắt đầu bắn(sua)
                break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT : player.setMoveLeft(false); break;
                case RIGHT : player.setMoveRight(false); break;
                case UP : player.setMoveForward(false); break;
                case DOWN : player.setMoveBackward(false); break;
                case SPACE :
                    isShooting = false; // Ngừng bắn(sua)
            }
        });
    }

    private void update() {
        if (!gameRunning) return;

        // Kiểm tra trạng thái bắn
        if (isShooting) {
            long currentTime = System.nanoTime();
            if (currentTime - lastShootTime >= SHOOT_DELAY) {
                player.shoot(gameObjects); // Bắn đạn
                lastShootTime = currentTime; // Cập nhật thời gian bắn
            }
        }

        // Cập nhật tất cả các đối tượng
        for (GameObject obj : gameObjects) {
            obj.update();

            // Nếu đối tượng là Player, giới hạn di chuyển trong khung hình
            if (obj instanceof Player) {
                Player player = (Player) obj;

                // Giới hạn di chuyển theo chiều ngang
                if (player.getX() < 0) {
                    player.setX(0); // Không cho vượt quá bên trái
                } else if (player.getX() + player.getWidth() > WIDTH) {
                    player.setX(WIDTH - player.getWidth()); // Không cho vượt quá bên phải
                }

                // Giới hạn di chuyển theo chiều dọc
                if (player.getY() < 0) {
                    player.setY(0); // Không cho vượt quá phía trên
                } else if (player.getY() + player.getHeight() > HEIGHT) {
                    player.setY(HEIGHT - player.getHeight()); // Không cho vượt quá phía dưới
                }
            }

            // Kiểm tra nếu Enemy vượt quá đáy khung hình
            if (obj instanceof Enemy && obj.getY() > HEIGHT) {
                obj.setDead(true); // Đánh dấu Enemy là "dead"
                numLives--; // Giảm số mạng
            }
        }

        // Kiểm tra va chạm
        checkCollisions();

        // Loại bỏ các đối tượng "dead"
        gameObjects.removeIf(GameObject::isDead);

        // Sinh thêm kẻ địch, boss, và power-up
        spawnEnemy();
        if (score >= lastBossSpawnScore + 400 && !bossExists) { // Kiểm tra nếu đạt mốc 400 điểm kế tiếp
            spawnBossEnemy();
            lastBossSpawnScore += 400; // Cập nhật mốc điểm tiếp theo
        }
        spawnPowerUp(); // Gọi phương thức sinh power-up

        // Cập nhật UI
        scoreLabel.setText("Score: " + score);

        // Kiểm tra nếu hết mạng
        if (numLives <= 0) {
            resetGame();
        }
    }

    private void render() {
        // Vẽ ảnh nền
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT); // Vẽ ảnh nền toàn màn hình
        } else {
            gc.setFill(Color.BLACK); // Nếu không tải được ảnh, vẽ nền màu đen
            gc.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Vẽ các đối tượng trong trò chơi
        for (GameObject obj : gameObjects) {
            obj.render(gc);
        }

        // Vẽ số mạng bằng hình ảnh trái tim
        drawLives();
    }

    private void drawLives() {
        // Vẽ chữ "Lives:" ở góc trên bên phải
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(20)); // Đặt font chữ
        gc.fillText("Lives:", WIDTH - 200, 30); // Dịch chữ "Lives:" sang trái một chút

        // Vẽ hình ảnh trái tim bên phải chữ "Lives:"
        if (heartImage != null) {
            for (int i = 0; i < numLives; i++) {
                gc.drawImage(heartImage, WIDTH - 140 + i * 40, 10, 20, 20); // Dịch trái tim sang trái một chút
            }
        } else {
            // Nếu không tải được ảnh, hiển thị số mạng bằng chữ
            gc.fillText(String.valueOf(numLives), WIDTH - 140, 30);
        }
    }

    private void spawnPowerUp() {
        // Xác suất xuất hiện power-up (ví dụ: 0.002 = 0.2%)
        double SPAWN_PROBABILITY = 0.0005;

        if (Math.random() < SPAWN_PROBABILITY) {
            // Tạo power-up tại một vị trí ngẫu nhiên trên màn hình
            double x = Math.random() * (WIDTH - PowerUp.WIDTH) + PowerUp.WIDTH / 2;
            double y = -PowerUp.HEIGHT / 2; // Xuất hiện từ phía trên màn hình
            gameObjects.add(new PowerUp(x, y));
        }
    }

    private void spawnEnemy() {
        // Tăng xác suất xuất hiện kẻ địch dựa trên điểm số
        double baseProbability = 0.004; // Xác suất cơ bản
        double difficultyMultiplier = 1 + (score / 400) * 0.4; // Tăng độ khó mỗi 500 điểm
        double spawnProbability = baseProbability * difficultyMultiplier;

        if (Math.random() < spawnProbability) {
            double x = Math.random() * (WIDTH - Enemy.WIDTH) + Enemy.WIDTH / 2;
            gameObjects.add(new Enemy(x, -Enemy.HEIGHT / 2));
        }
    }

    private void spawnBossEnemy() {
        bossExists = true;
        gameObjects.add(new BossEnemy(WIDTH / 2, -BossEnemy.HEIGHT / 2));
    }
    
    private void checkCollisions() {
        for (GameObject obj1 : gameObjects) {
            for (GameObject obj2 : gameObjects) {
                if (obj1 != obj2) {
                    // Kiểm tra va chạm dựa trên khoảng cách giữa tâm của các đối tượng
                    double dx = obj1.getX() + obj1.getWidth() / 2 - (obj2.getX() + obj2.getWidth() / 2);
                    double dy = obj1.getY() + obj1.getHeight() / 2 - (obj2.getY() + obj2.getHeight() / 2);
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    // Xác định bán kính va chạm (giảm một chút để tránh va chạm sớm)
                    double collisionRadius = (Math.min(obj1.getWidth(), obj1.getHeight()) +
                                            Math.min(obj2.getWidth(), obj2.getHeight())) / 2 * 0.8;

                    if (distance < collisionRadius) {
                        // Xử lý va chạm
                        if ((obj1 instanceof Bullet && obj2 instanceof Enemy) || 
                            (obj1 instanceof Enemy && obj2 instanceof Bullet)) {
                            obj1.setDead(true); // Đánh dấu Bullet là "dead"
                            obj2.setDead(true); // Đánh dấu Enemy là "dead"
                            score += 10; // Tăng điểm
                        } else if (obj1 instanceof Player && obj2 instanceof Enemy) {
                            obj2.setDead(true);
                            numLives--;
                        } else if (obj1 instanceof Bullet && obj2 instanceof BossEnemy) {
                            Bullet bullet = (Bullet) obj1;
                            BossEnemy boss = (BossEnemy) obj2;

                        // Gây sát thương cho boss
                        boss.takeDamage(17); // Gây sát thương 1 cho Boss    (sua)
                        bullet.setDead(true); // Đánh dấu viên đạn là "chết"

                            // Nếu boss chết, tăng điểm và đánh dấu boss không còn tồn tại
                            if (boss.isDead()) {
                                score += 50;
                                bossExists = false;
                            }
                        } 
                        // Kiểm tra nếu Player trúng đạn của Enemy hoặc BossEnemy
                        else if (obj1 instanceof Player && obj2 instanceof EnemyBullet) {
                            obj2.setDead(true); // Đánh dấu viên đạn là "dead"
                            numLives--; // Giảm số mạng
                        }
                        // Kiểm tra nếu Player thu thập PowerUp
                        else if (obj1 instanceof Player && obj2 instanceof PowerUp) {
                            obj2.setDead(true); // Đánh dấu PowerUp là "dead"

                            // Nếu số mạng hiện tại là 4, luôn tăng số đạn bắn
                            if (numLives == 4) {
                                player.increaseBulletCount(); // Tăng số đạn bắn
                                System.out.println("PowerUp: Increased bullet count!");
                            } else {
                                // Random hiệu ứng: tăng số đạn bắn hoặc tăng số mạng
                                if (Math.random() < 0.5) {
                                    player.increaseBulletCount(); // Tăng số đạn bắn
                                    System.out.println("PowerUp: Increased bullet count!");
                                } else {
                                    numLives++; // Tăng số mạng
                                    System.out.println("PowerUp: Extra life gained!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

private void showStartScreen(Stage primaryStage) {
    VBox rootStart = new VBox(20);
    rootStart.setStyle("-fx-background-color: #000000;"); // Đặt màu nền thành đen
    rootStart.setAlignment(Pos.CENTER);

    // Đặt hình nền
    Image startImage = new Image("start.png");
    ImageView startImageView = new ImageView(startImage);
    startImageView.setFitWidth(WIDTH); // Đặt chiều rộng bằng chiều rộng màn hình
    startImageView.setFitHeight(HEIGHT); // Đặt chiều cao bằng chiều cao màn hình
    startImageView.setPreserveRatio(false); // Không giữ tỷ lệ gốc để ảnh vừa khung

    // Tạo nút START
    Button startButton = new Button("START");
    startButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-font-size: 20px; -fx-background-color: #45a049; -fx-text-fill: white;"));
    startButton.setOnMouseExited(e -> startButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;"));
    startButton.setOnAction(e -> startGame(primaryStage)); // Gắn sự kiện để bắt đầu trò chơi

    // Tạo nút INSTRUCTIONS
    Button instructionsButton = new Button("INSTRUCTIONS");
    instructionsButton.setStyle("-fx-font-size: 20px; -fx-background-color: #2196F3; -fx-text-fill: white;");
    instructionsButton.setOnMouseEntered(e -> instructionsButton.setStyle("-fx-font-size: 20px; -fx-background-color: #1e88e5; -fx-text-fill: white;"));
    instructionsButton.setOnMouseExited(e -> instructionsButton.setStyle("-fx-font-size: 20px; -fx-background-color: #2196F3; -fx-text-fill: white;"));
    instructionsButton.setOnAction(e -> showInstructionsScreen(primaryStage));

    // Tạo nút QUIT
    Button quitButton = new Button("QUIT");
    quitButton.setStyle("-fx-font-size: 20px; -fx-background-color: #f44336; -fx-text-fill: white;");
    quitButton.setOnMouseEntered(e -> quitButton.setStyle("-fx-font-size: 20px; -fx-background-color: #d32f2f; -fx-text-fill: white;"));
    quitButton.setOnMouseExited(e -> quitButton.setStyle("-fx-font-size: 20px; -fx-background-color: #f44336; -fx-text-fill: white;"));
    quitButton.setOnAction(e -> System.exit(0));

    // Thêm các nút vào giao diện
    rootStart.getChildren().addAll(startButton, instructionsButton, quitButton);

    Scene startScene = new Scene(rootStart, WIDTH, HEIGHT);
    primaryStage.setScene(startScene);
    primaryStage.show();
}

    private void showInstructionsScreen(Stage primaryStage) {
        VBox rootInstructions = new VBox(20);
        rootInstructions.setAlignment(Pos.CENTER);
        rootInstructions.setStyle("-fx-background-color: black;");

        // Đặt hình nền
        Image instructionsImage = new Image("instructions.png");
        ImageView instructionsImageView = new ImageView(instructionsImage);
        instructionsImageView.setFitWidth(WIDTH); // Đặt chiều rộng bằng chiều rộng màn hình
        instructionsImageView.setFitHeight(HEIGHT); // Đặt chiều cao bằng chiều cao màn hình
        instructionsImageView.setPreserveRatio(false); // Không giữ tỷ lệ gốc để ảnh vừa khung

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showStartScreen(primaryStage));

        rootInstructions.getChildren().addAll(instructionsImageView, backButton);

        Scene instructionsScene = new Scene(rootInstructions, WIDTH, HEIGHT);
        primaryStage.setScene(instructionsScene);
        primaryStage.show();
    }

    private void startGame(Stage primaryStage) {
        root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT); // Đặt kích thước Canvas theo khung hình mới
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);
        root.getChildren().add(scoreLabel);

        scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Tải ảnh nền
        try {
            backgroundImage = new Image("background.png");
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh nền: " + e.getMessage());
            backgroundImage = null; // Fallback nếu không tải được ảnh
        }

        // Tải ảnh trái tim
        try {
            heartImage = new Image("heart.png");
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh heart: " + e.getMessage());
            heartImage = null; // Fallback nếu không tải được ảnh
        }

        initGame();
        initEventHandlers();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
    }

    // Removed duplicate start(Stage primaryStage) method to resolve the error.

    private void resetGame() {
        gameRunning = false;
        if (gameLoop != null) {
            gameLoop.stop(); // Dừng vòng lặp trò chơi
        }

        System.out.println("Game Over! Final Score: " + score);

        // Hiển thị màn hình bắt đầu
        Stage primaryStage = (Stage) root.getScene().getWindow();
        showStartScreen(primaryStage);
    }
}