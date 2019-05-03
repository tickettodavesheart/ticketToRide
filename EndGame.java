import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * Frame displayed when game ends.
 * @author Michael Vasile
 * @version 20190430
 */

public class EndGame extends JFrame {

   //private JButton lobbyBtn;
   private JButton closeBtn;

   /**
    * EndGame Constructor
    */
   public EndGame() {

      // Set frame layout manager to BorderLayout
      setLayout(new BorderLayout(5,5));

      // Create a JPanel containing all game info
      JPanel infoPanel = new JPanel();
      infoPanel.setLayout(new FlowLayout());
      
      // title of Frame that says "Game Over"
      JLabel paneTitle = new JLabel("<html><center><font size=+2 align=center color=red>Game Over!");

      // Variables to score winner info, such as name and score
      String winnerName = "WINNER NAME";
      int winnerScore = 1000;

      // JLabel displaying the player's score
      JLabel winner = new JLabel("<html><font size=+1>" + String.format("%s has won the game with %,d points.", winnerName, winnerScore));

      // add objects to infoPanel
      infoPanel.add(paneTitle);
      infoPanel.add(winner);
      
      JPanel scorePanel = new JPanel();
      scorePanel.setLayout(new GridLayout(0,1));
      
      // Scores
      ArrayList<String> scoreList = new ArrayList<String>();
      
      // DO SOMETHING HERE THAT TAKES SCORES AND STORES THEM INTO ARRAY LIST
      
      // Display scores
      
      // Heading of scores pane
      JLabel scoresTitle = new JLabel("<html><font size=><u>Final Scores:");
      scoresTitle.setHorizontalAlignment(JLabel.CENTER);
      scorePanel.add(scoresTitle);

      // Add each score in the scoreList to the frame as a label
      for (String score : scoreList) {
         JLabel scoreLabel = new JLabel(score);
         scoreLabel.setHorizontalAlignment(JLabel.CENTER);
         scorePanel.add(scoreLabel);
      }
      // add the score pane to the JFrame
      infoPanel.add(scorePanel);

      // Create a JPanel containing all frame controls
      JPanel controlPanel = new JPanel();
      controlPanel.setLayout(new FlowLayout());
      
      // Initialize close game buttons
      //lobbyBtn = new JButton("Exit to Lobby");
      closeBtn = new JButton("Close Game");

      // ActionListener
      ButtonListener bl = new ButtonListener();
      //lobbyBtn.addActionListener(bl);
      closeBtn.addActionListener(bl);
      
      // add elements to JFrame

      //controlPanel.add(lobbyBtn);
      controlPanel.add(closeBtn);

      // add panes to specific regions
      add(infoPanel, BorderLayout.CENTER);
      add(controlPanel, BorderLayout.SOUTH);
      
      setTitle("Game Over");
      setSize(500,215);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      setLocationRelativeTo(null);
      setUndecorated(true);
      setVisible(true);
   }

   public static void main(String[] args) {
      new EndGame();
   }

   class ButtonListener implements ActionListener {

      public void actionPerformed(ActionEvent ae) {
         if (ae.getSource().equals(closeBtn)) {
            System.err.println("Program exiting with code 0.");
            System.exit(0);
         }
      }

   }

}