/*
 * Kaibo Huang
 * Date: 2024/05/21
 * This class handles all the graphics and content on the More Info Page
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

class MoreInfoPage extends JPanel {
	
	//initialize all panels and buttons
	JPanel topPanel;
	JButton backButton;
	JPanel alignmentPanel;
	JPanel contentPanel;
	JPanel rulesPanel;
	JPanel controlsPanel;

	// Constructor takes a reference to the parent GameFrame
	public MoreInfoPage(GameFrame parentFrame) {
		this.setLayout(new BorderLayout()); // Set layout manager to BorderLayout
		this.setBackground(Color.WHITE); // Set background color to white

		// Create top panel for header and back button
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Create back button
		backButton = new JButton("<<<");
		backButton.setFont(new Font("Arial", Font.BOLD, 24));
		backButton.setPreferredSize(new Dimension(80, 50));
		backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		backButton.addActionListener(e -> {
			playSound("Music/Click.wav"); // Play sound on button click
			parentFrame.remove(this); // Remove the MoreInfoPage panel
			parentFrame.createStartPanel(); // Recreate the start panel
			parentFrame.add(parentFrame.startPanel); // Add the start panel to the frame
			parentFrame.revalidate(); // Refresh the frame
			parentFrame.repaint(); // Repaint the frame
		});

		// Create panel for horizontal alignment
		alignmentPanel = new JPanel(new BorderLayout());
		alignmentPanel.setOpaque(false); // Make panel transparent
		alignmentPanel.add(backButton, BorderLayout.WEST); // Add back button to the left

		// Add alignment panel to top panel
		topPanel.add(alignmentPanel, BorderLayout.WEST);

		// Add top panel to the north region of the MoreInfoPage
		this.add(topPanel, BorderLayout.NORTH);

		// Create main content panel for rules and controls
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Set layout manager to BoxLayout
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Create section panel for rules and add to content panel
		rulesPanel = createSectionPanel("Rules", "1. Serve the ball by pressing SPACE.\n\n"
				+ "2. Use the paddles to hit the ball back and forth.\n\n"
				+ "3. The ball bounces off the top and bottom walls.\n\n"
				+ "4. The game ends when a player scores 5 points.\n\n"
				+ "5. Points are gained when the ball touches the opponent's side.\n\n"
				+ "6. The player who lost the previous point serves the ball next.\n\n"
				+ "7. If you hit the ball WHILE the paddle is in motion, you will increase the ball's y-velocity in that direction.\n\n"
				+ "8. The game will display the winner and provide options to restart or return to the main menu after a player wins.");
		contentPanel.add(rulesPanel);

		// Create section panel for controls and add to content panel
		controlsPanel = createSectionPanelWithKeys("Controls", "Player 1:\nW - Move up\nS - Move down",
				"Player 2:\nI - Move up\nK - Move down");
		contentPanel.add(controlsPanel);

		// Add content panel to the center region of the MoreInfoPage
		this.add(contentPanel, BorderLayout.CENTER);
	}

	// Method to create a panel section with a header and content text
	private JPanel createSectionPanel(String headerText, String contentText) {
		JPanel panel = new JPanel(new BorderLayout()); // Create a JPanel with BorderLayout
		panel.setBackground(Color.WHITE); // Set the background color of the panel to white
		panel.setBorder(BorderFactory.createCompoundBorder( // Set a compound border for the panel
				BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), // Create a line border with light gray color
				BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Create an empty border with padding

		JLabel header = new JLabel(headerText); // Create a JLabel for the header with the specified text
		header.setFont(new Font("Arial", Font.BOLD, 28)); // Set the font of the header
		header.setHorizontalAlignment(JLabel.CENTER); // Set the horizontal alignment of the header to center
		panel.add(header, BorderLayout.NORTH); // Add the header to the north region of the panel

		JTextArea content = new JTextArea(contentText); // Create a JTextArea for the content with the specified text
		content.setFont(new Font("Arial", Font.PLAIN, 18)); // Set the font of the content
		content.setEditable(false); // Set the content area as not editable
		content.setWrapStyleWord(true); // Enable word wrapping in the content area
		content.setLineWrap(true); // Enable line wrapping in the content area
		content.setOpaque(false); // Make the content area transparent
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Set an empty border with padding for the
																			// content area
		panel.add(content, BorderLayout.CENTER); // Add the content area to the center region of the panel

		return panel; // Return the created panel
	}

	// Method to create a panel section with controls for both players
	private JPanel createSectionPanelWithKeys(String headerText, String contentText1, String contentText2) {
		JPanel panel = new JPanel(new BorderLayout()); // Create a JPanel with BorderLayout
		panel.setBackground(Color.WHITE); // Set the background color of the panel to white
		panel.setBorder(BorderFactory.createCompoundBorder( // Set a compound border for the panel
				BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), // Create a line border with light gray color
				BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Create an empty border with padding

		JLabel header = new JLabel(headerText); // Create a JLabel for the header with the specified text
		header.setFont(new Font("Arial", Font.BOLD, 28)); // Set the font of the header
		header.setHorizontalAlignment(JLabel.CENTER); // Set the horizontal alignment of the header to center
		panel.add(header, BorderLayout.NORTH); // Add the header to the north region of the panel

		JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // Create a JPanel with GridLayout for the
																		// content panel
		contentPanel.setOpaque(false); // Make the content panel transparent

		// Create control panels for player 1 and player 2
		JPanel player1Panel = createControlPanel("Player 1 (Left Padel)", "W - Move up", "S - Move down");
		JPanel player2Panel = createControlPanel("Player 2 (Right Padel)", "I - Move up", "K - Move down");

		contentPanel.add(player1Panel); // Add the control panel for player 1 to the content panel
		contentPanel.add(player2Panel); // Add the control panel for player 2 to the content panel

		panel.add(contentPanel, BorderLayout.CENTER); // Add the content panel to the center region of the panel

		return panel; // Return the created panel
	}

	// Method to create a control panel for each player
	private JPanel createControlPanel(String player, String upControl, String downControl) {
		JPanel panel = new JPanel(); // Create a JPanel for the control panel
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Set the layout manager to BoxLayout with vertical
																	// alignment
		panel.setBackground(Color.WHITE); // Set the background color of the panel to white
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Create an empty border with padding

		JLabel playerLabel = new JLabel(player); // Create a JLabel for the player label with the specified text
		playerLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set the font of the player label
		playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Set the horizontal alignment of the player label to
																// center

		JLabel upLabel = new JLabel(upControl); // Create a JLabel for the up control with the specified text
		upLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set the font of the up control label
		upLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Set the horizontal alignment of the up control label to
															// center

		JLabel downLabel = new JLabel(downControl); // Create a JLabel for the down control with the specified text
		downLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set the font of the down control label
		downLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Set the horizontal alignment of the down control label
																// to center

		panel.add(playerLabel); // Add the player label to the control panel
		panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add a rigid area for spacing
		panel.add(upLabel); // Add the up control label to the control panel
		panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add a rigid area for spacing
		panel.add(downLabel); // Add the down control label to the control panel

		return panel; // Return the created control panel
	}

	// Method to play short sound effects
	private void playSound(String soundFile) {
		try {
			File file = new File(soundFile); // Create a File object with the specified sound file path
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(file); // Get an audio input stream from the
																					// file
			Clip clip = AudioSystem.getClip(); // Get a Clip object for playing audio
			clip.open(audioStream); // Open the audio clip
			clip.start(); // Start playing the audio clip
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace(); // Print stack trace if an exception occurs
		}
	}

}
