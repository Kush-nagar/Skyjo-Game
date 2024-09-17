package manager;

import score.PlayerScore;
import cards.Card;
import java.util.ArrayList;
import java.util.Collections;

import Pile.DiscardPile;
import Pile.DrawPile;

public class RoundManager {
    private ArrayList<PlayerScore> playerScores;
    private ArrayList<ArrayList<Card>> playersCards;
    private ArrayList<ArrayList<PlayerScore>> allRoundScores; // Stores all scores by round
    @SuppressWarnings("unused")
    private int currentPlayer;
    private boolean isRoundActive;

    public RoundManager(ArrayList<PlayerScore> playerScores, ArrayList<ArrayList<Card>> playersCards) {
        this.playerScores = playerScores;
        this.playersCards = playersCards;
        this.allRoundScores = new ArrayList<>();
        this.isRoundActive = true;
    }

    public boolean hasPlayerRevealedAllCards(int playerIndex) {
        for (Card card : playersCards.get(playerIndex)) {
            // Skip null cards (cards that have been removed from the grid)
            if (card == null) {
                continue; // Ignore null cards as they are already removed
            }
            // If any non-null card is face down, return false
            if (!card.isFaceUp()) {
                return false;
            }
        }
        return true; // All cards are either revealed or removed
    }

    public void allowLastTurnForOtherPlayers(int revealingPlayerIndex) {
        for (int i = 0; i < playerScores.size(); i++) {
            if (i != revealingPlayerIndex) {
                // Check if this player has not revealed all cards
                if (!hasPlayerRevealedAllCards(i)) {
                    System.out.println("Player " + (i + 1) + " has one last turn to replace a card.");
                }
            }
        }
    }
    
    public boolean areAllOtherPlayersTurnsCompleted(int revealingPlayerIndex) {
        for (int i = 0; i < playerScores.size(); i++) {
            if (i != revealingPlayerIndex && !hasPlayerReplacedCard(i)) {
                return false; // Not all players have completed their turns
            }
        }
        return true; // All other players have completed their turns
    }
    
    private boolean hasPlayerReplacedCard(int playerIndex) {
        for (Card card : playersCards.get(playerIndex)) {
            // Skip null cards (cards removed from the grid)
            if (card == null) continue;
            
            // If any non-null card is face-up, it indicates the player has replaced a card
            if (card.isFaceUp()) {
                return true;
            }
        }
        return false;
    }   

    // Update the scores for all players and check for doubling the revealing
    // player's score
    public void updateScoresAndCheckForDoubling(int revealingPlayerIndex) {
        int revealingPlayerScore = playerScores.get(revealingPlayerIndex).getScore();
        int minScore = Integer.MAX_VALUE;

        // Find the minimum score from other players
        for (int i = 0; i < playerScores.size(); i++) {
            if (i != revealingPlayerIndex) {
                int score = playerScores.get(i).getScore();
                minScore = Math.min(minScore, score);
            }
        }

        // Double the score if the revealing player does not have the lowest score
        if (revealingPlayerScore > 0 && revealingPlayerScore > minScore) {
            playerScores.get(revealingPlayerIndex).doubleScore();
            System.out.println("Player " + (revealingPlayerIndex + 1) + "'s score is doubled!");
        }
    }

    public void startNewRound() {
        //saveCurrentRoundScores(); // Save the scores for the current round before resetting
        Card.initializeCards(); // Reinitialize the deck

        // Deal new cards to each player
        for (int i = 0; i < playersCards.size(); i++) {
            ArrayList<Card> newCards = Card.dealCards(12);
            playersCards.set(i, newCards);

            // Ensure only 2 cards are flipped for each player
            flipTwoRandomCards(playersCards.get(i));
        }

        // Recreate the draw and discard piles
        @SuppressWarnings("unused")
        DrawPile drawPile = new DrawPile();
        @SuppressWarnings("unused")
        DiscardPile discardPile = new DiscardPile();

        isRoundActive = true;
        System.out.println("A new round has started!");
    }

    // Helper function to save the current round's scores
    private void saveCurrentRoundScores() {
        ArrayList<PlayerScore> roundScores = new ArrayList<>();
        for (PlayerScore score : playerScores) {
            roundScores.add(new PlayerScore(score.getScore())); // Create a copy of the scores for this round
        }
        allRoundScores.add(roundScores); // Save scores for this round
    }

    private void flipTwoRandomCards(ArrayList<Card> playerCards) {
        @SuppressWarnings("unused")
        ArrayList<Integer> faceUpIndices = new ArrayList<>();

        // Ensure no cards are flipped at the start
        for (Card card : playerCards) {
            card.setFaceUp(false);
        }

        // Create a list of indices for the cards
        ArrayList<Integer> cardIndices = new ArrayList<>();
        for (int i = 0; i < playerCards.size(); i++) {
            cardIndices.add(i);
        }

        // Shuffle the indices to randomly pick two cards
        Collections.shuffle(cardIndices);

        // Flip the first 2 randomly chosen cards
        for (int i = 0; i < 2; i++) {
            int cardIndex = cardIndices.get(i);
            playerCards.get(cardIndex).setFaceUp(true);
        }
    }

    public boolean isRoundActive() {
        return isRoundActive;
    }

    public void endRound() {
        isRoundActive = false;
        saveCurrentRoundScores(); // Save the round's scores

        // Check if the game is over
        if (checkGameOver()) {
            System.out.println("Game over!");
            // Implement logic to handle game over, e.g., show the winner and stop the game
        } else {
            System.out.println("Proceed to the next round.");
        }
    }

    // Getter to retrieve scores of all rounds for the score card
    public ArrayList<ArrayList<PlayerScore>> getAllRoundScores() {
        return allRoundScores;
    }

    public int[] calculateTotalScores() {
        int[] totalScores = new int[playerScores.size()];

        // Iterate through all rounds and sum scores for each player
        for (ArrayList<PlayerScore> roundScores : allRoundScores) {
            for (int playerIndex = 0; playerIndex < playerScores.size(); playerIndex++) {
                totalScores[playerIndex] += roundScores.get(playerIndex).getScore();
            }
        }
        return totalScores;
    }

    public boolean checkGameOver() {
        int[] totalScores = calculateTotalScores();
        int maxScore = 0;
        int minScore = Integer.MAX_VALUE;
        int minScorePlayer = -1;

        // Find the maximum and minimum scores
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] > maxScore) {
                maxScore = totalScores[i];
            }
            if (totalScores[i] < minScore) {
                minScore = totalScores[i];
                minScorePlayer = i;
            }
        }

        // Check if any player has reached 100 or more points
        if (maxScore >= 100) {
            System.out.println("Player " + (minScorePlayer + 1) + " wins with the least score of " + minScore + "!");
            return true; // Game over, declare winner
        }

        return false; // Continue playing
    }

    public boolean isRoundFinished() {
        return !isRoundActive;
    }
}
