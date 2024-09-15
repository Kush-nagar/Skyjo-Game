package score;

import java.util.ArrayList;
import cards.Card;

public class PlayerScore {
    private int score;

    // Default constructor
    public PlayerScore() {
        this.score = 0;
    }

    // Constructor to initialize with a given score (for copying purposes)
    public PlayerScore(int score) {
        this.score = score;
    }

    // Update the score based on face-up cards
    public void updateScore(ArrayList<Card> playerCards) {
        int newScore = 0;
        for (Card card : playerCards) {
            if (card != null && card.isFaceUp()) {  // Ensure the card is not null and is face-up
                newScore += card.getValue();
            }
        }
        this.score = newScore;
    }

    // Getter for score
    public int getScore() {
        return score;
    }

    // Method to double the score
    public void doubleScore() {
        this.score *= 2;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
}