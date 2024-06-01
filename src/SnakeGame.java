import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;


import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;


public class SnakeGame extends GraphicsProgram implements ActionListener {


    private static final int SNAKE_SIZE = 20;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final Color FAKE_FOOD_COLOR = Color.BLUE;
    private static final Color SUPER_FOOD_COLOR = Color.MAGENTA;

    private ArrayList<SnakePart> snakeBody;
    private int snakeX, snakeY;
    private int snakeDirection; // 0 = up, 1 = right, 2 = down, 3 = left


    private Timer timer, timeTimer, fakeFoodTimer;
    private int elapsedTime;
    private boolean isPlaying, isGameOver, isPaused;
    private int score, foodCount;
    private GLabel scoreLabel, foodLabel, pauseLabel, countdownLabel, timeLabel;
    private GOval food;
    private int countdownTime = 3; // Countdown time in seconds


    public void run() {
        showInstructions();
    }

    private void showInstructions() {

        GRect instructionsBg = new GRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        instructionsBg.setFilled(true);
        instructionsBg.setColor(Color.BLACK);
        add(instructionsBg);

        String instructionsText = "Welcome to Snake. This is a modified classic retro snake game.\n" + "Red foods are normal, blue foods are deadly.\n" +
                "Pink foods may slow you down for a while, be careful.\n" + "P = Pause/Resume, use WASD or arrow keys to control the snake.\n" +
                "Have fun!";

        String[] instructionsLines = instructionsText.split("\n");

        int lineHeight = 20;
        int startY = WINDOW_HEIGHT / 4;

        for (int i = 0; i < instructionsLines.length; i++) {
            GLabel instructionLine = new GLabel(instructionsLines[i]);
            instructionLine.setFont("Arial-18");
            instructionLine.setColor(Color.WHITE);
            add(instructionLine, (WINDOW_WIDTH - instructionLine.getWidth()) / 2, startY + i * lineHeight);
        }

        GLabel startLabel = new GLabel("Click anywhere to start the game");
        startLabel.setFont("Arial-16");
        startLabel.setColor(Color.WHITE);
        add(startLabel, (WINDOW_WIDTH - startLabel.getWidth()) / 2, WINDOW_HEIGHT / 2);

        addMouseListeners();
    }

    public void mouseClicked(MouseEvent e) {

        removeAll(); // Reset Game
        setUpGame();
        addKeyListeners();
        timer.start();

    }


    private void setUpGame() {

        // Initial background creation
        GRect background = new GRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        background.setFilled(true);
        background.setColor(Color.BLACK);
        add(background);

        // Initial border creation
        GRect border = new GRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        border.setFilled(false);
        border.setColor(Color.YELLOW);
        add(border);

        snakeBody = new ArrayList<>();
        snakeX = WINDOW_WIDTH / 2;
        snakeY = WINDOW_HEIGHT / 2;
        snakeDirection = 1; // Start moving right

        isPlaying = true;
        isGameOver = false;
        isPaused = false;

        score = 0;
        foodCount = 0;
        elapsedTime = 0;

        snakeBody.add(new SnakePart(snakeX, snakeY));

        timer = new Timer(100, this);

        timeLabel = new GLabel("Time: 0"); // Create time label
        timeLabel.setFont("Arial-20");
        timeLabel.setColor(Color.WHITE);
        add(timeLabel, WINDOW_WIDTH - 100, 21); // Add time label to canvas

        scoreLabel = new GLabel("Score: 0");
        scoreLabel.setFont("Arial-20");
        scoreLabel.setColor(Color.WHITE);
        add(scoreLabel, 5, 41);

        foodLabel = new GLabel("Food: 0");
        foodLabel.setFont("Arial-20");
        foodLabel.setColor(Color.WHITE);
        add(foodLabel, 5, 21);

        spawnFood();

        timeTimer = new Timer(1000, new ActionListener() { // Create time timer
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timeLabel.setLabel("Time: " + elapsedTime);
            }
        });
        timeTimer.start(); // Start time timer
    }


    private void drawSnake() {

        for (SnakePart part : snakeBody) {

            GRect snakePart = new GRect(part.x, part.y, SNAKE_SIZE, SNAKE_SIZE);

            snakePart.setFilled(true);
            snakePart.setColor(Color.GREEN);
            add(snakePart);

        }
    }


    private void spawnFood() {

        Random rand = new Random();
        int foodX = rand.nextInt(WINDOW_WIDTH / SNAKE_SIZE) * SNAKE_SIZE;
        int foodY = rand.nextInt(WINDOW_HEIGHT / SNAKE_SIZE) * SNAKE_SIZE;

        int foodType = rand.nextInt(9); // 0 = regular food, 1 = fake food, 2 = super food

        food = new GOval(foodX, foodY, SNAKE_SIZE, SNAKE_SIZE);
        food.setFilled(true);

        if (foodType <= 5) { // 0 to 5 = 6/10 => Regular food, 60% chance of spawning

            food.setColor(Color.RED);

        } else if (foodType == 6) { // 6 only => Fake food, 10% chance of spawning

            food.setColor(FAKE_FOOD_COLOR);
            startFakeFoodTimer(); // Start timer for fake food

        } else { // 7, 8, 9 = 3/10 = 30% => Super food, 30% chance of spawning

            food.setColor(SUPER_FOOD_COLOR);

        }


        add(food);
    }


    private void startFakeFoodTimer() {
        fakeFoodTimer = new Timer(3000, new ActionListener() { // Fake food lasts for 3 seconds
            @Override
            public void actionPerformed(ActionEvent e) {

                remove(food); // Remove the fake food
                spawnFood(); // Spawn a new food item
                ((Timer) e.getSource()).stop(); // Stop the timer

            }
        });

        fakeFoodTimer.setRepeats(false); // Timer should fire only once
        fakeFoodTimer.start();

    }


    public void keyPressed(KeyEvent keyPressed) {

        int keyCode = keyPressed.getKeyCode();

        if (keyCode == KeyEvent.VK_R) {

            if (isGameOver) {
                setUpGame();
                redrawSnake();
                timer.start();
            }

        } else if (keyCode == KeyEvent.VK_P) { // Pause/Resume game

            if (isPlaying && !isGameOver) {

                isPaused = !isPaused;

                if (isPaused) {

                    pauseLabel = new GLabel("Game Paused");
                    pauseLabel.setFont("Arial-24");
                    pauseLabel.setColor(Color.WHITE);
                    add(pauseLabel, (WINDOW_WIDTH - pauseLabel.getWidth()) / 2, WINDOW_HEIGHT / 2);
                    timer.stop();

                } else {

                    remove(pauseLabel);
                    startCountdown();

                }
            }

        } else if (!isPaused && (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) && snakeDirection != 2) {

            snakeDirection = 0; // Up

        } else if (!isPaused && (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) && snakeDirection != 3) {

            snakeDirection = 1; // Right

        } else if (!isPaused && (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) && snakeDirection != 0) {

            snakeDirection = 2; // Down

        } else if (!isPaused && (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) && snakeDirection != 1) {

            snakeDirection = 3; // Left

        }
    }


    private void redrawSnake() {

        removeAll(); // Reset elements

        // Add the black background rectangle
        GRect background = new GRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        background.setFilled(true);
        background.setColor(Color.BLACK);
        add(background);


        // Clearly defined border, not red nor green nor black
        GRect border = new GRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        border.setFilled(false);
        border.setColor(Color.YELLOW);
        add(border);


        drawSnake();
        add(foodLabel, 5, 21);
        add(scoreLabel, 5, 41);
        add(food);
        add(timeLabel, WINDOW_WIDTH - 100, 21); // Add time label to canvas
    }


    private void growSnake() {

        int newX = snakeBody.get(snakeBody.size() - 1).x;
        int newY = snakeBody.get(snakeBody.size() - 1).y;

        switch (snakeDirection) {
            case 0: // Up
                newY -= SNAKE_SIZE;
                break;
            case 1: // Right
                newX += SNAKE_SIZE;
                break;
            case 2: // Down
                newY += SNAKE_SIZE;
                break;
            case 3: // Left
                newX -= SNAKE_SIZE;
                break;

        }

        snakeBody.add(new SnakePart(newX, newY));
        foodCount++;
        foodLabel.setLabel("Food: " + foodCount);
        score += 15; // Increase score by 15 points
        scoreLabel.setLabel("Score: " + score);

    }


    private void handleSuperFood() {

        Random rand = new Random();
        int slowdownTime = rand.nextInt(11); // Random value between 0 and 10 seconds

        if (slowdownTime > 0) {
            timer.setDelay(500); // Slow down the snake

            // Create a timer to reset the delay after the slowdown time
            Timer slowdownTimer = new Timer(slowdownTime * 1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    timer.setDelay(100); // Reset the timer delay to normal
                    ((Timer) e.getSource()).stop();
                }
            });
            slowdownTimer.setRepeats(false);
            slowdownTimer.start();
        }
    }


    private void moveSnake() {

        int newX = snakeBody.get(0).x;
        int newY = snakeBody.get(0).y;

        switch (snakeDirection) {
            case 0: // Up
                newY -= SNAKE_SIZE;
                break;
            case 1: // Right
                newX += SNAKE_SIZE;
                break;
            case 2: // Down
                newY += SNAKE_SIZE;
                break;
            case 3: // Left
                newX -= SNAKE_SIZE;
                break;
        }

        // Check for collisions
        if (newX < 0 || newX >= WINDOW_WIDTH || newY < 0 || newY >= WINDOW_HEIGHT || snakeBody.contains(new SnakePart(newX, newY))) {
            isGameOver = true;
            return;
        }

        snakeBody.add(0, new SnakePart(newX, newY));

        // Check if snake ate the food
        if (newX == food.getX() && newY == food.getY()) {

            if (food.getColor() == FAKE_FOOD_COLOR) {

                isGameOver = true; // Fake food kills the snake

            } else if (food.getColor() == SUPER_FOOD_COLOR) {

                handleSuperFood(); // Super food slows down the snake
                growSnake();
                spawnFood();

            } else {

                growSnake();
            }

            spawnFood();

        } else {

            snakeBody.remove(snakeBody.size() - 1);

        }
    }


    private void startCountdown() {

        countdownTime = 3;
        countdownLabel = new GLabel("Resuming in " + countdownTime);
        countdownLabel.setFont("Arial-24");
        countdownLabel.setColor(Color.WHITE);
        add(countdownLabel, (WINDOW_WIDTH - countdownLabel.getWidth()) / 2, WINDOW_HEIGHT / 2);

        Timer countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // method allows timer to expect an actionEvent of a resume

                countdownTime--;
                countdownLabel.setLabel("Resuming in " + countdownTime);

                if (countdownTime == 0) {

                    ((Timer) e.getSource()).stop();
                    remove(countdownLabel);
                    timer.start(); // main game timer, resumes the game

                }
            }
        });

        countdownTimer.start();
    }


    @Override
    public void actionPerformed(ActionEvent arg0) { // default method used to detect if the game is active or over

        if (isPlaying && !isGameOver && !isPaused) {

            moveSnake();
            redrawSnake();

        } else if (isGameOver) {

            timer.stop();
            timeTimer.stop();

            removeAll();

            GLabel gameOverLabel = new GLabel("Game Over! Score: " + score);
            gameOverLabel.setFont("Arial-24");
            add(gameOverLabel, (WINDOW_WIDTH - gameOverLabel.getWidth()) / 2, WINDOW_HEIGHT / 2);

            GLabel restartLabel = new GLabel("Press 'R' to restart");
            restartLabel.setFont("Arial-18");
            add(restartLabel, (WINDOW_WIDTH - restartLabel.getWidth()) / 2, WINDOW_HEIGHT / 2 + 30);

            isPlaying = false;

        }
    }

    public static void main(String[] args) {

        new SnakeGame().start();

    }


    private class SnakePart { // class is nested as it simplifies the program; snake is a fairly small game and SnakePart is a small and contained class
        int x, y;

        public SnakePart(int x, int y) {

            this.x = x;
            this.y = y;

        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof SnakePart) {

                SnakePart other = (SnakePart) obj;
                return x == other.x && y == other.y;


            }

            return false;

        }
    }
}


