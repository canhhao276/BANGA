package game;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
        showMainMenu(primaryStage);
    }

    private void showMainMenu(Stage primaryStage) {
        VBox rootStart = new VBox(20);
        rootStart.setAlignment(Pos.CENTER);

        // Thêm ảnh nền
        Image backgroundImage = new Image("backgroudMainMenu.png");
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        rootStart.setBackground(new Background(bgImage));

        // Tiêu đề
        Text title = new Text("Space Shooter");
        title.setFont(Font.font("Arial", 30));
        title.setFill(Color.WHITE);

        // Nút START
        Button startButton = new Button("START");
        startButton.setFont(Font.font("Arial", 20));
        startButton.setOnAction(e -> {
            resetGame(); // Đặt lại trạng thái trò chơi
            startGame(primaryStage); // Bắt đầu trò chơi mới
        });

        // Nút INSTRUCTIONS
        Button instructionsButton = new Button("INSTRUCTIONS");
        instructionsButton.setFont(Font.font("Arial", 20));
        instructionsButton.setOnAction(e -> showInstructions(primaryStage));

        // Nút QUIT
        Button quitButton = new Button("QUIT");
        quitButton.setFont(Font.font("Arial", 20));
        quitButton.setOnAction(e -> primaryStage.close());

        rootStart.getChildren().addAll(title, startButton, instructionsButton, quitButton);

        Scene startScene = new Scene(rootStart, WIDTH, HEIGHT);
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private void showInstructions(Stage primaryStage) {
        VBox rootInstructions = new VBox(20);
        rootInstructions.setAlignment(Pos.CENTER);
        rootInstructions.setStyle("-fx-background-color: black;");

        // Nội dung hướng dẫn
        Text instructionsText = new Text(
            "Use the < , ^, v and > keys or the arrow keys to move your spaceship.\n" +
            "Press SPACE to shoot bullets and destroy the enemies.\n" +
            "If an enemy reaches the bottom of the screen, you lose a life.\n" +
            "The game resets if you lose all lives.\n" +
            "Collect power-ups to increase your score.\n" +
            "Defeat the boss enemy to level up and increase the difficulty.\n" +
            "Good luck and have fun!"
        );
        instructionsText.setFont(Font.font("Arial", 20));
        instructionsText.setFill(Color.WHITE);
        instructionsText.setWrappingWidth(WIDTH - 50);

        // Nút "Back" để quay lại màn hình chính
        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Arial", 20));
        backButton.setOnAction(e -> showMainMenu(primaryStage));

        rootInstructions.getChildren().addAll(instructionsText, backButton);

        Scene instructionsScene = new Scene(rootInstructions, WIDTH, HEIGHT);
        primaryStage.setScene(instructionsScene);
        primaryStage.show();
    }

    private void showStartScreen(Stage primaryStage) {
        VBox rootStart = new VBox(20);
        rootStart.setAlignment(Pos.CENTER);

        // Thêm ảnh nền
        Image backgroundImage = new Image("backgroudMainMenu.png");
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        rootStart.setBackground(new Background(bgImage));

        // Tiêu đề
        Text title = new Text("Space Shooter");
        title.setFont(Font.font("Arial", 30));
        title.setFill(Color.WHITE);

        // Nút START
        Button startButton = new Button("START");
        startButton.setFont(Font.font("Arial", 20));
        startButton.setOnAction(e -> {
            // Đặt lại trạng thái trò chơi
            resetGame();
            startGame(primaryStage);
        });

        // Nút QUIT
        Button quitButton = new Button("QUIT");
        quitButton.setFont(Font.font("Arial", 20));
        quitButton.setOnAction(e -> primaryStage.close());

        rootStart.getChildren().addAll(title, startButton, quitButton);

        Scene startScene = new Scene(rootStart, WIDTH, HEIGHT);
        primaryStage.setScene(startScene);
        primaryStage.show();
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
                    isShooting = true; // Bắt đầu bắn
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
                    isShooting = false; // Ngừng bắn
            }
        });
    }

    private void update() {
        if (numLives <= 0) {
            System.out.println("Game Over! Returning to game over screen...");
            showGameOverScreen((Stage) root.getScene().getWindow());
            return;
        }

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
        // Tạo danh sách tạm thời để lưu các đối tượng cần đánh dấu là "dead"
        List<GameObject> toRemove = new ArrayList<>();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = gameObjects.get(i);
            for (int j = i + 1; j < gameObjects.size(); j++) {
                GameObject obj2 = gameObjects.get(j);

                // Sử dụng bounding box để kiểm tra va chạm
                if (obj1.getBounds().intersects(obj2.getBounds())) {
                    // Xử lý va chạm
                    if ((obj1 instanceof Bullet && obj2 instanceof Enemy) || 
                        (obj1 instanceof Enemy && obj2 instanceof Bullet)) {
                        // Đánh dấu cả Bullet và Enemy là "dead"
                        obj1.setDead(true);
                        obj2.setDead(true);
                        toRemove.add(obj1);
                        toRemove.add(obj2);
                        score += 10; // Tăng điểm
                    } else if (obj1 instanceof Player && obj2 instanceof Enemy) {
                        obj2.setDead(true);
                        toRemove.add(obj2);
                        numLives--;
                    } else if (obj1 instanceof Bullet && obj2 instanceof BossEnemy) {
                        Bullet bullet = (Bullet) obj1;
                        BossEnemy boss = (BossEnemy) obj2;

                        // Gây sát thương cho boss
                        boss.takeDamage(bullet.getDamage());
                        bullet.setDead(true);
                        toRemove.add(bullet);

                        // Nếu boss chết, tăng điểm và đánh dấu boss không còn tồn tại
                        if (boss.isDead()) {
                            score += 50;
                            bossExists = false;
                            toRemove.add(boss);
                        }
                    } 
                    // Kiểm tra nếu Player trúng đạn của Enemy hoặc BossEnemy
                    else if (obj1 instanceof Player && obj2 instanceof EnemyBullet) {
                        obj2.setDead(true);
                        toRemove.add(obj2);
                        numLives--;
                    }
                    // Kiểm tra nếu Player thu thập PowerUp
                    else if (obj1 instanceof Player && obj2 instanceof PowerUp) {
                        obj2.setDead(true);
                        toRemove.add(obj2);

                        // Kích hoạt PowerUp với thời gian hiệu lực 10 giây (10000ms)
                        player.activatePowerUp(10000);

                        System.out.println("PowerUp activated: Increased bullet count for 10 seconds!");
                    }
                }
            }
        }

        // Loại bỏ các đối tượng đã đánh dấu là "dead"
        gameObjects.removeAll(toRemove);
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

    private void resetToStartScreen(Stage primaryStage) {
        // Dừng game loop nếu đang chạy
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Đặt lại trạng thái trò chơi
        resetGame();

        // Hiển thị màn hình bắt đầu
        showMainMenu(primaryStage);
    }

    private void resetGame() {
        gameObjects.clear(); // Xóa tất cả các đối tượng trong trò chơi
        numLives = 3;        // Đặt lại số mạng
        score = 0;           // Đặt lại điểm số
        bossExists = false;  // Đặt lại trạng thái boss
        gameRunning = false; // Đặt lại trạng thái trò chơi
        isShooting = false;  // Đặt lại trạng thái bắn
    }

    private void showGameOverScreen(Stage primaryStage) {
        VBox rootGameOver = new VBox(20);
        rootGameOver.setAlignment(Pos.CENTER);
        rootGameOver.setStyle("-fx-background-color: black;");

        // Tiêu đề "Game Over"
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Arial", 50));
        gameOverText.setFill(Color.RED);

        // Nút "Quay về" để quay lại màn hình chính
        Button backButton = new Button("Quay về");
        backButton.setFont(Font.font("Arial", 20));
        backButton.setOnAction(e -> {
            resetGame(); // Đặt lại trạng thái trò chơi
            showMainMenu(primaryStage); // Hiển thị màn hình chính
        });

        rootGameOver.getChildren().addAll(gameOverText, backButton);

        Scene gameOverScene = new Scene(rootGameOver, WIDTH, HEIGHT);
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
    }
}