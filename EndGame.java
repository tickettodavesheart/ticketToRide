import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

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
   public EndGame(HashMap<String, Integer> scoreList) {

      Map<String,Integer> scoreTable = scoreList;
      Map<String, Integer> sorted = scoreTable
            .entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .collect(
               toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                   LinkedHashMap::new));

      Object[] keys = sorted.keySet().toArray();

      
      // Set frame layout manager to BorderLayout
      setLayout(new BorderLayout(5,5));

      // Create a JPanel containing all game info
      JPanel infoPanel = new JPanel();
      infoPanel.setLayout(new GridLayout(0,1));
      
      // title of Frame that says "Game Over"
      JLabel paneTitle = new JLabel("<html><center><font size=+2 align=center color=red>Game Over!");
      paneTitle.setHorizontalAlignment(JLabel.CENTER);
      paneTitle.setPreferredSize(new Dimension(500, 20));

      // Variables to score winner info, such as name and score
      String winnerName = ((String) keys[0]).toUpperCase();
      int winnerScore = sorted.get(keys[0]);

      // JLabel displaying the player's score
      JLabel winner = new JLabel("<html><font size=+1>" + String.format("%s has won the game with %,d points.", winnerName, winnerScore));
      winner.setHorizontalAlignment(JLabel.CENTER);
      winner.setPreferredSize(new Dimension(500, 20));

      JLabel scoresTitle = new JLabel("<html><font size=><u>Final Scores:");
      scoresTitle.setHorizontalAlignment(JLabel.CENTER);

      // add objects to infoPanel
      infoPanel.add(paneTitle);
      infoPanel.add(winner);
      infoPanel.add(scoresTitle);

      
      JPanel scorePanel = new JPanel();
      scorePanel.setLayout(new GridLayout(0,2));
      
      // Scores
      
      // DO SOMETHING HERE THAT TAKES SCORES AND STORES THEM INTO ARRAY LIST
      
      
      // Display scores
      
      // Heading of scores pane

      // Add each score in the scoreList to the frame as a label
      for (Object key : keys) {
         String player = (String) key;
         String playerScore = sorted.get(key).toString();
         JLabel playerLabel = new JLabel(player + " \t \t \t ");
         JLabel scoreLabel = new JLabel(" \t \t \t " + playerScore);
         scoreLabel.setHorizontalAlignment(JLabel.LEFT);
         playerLabel.setHorizontalAlignment(JLabel.RIGHT);
         scorePanel.add(playerLabel);
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

   class ButtonListener implements ActionListener {

      public void actionPerformed(ActionEvent ae) {
         if (ae.getSource().equals(closeBtn)) {
            System.err.println("Program exiting with code 0.");
            System.exit(0);
         }
      }

   }

}