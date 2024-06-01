/*
 * Kaibo Huang
 * Date: 2024/05/21
 * This class constructs the main window for a Pong game, manages game initialization, 
 * and controls the transition between the start screen and the game panel. It includes methods 
 * for handling user input, displaying the start screen, starting the game, and playing sound effects.
 */

import java.awt.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

// GameFrame class extending JFrame to create the main game window
public class GameFrame extends JFrame {

	GamePanel gamePanel; // The panel where the game is played
	JPanel startPanel; // The panel for the start screen

	// initialize buttons, labels, and panels
	JButton playButton, moreInfoButton;
	JLabel titleLabel;
	MoreInfoPage moreInfoPanel;

	// Constructor for GameFrame
	public GameFrame() {
		this.setTitle("Pong"); // Set the title of the window
		this.setResizable(false); // Disable resizing
		this.setBackground(Color.white); // Set background color
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close application on window close
		this.setSize(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT); // Set the window size

		createStartPanel(); // Initialize the start panel

		this.add(startPanel); // Add the start panel to the frame
		this.setLocationRelativeTo(null); // Center the window on the screen
		this.setVisible(true); // Make the window visible
		GamePanel.playMenu(); // Play the menu music
	}

	// Method to create the start panel
	void createStartPanel() {
		startPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawStartScreen(g); // Custom method to draw the start screen
			}
		};

		startPanel.setLayout(null); // Use absolute positioning for components

		// Create title label
		titleLabel = new JLabel("Pong");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 150)); // Set font size and style

		titleLabel.setBounds((GamePanel.GAME_WIDTH - titleLabel.getPreferredSize().width) / 2, 200,
				titleLabel.getPreferredSize().width, 170); // Set position and size of the label
		startPanel.add(titleLabel); // Add title label to the start panel

		// Create play button
		playButton = createButton("Play", 240, 80, 400);
		
		//remove textbox highlight
		playButton.setFocusable(false);
		
		playButton.addActionListener(e -> startGame()); // Start game on button click
		startPanel.add(playButton); // Add play button to the start panel

		// Create "More Info" button
		moreInfoButton = createButton("More Info", 240, 80, 500);
		
		//remove textbox highlight
		moreInfoButton.setFocusable(false);
		
		moreInfoButton.addActionListener(e -> openMoreInfoPage()); // Open more info page on button click
		startPanel.add(moreInfoButton); // Add more info button to the start panel
	}

	// Method to create a button with specified text, size, and position
	private JButton createButton(String text, int width, int height, int y) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.PLAIN, 24)); // Set font size and style
		button.setBounds((GamePanel.GAME_WIDTH - width) / 2, y, width, height); // Set position and size of the button
		button.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Set black border
		button.addActionListener(e -> playSound("Music/Click.wav")); // Play click sound on button click
		return button; // Return the created button
	}

	// Method to start the game
	private void startGame() {
		remove(startPanel); // Remove the start panel
		gamePanel = new GamePanel(); // Create a new game panel
		this.add(gamePanel); // Add the game panel to the frame
		revalidate(); // Refresh the frame
		gamePanel.requestFocusInWindow(); // Set focus on the game panel

		GamePanel.stopMenu(); // Stop the menu music
		GamePanel.playGame(); // Start the game music
	}

	// Method to draw the start screen
	private void drawStartScreen(Graphics g) {
		g.setColor(Color.WHITE); // Set color to white
		g.fillRect(0, 0, getWidth(), getHeight()); // Draw the background

	}

	// Method to open the "More Info" page
	private void openMoreInfoPage() {
		remove(startPanel); // Remove the start panel
		moreInfoPanel = new MoreInfoPage(this); // Create the more info panel
		this.add(moreInfoPanel); // Add the more info panel to the frame
		revalidate(); // Refresh the frame
		moreInfoPanel.requestFocusInWindow(); // Set focus on the more info panel
	}

	// Method to show the start panel
	public void showStartPanel() {
		if (gamePanel != null) {
			remove(gamePanel); // Remove the game panel if it exists
		}
		this.add(startPanel); // Add the start panel to the frame
		revalidate(); // Refresh the frame
		repaint(); // Repaint the frame
		startPanel.requestFocusInWindow(); // Set focus on the start panel
	}

	// Method to play short sound from a specified file
	private void playSound(String soundFile) {
		try {
			File file = new File(soundFile); // Load the sound file
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(file); // Get audio input stream
			Clip clip = AudioSystem.getClip(); // Get a clip for playing audio
			clip.open(audioStream); // Open the audio clip
			clip.start(); // Start playing the audio
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace(); // Print the exception if any
		}
	}
}
