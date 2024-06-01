/*
 * Kaibo Huang
 * Date: 2024/05/21
 * Padel1 class defines behaviors for the player-controlled paddle
 */

import java.awt.*; 
import java.awt.event.*; 


public class Padel extends Rectangle {

	// Velocity and speed of the paddle
		public int yVelocity;
		public final int SPEED = 10;

		// Dimensions of the paddle
		public static final int PADEL_LENGTH = 10;
		public static final int PADEL_WIDTH = 150;
		
		//variables to store the controls for the padel
		private char up, down;

		// Temporary y-coordinate for boundary checking
		private int tempY;

		// Constructor creates paddle at given location with given dimensions
		public Padel(int x, int y, char U, char D) {
			super(x, y, PADEL_LENGTH, PADEL_WIDTH);
			up = U;
			down = D;
		}

		// Updates the direction of the paddle based on user input
		
		public void keyPressed(KeyEvent e) {
			//move padel up when i is pressed
			if (e.getKeyChar() == up) {
				setYDirection(SPEED * -1);
				move();
			}
			//move padel down when k is pressed
			if (e.getKeyChar() == down) {
				setYDirection(SPEED);
				move();
			}
		}

		
		// Stops the paddle movement when 'w' or 's' is released
		public void keyReleased(KeyEvent e) {
			if (e.getKeyChar() == up || e.getKeyChar() == down) {
				setYDirection(0);
				move();
			}
		}

		// Sets the vertical direction of the paddle
		public void setYDirection(int yDirection) {
			yVelocity = yDirection;
		}

		// method that Updates the current location of the paddle
		public void move() {
			tempY = y + yVelocity;

			// Ensure the paddle stays within the screen bounds, considering the top border
			if (tempY >= 0 && tempY <= GamePanel.GAME_HEIGHT - PADEL_WIDTH) {
				y = tempY;
			} else if (tempY < 0) { // Adjusts if the paddle tries to move beyond the top border
				y = 0;
			}
		}

		// Draws the current location of the paddle to the screen
		public void draw(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(x, y, PADEL_LENGTH, PADEL_WIDTH);
		}
	}
