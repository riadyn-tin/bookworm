import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.border.LineBorder;

public class BookWormGame {

    private Font customFont;
    private Set<String> wordList = new HashSet<>();
    private int totalScore = 0;
    private JTextField inputField;
    private JPanel gridPanel;
    private JLabel imageLabel;
    private StringBuilder currentWord = new StringBuilder();
    private LinkedList<String> wordHistory = new LinkedList<>();
    private Map<String, Integer> wordCountMap = new HashMap<>();
    private int textYPosition = 100; // Initial y-position for text
    private int wordCounter = 1; // Counter for numbering words
    private int hearts = 6; // Number of hearts
    private JFrame frame; // Declare frame as a class-level variable
    private JPanel healthPanel; // Panel to display heart images
    private JLabel displayLabel; 
    // Declare as a class-level variable

    // Declare the list to hold letter buttons
    List<JButton> letterButtons = new ArrayList<>();

    private final int maxHearts = 6;
    private Map<String, Integer> inventory = new HashMap<>();
    private JButton inventoryButton; // Button to open inventory
    private Point initialClick; // Declare initialClick as a class-level variable

    private JButton potionSlot = null; // Class-level variable
    private boolean[] isPotionSelected = {false}; // Use an array to hold the state

    private JLabel descriptionLabel; // Declare at the class level

    private Cursor hoverCursor;
    private Cursor defaultCursor;

    private int comboMultiplier = 1; // Initialize combo multiplier
    private int consecutiveValidWords = 0; // Track consecutive valid words

    private Map<Character, Integer> letterValues = new HashMap<>();

    public BookWormGame() {
        // Load and set the custom font
        customFont = loadCustomFont("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\build\\font\\QuinqueFive.ttf");
        if (customFont != null) {
            setDefaultFont(customFont.deriveFont(8f)); // Set the default font size as needed
        }
        
        loadDictionary("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\src\\javaapplication1\\dictionary");

        frame = new JFrame("Bookworm Game"); // Initialize frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setResizable(false); // Make the frame unresizable

        // Create a custom panel with a background image
        BackgroundPanel backgroundPanel = new BackgroundPanel("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\background.png");
        backgroundPanel.setLayout(null); // Use null layout for absolute positioning
        frame.setContentPane(backgroundPanel);

        // Reset image on exit
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resetImage();
            }
        });

        // Initialize gridPanel
        gridPanel = new JPanel();
        gridPanel.setLayout(null); // Use null layout for manual positioning
        gridPanel.setOpaque(false);

        // Set bounds for gridPanel
        gridPanel.setBounds(100, 150, 600, 600); // Adjust x, y, width, height as needed
        backgroundPanel.add(gridPanel);

        // Load the button image
        ImageIcon buttonIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\buttonImage.png");

        // Add letter buttons to gridPanel
        char[][] letterGrid = {
            {'a', 'b', 'c', 'd', 'e'},
            {'f', 'g', 'h', 'i', 'j'},
            {'k', 'l', 'm', 'n', 'o'},
            {'p', 'q', 'r', 's', 't'},
            {'u', 'v', 'w', 'x', 'y'},
            {'z'}
        };

        int startX = 0;
        int startY = 0;
        int buttonWidth = 50;  // Adjust to match image width
        int buttonHeight = 40; // Adjust to match image height
        int gap = 10; // Adjust gap if needed

        for (int row = 0; row < letterGrid.length; row++) {
            for (int col = 0; col < letterGrid[row].length; col++) {
                char letter = letterGrid[row][col];
                JButton letterButton = new JButton(String.valueOf(letter));
                letterButton.setFont(customFont.deriveFont(Font.BOLD, 12f));
                letterButton.setHorizontalTextPosition(SwingConstants.CENTER);
                letterButton.setVerticalTextPosition(SwingConstants.CENTER);
                letterButton.setIcon(buttonIcon);
                letterButton.setOpaque(false);
                letterButton.setContentAreaFilled(false);
                letterButton.setBorderPainted(false);
                letterButton.setFocusPainted(false);
                letterButton.setForeground(Color.WHITE); // Set text color

                // Calculate position
                int x = startX + col * (buttonWidth + gap);
                int y = startY + row * (buttonHeight + gap);

                letterButton.setBounds(x, y, buttonWidth, buttonHeight);

                // Add action listener for click event
                letterButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playSound("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\sound\\button (online-audio-converter.com).wav");
                        currentWord.append(letter);
                        inputField.setText(currentWord.toString());
                        updateDisplayLabel(displayLabel, currentWord.toString());
                    }
                });

                gridPanel.add(letterButton);
                letterButtons.add(letterButton);
            }
        }

        // Refresh the panel
        gridPanel.revalidate();
        gridPanel.repaint();

        // Create input field and button
        inputField = new JTextField();
        inputField.setEditable(false);
        inputField.setFont(new Font("Arial", Font.BOLD, 16));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setOpaque(false);

        // Load the submit button image
        ImageIcon submitIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\submit.png");

        JButton submitButton = new JButton("Submit", submitIcon);
        submitButton.setFont(customFont.deriveFont(Font.BOLD, 12f));
        submitButton.setHorizontalTextPosition(SwingConstants.CENTER);
        submitButton.setVerticalTextPosition(SwingConstants.CENTER);
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);

        // Set bounds for submitButton
        submitButton.setBounds(390, 105, 200, 30); // Adjust x, y, width, height as needed

        // Add the submitButton to the backgroundPanel
        backgroundPanel.add(submitButton);

        // Refresh the panel
        backgroundPanel.revalidate();
        backgroundPanel.repaint();

        // Load the image into an ImageIcon
        ImageIcon scoreIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\scoring.png");

        // Create a JLabel for the score image
        JLabel scoreBackground = new JLabel(scoreIcon);
        scoreBackground.setLayout(new BorderLayout());
        scoreBackground.setText("00");
        scoreBackground.setHorizontalTextPosition(JLabel.CENTER);
        scoreBackground.setVerticalTextPosition(JLabel.CENTER);
        scoreBackground.setFont(customFont.deriveFont(Font.BOLD, 8f));
        scoreBackground.setForeground(Color.WHITE); // Set text color for visibility

        // Set bounds for score background
        scoreBackground.setBounds(-50, 0, 200, 100); // Adjust x, y, width, height as needed
        backgroundPanel.add(scoreBackground);

        // Load and scale the book image
        ImageIcon bookIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\images\\spellbookForFlare.png");
        Image image = bookIcon.getImage();
        Image scaledImage = image.getScaledInstance(400, 225, Image.SCALE_SMOOTH); // Adjust size as needed
        bookIcon = new ImageIcon(scaledImage);

        // Initialize the imageLabel with the scaled bookIcon
        imageLabel = new JLabel(bookIcon);

        // Set bounds for imageLabel (x, y, width, height)
        imageLabel.setBounds(300, 100, 400, 400); // Adjust x, y, width, height as needed
        backgroundPanel.add(imageLabel);

        new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\heart.png");

        // Create the combined health bar image
        createHealthBarImage();

        // Initialize the health panel
        healthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        healthPanel.setOpaque(false);
        updateHealthDisplay();

        // Layout components
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setOpaque(false);
        controlPanel.add(inputPanel, BorderLayout.CENTER);
        controlPanel.add(healthPanel, BorderLayout.SOUTH);

        // Set bounds for healthPanel
        healthPanel.setBounds(0, 56, 200, 50); // Adjust x, y, width, height
        backgroundPanel.add(healthPanel);

        // Set bounds for controlPanel
        controlPanel.setBounds(300, 520, 400, 50); // Adjust as needed
        backgroundPanel.add(controlPanel);

        // Set bounds for imageLabel
        imageLabel.setBounds(580, 100, 400, 400); // Adjust as needed
        backgroundPanel.add(imageLabel);

        // Load the undo button image
        ImageIcon undoIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\undo.png");

        JButton undoButton = new JButton(undoIcon);
        undoButton.setOpaque(false);
        undoButton.setContentAreaFilled(false);
        undoButton.setBorderPainted(false);
        undoButton.setFocusPainted(false);

        // Set bounds for undoButton
        undoButton.setBounds(520, 42, undoIcon.getIconWidth(), undoIcon.getIconHeight()); // Adjust x, y as needed
        backgroundPanel.add(undoButton);

        // Set hand cursor for the undoButton
        undoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add mouse listener for hover effect
        undoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                undoButton.setCursor(hoverCursor); // Use custom hover cursor
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                undoButton.setCursor(defaultCursor); // Reset to default cursor
            }
        });

        // Add action listener to the undo button
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentWord.length() > 0) {
                    currentWord.deleteCharAt(currentWord.length() - 1);
                    inputField.setText(currentWord.toString());
                    updateDisplayLabel(displayLabel, currentWord.toString());
                } else {
                    JOptionPane.showMessageDialog(frame, "No letters to undo.");
                }
            }
        });

        // Revalidate and repaint the panel to apply changes
        backgroundPanel.revalidate();
        backgroundPanel.repaint();

        frame.setVisible(true);

        // Add action listener to the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\sound\\submit.wav");
                String word = currentWord.toString().toLowerCase();
                if (wordList.contains(word)) {
                    int score = calculateScore(word);
                    totalScore += score;
                    wordHistory.add(word);
                    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                    updateHistoryArea();
                    
                    // Show custom message dialog
                    showCustomMessage("Valid word!", score);
                    
                    // Update the score label
                    scoreBackground.setText("" + totalScore);
                    
                    // Increase combo multiplier
                    consecutiveValidWords++;
                    comboMultiplier = Math.min(5, 1 + consecutiveValidWords / 3); // Max multiplier of 5
                    
                    // Write the submitted word to the image
                    String imagePath = "C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\images\\spellbookForFlare.png";
                    String outputPath = "C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\images\\spellbookForFlare_with_text.png";
                    writeTextOnImage(imagePath, outputPath, word, 400);
                    
                    // Update the image label with the new image
                    setImageLabel(outputPath, 400);
                } else {
                    showInvalidWordMessage();
                    loseHeart();

                    // Reset combo multiplier
                    consecutiveValidWords = 0;
                    comboMultiplier = 1;
                }
                currentWord.setLength(0);
                inputField.setText("");
            }
        });

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\spelling.png");

        // Use the original icon for your component
        displayLabel = new JLabel(backgroundIcon, JLabel.CENTER);
        displayLabel.setHorizontalTextPosition(JLabel.CENTER);
        displayLabel.setVerticalTextPosition(JLabel.CENTER);
        displayLabel.setFont(customFont.deriveFont(Font.BOLD, 8f));
        displayLabel.setForeground(Color.WHITE);

        // Set bounds for displayLabel (x, y, width, height)
        displayLabel.setBounds(425, 43, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());

        // Add the display label to your panel
        backgroundPanel.setLayout(null);
        backgroundPanel.add(displayLabel);

        // Refresh the panel
        backgroundPanel.revalidate();
        backgroundPanel.repaint();

        // Load the cursor image
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImage = toolkit.getImage("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\cursor.png");

        // Ensure the image is loaded completely
        MediaTracker tracker = new MediaTracker(new Component() {});
        tracker.addImage(cursorImage, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Scale the image to the desired size, e.g., 32x32 pixels
        Image scaledCursorImage = cursorImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);

        // Create a custom cursor with the scaled image
        Cursor customCursor = toolkit.createCustomCursor(scaledCursorImage, new Point(0, 0), "Custom Cursor");

        // Set the custom cursor for the entire frame
        frame.setCursor(customCursor);

        // Revalidate and repaint the frame to ensure the cursor change is applied
        frame.revalidate();
        frame.repaint();

        // Load the default cursor image
        Image defaultCursorImage = toolkit.getImage("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\cursor.png");
        Image scaledDefaultCursorImage = defaultCursorImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        defaultCursor = toolkit.createCustomCursor(scaledDefaultCursorImage, new Point(0, 0), "Default Cursor");

        // Load the hover cursor image only once
        Image hoverCursorImage = toolkit.getImage("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\cursor1.png");
        hoverCursor = toolkit.createCustomCursor(
            hoverCursorImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 
            new Point(0, 0), 
            "Hover Cursor"
        );

        // Set the default cursor for the entire frame
        frame.setCursor(defaultCursor);

        // Assuming you have a list or array of letter tile buttons
        for (JButton letterButton : letterButtons) {
            // Add mouse listener to change cursor on hover
            letterButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    letterButton.setCursor(hoverCursor);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    letterButton.setCursor(defaultCursor);
                }
            });
        }

        // Revalidate and repaint the frame to ensure the cursor change is applied
        frame.revalidate();
        frame.repaint();

        // Initialize inventory with a health potion
        inventory.put("Health Potion", 1);

        // Create and set up the inventory button
        inventoryButton = new JButton("Open Inventory");
        inventoryButton.setBounds(-35, 500, 300, 30); // Adjust x, y, width, height as needed
        inventoryButton.setContentAreaFilled(false); // Make button transparent
        inventoryButton.setBorderPainted(false);
        inventoryButton.setFocusPainted(false);
        inventoryButton.setOpaque(false);
        inventoryButton.setFont(customFont.deriveFont(Font.BOLD, 10f));
        inventoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Set default cursor
        inventoryButton.setForeground(Color.WHITE);

        // Add action listener for the inventory button
        inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openInventory();
            }
        });
        backgroundPanel.add(inventoryButton);

        // Load the info image
        ImageIcon infoIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\info.png");
        JLabel infoLabel = new JLabel(infoIcon);
        infoLabel.setBounds(5, 507, infoIcon.getIconWidth(), infoIcon.getIconHeight()); // Adjust position as needed
        backgroundPanel.add(infoLabel);

        // Set hover cursor for the inventory button
        inventoryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                inventoryButton.setCursor(hoverCursor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                inventoryButton.setCursor(defaultCursor);
            }
        });

        // Initialize descriptionLabel
        descriptionLabel = new JLabel("");
        descriptionLabel.setBounds(50, 260, 150, 25); // Adjust as needed
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Temporary font

        initializeLetterValues();
    }

    private void initializeLetterValues() {
        // Assign values to each letter
        letterValues.put('a', 1);
        letterValues.put('b', 3);
        letterValues.put('c', 3);
        letterValues.put('d', 2);
        letterValues.put('e', 1);
        letterValues.put('f', 4);
        letterValues.put('g', 2);
        letterValues.put('h', 4);
        letterValues.put('i', 1);
        letterValues.put('j', 8);
        letterValues.put('k', 5);
        letterValues.put('l', 1);
        letterValues.put('m', 3);
        letterValues.put('n', 1);
        letterValues.put('o', 1);
        letterValues.put('p', 3);
        letterValues.put('q', 10);
        letterValues.put('r', 1);
        letterValues.put('s', 1);
        letterValues.put('t', 1);
        letterValues.put('u', 1);
        letterValues.put('v', 4);
        letterValues.put('w', 4);
        letterValues.put('x', 8);
        letterValues.put('y', 4);
        letterValues.put('z', 10);
    }

    private void updateHistoryArea() {
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCountMap.entrySet());
        sortedWords.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        StringBuilder history = new StringBuilder("Word History (sorted by frequency):\n");
        for (Map.Entry<String, Integer> entry : sortedWords) {
            history.append(entry.getKey()).append(" (").append(entry.getValue()).append(" times)\n");
        }
        // Update the image label with the new image
        setImageLabel("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\images\\spellbookForFlare_with_text.png", 400);
    }

    private int calculateScore(String word) {
        int baseScore = 0;
        for (char letter : word.toCharArray()) {
            baseScore += letterValues.getOrDefault(letter, 0);
        }

        // Apply variable word length bonus
        int lengthBonus = (int) Math.pow(2, word.length() - 3); // Exponential bonus for words longer than 3 letters

        // Calculate total score with combo multiplier
        int totalScore = (baseScore + lengthBonus) * comboMultiplier;

        return totalScore;
    }

    private void loadDictionary(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordList.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playSound(String soundFile) {
        try {
            File audioFile = new File(soundFile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Line not supported: " + format);
                return;
            }

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeTextOnImage(String imagePath, String outputPath, String text, int width) {
        try {
            // Load the existing image with previous words
            BufferedImage image = ImageIO.read(new File(outputPath));

            // Calculate font size based on image width
            int fontSize = width / 10;

            // Create a graphics context
            Graphics2D g2d = image.createGraphics();

            // Set rendering hints for better text quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Set font and color
            g2d.setFont(new Font("Times New Roman", Font.PLAIN, fontSize));
            g2d.setColor(new Color(50, 30, 10));

            // Write the numbered word at the current y-position
            g2d.drawString(wordCounter + ". " + text, 150, textYPosition);

            // Increment the y-position and word counter for the next word
            textYPosition += fontSize + 10; // Adjust spacing as needed
            wordCounter++;

            // Dispose the graphics context
            g2d.dispose();

            // Save the updated image
            ImageIO.write(image, "png", new File(outputPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageLabel(String imagePath, int width) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int height = (int) (originalImage.getHeight() * (width / (double) originalImage.getWidth()));
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetImage() {
        try {
            String originalPath = "C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\images\\spellbookForFlare.png";
            String outputPath = "C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\images\\spellbookForFlare_with_text.png";
            Files.copy(new File(originalPath).toPath(), new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateHealthDisplay() {
        createHealthBarImage(); // Recreate the health bar image
        healthPanel.removeAll();
        // Reload the combined health bar image
        try {
            BufferedImage updatedImage = ImageIO.read(new File("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\combined_healthbar.png"));
            healthPanel.add(new JLabel(new ImageIcon(updatedImage)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        healthPanel.revalidate();
        healthPanel.repaint();
    }

    private void loseHeart() {
        if (hearts > 0) {
            hearts--;
            System.out.println("Hearts remaining: " + hearts); // Debug print
            updateHealthDisplay(); // Update heart display
            if (hearts == 0) {
                JOptionPane.showMessageDialog(frame, "Game Over! You've lost all your hearts.");
                // Optionally, reset the game or disable further input
            }
        }
    }

    private void createHealthBarImage() {
        try {
            // Load the health bar background image
            BufferedImage healthBar = ImageIO.read(new File("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\healthbar.png"));

            // Load the heart image
            BufferedImage heart = ImageIO.read(new File("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\heart.png"));

            // Create a new image with the same dimensions as the health bar
            BufferedImage combinedImage = new BufferedImage(
                healthBar.getWidth(), healthBar.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Create a graphics 
            Graphics2D g2d = combinedImage.createGraphics();

            // Draw the health bar background
            g2d.drawImage(healthBar, 0, 0, null);

            // Adjustable parameters
            int heartWidth = 9; // Adjust heart width
            int heartHeight = 15; // Adjust heart height
            int spacing = 1; // Adjust spacing between hearts
            int startX = 10; // Adjust starting X position
            int startY = 8; // Adjust starting Y position

            // Draw the hearts
            for (int i = 0; i < hearts; i++) { // Draw based on current hearts
                g2d.drawImage(heart, startX + i * (heartWidth + spacing), startY, heartWidth, heartHeight, null);
            }

            // Dispose the graphics context
            g2d.dispose();

            // Save the combined image
            ImageIO.write(combinedImage, "png", new File("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\combined_healthbar.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Custom JPanel class to paint the background image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String filePath) {
            try {
                backgroundImage = ImageIO.read(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Method to update the display label text
    private void updateDisplayLabel(JLabel label, String text) {
        label.setText(text);
    }

    private void useHealthPotion() {
        if (inventory.getOrDefault("Health Potion", 0) > 0 && hearts < maxHearts) {
            hearts = Math.min(hearts + 1, maxHearts);
            inventory.put("Health Potion", inventory.get("Health Potion") - 1);
            updateHealthDisplay();
            
            // Call the showHealthPotionMessage method here
            showHealthPotionMessage(hearts);
        } else {
            // Call the showNoHealthPotionMessage method here
            showNoHealthPotionMessage();
        }
    }

    private void openInventory() {
        JFrame inventoryFrame = new JFrame("Inventory");
        inventoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inventoryFrame.setUndecorated(true); // Remove window borders
        inventoryFrame.setBackground(new Color(0, 0, 0, 0)); // Set background to transparent

        InventoryGUI inventoryGUI = new InventoryGUI();
        inventoryFrame.add(inventoryGUI);
        inventoryFrame.pack();
        inventoryFrame.setLocationRelativeTo(null); // Center the frame
        inventoryFrame.setVisible(true);

        // Add mouse listener for dragging
        inventoryGUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint(); // Initialize initialClick with the point of the mouse press
            }
        });

        inventoryGUI.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) { // Check if initialClick is initialized
                    // Get location of window
                    int thisX = inventoryFrame.getLocation().x;
                    int thisY = inventoryFrame.getLocation().y;

                    // Determine how much the mouse moved since the initial click
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    // Move window to this position
                    int X = thisX + xMoved;
                    int Y = thisY + yMoved;
                    inventoryFrame.setLocation(X, Y);
                }
            }
        });

        // Assuming you have a JButton for the "X" button
        JButton closeButton = new JButton("X");
        closeButton.setBounds(230, 10, 30, 40); // Adjust position and size as needed
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setOpaque(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener to close the inventory window
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryFrame.dispose(); // Close the inventory window
            }
        });

        // Add the close button to the inventory GUI
        inventoryGUI.add(closeButton);
    }

    // Inventory GUI class
    class InventoryGUI extends JPanel {
        private BufferedImage backgroundImage;
        private JLabel descriptionLabel; // Declare the JLabel

        public InventoryGUI() {
            setLayout(null);
            setPreferredSize(new Dimension(274, 300));
            setOpaque(false);

            try {
                backgroundImage = ImageIO.read(new File("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\inventory_background.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Initialize the description label
            descriptionLabel = new JLabel("");
            descriptionLabel.setBounds(40, 225, 150, 25); // Adjust position and size as needed
            descriptionLabel.setForeground(Color.WHITE); // Set text color
            add(descriptionLabel);

            ImageIcon potionIcon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\healthpotion.png");
            Image potionImage = potionIcon.getImage();
            Image scaledPotionImage = potionImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            potionIcon = new ImageIcon(scaledPotionImage);

            for (int i = 0; i < 12; i++) {
                JButton slot = new JButton();
                int gap = -17;
                int x = 28 + (i % 4) * (56 + gap);
                int y = 95 + (i / 4) * (56 + gap);
                
                slot.setBounds(x, y, 26, 26);
                slot.setContentAreaFilled(false);
                slot.setBorderPainted(false);
                slot.setOpaque(false);
                slot.setFocusPainted(false);
                slot.setFocusable(false);
                slot.setCursor(new Cursor(Cursor.HAND_CURSOR));

                if (i == 0 && inventory.getOrDefault("Health Potion", 0) > 0) {
                    potionSlot = slot;
                    slot.setIcon(potionIcon);
                    slot.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            isPotionSelected[0] = !isPotionSelected[0];
                            if (isPotionSelected[0]) {
                                slot.setBorder(new LineBorder(new Color(0, 0, 0, 0), 2));
                            descriptionLabel.setText("<html>Restores 1<br>heart</html>");
                            descriptionLabel.setFont(customFont.deriveFont(Font.BOLD, 9f)); // Set description
                            add(descriptionLabel);
                            } else {
                                slot.setBorder(null);
                                descriptionLabel.setText(""); // Clear description
                            }
                        }
                    });
                }

                add(slot);
            }

            JButton useButton = new JButton("Use");
            useButton.setBounds(170, 225, 40, 25);
            useButton.setContentAreaFilled(false);
            useButton.setBorderPainted(false);
            useButton.setOpaque(false);
            useButton.setFocusPainted(false);
            useButton.setFocusable(false);
            useButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            useButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isPotionSelected[0] && potionSlot != null) {
                        useHealthPotion();
                        isPotionSelected[0] = false;
                        potionSlot.setBorder(null);
                        descriptionLabel.setText(""); // Clear description after use
                    } else {
                        JOptionPane.showMessageDialog(null, "No potion selected!");
                    }
                }
            });
            add(useButton);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Method to load and return a custom font
    private Font loadCustomFont(String fontPath) {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont;
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to set the default font for all components
    private void setDefaultFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
        // Add more components as needed
    }

    private void showCustomMessage(String message, int score) {
        JDialog dialog = new JDialog(frame, "Message", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\msgcard.png");
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null); // Use null layout for absolute positioning
        panel.setPreferredSize(new Dimension(280, 100)); // Adjust height for three messages
        panel.setOpaque(false);

        JLabel validWordLabel = new JLabel(message, JLabel.CENTER);
        validWordLabel.setForeground(Color.WHITE);
        validWordLabel.setFont(customFont.deriveFont(Font.BOLD, 8f)); // Use custom font
        validWordLabel.setBounds(45, 15, 170, 30); // Set bounds for "Valid Word" message

        JLabel scoreLabel = new JLabel("You scored " + score, JLabel.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(customFont.deriveFont(Font.BOLD, 8f)); // Use custom font
        scoreLabel.setBounds(55, 35, 170, 30); // Set bounds for score message

        JLabel pointsLabel = new JLabel("points.", JLabel.CENTER);
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setFont(customFont.deriveFont(Font.BOLD, 8f)); // Use custom font
        pointsLabel.setBounds(25, 50, 170, 30); // Set bounds for "points" message below the score

        // Create a close button
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(250, 20, 40, 60); // Adjust position and size as needed
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener to the close button
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the dialog
            }
        });

        panel.add(validWordLabel);
        panel.add(scoreLabel);
        panel.add(pointsLabel); // Add the points label to the panel
        panel.add(closeButton); // Add the close button to the panel

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        final Point[] initialClick = {null};

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick[0] != null) {
                    int thisX = dialog.getLocation().x;
                    int thisY = dialog.getLocation().y;

                    int xMoved = e.getX() - initialClick[0].x;
                    int yMoved = e.getY() - initialClick[0].y;

                    int X = thisX + xMoved;
                    int Y = thisY + yMoved;
                    dialog.setLocation(X, Y);
                }
            }
        });

        dialog.setVisible(true); // Show the dialog
    }

    private void showInvalidWordMessage() {
        JDialog dialog = new JDialog(frame, "Message", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\warningcard.png");
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null); // Use null layout for absolute positioning
        panel.setPreferredSize(new Dimension(280, 100)); // Adjust height for three messages
        panel.setOpaque(false);

        JLabel invalidWordLabel = new JLabel("INVALID WORD.", JLabel.CENTER);
        invalidWordLabel.setForeground(Color.WHITE);
        invalidWordLabel.setFont(customFont.deriveFont(Font.BOLD, 8f)); // Use custom font
        invalidWordLabel.setBounds(12, 25, 260, 30); // Set bounds for the invalid word message

        JLabel TryAgainLabel = new JLabel("TRY AGAIN.", JLabel.CENTER);
        TryAgainLabel.setForeground(Color.WHITE);
        TryAgainLabel.setFont(customFont.deriveFont(Font.BOLD, 8f)); // Use custom font
        TryAgainLabel.setBounds(-3, 40, 260, 30); // Set bounds for the invalid word message


        // Create a close button
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(240, 15, 40, 80); // Adjust position and size as needed
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener to the close button
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the dialog
            }
        });

        panel.add(TryAgainLabel);
        panel.add(invalidWordLabel);
        panel.add(closeButton); // Add the close button to the panel

        dialog.add(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);

    // Make the dialog movable
    final Point[] initialClick = {null};

    panel.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            initialClick[0] = e.getPoint(); // Store the initial click point
        }
    });

    panel.addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (initialClick[0] != null) {
                int thisX = dialog.getLocation().x;
                int thisY = dialog.getLocation().y;

                int xMoved = e.getX() - initialClick[0].x;
                int yMoved = e.getY() - initialClick[0].y;

                dialog.setLocation(thisX + xMoved, thisY + yMoved); // Move the dialog
            }
        }
    });

    dialog.setVisible(true); // Show the dialog
}

    private void showHealthPotionMessage(int hearts) {
        JDialog dialog = new JDialog(frame, "Message", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\infocard.png");
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null); // Use null layout for absolute positioning
        panel.setPreferredSize(new Dimension(280, 100)); // Adjust height for three messages
        panel.setOpaque(false);

        JLabel potionMessageLabel = new JLabel("YOU USED A");
        potionMessageLabel.setForeground(Color.WHITE);
        potionMessageLabel.setFont(customFont.deriveFont(Font.BOLD, 8)); // Use custom font
        potionMessageLabel.setBounds(78, 25, 260, 30); // Set bounds for the potion message

        JLabel potionMessagesLabel = new JLabel("HEALTH POTION!");
        potionMessagesLabel.setForeground(Color.WHITE);
        potionMessagesLabel.setFont(customFont.deriveFont(Font.BOLD, 8)); // Use custom font
        potionMessagesLabel.setBounds(78, 40, 260, 30); // Set bounds for the potion message


        // Create a close button
        JButton closeButton = new JButton("OK");
        closeButton.setBounds(230, 25, 40, 80); // Adjust position and size as needed
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener to the close button
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the dialog
            }
        });

        panel.add(potionMessageLabel);
        panel.add(potionMessagesLabel);
        panel.add(closeButton); // Add the close button to the panel

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        // Make the dialog movable
        final Point[] initialClick = {null};

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint(); // Store the initial click point
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick[0] != null) {
                    int thisX = dialog.getLocation().x;
                    int thisY = dialog.getLocation().y;

                    int xMoved = e.getX() - initialClick[0].x;
                    int yMoved = e.getY() - initialClick[0].y;

                    dialog.setLocation(thisX + xMoved, thisY + yMoved); // Move the dialog
                }
            }
        });

        dialog.setVisible(true); // Show the dialog
    }

    private void showNoHealthPotionMessage() {
        JDialog dialog = new JDialog(frame, "Message", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\riadyn\\Documents\\NetBeansProjects\\JavaApplication1\\gui\\infocard.png");
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null); // Use null layout for absolute positioning
        panel.setPreferredSize(new Dimension(280, 100)); // Adjust height for three messages
        panel.setOpaque(false);

        JLabel messageLabel = new JLabel("HEARTS ARE", JLabel.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(customFont.deriveFont(Font.BOLD, 8f)); // Use custom font
        messageLabel.setBounds(0, 15, 260, 30); // Set bounds for the message
        

        JLabel fullLabel = new JLabel("FULL!", JLabel.CENTER);
        fullLabel.setForeground(Color.WHITE);
        fullLabel.setFont(customFont.deriveFont(Font.BOLD, 8f)); // Use custom font
        fullLabel.setBounds(-25, 35, 260, 30); // Set bounds for the message

        // Create a close button
        JButton closeButton = new JButton("OK");
        closeButton.setBounds(230, 25, 40, 80); // Adjust position and size as needed
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // Add action listener to the close button
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close the dialog
            }
        });

        panel.add(fullLabel);
        panel.add(messageLabel);
        panel.add(closeButton); // Add the close button to the panel

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        // Make the dialog movable
        final Point[] initialClick = {null};

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint(); // Store the initial click point
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick[0] != null) {
                    int thisX = dialog.getLocation().x;
                    int thisY = dialog.getLocation().y;

                    int xMoved = e.getX() - initialClick[0].x;
                    int yMoved = e.getY() - initialClick[0].y;

                    dialog.setLocation(thisX + xMoved, thisY + yMoved); // Move the dialog
                }
            }
        });

        dialog.setVisible(true); // Show the dialog
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookWormGame::new);
    }
}