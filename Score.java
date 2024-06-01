/*
 * Kaibo Huang
 * Date: 2024/05/21
 * Scpre class defines behaviors for keeping track of the Score
 */
import java.awt.Color; 
import java.awt.Font; 
import java.awt.Graphics; 

public class Score {
    public int player1Score; // Variable to store Player 1's score
    public int player2Score; // Variable to store Player 2's score

    // Constructor to initialize the scores to 0
    public Score() {
        player1Score = 0; // Initialize Player 1's score
        player2Score = 0; // Initialize Player 2's score
    }

    // Method to draw the scores on the screen
    public void draw(Graphics g) {
        g.setColor(Color.BLACK); // Set the color for drawing the text to black
        g.setFont(new Font("Arial", Font.BOLD, 36)); // Set the font to Arial, bold, size 36
        // Draw Player 1's score at 1/4th of the game width, 50 pixels from the top
        g.drawString(String.valueOf(player1Score), GamePanel.GAME_WIDTH / 4, 50);
        // Draw Player 2's score at 3/4th of the game width, 50 pixels from the top
        g.drawString(String.valueOf(player2Score), GamePanel.GAME_WIDTH * 3 / 4, 50);
    }
}
