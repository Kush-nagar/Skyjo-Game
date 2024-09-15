package layout;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import cards.Card;
import Pile.DrawPile;
import Pile.DiscardPile;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import score.PlayerScore;
import manager.RoundManager;

public class PlayerLayout extends JPanel implements MouseListener, MouseMotionListener {
    private ArrayList<ArrayList<Card>> playersCards;
    private int numPlayers;
    private int currentPlayer;
    private boolean cardDrawn;
    @SuppressWarnings("unused")
    private DrawPile cardPile;
    private JLabel instructionLabel;
    private Card drawnCard;
    private boolean isDragging;
    private int dragOffsetX, dragOffsetY;
    private DiscardPile discardPile;
    private JButton nextPlayerButton;
    private boolean canDraw; // New variable to track if the player can draw
    private boolean flipCardPromptActive;
    private boolean hasReplacedCard; // Tracks if the player has replaced a card this turn
    private ArrayList<PlayerScore> playerScores; // Track each player's score
    private RoundManager roundManager;
    private int roundNumber = 1; // Add a variable to track the current round
    private BufferedImage backgroundImage;


    public PlayerLayout(int numPlayers) {
        this.numPlayers = numPlayers;
        this.currentPlayer = 0;
        this.cardDrawn = false;
        this.isDragging = false;
        this.canDraw = true;
        this.flipCardPromptActive = false;
        this.hasReplacedCard = false;

        playersCards = new ArrayList<>();
        playerScores = new ArrayList<>(); // Initialize the score list

        try {
            backgroundImage = ImageIO.read(PlayerLayout.class.getResource("/Image/back.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        // Initialize cards for each player and their scores
        Card.initializeCards();
        Random random = new Random();

        for (int i = 0; i < numPlayers; i++) {
            ArrayList<Card> playerCards = Card.dealCards(12);

            // Randomly select two unique cards to be face up
            int firstCardIndex = random.nextInt(12);
            int secondCardIndex;
            do {
                secondCardIndex = random.nextInt(12);
            } while (secondCardIndex == firstCardIndex);

            // Set the selected cards face up
            playerCards.get(firstCardIndex).setFaceUp(true);
            playerCards.get(secondCardIndex).setFaceUp(true);

            playersCards.add(playerCards);

            // Create a new PlayerScore and update it based on the face-up cards
            PlayerScore score = new PlayerScore();
            score.updateScore(playerCards);
            playerScores.add(score);
        }

        this.cardPile = new DrawPile();
        this.discardPile = new DiscardPile();

        // Add one card to the discard pile before the game starts
        Card initialDiscardCard = Card.drawRandomCard();
        if (initialDiscardCard != null) {
            initialDiscardCard.setFaceUp(true); // Ensure the card is face up in the discard pile
            this.discardPile.add(initialDiscardCard);
        }

        setPreferredSize(new Dimension(400 + (numPlayers * 200), 600));
        
        // Set the layout to null for absolute positioning
        setLayout(null);

        // Create the instruction label
        instructionLabel = new JLabel("Player 1, click the draw pile or drag the card from the discard pile to start your turn");
        instructionLabel.setForeground(Color.white);
        instructionLabel.setHorizontalAlignment(JLabel.CENTER);

        // Set the size and position (x, y coordinates) of the instructionLabel
        instructionLabel.setFont(new Font("Monospace", Font.BOLD, 18));
        instructionLabel.setBounds(500, 400, 1000, 30);  // Example: x=100, y=50, width=400, height=30

        // Add the label to the panel
        add(instructionLabel);

        setLayout(new BorderLayout());
        roundManager = new RoundManager(playerScores, playersCards);
        
        // Use a FlowLayout at the bottom for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Spacing between buttons
        buttonPanel.setBackground(Color.black); // Set the background color for the button panel

        
        // Next Player button
        nextPlayerButton = new JButton("Next Player");
        nextPlayerButton.setPreferredSize(new Dimension(120, 30)); // Set a smaller width and height
        nextPlayerButton.setEnabled(false);
        nextPlayerButton.setBackground(Color.CYAN); // Set the background color for the button
        nextPlayerButton.setForeground(Color.BLACK); // Set the text color
        nextPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveToNextPlayer();
            }
        });
        buttonPanel.add(nextPlayerButton);
        
        // Show Scores button
        JButton scoreCardButton = new JButton("Show Scores");
        scoreCardButton.setPreferredSize(new Dimension(120, 30)); // Set a smaller width and height
        scoreCardButton.setBackground(Color.ORANGE); // Set the background color for the button
        scoreCardButton.setForeground(Color.BLACK); // Set the text color
        scoreCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGameAndShowScoreCard(); // Call method to pause the game and show scores
                if(nextPlayerButton.isEnabled()){
                    nextPlayerButton.setEnabled(true);
                }else{
                    nextPlayerButton.setEnabled(false);
                }
            }
        });
        buttonPanel.add(scoreCardButton);
        
        // Add the button panel to the bottom
        add(buttonPanel, BorderLayout.SOUTH);
        

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw other players at the top
        for (int i = 0; i < numPlayers; i++) {
            if (i != currentPlayer) {
                int spacePerPlayer = getWidth() / (numPlayers);
                int centerX = (spacePerPlayer * i) + (spacePerPlayer / 2);
                int startX = centerX - (int) (150 * 0.5);

                // Draw other players' cards (null-safe handling in drawCards)
                g.setColor(Color.orange);
                Card.drawCards(g, playersCards.get(i), startX, 50, "Player " + (i + 1), 0.5);
            }
        }

        // Draw the current player's cards at the bottom (null-safe handling in
        // drawCards)
        g.setColor(Color.GREEN);
        Card.drawCards(g, playersCards.get(currentPlayer), 100, 350, "Current Player " + (currentPlayer + 1), 1.0);

        // Draw the draw pile of cards
        DrawPile.drawPileCards(g, 1);
        
        // Draw the label "Draw Pile" under the draw pile
        g.setColor(Color.black); // Set color for the text
        g.setFont(new Font("Monospace", Font.BOLD, 16));
        int drawPileX = 1020;  // Adjust this X value to match the draw pile's position
        int drawPileY = 500 + Card.getCardHeight() + 30;  // Positioning the text slightly below the draw pile
        g.drawString("Draw Pile", drawPileX, drawPileY);

        // Draw the discard pile of cards
        discardPile.drawDiscardPile(g, 1);

        // Draw the card being dragged, if any
        if (isDragging && drawnCard != null) {
            Card.drawSingleCard(g, drawnCard, dragOffsetX, dragOffsetY, 1.0);
        } else if (cardDrawn && drawnCard != null) {
            // Draw the card to the left of the draw pile if it has been drawn but not
            // dragged
            int drawX = 850; // Adjust the position for visual balance
            int drawY = 650; // Align with the Y position of the draw pile

            Card.drawSingleCard(g, drawnCard, drawX, drawY, 1.0);
        }

        // Draw the scores to the right of the draw pile
        g.setFont(new Font("Monospace", Font.BOLD, 18));
        int scoreX = 1300; // Set the X position for the scores
        int scoreY = 500; // Initial Y position for the first score

        for (int i = 0; i < numPlayers; i++) {
            g.setColor(Color.YELLOW);
            g.drawString("Player " + (i + 1) + " Score: " + playerScores.get(i).getScore(),  scoreX, scoreY + (i * 30));
        }

        // Draw the current round number in the bottom right corner
        g.setFont(new Font("Monospace", Font.BOLD, 30));
        g.setColor(Color.GREEN);
        String roundText = "Round " + roundNumber; // Display the current round number
        int roundX = 260; // X position for the round text
        int roundY = getHeight() - 80; // Y position for the round text
        g.drawString(roundText, roundX, roundY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (flipCardPromptActive) {
            // Get the index of the clicked card
            int clickedCardIndex = Card.getClickedCard(playersCards.get(currentPlayer), 100, 350, e.getX(), e.getY(),
                    1.0);

            if (clickedCardIndex != Integer.MIN_VALUE) {
                Card cardToFlip = playersCards.get(currentPlayer).get(clickedCardIndex);

                // Flip the card if it's face-down
                if (!cardToFlip.isFaceUp()) {
                    cardToFlip.setFaceUp(true);

                    // End turn after flipping the card
                    flipCardPromptActive = false;
                    instructionLabel.setText(
                            "Player " + (currentPlayer + 1) + ", turn finished. Press 'Next Player' to continue.");
                    nextPlayerButton.setEnabled(true); // Enable the "Next Player" button
                    canDraw = false; // End the player's turn after flipping
                    hasReplacedCard = true; // Prevent further card replacement this turn
                    repaint();
                } else {
                    instructionLabel.setText("Please select a face-down card to flip.");
                }
            }
        } else if (canDraw && !cardDrawn) {
            // Handle card drawing
            if (DrawPile.isDrawPileClicked(e.getX(), e.getY()) && DrawPile.hasCards()) {
                drawnCard = DrawPile.drawTopCard(e.getX(), e.getY());
                cardDrawn = true;
                canDraw = false; // Disable drawing another card

                instructionLabel.setText(
                        "Player " + (currentPlayer + 1) + ", drag the card to your grid to replace one of your cards or to the discard pile.");
                repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (flipCardPromptActive) {
            // Do nothing to prevent other actions
        } else {
            // Prevent dragging from discard pile if a card has been replaced
            if (!hasReplacedCard) {
                // Handle dragging a card from the draw pile
                if (cardDrawn && drawnCard != null) {
                    int drawX = 850;
                    int drawY = 650;

                    if (e.getX() >= drawX && e.getX() <= drawX + Card.getCardWidth() &&
                            e.getY() >= drawY && e.getY() <= drawY + Card.getCardHeight()) {
                        isDragging = true;
                        dragOffsetX = e.getX() - drawX;
                        dragOffsetY = e.getY() - drawY;
                    }
                }
                // Handle dragging a card from the discard pile
                else if (discardPile.isDiscardPileClicked(e.getX(), e.getY()) && discardPile.hasCards()) {
                    drawnCard = discardPile.getTopCard();
                    discardPile.removeTopCard(); // Remove the card from discard pile
                    isDragging = true;
                    dragOffsetX = e.getX() - 600; // Adjust based on discard pile position
                    dragOffsetY = e.getY() - 500; // Adjust based on discard pile position
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragging && drawnCard != null) {
            isDragging = false;

            // If the player has already replaced a card, disallow further actions
            if (hasReplacedCard) {
                instructionLabel.setText("You can only replace one card per turn. Press 'Next Player' to continue.");
                repaint();
                return;
            }

            // Handle discarding a card from the draw pile to the discard pile
            if (discardPile.isDiscardPileClicked(e.getX(), e.getY())) {
                discardPile.add(drawnCard); // Add the drawn card to the discard pile
                drawnCard = null;
                cardDrawn = false;

                // Allow the player to flip a card after discarding
                flipCardPromptActive = true;
                instructionLabel.setText("Player " + (currentPlayer + 1) + ", select a face-down card to flip.");
                nextPlayerButton.setEnabled(false); // Disable the "Next Player" button until the flip is done
                repaint();
            }
            // Handle replacing a card in the player's grid
            else {
                int clickedCardIndex = Card.getClickedCard(playersCards.get(currentPlayer), 100, 350, e.getX(),
                        e.getY(), 1.0);

                if (clickedCardIndex != Integer.MIN_VALUE) {
                    // Replace the clicked card with the drawn card
                    Card replacedCard = playersCards.get(currentPlayer).get(clickedCardIndex);
                    replacedCard.setFaceUp(true);
                    playersCards.get(currentPlayer).set(clickedCardIndex, drawnCard);

                    // Add the replaced card to the discard pile
                    discardPile.add(replacedCard);

                    // Mark that the player has replaced a card
                    hasReplacedCard = true;

                    // End turn after replacement
                    drawnCard = null;
                    cardDrawn = false;
                    instructionLabel.setText(
                            "Player " + (currentPlayer + 1) + ", turn finished. Press 'Next Player' to continue.");
                    nextPlayerButton.setEnabled(true);
                    canDraw = false; // Prevent further actions this turn
                    repaint();

                    // Check and remove columns with matching cards
                    checkAndRemoveMatchingColumns();
                } else {
                    // If not placed on a valid card, return the card to the discard pile
                    discardPile.add(drawnCard);
                    drawnCard = null;
                    repaint();
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (flipCardPromptActive) {
            // Do nothing to prevent dragging while flipping
        } else {
            if (isDragging && drawnCard != null) {
                dragOffsetX = e.getX() - (Card.getCardWidth() / 2);
                dragOffsetY = e.getY() - (Card.getCardHeight() / 2);
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Not used but required by MouseMotionListener
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void showRoundCompletedPopup() {
        JOptionPane.showMessageDialog(this, "Round " + roundNumber + " is completed!", "Round Completed",
                JOptionPane.INFORMATION_MESSAGE);

        // Increment round number
        roundNumber++;

        // Show a confirmation dialog to start the next round
        int choice = JOptionPane.showConfirmDialog(this, "Start Round " + roundNumber + "?", "Next Round",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            roundManager.endRound(); // End the current round

            // Properly reset and start a new round
            roundManager.startNewRound(); // Start a new round
            resetGameStateForNewRound(); // Reset game state for the new round

            instructionLabel.setText("Round " + roundNumber + " started. Player 1, click the draw pile to begin.");
            repaint(); // Ensure the new round UI updates properly
        }
    }

    private void moveToNextPlayer() {
        updatePlayerScore();

        currentPlayer = (currentPlayer + 1) % numPlayers;
        instructionLabel.setText("Player " + (currentPlayer + 1) + ", click the draw pile or drag the card from the discard pile to start your turn.");
        nextPlayerButton.setEnabled(false);
        canDraw = true;
        cardDrawn = false;
        drawnCard = null;
        hasReplacedCard = false;
        flipCardPromptActive = false;
        repaint();

        if (roundManager.hasPlayerRevealedAllCards(currentPlayer)) {
            handlePlayerRevealsAllCards(currentPlayer);
        }

        if (roundManager.checkGameOver()) {
            endGame();
        }
    }


    // Method to handle when a player reveals all their cards
    private void handlePlayerRevealsAllCards(int revealingPlayerIndex) {
        // Start the process of giving the remaining players their last turn
        int remainingPlayers = numPlayers - 1; // All other players except the one who revealed all cards
        giveRemainingPlayersLastTurn(revealingPlayerIndex, remainingPlayers);
    }

    // Method to give remaining players their last turns
    private void giveRemainingPlayersLastTurn(int revealingPlayerIndex, int remainingPlayers) {
        moveToNextRemainingPlayer(revealingPlayerIndex);

        // Recursively give turns to remaining players
        if (remainingPlayers > 0) {
            remainingPlayers--;
            giveRemainingPlayersLastTurn(revealingPlayerIndex, remainingPlayers);
        } else {
            // Once all remaining players have had their last turn, complete the round
            roundManager.updateScoresAndCheckForDoubling(revealingPlayerIndex);
            showRoundCompletedPopup();
        }
    }

    // Move to the next remaining player and give them a turn
    private void moveToNextRemainingPlayer(int revealingPlayerIndex) {
        do {
            currentPlayer = (currentPlayer + 1) % numPlayers; // Move to the next player
        } while (roundManager.hasPlayerRevealedAllCards(currentPlayer) && currentPlayer != revealingPlayerIndex);

        // Give the current player one last turn
        instructionLabel
                .setText("Player " + (currentPlayer + 1) + ", this is your last turn. Click the draw pile to start.");
        nextPlayerButton.setEnabled(true);
        repaint();
    }

    private void resetGameStateForNewRound() {
        // Reset player states and game variables for the new round
        currentPlayer = 0; // Start with the first player
        canDraw = true;
        cardDrawn = false;
        drawnCard = null;
        hasReplacedCard = false;
        flipCardPromptActive = false;

        // Reset scores for all players if necessary (or retain scores based on game
        // rules)
        for (PlayerScore score : playerScores) {
            score.updateScore(playersCards.get(currentPlayer)); // Recalculate or reset scores
        }

        // Refresh the UI and any game state required for the new round
        nextPlayerButton.setEnabled(false); // Disable the next player button until the first turn
        repaint();
    }

private void pauseGameAndShowScoreCard() {
    // Pause the game state
    canDraw = false;
    nextPlayerButton.setEnabled(false);

    // Prepare scores for all previous rounds
    ArrayList<ArrayList<PlayerScore>> allRoundScores = roundManager.getAllRoundScores();

    // Determine if the current round has ended
    boolean roundEnded = roundManager.isRoundFinished(); // Use the new method

    // Show the score card (without passing roundNumber, since it's no longer needed)
    ScoreCard scoreCard = new ScoreCard((JFrame) SwingUtilities.getWindowAncestor(this), allRoundScores, numPlayers, roundEnded); // Pass roundEnded status
    scoreCard.setVisible(true); // Modal dialog, will pause game until closed

    // Resume game state after score card is closed
    resumeGame();
}

    

    private void resumeGame() {
        canDraw = true;
        nextPlayerButton.setEnabled(true); // Re-enable button
        repaint(); // Ensure game UI is updated
    }

    private void checkAndRemoveMatchingColumns() {
        ArrayList<Card> currentPlayerCards = playersCards.get(currentPlayer);
        ArrayList<Integer> columnsToRemove = new ArrayList<>();

        // Check each column (0 to 3)
        for (int col = 0; col < 4; col++) {
            boolean allSame = true;
            int firstValue = -1;

            // Check if all the face-up cards in the column are the same
            for (int row = 0; row < 3; row++) {
                Card card = currentPlayerCards.get(row * 4 + col);

                // If the card is face-down or null, skip the column
                if (card == null || !card.isFaceUp()) {
                    allSame = false;
                    break;
                }

                // Check if all cards in the column have the same value
                if (row == 0) {
                    firstValue = card.getValue();
                } else if (card.getValue() != firstValue) {
                    allSame = false;
                    break;
                }
            }

            // If all values in the column are the same, mark the column for removal
            if (allSame) {
                columnsToRemove.add(col);
            }
        }

        // If there are columns to remove, process the removal
        if (!columnsToRemove.isEmpty()) {
            removeColumns(columnsToRemove);
            updatePlayerScore(); // Update the score after removing columns
            repaint();
        }
    }

    private void removeColumns(ArrayList<Integer> columnsToRemove) {
        ArrayList<Card> currentPlayerCards = playersCards.get(currentPlayer);

        // For each column to be removed, remove the cards and add them to the discard
        // pile
        for (int col : columnsToRemove) {
            for (int row = 0; row < 3; row++) {
                int cardIndex = row * 4 + col;
                Card cardToRemove = currentPlayerCards.get(cardIndex);
                if (cardToRemove != null) {
                    discardPile.add(cardToRemove); // Add the card to the discard pile
                    currentPlayerCards.set(cardIndex, null); // Set the card to null to leave the space empty
                }
            }
        }

        // After removal, keep the grid intact but with empty spaces
        playersCards.set(currentPlayer, reorganizeGrid(currentPlayerCards));

        // Update the player's score after the columns are removed
        updatePlayerScore();
        repaint();
    }

    private ArrayList<Card> reorganizeGrid(ArrayList<Card> playerCards) {
        ArrayList<Card> newGrid = new ArrayList<>(playerCards.size());

        // Loop through the player's cards and retain the structure with empty spots for
        // removed columns
        for (Card card : playerCards) {
            newGrid.add(card != null ? card : null); // Keep empty spots for removed columns
        }

        return newGrid; // Return the updated grid with removed columns
    }

    private void updatePlayerScore() {
        // Use the PlayerScore class to update the current player's score
        playerScores.get(currentPlayer).updateScore(playersCards.get(currentPlayer));
    }

    private void endGame() {
        // Display a dialog or label showing the game has ended
        JOptionPane.showMessageDialog(this, "Game over! The player with the least score wins!", "Game Over",
                JOptionPane.INFORMATION_MESSAGE);

        // Disable further actions (e.g., disable buttons, prevent drawing cards)
        nextPlayerButton.setEnabled(false);
        canDraw = false;
        cardDrawn = false;
        drawnCard = null;
        hasReplacedCard = false;
        flipCardPromptActive = false;

        // Optionally, repaint the board to reflect the final state
        repaint();
    }

}