import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter; // Import for KeyAdapter
import java.awt.event.KeyEvent; // Import for KeyEvent
import java.io.File;
import java.io.IOException;
import java.util.ArrayList; // Import for ArrayList
import java.util.List; // Import for List

public class GameMenu {

    private JFrame frame;
    private JPanel panel;
    private JButton playButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel playLabel;
    private JLabel exitLabel;
    private Character character;
    private List<Coin> coins; // List to hold coins
    private String collectedLetters = ""; // String to hold collected letters
    private Font customFont; // Declare customFont as a class-level variable

    public GameMenu() {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setResizable(false); // Make the frame unresizable

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // Call the superclass method
                ImageIcon background = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\GameMenuBG.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
                
                // Draw shadow for the title
                g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black for shadow
                g.fillRect(145, 145, 310, 60); // Adjust position and size as needed for shadow

                character.draw(g);
                for (Coin coin : coins) {
                    coin.update(); // Update each coin's animation frame
                    coin.draw(g); // Draw each coin
                }
                drawCollectedLetters(g); // Draw the collected letters
            }
        };
        panel.setLayout(null);

        // Load icons and custom font
        ImageIcon playIcon = new ImageIcon("C:/Users/riadyn/Documents/NetBeansProjects/JavaApplication1/gui/play.png");
        ImageIcon exitIcon = new ImageIcon("C:/Users/riadyn/Documents/NetBeansProjects/JavaApplication1/gui/exit.png");

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:/Users/riadyn/Documents/NetBeansProjects/JavaApplication1/build/font/QuinqueFive.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 24); // Fallback font
        }

        titleLabel = new JLabel("Game Menu", SwingConstants.CENTER);
        titleLabel.setFont(customFont);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(150, 150, 300, 50); // Position the title above the buttons

        playLabel = new JLabel("Play", SwingConstants.CENTER);
        playLabel.setFont(customFont.deriveFont(8f));
        playLabel.setForeground(Color.WHITE);
        playLabel.setBounds(160, 300, 100, 30); // Position the play label above the play button

        exitLabel = new JLabel("Exit", SwingConstants.CENTER);
        exitLabel.setFont(customFont.deriveFont(8f));
        exitLabel.setForeground(Color.WHITE);
        exitLabel.setBounds(185, 335, 100, 30); // Position the exit label above the exit button
        
        playButton = new JButton(playIcon);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setOpaque(false);
        playButton.setPressedIcon(playIcon);
        playButton.setSelectedIcon(playIcon);
        playButton.setBounds(150, 300, playIcon.getIconWidth(), playIcon.getIconHeight());

        exitButton = new JButton(exitIcon);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setFocusable(false);
        exitButton.setPressedIcon(exitIcon);
        exitButton.setSelectedIcon(exitIcon);

        playButton.setBounds(150, 300, playIcon.getIconWidth(), playIcon.getIconHeight());
        exitButton.setBounds(170, 330, 100, 40);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the menu
                new BookWormGame(); // Start the game
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        panel.add(titleLabel); // Add the title label to the panel
        panel.add(playLabel);  // Add the play label to the panel
        panel.add(exitLabel);  // Add the exit label to the panel
        panel.add(playButton);
        panel.add(exitButton);
        frame.add(panel);
        frame.setVisible(true); // Ensure the frame is visible

        character = new Character();

        coins = new ArrayList<>(); // Initialize the coins list
        spawnCoins(); // Method to spawn coins

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                character.move(e.getKeyCode());
                checkCoinCollection(); // Check for coin collection
                panel.repaint();
            }
        });
        panel.setFocusable(true);

        // Timer to repaint the panel at regular intervals
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Coin coin : coins) {
                    coin.update(); // Update each coin's animation frame
                }
                panel.repaint(); // Repaint the panel to reflect changes
            }
        });
        timer.start(); // Start the timer
    }

    private void spawnCoins() {
        // Define specific spawning points and associated letters for the coins
        Object[][] spawnData = {
            {400, 450, new char[] {'L', 'A'}}, // Coin 3
            {450, 450, new char[] {'Q', 'U'}}, // Coin 4
            {500, 450, new char[] {'I', 'T'}}, // Coin 5
            {550, 450, new char[] {'E'}}, 
            {600, 450, new char[] {' ', '&', ' '}}, // Coin 6
            {650, 450, new char[] {'P', 'A'}}, // Coin 7
            {700, 450, new char[] {'C', 'A'}}, // Coin 8
            {750, 450, new char[] {'N', 'Z'}}, // Coin 9
            {800, 450, new char[] {'A',}}
        };

        // Add coins at the specified points with their corresponding letters
        for (Object[] data : spawnData) {
            int x = (int) data[0];
            int y = (int) data[1];
            char[] letters = (char[]) data[2];
            coins.add(new Coin(x, y, letters)); // Create a coin with the specified letters
        }
    }

    private void checkCoinCollection() {
        for (int i = coins.size() - 1; i >= 0; i--) {
            Coin coin = coins.get(i);
            if (character.getBounds().intersects(coin.getBounds())) {
                // Collect letters from Coin 1 and Coin 2 only
                if (collectedLetters.isEmpty()) {
                    collectedLetters += String.valueOf(coin.getLetters()); // Add letters for the first coin
                } else {
                    collectedLetters += String.valueOf(coin.getLetters()); // Add letters for subsequent coins without spaces
                }
                coins.remove(i); // Remove the coin if collected
            }
        }
        System.out.println("Collected Letters: " + collectedLetters); // Print collected letters for debugging
        panel.repaint(); // Request a repaint to update the display
    }

    private void drawCollectedLetters(Graphics g) {
        // Set the shadow color and draw the background rectangle
        g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black for shadow
        g.fillRect(15, 538, 300, 20); // Adjust the position and size as needed

        g.setColor(Color.WHITE); // Set the color for the text
        g.setFont(customFont.deriveFont(8f)); // Set the font size to 8 (or any size you prefer)
        g.drawString("Created by: " + collectedLetters, 20, 550); // Draw the collected letters on the panel
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameMenu::new);
    }

    class Character {
        private int x, y;
        private ImageIcon[] frames;
        private int currentFrame;
        private boolean isRunning;
        private long keyPressStartTime;
        private boolean facingLeft;

        // Define movement bounds
        private final int MIN_X = 0; // Minimum x position
        private final int MAX_X = 1200; // Maximum x position (adjust as needed)
        private final int MIN_Y = 0; // Minimum y position
        private final int MAX_Y = 0; // Maximum y position (adjust as needed)

        public Character() {
            x = 100;
            y = 450;
            loadAnimationFrames();
            isRunning = false;
            facingLeft = false;
        }

        private void loadAnimationFrames() {
            frames = new ImageIcon[3];
            frames[0] = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\walk_frame1.png");
            frames[1] = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\walk_frame2.png");
            frames[2] = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\run_frame.png");
        }

        public void move(int keyCode) {
            int newX = x;
            int newY = y;

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    newX -= 5; // Move left
                    facingLeft = true; // Set direction to left
                    break;
                case KeyEvent.VK_RIGHT:
                    newX += 5; // Move right
                    facingLeft = false; // Set direction to right
                    break;
                case KeyEvent.VK_UP:
                    newY -= 5; // Move up
                    break;
                case KeyEvent.VK_DOWN:
                    newY += 5; // Move down
                    break;
            }

            // Check bounds before updating position
            if (newX >= MIN_X && newX <= MAX_X) {
                x = newX; // Update x position if within bounds
            }
            if (newY >= MIN_Y && newY <= MAX_Y) {
                y = newY; // Update y position if within bounds
            }

            // Check if the key is pressed for more than 3 seconds
            long currentTime = System.currentTimeMillis();
            if (keyPressStartTime > 0 && currentTime - keyPressStartTime > 3000) {
                isRunning = true; // Set running state
                currentFrame = 2; // Switch to the running frame
            } else {
                isRunning = false; // Reset running state if not held long enough
                currentFrame = (currentFrame + 1) % 2; // Cycle through walking frames
            }
        }

        public void keyPressed() {
            if (keyPressStartTime == 0) {
                keyPressStartTime = System.currentTimeMillis(); // Start timing
            }
        }

        public void keyReleased() {
            isRunning = false; // Reset running state on key release
            currentFrame = 0; // Reset to the first walking frame
            keyPressStartTime = 0; // Reset key press time
        }

        public void draw(Graphics g) {
            // Draw the character facing left or right
            if (facingLeft) {
                g.drawImage(frames[isRunning ? 2 : currentFrame].getImage(), x + frames[currentFrame].getIconWidth(), y, -frames[currentFrame].getIconWidth(), frames[currentFrame].getIconHeight(), null);
            } else {
                g.drawImage(frames[isRunning ? 2 : currentFrame].getImage(), x, y, null);
            }
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, frames[currentFrame].getIconWidth(), frames[currentFrame].getIconHeight());
        }
    }

    class Coin {
        private int x, y;
        private final int SIZE = 20; // Size of the coin
        private ImageIcon[] frames; // Array to hold coin animation frames
        private int currentFrame; // Current frame index
        private long lastFrameChangeTime; // Time of the last frame change
        private final long FRAME_DURATION = 100; // Duration for each frame in milliseconds
        private char[] letters; // Array to hold letters associated with the coin

        public Coin(int x, int y, char[] letters) {
            this.x = x;
            this.y = y;
            this.letters = letters; // Set the letters for the coin
            loadAnimationFrames(); // Load the coin animation frames
        }

        private void loadAnimationFrames() {
            frames = new ImageIcon[8]; // Assuming you have 8 frames
            for (int i = 0; i < frames.length; i++) {
                frames[i] = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\coins\\coin_0" + (i + 1) + ".png"); // Update with your image paths
            }
        }

        public void update() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameChangeTime > FRAME_DURATION) {
                currentFrame = (currentFrame + 1) % frames.length; // Cycle through frames
                lastFrameChangeTime = currentTime; // Update the last frame change time
            }
        }

        public void draw(Graphics g) {
            g.drawImage(frames[currentFrame].getImage(), x, y, SIZE, SIZE, null); // Draw the current frame
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, SIZE, SIZE); // Define the bounds for collision detection
        }

        public String getLetters() {
            return new String(letters); // Convert char array to String
        }
    }
}