package Pile;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import cards.Card;

public class DiscardPile {
    private ArrayList<Card> discardPile;

    public DiscardPile() {
        discardPile = new ArrayList<>();
    }

    // Add a card to the discard pile
    public void add(Card card) {
        discardPile.add(card);
    }

    // Get the top card from the discard pile
    public Card getTopCard() {
        if (discardPile.isEmpty()) {
            return null; // No cards in the discard pile
        }
        return discardPile.get(discardPile.size() - 1);
    }

    // Check if the discard pile has any cards
    public boolean hasCards() {
        return !discardPile.isEmpty();
    }

    // Draw the discard pile
    public void drawDiscardPile(Graphics g, int numCardsToDraw) {
        int x = 700; // Adjust as necessary for your layout
        int y = 500; // Adjust as necessary for your layout

        if (hasCards()) {
            Card topCard = getTopCard();
            Card.drawSingleCard(g, topCard, x, y, 1.0);

            // Draw the card count on top of the discard pile
            g.setColor(Color.black);
            g.setFont(new Font("Monospace", Font.BOLD, 15));
            g.drawString("Discard Pile", x + 10, y + 170); // Adjust positioning as needed
        } else {
            // Draw a placeholder where the discard pile should be
            Card.drawPlaceholderCard(g, x, y, 1.0);

            // Display "Empty" text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Empty", x + 10, y + 20); // Adjust positioning as needed
        }
    }

    // Get the size of the discard pile
    public int size() {
        return discardPile.size();
    }

    // Add this method in the DiscardPile class
    public boolean isDiscardPileClicked(int x, int y) {
        int scaledCardWidth = Card.getCardWidth();
        int scaledCardHeight = Card.getCardHeight();
        int discardPileX = 700; // Adjust this to the actual x position of the discard pile
        int discardPileY = 500; // Adjust this to the actual y position of the discard pile

        return x >= discardPileX && x <= discardPileX + scaledCardWidth &&
            y >= discardPileY && y <= discardPileY + scaledCardHeight;
    }

    public Card removeTopCard() {
        if (!discardPile.isEmpty()) {
            return discardPile.remove(discardPile.size() - 1);
        }
        return null;
    }
}