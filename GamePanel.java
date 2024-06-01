/*
 * Kaibo Huang
 * Date: 2024/05/21
 * This class represents the main game panel for a Pong game, handling rendering, 
 * logic, and user input. It includes methods for initializing game components, managing game state, 
 * and playing sound effects and music.
 */
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable, KeyListener {
	// Constants for game dimensions and initial ball velocity
	public static final int GAME_WIDTH = 1280;
	public static final int GAME_HEIGHT = 780;
	public static final int BALL_INITIAL_VELOCITY = 9;
	public static final String START_MESSAGE = "Click SPACE to Serve";

	// Thread for running the game loop
	private Thread gameThread;
	// Off-screen image for double buffering
	private Image image;
	// Graphics object for drawing on the off-screen image
	private Graphics graphics;

	// Game components: ball, paddles, and score
	private PlayerBall ball;
	private Padel padel1;
	private Padel padel2;
	private Score score;

	// Flags to control game state
	private boolean gameStarted = false;
	private boolean showStartMessage = true;
	private boolean gameOver = false;
	// Message to display the winner
	private String winnerMessage = "";
	// Last player who won a point
	private int lastWinningPlayer; // 1 for player 1, 2 for player 2

	// Buttons for play again and main menu
	private JButton playAgainButton;
	private JButton mainMenuButton;
	// Panel to hold buttons
	private JPanel buttonPanel;

	// Static clips for background music
	static Clip menu, game;

	// Constructor to initialize game panel
	public GamePanel() {
		setLayout(null); // Use absolute positioning for all components
		score = new Score(); // Initialize score
		ball = new PlayerBall(GAME_WIDTH / 2 - PlayerBall.BALL_DIAMETER / 2,
				GAME_HEIGHT / 2 - PlayerBall.BALL_DIAMETER / 2); // Initialize ball
		padel1 = new Padel(0, (GAME_HEIGHT / 2) - Padel.PADEL_WIDTH / 2, 'w', 's'); // Initialize paddle 1
		padel2 = new Padel(GAME_WIDTH - 26, (GAME_HEIGHT / 2) - Padel.PADEL_WIDTH / 2, 'i', 'k'); // Initialize
																											// paddle 2
		playAgainButton = new JButton("Play Again"); // Initialize play again button
		mainMenuButton = new JButton("Main Menu"); // Initialize main menu button

		playAgainButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Set font for play again button
		mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 24)); // Set font for main menu button

		playAgainButton.addActionListener(e -> {
			playSound("Music/Click.wav"); // Play sound on button click
			restartGame(); // Restart the game
		});

		mainMenuButton.addActionListener(e -> {
			playSound("Music/Click.wav"); // Play sound on button click
			returnToMainMenu(); // Return to main menu
		});

		playAgainButton.setVisible(false); // Hide play again button initially
		mainMenuButton.setVisible(false); // Hide main menu button initially

		buttonPanel = new JPanel(); // Create a panel to hold buttons
		buttonPanel.setLayout(new FlowLayout()); // Set layout for button panel
		buttonPanel.setBounds(GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 + 50, 300, 100); // Set bounds for button panel
		buttonPanel.setOpaque(false); // Make the panel transparent

		buttonPanel.add(playAgainButton); // Add play again button to panel
		buttonPanel.add(mainMenuButton); // Add main menu button to panel
		
		this.add(buttonPanel); // Add button panel to the game panel
		
		setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT)); // Set preferred size
		setFocusable(true); // Make the panel focusable
		addKeyListener(this); // Add key listener to the panel

		gameThread = new Thread(this); // Create and start game thread
		gameThread.start();
		lastWinningPlayer = (int) (Math.random() * 2 + 1);
		; // Initialize last winning player randomly with either player 1 or 2
	}

	@Override
	// method to double buffer
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Call superclass method
		image = createImage(GAME_WIDTH, GAME_HEIGHT); // Create off-screen image
		graphics = image.getGraphics(); // Get graphics context for off-screen image
		draw(graphics); // Draw game elements
		g.drawImage(image, 0, 0, this); // Draw off-screen image to screen
	}

	// Method to draw game elements
	private void draw(Graphics g) {
		ball.draw(g); // Draw ball
		padel1.draw(g); // Draw paddle 1
		padel2.draw(g); // Draw paddle 2
		
		//display serve message
		if (showStartMessage && !gameOver) {
			// change color and font
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 36));
			FontMetrics fm = g.getFontMetrics();

			// Draw start message
			g.drawString(START_MESSAGE, (GAME_WIDTH - fm.stringWidth(START_MESSAGE)) / 2, GAME_HEIGHT - 50);

			// Draw arrow indicating serve direction
			if (lastWinningPlayer == 1) {
				drawServeArrow(g, true); // Serve towards player 2
			} else if (lastWinningPlayer == 2) {
				drawServeArrow(g, false); // Serve towards player 1
			}
			//display winner message
		} else if (gameOver) {
			// change color and font
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, 36));
			FontMetrics fm = g.getFontMetrics();
			
			//draw winner message
			g.drawString(winnerMessage, (GAME_WIDTH - fm.stringWidth(winnerMessage)) / 2, GAME_HEIGHT / 2 - 50); 
			
			playAgainButton.setVisible(true); // Show play again button
			mainMenuButton.setVisible(true); // Show main menu button
		}

		score.draw(g); // Draw score
	}

	// Method to move game elements
	private void move() {
		if (gameStarted && !gameOver) {
			ball.move(); // Move ball if game started and not over
		}
		padel1.move(); // Move paddle 1
		padel2.move(); // Move paddle 2
	}

	// Method to check collisions
	private void checkCollision() {
		//if ball hits the top edge
		if (ball.y <= 0) {
			ball.y = 0; //set the balls position to the top
			ball.yVelocity = -ball.yVelocity; // Reverse ball's Y velocity at the top
		}
		//if ball hits bottom edge
		if (ball.y + PlayerBall.BALL_DIAMETER + 28 >= GAME_HEIGHT) {
			ball.y = GAME_HEIGHT - PlayerBall.BALL_DIAMETER - 28; // Ensure ball doesn't go beyond the bottom by resetting position
			ball.yVelocity = -ball.yVelocity; // Reverse ball's Y velocity at the bottom
		}
		//if the ball hits player 1 padel
		if (ball.intersects(padel1)) {
			ball.xVelocity = -ball.xVelocity; // Reverse ball's X velocity on paddle 1 hit
			ball.yVelocity += padel1.yVelocity / 5 + (-1 + (int) (Math.random() * (3))); // Adjust ball's Y velocity
			playSound("Music/Ping.wav"); // Play ping sound
		}
		//if the ball hits player 2 padel
		if (ball.intersects(padel2)) {
			ball.xVelocity = -ball.xVelocity; // Reverse ball's X velocity on paddle 2 hit
			ball.yVelocity += padel2.yVelocity / 5 + (-1 + (int) (Math.random() * (3))); // Adjust ball's Y velocity
			playSound("Music/Ping.wav"); // Play ping sound
		}
		
		//if the ball reaches the left side of the screen
		if (ball.x <= 0) {
			score.player2Score++; // Increment player 2 score
			lastWinningPlayer = 1; // Update last winning player
			playSound("Music/Lost.wav"); // Play lost sound
			checkWinCondition(); // Check if game is won
			resetGame(); // Reset game
		}
		//if the ball reaches the right side of the screen
		if (ball.x + PlayerBall.BALL_DIAMETER >= GAME_WIDTH) {
			score.player1Score++; // Increment player 1 score
			lastWinningPlayer = 2; // Update last winning player
			playSound("Music/Lost.wav"); // Play lost sound
			checkWinCondition(); // Check if game is won
			resetGame(); // Reset game
		}
	}

	@Override
	//method which makes the game continue running without end
	public void run() {
		long lastTime = System.nanoTime(); // Get initial time
		double amountOfTicks = 60.0; // Number of ticks per second
		double ns = 1000000000 / amountOfTicks; // Nanoseconds per tick
		double delta = 0;

		while (true) { // Game loop
			long now = System.nanoTime(); // Get current time
			delta += (now - lastTime) / ns; // Calculate time difference
			lastTime = now;

			if (delta >= 1) {
				move(); // Move game elements
				if (gameStarted && !gameOver) {
					checkCollision(); // Check collisions if game started and not over
				}
				repaint(); // Repaint the game panel
				delta--;
			}
		}
	}

	@Override
	//method to check if a key is pressed
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			//if the game has not started and the game is not yet over
			if (!gameStarted && !gameOver) {
				// Adjust ball serve direction based on the last winning player
				if (lastWinningPlayer == 1) {
					ball.xVelocity = -BALL_INITIAL_VELOCITY; // Serve ball to the left
				} else if (lastWinningPlayer == 2) {
					ball.xVelocity = BALL_INITIAL_VELOCITY; // Serve ball to the right
				}

				playSound("Music/Serve.wav"); // Play serve sound
				gameStarted = true; // Set game started flag
				showStartMessage = false; // Hide start message
			}
		}
		padel1.keyPressed(e); // Handle paddle 1 key press
		padel2.keyPressed(e); // Handle paddle 2 key press
	}

	@Override
	//method to check if a key is released
	public void keyReleased(KeyEvent e) {
		padel1.keyReleased(e); // Handle paddle 1 key release
		padel2.keyReleased(e); // Handle paddle 2 key release
	}

	@Override
	//method not used but required for KeyListener
	public void keyTyped(KeyEvent e) {
		
	}
	// Method to play short sound clips
	private void playSound(String soundFile) {
		try {
			// open theme song file
			File file = new File(soundFile);
			
			 // Get audio input stream
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
			// Open and play the theme song
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.start(); // Start playing the sound
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	// Method to play menu music
	public static void playMenu() {
		try {
			File file = new File("Music/MainMenu.wav"); // Open menu music file
			AudioInputStream audio = AudioSystem.getAudioInputStream(file); // Get audio input stream
			menu = AudioSystem.getClip(); // Get a clip for playing audio

			menu.open(audio); // Open the audio clip
			menu.addLineListener(event -> {
				// Restart the music if it stops
				if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
					menu.setMicrosecondPosition(0);
					menu.start();
				}
			});
			menu.start(); // Start playing menu music
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to stop playing the menu music
	public static void stopMenu() {
		if (menu != null && menu.isOpen()) {
			menu.stop(); // Stop the menu music
			menu.setMicrosecondPosition(0); // Reset its position to the beginning
			menu.close(); // Close the clip
		}
	}

	// Method to play game music
	public static void playGame() {
		try {
			File file = new File("Music/Game.wav"); // Open game music file
			AudioInputStream audio = AudioSystem.getAudioInputStream(file); // Get audio input stream
			game = AudioSystem.getClip(); // Get a clip for playing audio

			game.open(audio); // Open the audio clip
			game.addLineListener(event -> {
				// Restart the theme song if it stops
				if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
					game.setMicrosecondPosition(0);
					game.start();
				}
			});
			game.start(); // Start playing game music
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to stop playing the game music
	public static void stopGame() {
		if (game != null && game.isOpen()) {
			game.stop(); // Stop the game music
			game.setMicrosecondPosition(0); // Reset its position to the beginning
			game.close(); // Close the clip
		}
	}

	// Method to check if a player has won the game
	private void checkWinCondition() {
		if (score.player1Score >= 5) {
			gameOver = true; // Set game over flag
			winnerMessage = "Player 1 has won!"; // Set winner message
			showStartMessage = false; // Hide start message
		} else if (score.player2Score >= 5) {
			gameOver = true; // Set game over flag
			winnerMessage = "Player 2 has won!"; // Set winner message
			showStartMessage = false; // Hide start message
		}

		if (gameOver) {
			playAgainButton.setVisible(true); // Show play again button
			mainMenuButton.setVisible(true); // Show main menu button
			stopGame(); // Stop game music
			playMenu(); // Play menu music
		}
	}

	// Method to reset game state for a new round
	private void resetGame() {
		if (!gameOver) {
			ball.x = GAME_WIDTH / 2 - PlayerBall.BALL_DIAMETER / 2; // Reset ball position
			ball.y = GAME_HEIGHT / 2 - PlayerBall.BALL_DIAMETER / 2;
			ball.xVelocity = 0; // Reset ball velocity
			ball.yVelocity = 0;
			gameStarted = false; // Reset game started flag
			showStartMessage = true; // Show start message

			padel1.x = 0; // Reset paddle 1 position
			padel1.y = (GAME_HEIGHT / 2) - Padel.PADEL_WIDTH / 2;

			padel2.x = GAME_WIDTH - 26; // Reset paddle 2 position
			padel2.y = (GAME_HEIGHT / 2) - Padel.PADEL_WIDTH / 2;
		}
	}

	// Method to restart the game
	private void restartGame() {
		stopMenu(); // Stop menu music
		playGame(); // Play game music
		score.player1Score = 0; // Reset player 1 score
		score.player2Score = 0; // Reset player 2 score
		gameOver = false; // Reset game over flag
		winnerMessage = ""; // Clear winner message
		playAgainButton.setVisible(false); // Hide play again button
		mainMenuButton.setVisible(false); // Hide main menu button
		resetGame(); // Reset game state
		repaint(); // Repaint the game panel
	}

	// Method to return to the main menu
	private void returnToMainMenu() {
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.remove(this); // Remove current game panel
		((GameFrame) topFrame).showStartPanel(); // Show start panel
		topFrame.revalidate(); // Revalidate the frame
		topFrame.repaint(); // Repaint the frame
	}

	// Method to draw serve arrow
	private void drawServeArrow(Graphics g, boolean towardsLeft) {
		int arrowX = (GAME_WIDTH - PlayerBall.BALL_DIAMETER) / 2;
		int arrowY = GAME_HEIGHT / 2;
		g.setFont(new Font("Arial", Font.BOLD, 36)); // Larger font for arrow

		if (!towardsLeft) {
			g.drawString(">>>", arrowX + 30, arrowY + 12); // Arrow pointing to the right
		} else {
			g.drawString("<<<", arrowX - 75, arrowY + 12); // Arrow pointing to the left
		}
	}
}
