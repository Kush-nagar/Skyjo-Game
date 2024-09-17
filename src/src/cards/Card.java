package cards;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


public class Card {
    private int value;
    private BufferedImage faceUpImage;
    private static BufferedImage faceDownImg;
    private boolean isFaceUp;
    private static HashMap<Integer, BufferedImage> numToImgMap = new HashMap<>();
    private static ArrayList<Card> cards = new ArrayList<>();
    private static int cardWidth = 100; // Example size
    private static int cardHeight = 150; // Example size
    private static int horizontalGap = 10;
    private static int verticalGap = 10;

    public void setFaceUp(boolean faceUp) {
        this.isFaceUp = faceUp;
    }

    public Card(int value, BufferedImage faceUpImage) {
        this.value = value;
        this.faceUpImage = faceUpImage;
        this.isFaceUp = false; // Cards start face down
    }

    public int getValue() {
        return value;
    }

    public BufferedImage getFaceUpImage() {
        return faceUpImage;
    }

    public static void initializeCards() {
        try {
            faceDownImg = ImageIO.read(Card.class.getResource("/Image/image15.png"));

            // Load all card images
            for (int j = 0; j < 15; j++) {
                BufferedImage img = ImageIO.read(Card.class.getResource("/Image/image" + j + ".png"));
                numToImgMap.put(j - 2, img);
            }

            // Add cards with -2, -1, 0 values
            for (int i = -2; i <= 0; i++) {
                int count = (i == -2) ? 5 : (i == -1) ? 10 : 15;
                for (int j = 0; j < count; j++) {
                    cards.add(new Card(i, numToImgMap.get(i)));
                }
            }

            // Add cards with values from 1 to 12
            for (int j = 1; j < 13; j++) {
                for (int k = 0; k < 10; k++) {
                    cards.add(new Card(j, numToImgMap.get(j)));
                }
            }

            Collections.shuffle(cards);

        } catch (Exception e) {
            System.out.println("Exception while initializing cards!");
            e.printStackTrace();
        }
    }

    public static ArrayList<Card> dealCards(int numberOfCards) {
        ArrayList<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < numberOfCards && !cards.isEmpty(); i++) {
            dealtCards.add(cards.remove(0));
        }
        return dealtCards;
    }

    public static void drawCards(Graphics g, ArrayList<Card> playerCards, int startX, int startY, String playerName, double scale) {
        // Draw player name
        g.setFont(new Font("Monospace", Font.BOLD, 16));
        g.drawString(playerName, startX, startY - 10);
    
        // Apply scaling to card width and height
        int scaledCardWidth = (int) (cardWidth * scale);
        int scaledCardHeight = (int) (cardHeight * scale);
        int scaledHorizontalGap = (int) (horizontalGap * scale);
        int scaledVerticalGap = (int) (verticalGap * scale);
    
        for (int i = 0; i < playerCards.size(); i++) {
            int row = i / 4; // Assume a 4-column grid
            int col = i % 4;
            int x = startX + col * (scaledCardWidth + scaledHorizontalGap);
            int y = startY + row * (scaledCardHeight + scaledVerticalGap);
    
            Card card = playerCards.get(i);
    
            // If the card is null (column removed), draw an empty space (white rectangle)
            if(card != null) {
                // If the card exists, draw the card image (face-up or face-down)
                BufferedImage cardImage = card.isFaceUp() ? card.getFaceUpImage() : faceDownImg;
                if (cardImage != null) {
                    g.drawImage(cardImage, x, y, scaledCardWidth, scaledCardHeight, null);
                }
            }
        }
    }
    
    
    public static int getClickedCard(ArrayList<Card> playerCards, int startX, int startY, int x, int y, double scale) {
        int scaledCardWidth = (int) (cardWidth * scale);
        int scaledCardHeight = (int) (cardHeight * scale);
        int scaledHorizontalGap = (int) (horizontalGap * scale);
        int scaledVerticalGap = (int) (verticalGap * scale);
    
        for (int i = 0; i < playerCards.size(); i++) {
            if (playerCards.get(i) == null) {
                continue; // Skip null cards (empty spots)
            }
    
            int row = i / 4;
            int col = i % 4;
            int cardX = startX + col * (scaledCardWidth + scaledHorizontalGap);
            int cardY = startY + row * (scaledCardHeight + scaledVerticalGap);
    
            if (x >= cardX && x < cardX + scaledCardWidth && y >= cardY && y < cardY + scaledCardHeight) {
                return i; // Return the index of the clicked card
            }
        }
        return Integer.MIN_VALUE; // No card clicked
    }
    
    
    public static void drawCardStack(Graphics g, ArrayList<Card> cardStack, int x, int y, double scale) {
        if (cardStack.isEmpty()) {
            return; // No cards to draw
        }

        // Apply scaling to card width and height
        int scaledCardWidth = (int) (cardWidth * scale);
        int scaledCardHeight = (int) (cardHeight * scale);

        // Define the offset for each card in the stack
        int offsetX = 2;
        int offsetY = 2;

        // Calculate the maximum number of visible cards in the stack
        int maxVisibleCards = Math.min(cardStack.size(), 5);

        // Draw the cards from bottom to top
        for (int i = 0; i < maxVisibleCards; i++) {
            int index = cardStack.size() - maxVisibleCards + i;
            Card card = cardStack.get(index);
            BufferedImage cardImage = card.isFaceUp() ? card.getFaceUpImage() : getFaceDownImg();

            if (cardImage != null) {
                int cardX = x + (offsetX * i);
                int cardY = y + (offsetY * i);
                g.drawImage(cardImage, cardX, cardY, scaledCardWidth, scaledCardHeight, null);
            }
        }
    }

    public void flip() {
        isFaceUp = !isFaceUp; // Toggle the card's face-up state
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public static ArrayList<Card> getCards(){
        return cards;
    }

    public static int getCardHeight(){
        return cardHeight;
    }

    public static int getCardWidth(){
        return cardWidth;
    }

    public static BufferedImage getFaceDownImg(){
        return faceDownImg;
    }

    public static void drawSingleCard(Graphics g, Card card, int x, int y, double scale) {
        if (card.isFaceUp()) {
            // Draw the face-up card
            g.drawImage(card.getFaceUpImage(), x, y, (int)(cardWidth * scale), (int)(cardHeight * scale), null);
        } else {
            // Draw the face-down card
            g.drawImage(faceDownImg, x, y, (int)(cardWidth * scale), (int)(cardHeight * scale), null);
        }
    }

    // Method to draw a placeholder card
    public static void drawPlaceholderCard(Graphics g, int x, int y, double scale) {
        int scaledWidth = (int) (cardWidth * scale);
        int scaledHeight = (int) (cardHeight * scale);

        // Draw a rectangle as a placeholder
        g.setColor(Color.GRAY);
        g.fillRect(x, y, scaledWidth, scaledHeight);

        // Draw a border around the placeholder
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, scaledWidth, scaledHeight);

        // Draw a "Placeholder" text in the center
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospace", Font.PLAIN, 14));
        g.drawString("Placeholder", x + scaledWidth / 4, y + scaledHeight / 2);
    }

    public static Card drawRandomCard() {
        if (cards.isEmpty()) {
            return null; // Return null if there are no cards left
        }
        
        int randomIndex = (int) (Math.random() * cards.size());
        return cards.remove(randomIndex);
    }
}