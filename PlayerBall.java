/*
 * Kaibo Huang
 * Date: 2024/05/21
 * This class defines behaviors for the ball  
 */

import java.awt.*;

public class PlayerBall extends Rectangle {

	// variables to store the x and y velocity
	public int yVelocity;
	public int xVelocity;
	public final int SPEED = 20; // movement speed of ball
	public static final int BALL_DIAMETER = 20; // size of ball

	// constructor creates ball at given location with given dimensions
	public PlayerBall(int x, int y) {
		super(x, y, BALL_DIAMETER, BALL_DIAMETER);

	}

	// method that updates the current location of the ball
	public void move() {
		y = y + yVelocity;
		x = x + xVelocity;
	}

	
	// draws the current location of the ball to the screen
	public void draw(Graphics g) {
		g.setColor(Color.black);
		g.fillOval(x, y, BALL_DIAMETER, BALL_DIAMETER);
	}

}