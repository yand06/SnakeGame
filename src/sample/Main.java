package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.util.Duration;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final int WIDTH = 1000;
    private static final int HEIGTH = WIDTH;
    private static final int ROWS = 20;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private static final String[] FOODS_IMAGE = new String[]{
            "/img/ic_orange.png",
            "/img/ic_apple.png",
            "/img/ic_cherry.png",
            "/img/ic_berry.png",
            "/img/ic_coconut.png",
            "/img/ic_peach.png",
            "/img/ic_watermelon.png",
            "/img/ic_orange.png",
            "/img/ic_pomegranate.png"};

    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private GraphicsContext gc;
    private final List<Point> snakeBody = new ArrayList<>();
    private Point snakeHead;
    private boolean gameOver;
    private int foodX;
    private int foodY;
    private int currentDirection;
    private Image foodImage;
    private int score = 0;
    private Image snakeHeadImage;



    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Snake");
        Group root = new Group();
        javafx.scene.canvas.Canvas canvas = new Canvas(WIDTH, HEIGTH);
        gc = canvas.getGraphicsContext2D(); // Menginisialisasi GraphicsContext
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        gc = canvas.getGraphicsContext2D();

        snakeHeadImage = new Image("/img/emot_batu.png");
        bgImage = new Image("/img/bgGameOver.png");

        // Tambahkan event handler untuk klik mouse di sini
        scene.setOnMouseClicked(event -> {
            if (gameOver) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                double buttonWidth = 200;
                double buttonHeight = 50;
                double buttonX = (double) WIDTH / 2 - buttonWidth / 2;
                double buttonY = (double) HEIGTH / 2 + 50;

                // Cek apakah klik pada tombol "Play Again"
                if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth && mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                    resetGame();
                }

                double quitButtonX = (double) WIDTH / 2 - buttonWidth / 2;
                double quitButtonY = (double) HEIGTH / 2 + 150;

                // Cek apakah klik pada tombol "Quit"
                if (mouseX >= quitButtonX && mouseX <= quitButtonX + buttonWidth && mouseY >= quitButtonY && mouseY <= quitButtonY + buttonHeight) {
                    System.exit(0); // Keluar dari aplikasi
                }
            }
        });

        primaryStage.show();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.RIGHT || code == KeyCode.D){
                if(currentDirection != LEFT){
                    currentDirection = RIGHT;
                }
            } else if (code == KeyCode.LEFT || code == KeyCode.A){
                if (currentDirection != RIGHT){
                    currentDirection = LEFT;
                }
            } else if (code == KeyCode.UP || code == KeyCode.W) {
                if (currentDirection != DOWN){
                    currentDirection = UP;
                }
            } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                if (currentDirection != UP){
                    currentDirection = DOWN;
                }
            }
        });

        for(int i = 0; i < 3; i++){
            snakeBody.add(new Point(5, ROWS/2));
        }
        snakeHead = snakeBody.get(0);
        generateFood();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(130),e->run(gc)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void run(GraphicsContext gc){
        if (gameOver){
            drawGameOverScreen(gc);
            return;
        }
        drawBackground(gc);
        drawFood(gc);
        drawSnake(gc);
        drawScore();

        // Simpan posisi terakhir dari ekor ular sebelum pemindahan
        Point oldTail = new Point(snakeBody.get(snakeBody.size() - 1)); // Simpan posisi ekor lama

        // Pindahkan posisi tubuh ular, mulai dari ekor ke arah kepala
        for (int i = snakeBody.size() - 1; i >= 1; i--){
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }


        switch (currentDirection){
            case RIGHT:
                snakeHead.x++;
                break;
            case LEFT:
                snakeHead.x--;
                break;
            case UP:
                snakeHead.y--;
                break;
            case DOWN:
                snakeHead.y++;
                break;
        }

        snakeBody.get(0).x = snakeHead.x;
        snakeBody.get(0).y = snakeHead.y;

        gameOver();
        eatFood();

        // Jika ular memakan makanan, tambahkan segmen tubuh baru
        if (snakeHead.getX() == foodX && snakeHead.getY() == foodY) {
            snakeBody.add(new Point(oldTail.x, oldTail.y)); // Tambahkan ekor baru
            score += 5;
            generateFood();
        }
    }

    private void drawBackground(GraphicsContext gc) {
        for (int i = 0; i < ROWS; i++){
            for (int j = 0; j < COLUMNS; j++){
                if ((i + j) % 2 == 0){
                    gc.setFill(Color.web("AAD751"));
                } else {
                    gc.setFill(Color.web("A2D149"));
                }
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void generateFood(){
        start :
        while (true){
            foodX = (int)(Math.random() * ROWS);
            foodY = (int)(Math.random() * COLUMNS);

            for (Point snake : snakeBody){
                if(snake.getX() == foodX && snake.getY() == foodY){
                    continue start;
                }
            }
            foodImage = new Image(FOODS_IMAGE[(int) (Math.random() * FOODS_IMAGE.length)]);
            break;
        }
    }

    private void drawFood(GraphicsContext gc){
        gc.drawImage(foodImage,foodX*SQUARE_SIZE,foodY*SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);
    }

    private void drawSnake(GraphicsContext gc) {
        gc.setFill(Color.web("4674E9"));
        gc.fillRoundRect(snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE - 100, SQUARE_SIZE - 100, 50, 50);

        for (int i = 1; i < snakeBody.size(); i++) {
            gc.fillRoundRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 50, 50);

        }

        gc.drawImage(snakeHeadImage,snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void gameOver(){
        if (snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGTH){
            gameOver = true;
        }

        for (int i = 1; i < snakeBody.size(); i++){
            if (snakeHead.x == snakeBody.get(i).getX() && snakeHead.y == snakeBody.get(i).getY()){
                gameOver = true;
                break;
            }
        }
    }

    private void resetGame() {
        // Reset semua variabel ke nilai awal
        snakeBody.clear();
        snakeHead = new Point(5, ROWS / 2);
        currentDirection = RIGHT;
        score = 0;
        gameOver = false;

        // Mulai ulang permainan dengan menghasilkan snakeBody baru dan makanan baru
        generateSnake();
        generateFood();
    }

    private void generateSnake() {
        snakeBody.clear(); // Kosongkan daftar segmen tubuh ular

        // Tambahkan kepala ular
        snakeBody.add(new Point(snakeHead));

        // Tambahkan segmen tubuh ular di belakang kepala
        for (int i = 1; i < 3; i++) {
            Point newSegment = new Point(snakeHead.x - i, snakeHead.y);
            snakeBody.add(newSegment);
        }
    }



    private void eatFood(){
        if (snakeHead.getX() == foodX && snakeHead.getY() == foodY){
            snakeBody.add(new Point(-1,-1));
            generateFood();
            score += 5;
        }
    }

    private void drawScore() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Digital-7", 35));
        gc.fillText("Score: " + score, 10, 35);
    }

    private Image bgImage;
    private void drawGameOverScreen(GraphicsContext gc) {
        // Gambar latar belakang
        gc.drawImage(bgImage, 0, 0, WIDTH, HEIGTH);

        // Gambar teks "Game Over"
        gc.setFill(Color.RED);
        Font gameOverFont = Font.font("Times New Roman", FontWeight.BOLD, 90); // Menggunakan FontWeight.BOLD untuk membuat tulisan bold
        gc.setFont(gameOverFont);
        gc.fillText("Game Over", (double) WIDTH / 2 - 200, (double) HEIGTH / 2 - 100); // Mengatur posisi teks "Game Over" di tengah

        // Gambar skor akhir
        gc.setFill(Color.WHITE);
        Font scoreFont = Font.font("Times New Roman", FontWeight.BOLD, 40); // Menggunakan FontWeight.BOLD untuk membuat tulisan bold
        gc.setFont(scoreFont);
        gc.fillText("Final Score: " + score, (double) WIDTH / 2 - 150, (double) HEIGTH / 2); // Mengatur posisi skor akhir di tengah

        // Gambar tombol "Play Again"
        double buttonWidth = 200;
        double buttonHeight = 50;
        double buttonX = (double) WIDTH / 2 - buttonWidth / 2;
        double buttonY = (double) HEIGTH / 2 + 50;
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK); // Mengatur warna border
        gc.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);
        gc.strokeRect(buttonX, buttonY, buttonWidth, buttonHeight); // Menggambar border
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Times New Roman", 30));
        gc.fillText("Play Again", buttonX + 35, buttonY + 35);

        // Gambar tombol "Quit"
        double quitButtonX = (double) WIDTH / 2 - buttonWidth / 2;
        double quitButtonY = (double) HEIGTH / 2 + 150;
        gc.setFill(Color.RED);
        gc.setStroke(Color.BLACK); // Mengatur warna border
        gc.fillRect(quitButtonX, quitButtonY, buttonWidth, buttonHeight);
        gc.strokeRect(quitButtonX, quitButtonY, buttonWidth, buttonHeight); // Menggambar border
        gc.setFill(Color.BLACK);
        gc.fillText("Quit", quitButtonX + 70, quitButtonY + 35);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
