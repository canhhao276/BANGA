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

    private long lastShootTime = 0; // Thời gian lần bắn cuối cùng(sua)
    private static final long SHOOT_DELAY = 200_000_000; // 300ms(đơn vị: nanoseconds)(sua)
    private boolean isShooting = false; // Biến theo dõi trạng thái bắn(sua)

    private double backgroundY = 0; // Vị trí Y của ảnh nền
    private double backgroundSpeed = 1; // Tốc độ di chuyển của ảnh nền

    private ScrollingBackground scrollingBackground;

    private double mouseX = -1, mouseY = -1; // Thêm biến lưu vị trí chuột

    public static List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Space Shooter");

        // Khởi tạo background động
        scrollingBackground = new ScrollingBackground("background.png", WIDTH, HEIGHT);

        // Hiển thị màn hình chính
        showMainMenu(primaryStage);
    }

    private void showMainMenu(Stage primaryStage) {
        Pane rootStart = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        rootStart.getChildren().add(canvas);

        // Lưu thông tin các nút để xử lý hover và click
        class MenuButton {
            String label;
            double x, y, width, height;

            MenuButton(String label, double x, double y, double width, double height) {
                this.label = label;
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }

            boolean isHovered(double mx, double my) {
                return mx >= x && mx <= x + width && my >= y && my <= y + height;
            }
        }

        double btnWidth = 260, btnHeight = 50, arc = 25;
        double startY = HEIGHT / 2 - 70;
        MenuButton[] buttons = {
            new MenuButton("START", WIDTH / 2 - btnWidth / 2, startY, btnWidth, btnHeight),
            new MenuButton("INSTRUCTIONS", WIDTH / 2 - btnWidth / 2, startY + 70, btnWidth, btnHeight),
            new MenuButton("QUIT", WIDTH / 2 - btnWidth / 2, startY + 140, btnWidth, btnHeight)
        };

        // Bắt sự kiện di chuyển chuột để cập nhật mouseX, mouseY
        canvas.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        // Tạo AnimationTimer để cập nhật và vẽ background động + menu
        AnimationTimer menuLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                scrollingBackground.update();
                scrollingBackground.render(gc);

                // Vẽ tiêu đề
                gc.setFill(Color.CYAN);
                gc.setFont(Font.font("Impact", 64)); // Đổi sang Impact, size lớn
                gc.fillText("Space Shooter🚀", WIDTH / 2 - 210, HEIGHT / 2 - 150);

                // Vẽ các nút với hiệu ứng hover
                for (int i = 0; i < buttons.length; i++) {
                    MenuButton btn = buttons[i];
                    boolean hovered = btn.isHovered(mouseX, mouseY);
                    double scale = hovered ? 0.93 : 1.0;
                    double w = btn.width * scale;
                    double h = btn.height * scale;
                    double x = btn.x + (btn.width - w) / 2;
                    double y = btn.y + (btn.height - h) / 2;

                    // Đổi màu nền từng nút
                    Color bgColor;
                    switch (i) {
                        case 0: bgColor = Color.rgb(0, 180, 80, 0.85); break;        // START: xanh lá
                        case 1: bgColor = Color.rgb(255, 140, 0, 0.85); break;       // INSTRUCTIONS: cam
                        case 2: bgColor = Color.rgb(200, 40, 40, 0.85); break;       // QUIT: đỏ
                        default: bgColor = Color.rgb(30, 30, 30, 0.8);
                    }
                    gc.setFill(bgColor);
                    gc.fillRoundRect(x, y, w, h, arc, arc);

                    // Vẽ chữ căn giữa với Impact cho tất cả nút
                    gc.setFill(Color.CYAN);
                    gc.setFont(Font.font("Impact", 32));
                    javafx.scene.text.Text tempText = new javafx.scene.text.Text(btn.label);
                    tempText.setFont(gc.getFont());
                    double textWidth = tempText.getLayoutBounds().getWidth();
                    gc.fillText(btn.label, WIDTH / 2 - textWidth / 2, y + h / 2 + 11);
                }
            }
        };
        menuLoop.start();

        Scene startScene = new Scene(rootStart, WIDTH, HEIGHT);
        primaryStage.setScene(startScene);

        // Xử lý sự kiện chuột cho các nút
        startScene.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();
            if (buttons[0].isHovered(x, y)) {
                menuLoop.stop();
                resetGame();
                startGame(primaryStage);
            } else if (buttons[1].isHovered(x, y)) {
                menuLoop.stop();
                showInstructions(primaryStage);
            } else if (buttons[2].isHovered(x, y)) {
                primaryStage.close();
            }
        });

        primaryStage.show();
    }

    private void showInstructions(Stage primaryStage) {
        Pane rootInstructions = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        rootInstructions.getChildren().add(canvas);

        // Tạo AnimationTimer để cập nhật và vẽ background động
        AnimationTimer instructionsLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                scrollingBackground.update(); // Cập nhật vị trí background
                scrollingBackground.render(gc); // Vẽ background

                // Vẽ nội dung hướng dẫn
                gc.setFill(Color.CYAN); // Đặt màu chữ là xanh dương nhạt
                gc.setFont(Font.font("Arial", 25)); // Đặt font chữ lớn hơn
                gc.fillText("Use the arrow keys to move your spaceship.", 50, 200);
                gc.fillText("Press SPACE to shoot bullets.", 50, 250);
                gc.fillText("Avoid enemies and collect power-ups.", 50, 300);
            }
        };
        instructionsLoop.start();

        // Thêm nút "Back" để quay lại màn hình chính
        Button backButton = new Button("Back");
        backButton.setFont(Font.font("Arial", 30)); // Tăng kích thước font chữ
        backButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        backButton.setLayoutX(WIDTH / 2 - 50); // Đặt nút ở giữa màn hình
        backButton.setLayoutY(HEIGHT - 200); // Đưa nút lên trên một chút
        backButton.setOnAction(e -> {
            instructionsLoop.stop(); // Dừng vòng lặp nền động
            showMainMenu(primaryStage); // Quay lại màn hình chính
        });

        rootInstructions.getChildren().add(backButton);

        Scene instructionsScene = new Scene(rootInstructions, WIDTH, HEIGHT);
        primaryStage.setScene(instructionsScene);

        // Xử lý sự kiện quay lại màn hình chính bằng phím BACK_SPACE
        instructionsScene.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("BACK_SPACE")) {
                instructionsLoop.stop();
                showMainMenu(primaryStage);
            }
        });

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
                case LEFT:
                    player.setMoveLeft(true);
                    break;
                case RIGHT:
                    player.setMoveRight(true);
                    break;
                case UP:
                    player.setMoveForward(true);
                    break;
                case DOWN:
                    player.setMoveBackward(true);
                    break;
                case SPACE:
                    isShooting = true; // Bắt đầu bắn
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT:
                    player.setMoveLeft(false);
                    break;
                case RIGHT:
                    player.setMoveRight(false);
                    break;
                case UP:
                    player.setMoveForward(false);
                    break;
                case DOWN:
                    player.setMoveBackward(false);
                    break;
                case SPACE:
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

        // Cập nhật vị trí ảnh nền
        backgroundY += backgroundSpeed;
        if (backgroundY >= HEIGHT) {
            backgroundY = 0; // Lặp lại ảnh nền
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
                    player.setX(0); // Không cho vượt quá phía trên
                } else if (player.getY() + player.getHeight() > HEIGHT) {
                    player.setX(HEIGHT - player.getHeight()); // Không cho vượt quá phía dưới
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

        // Kiểm tra sự tồn tại của boss sau khi loại bỏ các đối tượng "dead"
        boolean bossStillExists = false;
        for (GameObject obj : gameObjects) {
            if (obj instanceof BossEnemy && !obj.isDead()) {
                bossStillExists = true;
                break;
            }
        }
        bossExists = bossStillExists;

        // Sinh thêm kẻ địch, boss, và power-up
        spawnEnemy();
        if (score >= lastBossSpawnScore + 250 && !bossExists) {
            spawnBossEnemy();
            lastBossSpawnScore += 250; // Cập nhật mốc điểm tiếp theo
        }

        // Cập nhật UI
        scoreLabel.setText("Score: " + score);
    }

    private void render() {
        // Vẽ ảnh nền
        if (backgroundImage != null) {
            gc.drawImage(backgroundImage, 0, backgroundY, WIDTH, HEIGHT); // Vẽ ảnh nền ở vị trí hiện tại
            gc.drawImage(backgroundImage, 0, backgroundY - HEIGHT, WIDTH, HEIGHT); // Vẽ ảnh nền phía trên
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
                gc.drawImage(heartImage, WIDTH - 140 + i * 35, 10, 30, 30); // Dịch trái tim sang trái một chút
            }
        } else {
            // Nếu không tải được ảnh, hiển thị số mạng bằng chữ
            gc.fillText(String.valueOf(numLives), WIDTH - 140, 30);
        }
    }

    private void spawnEnemy() {
        // Tăng xác suất xuất hiện kẻ địch dựa trên điểm số
        double baseProbability = 0.008; // Xác suất cơ bản
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
                    // Xử lý va chạm giữa Bullet và Enemy
                    if ((obj1 instanceof Bullet && obj2 instanceof Enemy) ||
                        (obj1 instanceof Enemy && obj2 instanceof Bullet)) {
                        Bullet bullet = (Bullet) (obj1 instanceof Bullet ? obj1 : obj2);
                        Enemy enemy = (Enemy) (obj1 instanceof Enemy ? obj1 : obj2);

                        // Chỉ tạo hiệu ứng nổ nếu Enemy chưa chết
                        if (!enemy.isDead()) {
                            enemy.setDead(true); // Đánh dấu Enemy là "dead"
                            bullet.setDead(true); // Đánh dấu Bullet là "dead"

                            // Tạo hiệu ứng nổ tại vị trí Enemy
                            gameObjects.add(new Explosion(enemy.getX(), enemy.getY()));

                            // Tăng điểm
                            score += 10;

                            // Random tỷ lệ xuất hiện PowerUp
                            double dropChance = 0.1; // 10% tỷ lệ xuất hiện PowerUp
                            if (Math.random() < dropChance) {
                                double powerUpX = enemy.getX();
                                double powerUpY = enemy.getY();
                                gameObjects.add(new PowerUp(powerUpX, powerUpY)); // Tạo PowerUp tại vị trí Enemy
                            }
                        }

                        // Thêm Bullet và Enemy vào danh sách cần xóa
                        toRemove.add(bullet);
                        toRemove.add(enemy);
                    }

                    // Xử lý va chạm giữa Player và Enemy
                    else if (obj1 instanceof Player && obj2 instanceof Enemy) {
                        Player player = (Player) obj1;
                        Enemy enemy = (Enemy) obj2;

                        // Đánh dấu Enemy là "dead"
                        enemy.setDead(true);
                        toRemove.add(enemy);

                        // Giảm số mạng của Player
                        numLives--;

                        // Kích hoạt hiệu ứng nhấp nháy cho Player
                        player.triggerBlink();

                        // Kiểm tra nếu số mạng giảm xuống 0
                        if (numLives <= 0) {
                            gameRunning = false; // Kết thúc trò chơi
                            System.out.println("Game Over! Returning to game over screen...");
                            showGameOverScreen((Stage) root.getScene().getWindow());
                            return;
                        }
                    }

                    // Xử lý va chạm giữa Bullet và BossEnemy
                    else if ((obj1 instanceof Bullet && obj2 instanceof BossEnemy) ||
                             (obj1 instanceof BossEnemy && obj2 instanceof Bullet)) {
                        Bullet bullet = (Bullet) (obj1 instanceof Bullet ? obj1 : obj2);
                        BossEnemy boss = (BossEnemy) (obj1 instanceof BossEnemy ? obj1 : obj2);

                        // Gọi phương thức takeDamage của BossEnemy
                        boss.takeDamage(bullet.getDamage());

                        // Đánh dấu đạn là "dead" sau khi va chạm
                        bullet.setDead(true);
                    }

                    // Xử lý va chạm giữa Player và EnemyBullet
                    else if (obj1 instanceof Player && obj2 instanceof EnemyBullet) {
                        Player player = (Player) obj1;
                        EnemyBullet enemyBullet = (EnemyBullet) obj2;

                        // Đánh dấu EnemyBullet là "dead"
                        enemyBullet.setDead(true);
                        toRemove.add(enemyBullet);

                        // Giảm số mạng của Player
                        numLives--;

                        // Kích hoạt hiệu ứng nhấp nháy cho Player
                        player.triggerBlink();

                        // Kiểm tra nếu số mạng giảm xuống 0
                        if (numLives <= 0) {
                            gameRunning = false; // Kết thúc trò chơi
                            System.out.println("Game Over! Returning to game over screen...");
                            showGameOverScreen((Stage) root.getScene().getWindow());
                            return;
                        }
                    }

                    // Xử lý va chạm giữa Player và PowerUp
                    else if (obj1 instanceof Player && obj2 instanceof PowerUp) {
                        Player player = (Player) obj1;
                        PowerUp powerUp = (PowerUp) obj2;

                        // Đánh dấu PowerUp là "dead"
                        powerUp.setDead(true);
                        toRemove.add(powerUp);

                        // Tạo hiệu ứng động tại vị trí PowerUp
                        gameObjects.add(new PowerUpEffect(powerUp.getX(), powerUp.getY()));

                        // Random hóa hiệu ứng PowerUp
                        if (numLives < 4 && Math.random() < 0.5) {
                            numLives++; // Tăng mạng nếu chưa đạt tối đa
                            System.out.println("Extra life gained! Lives: " + numLives);
                        } else {
                            player.activatePowerUp(8000); // Tăng số lượng đạn trong 8 giây
                            System.out.println("PowerUp activated: Increased bullet count for 8 seconds!");
                        }
                    }
                }
            }
        }

        // Loại bỏ các đối tượng đã đánh dấu là "dead"
        gameObjects.removeAll(toRemove);
    }

    private void startGame(Stage primaryStage) {
        // Dừng game loop nếu đang chạy
        if (gameLoop != null) {
            gameLoop.stop();
        }

        root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT); // Đặt kích thước Canvas theo khung hình mới
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", 20)); // Tăng kích cỡ chữ lên 24
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

        // Tạo game loop mới
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
        lastBossSpawnScore = 0; // Đặt lại mốc xuất hiện boss
    }

    private void showGameOverScreen(Stage primaryStage) {
        VBox rootGameOver = new VBox(20);
        rootGameOver.setAlignment(Pos.CENTER);
        rootGameOver.setStyle("-fx-background-color: black;");

        // Tiêu đề "Game Over"
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Arial", 50));
        gameOverText.setFill(Color.RED);
        gameOverText.setTranslateY(-30); // Di chuyển chữ "GAME OVER" lên phía trên

        // Hiển thị điểm số
        Text scoreText = new Text("Your Score: " + score);
        scoreText.setFont(Font.font("Arial", 30));
        scoreText.setFill(Color.WHITE);

        // Nút "Thử Lại" để bắt đầu một ván chơi mới
        Button retryButton = new Button("Try Again");
        retryButton.setFont(Font.font("Arial", 25));
        retryButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;"); // Màu nền xám, chữ trắng
        retryButton.setOnAction(e -> {
            resetGame(); // Đặt lại trạng thái trò chơi
            startGame(primaryStage); // Bắt đầu trò chơi mới
        });

        // Hiệu ứng khi di chuột vào nút "Try Again"
        retryButton.setOnMouseEntered(e -> retryButton.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;")); // Nổi bật
        retryButton.setOnMouseExited(e -> retryButton.setStyle("-fx-background-color: gray; -fx-text-fill: white;")); // Trở lại bình thường

        // Nút "Thoát" để quay lại màn hình chính
        Button exitButton = new Button("Exit Game");
        exitButton.setFont(Font.font("Arial", 25));
        exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Màu nền đỏ, chữ trắng
        exitButton.setOnAction(e -> {
            resetGame(); // Đặt lại trạng thái trò chơi
            showMainMenu(primaryStage); // Hiển thị màn hình chính
        });

        // Hiệu ứng khi di chuột vào nút "Exit Game"
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: darkred; -fx-text-fill: white;")); // Nổi bật
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white;")); // Trở lại bình thường

        // Thêm các thành phần vào giao diện
        rootGameOver.getChildren().addAll(gameOverText, scoreText, retryButton, exitButton);

        Scene gameOverScene = new Scene(rootGameOver, WIDTH, HEIGHT);
        primaryStage.setScene(gameOverScene);
        primaryStage.show();
    }
}