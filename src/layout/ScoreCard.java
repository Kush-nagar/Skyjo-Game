package layout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import score.PlayerScore;
import java.util.ArrayList;

public class ScoreCard extends JDialog {
    private ArrayList<ArrayList<PlayerScore>> allRoundScores; // Scores for all rounds
    private int numPlayers;
    private boolean roundEnded; // Variable to track whether the current round has ended

    public ScoreCard(JFrame parentFrame, ArrayList<ArrayList<PlayerScore>> allRoundScores, int numPlayers, boolean roundEnded) {
        super(parentFrame, "Score Card", true); // Modal dialog to pause the game

        this.allRoundScores = allRoundScores;
        this.numPlayers = numPlayers;
        this.roundEnded = roundEnded;

        // Create a grid layout with one row for each player and two columns (player name + total score)
        JPanel scorePanel = new JPanel(new GridLayout(numPlayers + 1, 2, 10, 10)); // numPlayers rows + 1 for header

        // Add headers
        JLabel playerHeader = new JLabel("Player", SwingConstants.CENTER);
        playerHeader.setBorder(new LineBorder(Color.BLACK));
        scorePanel.add(playerHeader);

        JLabel totalHeader = new JLabel("Total Score", SwingConstants.CENTER);
        totalHeader.setBorder(new LineBorder(Color.BLACK));
        scorePanel.add(totalHeader);

        // Total scores array
        int[] totalScores = new int[numPlayers];

        // Calculate total scores for each player across all rounds
        for (int round = 0; round < allRoundScores.size(); round++) {
            for (int player = 0; player < numPlayers; player++) {
                if (round < allRoundScores.size() && allRoundScores.get(round).size() > player) {
                    totalScores[player] += allRoundScores.get(round).get(player).getScore();
                }
            }
        }

        int minScore = Integer.MAX_VALUE;
        int minScorePlayer = -1;
        boolean hasWinner = false;

        // Display player names and their total scores
        for (int player = 0; player < numPlayers; player++) {
            JLabel playerLabel = new JLabel("Player " + (player + 1), SwingConstants.CENTER);
            playerLabel.setBorder(new LineBorder(Color.BLACK));
            scorePanel.add(playerLabel);

            JLabel totalScoreLabel = new JLabel(String.valueOf(totalScores[player]), SwingConstants.CENTER);
            totalScoreLabel.setBorder(new LineBorder(Color.BLACK));
            scorePanel.add(totalScoreLabel);

            // Check if any player's total score has reached or exceeded 100
            if (totalScores[player] >= 100) {
                hasWinner = true;
            }

            // Find the player with the least score
            if (totalScores[player] < minScore) {
                minScore = totalScores[player];
                minScorePlayer = player;
            }
        }

        // If any player has a total score >= 100, declare the player with the least score as the winner
        if (hasWinner) {
            JLabel winnerLabel = new JLabel("Player " + (minScorePlayer + 1) + " wins with the least score!", SwingConstants.CENTER);
            winnerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            winnerLabel.setBorder(new LineBorder(Color.GREEN));
            winnerLabel.setForeground(Color.GREEN);
            add(winnerLabel, BorderLayout.NORTH);
        }

        add(scorePanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog and resume the game
            }
        });
        add(closeButton, BorderLayout.SOUTH);

        // Make the dialog resizable and pack it to fit the content
        setResizable(true);
        setMinimumSize(new Dimension(300, 200));
        pack();
        setLocationRelativeTo(parentFrame);
    }

    // New clear method to reset the allRoundScores
    public void clear() {
        if (allRoundScores != null) {
            allRoundScores.clear(); // Clear all previous round scores
        }
    }
}

