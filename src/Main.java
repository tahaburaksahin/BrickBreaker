import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame obj = new JFrame();
        Gameplay gamePlay = new Gameplay();

        obj.setBounds(10, 10, 780, 600); // Adjusted frame size for 200 blocks
        obj.setTitle("Breakout Ball");
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gamePlay);
    }
}

class MapGenerator {
    int[][] map;
    int brickWidth;
    int brickHeight;

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1;
            }
        }

        brickWidth = 600 / col;
        brickHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(Color.white);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    // Drawing borders around bricks
                    g.setStroke(new BasicStroke(2));
                    g.setColor(Color.darkGray);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}

class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 200; // Increased the number of bricks

    private Timer timer;
    private int delay = 5;

    private int playerX = 310;

    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -2;
    private int ballYdir = -3;

    private MapGenerator map;

    public Gameplay() {
        map = new MapGenerator(10, 20); // Adjusted for 200 blocks
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 792, 592); // Adjusted panel size for 200 blocks

        // Drawing the map
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.black);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 792, 3);
        g.fillRect(791, 0, 3, 592);

        // Scores
        g.setColor(Color.white);
        g.setFont(new Font("Times New Roman", Font.BOLD, 25)); // Changed font to Times New Roman
        g.drawString("Score: " + score, 590, 30);

        // Paddle
        g.setColor(Color.yellow);
        g.fillRect(playerX, 550, 100, 8);

        // Ball
        g.setColor(Color.blue);
        g.fillOval(ballposX, ballposY, 15, 15); // Reduced ball size

        // Game Over
        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("Times New Roman", Font.BOLD, 30));
            g.drawString("Game Over, Scores: " + score, 190, 300);

            g.setColor(Color.red);
            g.setFont(new Font("Times New Roman", Font.BOLD, 20));
            g.drawString("Press Space to Restart", 230, 350); // Changed restart key to Space
        }

        // Winning
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("Times New Roman", Font.BOLD, 30));
            g.drawString("You Won!", 260, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("Times New Roman", Font.BOLD, 20));
            g.drawString("Press Space to Restart", 230, 350); // Changed restart key to Space
        }

        g.dispose();
    }

    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            if (new Rectangle(ballposX, ballposY, 15, 15).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            } else if (new Rectangle(ballposX, ballposY, 15, 15).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }


            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 15, 15); // Reduced ball size
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballposX + 14 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }

                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 770) {
                ballXdir = -ballXdir;
            }

            repaint();
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 700) {
                playerX = 700;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Changed restart key to Space
            if (!play) {
                play = true;
                ballposX = 120;
                ballposY = 350;
                ballXdir = -2;
                ballYdir = -3;
                playerX = 310;
                score = 0;
                totalBricks = 200; // Resetting the number of bricks for restart
                map = new MapGenerator(10, 20); // Resetting the map for restart
                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }
}
