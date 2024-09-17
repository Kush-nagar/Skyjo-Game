package layout;
import javax.swing.*;

public class playerScreen extends JFrame {
    private final int width = 1600;
    private final int height = 1000;
    public playerScreen(String title, int numPlayers) {
        super(title);

        setSize(width, height);

        // Ensure the program exits when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the game logic panel
        add(new PlayerLayout(numPlayers));

        // Make the frame visible
        setVisible(true);
    }
}
