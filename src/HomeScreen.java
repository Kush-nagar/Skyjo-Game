import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import layout.playerMain; // Import the playerMain class

public class HomeScreen extends JFrame {

    private int numPlayers = 2; // Initialize the number of players to the minimum allowed

    public HomeScreen() {
        // Set the title of the window
        setTitle("Skyjo Game Home Screen");

        // Set the size of the window
        setSize(400, 300);

        // Specify an action for the close button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel to hold the components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1)); // Grid layout with 4 rows

        // Add a label to show the current number of players
        JLabel playerCountLabel = new JLabel("Number of players: " + numPlayers, SwingConstants.CENTER);
        panel.add(playerCountLabel);

        // Create a panel for the add and remove buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Create "Add Player" button
        JButton addButton = new JButton("Add Player");
        buttonPanel.add(addButton);

        // Create "Remove Player" button
        JButton removeButton = new JButton("Remove Player");
        buttonPanel.add(removeButton);

        // Add button panel to the main panel
        panel.add(buttonPanel);

        // Create a button to start the game
        JButton startButton = new JButton("Start Game");
        panel.add(startButton);

        // Add listeners to handle button clicks

        // Add Player button action listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (numPlayers < 5) { // Maximum of 5 players
                    numPlayers++;
                    playerCountLabel.setText("Number of players: " + numPlayers);
                } else {
                    JOptionPane.showMessageDialog(panel, "Maximum of 5 players allowed.");
                }
            }
        });

        // Remove Player button action listener
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (numPlayers > 2) { // Minimum of 2 players
                    numPlayers--;
                    playerCountLabel.setText("Number of players: " + numPlayers);
                } else {
                    JOptionPane.showMessageDialog(panel, "Minimum of 2 players required.");
                }
            }
        });

        // Start Game button action listener
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the home screen
                dispose();

                // Create and run the playerMain class, passing the number of players
                new playerMain(numPlayers);
            }
        });

        // Add the panel to the frame
        add(panel);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Make the window visible
        setVisible(true);
    }

    // Main method to run the program
    public static void main(String[] args) {
        // Run the GUI in the event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HomeScreen(); // Start the HomeScreen application
            }
        });
    }
}