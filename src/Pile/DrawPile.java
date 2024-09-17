package Pile;

import cards.Card;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;
import java.util.ArrayList;

public class DrawPile implements MouseMotionListener {
    @SuppressWarnings("unused")
    private static BufferedImage faceDownImg;
    public static ArrayList<Card> drawCards;
    private static ArrayList<Card> discardPile;
    private static int cardHeight = Card.getCardHeight();
    private static int cardWidth = Card.getCardWidth();
    @SuppressWarnings("unused")
    private static Point lastDrawPosition; // Store the last draw position
    private static Card draggedCard; // Card that is currently being dragged
    private static int mouseX, mouseY; // Mouse coordinates

    public DrawPile() {
        // Initialize the card deck
        Card.initializeCards();
        drawCards = new ArrayList<>(Card.getCards());
        discardPile = new ArrayList<>();
        faceDownImg = Card.getFaceDownImg();
    }

    public static void drawPileCards(Graphics g, double scale) {
        Card.drawCardStack(g, drawCards, 1000, 500, scale);
    }

    public static void drawDiscardPile(Graphics g, double scale) {
        Card.drawCardStack(g, discardPile, 1000, 700, scale);
    }

    public static Card drawTopCard(int x, int y) {
        if (!drawCards.isEmpty()) {
            Card topCard = drawCards.remove(drawCards.size() - 1);
            topCard.setFaceUp(true); // Set the card as face-up
            lastDrawPosition = new Point(x, y);
            draggedCard = topCard;
            return topCard;
        }
        return null;
    }

    public static void drawDraggedCard(Graphics g, double scale) {
        if (draggedCard != null) {
            // Draw the card at the current mouse position
            Card.drawSingleCard(g, draggedCard, mouseX - cardWidth / 2, mouseY - cardHeight / 2, scale);
        }
    }

    public static void placeDraggedCard(ArrayList<Card> playerCards, int startX, int startY, double scale) {
        if (draggedCard != null) {
            int cardIndex = Card.getClickedCard(playerCards, startX, startY, mouseX, mouseY, scale);
            if (cardIndex != Integer.MIN_VALUE) {
                // Replace the card at the clicked position with the dragged card
                discardPile.add(playerCards.set(cardIndex, draggedCard));
                draggedCard = null; // Clear the dragged card
            }
        }
    }

    public static boolean isDrawPileClicked(int x, int y) {
        int scaledCardWidth = (int) (cardWidth);
        int scaledCardHeight = (int) (cardHeight);
        return x >= 1000 && x <= 1000 + scaledCardWidth &&
                y >= 500 && y <= 500 + scaledCardHeight;
    }

    public static boolean hasCards() {
        return !drawCards.isEmpty();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Update mouse coordinates when dragging
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Update mouse coordinates when moving
        mouseX = e.getX();
        mouseY = e.getY();
    }
}