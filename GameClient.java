// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

// GUI
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Class for the GameClient.
 *
 * @author Lucas Kohorst, Michael Vasile
 * @version 3/25/19
 */
public class GameClient extends JFrame {

   // For RMI Registry
   private String ipaddress;

   // For Client
   private String nickname;
   private JLabel currPlayer;
   private JLabel currScore;
   private JLabel playerNames;
   private JLabel trainCardCount;

   private JPanel bottomBar = new JPanel();
   private BufferedImage trainDeck;

   // For RMI
   private GameStub stub;

   // Global Gameboard to access methods
   private GameBoard gb;

   private JPanel trainsPanel = new JPanel(null);

   // The train count labels
   private JLabel trainCountBlack;
   private JLabel trainCountBlue;
   private JLabel trainCountGreen;
   private JLabel trainCountNeutral;
   private JLabel trainCountOrange;
   private JLabel trainCountPink;
   private JLabel trainCountRed;
   private JLabel trainCountWhite;
   private JLabel trainCountYellow;

   // Boolean to create cards once
   private boolean cardsCreated = true;

   // For the player's destination cards
   private JLabel destCards;

   /**
     * Constructor for the ChatClient.
     *
     * @param ip     the IP adress for the main server
     * @param stubID the id for the GameStub for the specific server
     * @param name   the name of the current game
     */
   public GameClient(String ip, String stubID, String name) {

      // Getting the players name
      String[] optionsName = {"OK"};
      JPanel panelName = new JPanel();
      JLabel lblName = new JLabel("Enter your nickname: ");
      JTextField txtName = new JTextField(10);
      panelName.add(lblName);
      panelName.add(txtName);
      ImageIcon img = new ImageIcon("resources/placeholder.png");

      /**
         * MATTS CODE THE LEGEND LIVES ON.
         * DO NOT DELETE EVEN THOUGH IT IS NOT USED.
         * THANKS, LUCAS String[] optionsName = { "OK" }; JPanel panelName = new
         * JPanel(); JLabel lblName = new JLabel
         * ("Enter your unique nickname: ");
         * JTextField txtName = new JTextField(10); panelName.add(lblName);
         * panelName.add(txtName);
         */

      try {
         // Locating the Registry
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (GameStub) registry.lookup(stubID);

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }

      try {
         // pull the player names list from GameServer
         Vector<String> playerNames = stub.getPlayerNames();

         // loop to validate names
         boolean repeatPrompt = false;
         do {
            // prompt user for nickname
            JOptionPane.showOptionDialog(null, panelName, 
                   "Nickname", JOptionPane.NO_OPTION,
                   JOptionPane.QUESTION_MESSAGE, null, 
                   optionsName, optionsName[0]);
            nickname = txtName.getText();

            // default username
            if (nickname.length() < 1) {
               nickname = "Anonymous";
            }

            // check to see if it's already someone's name
            for (String player : playerNames) {
               // if name is taken, reprompt user
               if (player.equals(nickname)) {
                  repeatPrompt = true;
                  // otherwise, exit loop
               } else {
                  repeatPrompt = false;
               }
            }
         } while (repeatPrompt);
      } catch (RemoteException re) {
      }

      setLayout(new BorderLayout(10, 10));

      // Sidebar JPanel
      JPanel sideBar = new JPanel();
      sideBar.setLayout(new BorderLayout(10, 10));
      sideBar.setPreferredSize(new Dimension(325, 10));

      setLayout(new BorderLayout(10, 10));

      Thread chatThread = new Thread(new Runnable() {
         public void run() {
            sideBar.add(new ChatClient(ip, stubID, name, nickname), BorderLayout.SOUTH);
         }
      });
      Thread gameThread = new Thread(new Runnable() {
         public void run() {
            gb = new GameBoard(ip, stubID, name, nickname);
            add(gb, BorderLayout.CENTER);
            gb.revalidate();
         }
      });

      // Starting the threads
      chatThread.start();
      gameThread.start();

      add(sideBar, BorderLayout.EAST);

      // Game Info in Sidebar
      JPanel upperSidebar = new JPanel();
      upperSidebar.setLayout(new GridLayout(0,1));

      JPanel gameInfo = new JPanel();
     
      gameInfo.setLayout(new GridLayout(0, 2));
      gameInfo.setBorder(BorderFactory.createTitledBorder("Game Info"));

      gameInfo.add(new JLabel("Game: "));

      JLabel gameName = new JLabel(name);
      gameInfo.add(gameName);

      gameInfo.add(new JLabel("Current Player: "));

      currPlayer = new JLabel();
      gameInfo.add(currPlayer);

      gameInfo.add(new JLabel("Players: "));

      playerNames = new JLabel();
      gameInfo.add(playerNames);


      JPanel roundInfo = new JPanel();
      roundInfo.setLayout(new GridLayout(0,2));
      roundInfo.setBorder(BorderFactory.createTitledBorder("Your Hand"));
      
      // Train Card Count
      trainCardCount = new JLabel("45");

      //cardInfo.add(trainIconLabel);
      roundInfo.add(new JLabel("Train Cards: "));
      roundInfo.add(trainCardCount);

      roundInfo.add(new JLabel("Destination Cards: "));

      destCards = new JLabel();
      roundInfo.add(destCards);

      // add gameInfo to sidebar
      upperSidebar.add(gameInfo);

      upperSidebar.add(roundInfo);

      // add upperSidebar pane to sidebar
      sideBar.add(upperSidebar, BorderLayout.CENTER);

      // Bottom JPanel
      bottomBar.setLayout(new BorderLayout(10, 10));

      add(bottomBar, BorderLayout.SOUTH);

      // Image Imports
      BufferedImage trainImg = null;
      BufferedImage trainDeck = null;
      BufferedImage destDeckImg = null;
      BufferedImage trainDeckImg = null;
      try {
         trainImg = ImageIO.read(getClass().getResource("resources/train.png"));
         trainDeck = ImageIO.read(getClass().getResource("resources/cards/train/trainDeck.png"));
         destDeckImg = ImageIO.read(getClass().getResource("resources/DestinationDeckIcon.png"));
         trainDeckImg = ImageIO.read(getClass().getResource("resources/TrainDeckIcon.png"));
      } catch (Exception ex) { }

      // Game Information Timer
      java.util.Timer time = new java.util.Timer();
      time.schedule(new InfoTimer(), 500, 1500);

      // Train Count
      JPanel cardInfo = new JPanel();
      JLabel trainIconLabel = new JLabel();

      // Set the icon for trainIconLabel
      ImageIcon trainIcon = new ImageIcon(trainImg);
      trainIconLabel.setIcon(trainIcon);


      // Add cardInfo to bottomBar
      bottomBar.add(cardInfo, BorderLayout.WEST);

      // cardDecks JPanel
      JPanel cardDecks = new JPanel();
      cardDecks.setLayout(new FlowLayout(FlowLayout.LEFT, 60, 10));

      // Destination Card Deck Icon
      JLabel destDeckLabel = new JLabel();

      ImageIcon destDeckIcon = new ImageIcon(destDeckImg);
      destDeckLabel.setIcon(destDeckIcon);
      JButton destDeckButton = new JButton(destDeckIcon);
      cardDecks.add(destDeckButton);

      destDeckButton.addActionListener(e -> {
         // Calling the destination cards prompt
         gb.getDestinationCards();
      });

      // Train Card Deck Icon
      JLabel trainDeckLabel = new JLabel();

      ImageIcon trainDeckIcon = new ImageIcon(trainDeckImg);
      trainDeckLabel.setIcon(trainDeckIcon);
      JButton trainDeckButton = new JButton(trainDeckIcon);
      cardDecks.add(trainDeckButton);

      trainDeckButton.addActionListener(e -> {
         // Calling the train cards prompt
         gb.getTrainCards();
      });

      // add CardDecks to bottomBar
      bottomBar.add(cardDecks, BorderLayout.EAST);

      add(bottomBar, BorderLayout.SOUTH);

      // Set JFrame sizing
      setSize(1250, 750);
      setTitle("Ticket to Ride - In Game: " + name);
      setResizable(false);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      setVisible(true);
      setLocationRelativeTo(null);

      // Send exit message
      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
               // Get the game that the client is running
               // create registry and bind to the main server
               // Creating the registry
               try {
                  // Shutdown messages
                  stub.sendMessage(nickname + " has left the game.");

                  // Locating the Registry
                  Registry registry = LocateRegistry.getRegistry(ip);

                  // Looking up the ServerStub class
                  ServerStub serverStub = (ServerStub) registry.lookup("ServerStub");

                  // Calling the shutdown method on the server
                  serverStub.shutdownClient(name);

                  // Getting rid of the client
                  dispose();

                  // Creating the Lobby Again
                  new Lobby(ip);

               } catch (Exception oe) {
                  System.err.println("Client exception: " + oe.toString());
                  oe.printStackTrace();
               }
         }
      });

   } // End of Game Client Constructor

   /**
    * Timer class to update various elements on 
    * the GUI.
    * @author Micheal Vasille
    * @version 4-29-19
    */
   class InfoTimer extends TimerTask {
      /**
       * The Run method for the InfoTimer.
       */
      public void run() {
         try {
            // Updating the train card count
            trainCardCount.setText(Integer.toString(stub.getPlayerTrains(nickname)));
            // Getting the user's destination cards
            ArrayList<DestinationCard> dCards = gb.getDestinationCardsFromPlayer();
           
            String destCardsString = "<html>";
            for (DestinationCard c : dCards) {
               destCardsString += "<p>" +c.toString() + "</p>";
            }
   
            destCards.setText(destCardsString);

            ArrayList<String> currentPlayerTrainCards = stub.getPlayerTrainCards(nickname);
            int blackCount = 0;
            int blueCount = 0;
            int greenCount = 0;
            int neutralCount = 0;
            int orangeCount = 0;
            int pinkCount = 0;
            int redCount = 0;
            int whiteCount = 0;
            int yellowCount = 0;
            for (String color : currentPlayerTrainCards) {
               switch (color) {
                  case "BLACK":
                     blackCount++;
                     break;
                  case "BLUE":
                     blueCount++;
                     break;
                  case "GREEN":
                     greenCount++;
                     break;
                  case "NEUTRAL":
                     neutralCount++;
                     break;
                  case "ORANGE":
                     orangeCount++;
                     break;
                  case "PINK":
                     pinkCount++;
                     break;
                  case "RED":
                     redCount++;
                     break;
                  case "WHITE":
                     whiteCount++;
                     break;
                  case "YELLOW":
                     yellowCount++;
                     break;
               }
            }
            if (cardsCreated) {
               // Creating the counters once 
               createCounters();
            }

            // Updating the counters
            trainCountBlack.setText("<html><font color=red>" + Integer.toString(blackCount) + "</font>");
            trainCountBlue.setText("<html><font color=red>" + Integer.toString(blueCount) + "</font>");
            trainCountGreen.setText("<html><font color=red>" + Integer.toString(greenCount) + "</font>");
            trainCountNeutral.setText("<html><font color=red>" + Integer.toString(neutralCount) + "</font>");
            trainCountOrange.setText("<html><font color=red>" + Integer.toString(orangeCount) + "</font>");
            trainCountPink.setText("<html><font color=red>" + Integer.toString(pinkCount) + "</font>");
            trainCountRed.setText("<html><font color=red>" + Integer.toString(redCount) + "</font>");
            trainCountWhite.setText("<html><font color=red>" + Integer.toString(whiteCount) + "</font>");
            trainCountYellow.setText("<html><font color=red>" + Integer.toString(yellowCount) + "</font>");

            currPlayer.setText(stub.getTockenOwner());

            // Update playerlist
            Vector<String> playerList = stub.getPlayerNames();
            String players = "";
            for (int i = 0; i < playerList.size(); i++) {   
               if (i < playerList.size() - 1) {
                  players += playerList.get(i) + ", ";
               } else {
                  players += playerList.get(i);
               }
            }
            playerNames.setText(players);
         } catch (Exception oe) {
            System.err.println("Client exception" + oe.toString());
            oe.printStackTrace();
         }
      }
      
      /**
       * Resizes a given ImageIcon.
       * @param bg the image to resize
       * @param width the width to resize to
       * @param height the height to resize to
       * @return the resized image
       */
      public ImageIcon resizeImage(BufferedImage bg, int width, int height) {
         ImageIcon placeholder = new ImageIcon(bg);
         Image img = placeholder.getImage();
         Image resizedImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
         ImageIcon resizedIcon = new ImageIcon(resizedImg);
         return resizedIcon;
      }

      /**
       * Method to create each counter for the cards.
       */
      public void createCounters() {
         // Train Card Stack
         trainsPanel.setBounds(0, 0, 1389, 205);
         JLabel trains = new JLabel();
         trains.setBounds(0, 0, 750, 100);

         BufferedImage trainDeck = null;
         try {
            trainDeck = ImageIO.read(getClass().getResource("resources/cards/train/trainDeck.png"));
         } catch (Exception ex) {
         }
         trains.setIcon(resizeImage(trainDeck, 750, 100));

         trainCountBlue = new JLabel("<html><font color=red>-</font></", JLabel.CENTER);
         trainCountBlue.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountBlue.setBackground(Color.BLACK);
         trainCountBlue.setOpaque(true);
         trainCountBlue.setBounds(0, 0, 35, 40);
         trainsPanel.add(trainCountBlue);

         trainCountGreen = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountGreen.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountGreen.setBackground(Color.BLACK);
         trainCountGreen.setOpaque(true);
         trainCountGreen.setBounds(75, 0, 35, 40);
         trainsPanel.add(trainCountGreen);

         trainCountRed = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountRed.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountRed.setBackground(Color.BLACK);
         trainCountRed.setOpaque(true);
         trainCountRed.setBounds(150, 0, 35, 40);
         trainsPanel.add(trainCountRed);

         trainCountYellow = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountYellow.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountYellow.setBackground(Color.BLACK);
         trainCountYellow.setOpaque(true);
         trainCountYellow.setBounds(225, 0, 35, 40);
         trainsPanel.add(trainCountYellow);

         trainCountOrange = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountOrange.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountOrange.setBackground(Color.BLACK);
         trainCountOrange.setOpaque(true);
         trainCountOrange.setBounds(300, 0, 35, 40);
         trainsPanel.add(trainCountOrange);

         trainCountPink = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountPink.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountPink.setBackground(Color.BLACK);
         trainCountPink.setOpaque(true);
         trainCountPink.setBounds(375, 0, 35, 40);
         trainsPanel.add(trainCountPink);

         trainCountWhite = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountWhite.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountWhite.setBackground(Color.BLACK);
         trainCountWhite.setOpaque(true);
         trainCountWhite.setBounds(450, 0, 35, 40);
         trainsPanel.add(trainCountWhite);

         trainCountBlack = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountBlack.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountBlack.setBackground(Color.BLACK);
         trainCountBlack.setOpaque(true);
         trainCountBlack.setBounds(525, 0, 35, 40);
         trainsPanel.add(trainCountBlack);
      
         trainCountNeutral = new JLabel("<html><font color=red>-</font>", JLabel.CENTER);
         trainCountNeutral.setFont(new Font("Arial", Font.BOLD, 30));
         trainCountNeutral.setBackground(Color.BLACK);
         trainCountNeutral.setOpaque(true);
         trainCountNeutral.setBounds(600, 0, 35, 40);
         trainsPanel.add(trainCountNeutral);

         trainsPanel.add(trains);
         bottomBar.add(trainsPanel, BorderLayout.CENTER);
         add(bottomBar, BorderLayout.SOUTH);
         
         // only running this once
         cardsCreated = false;

      }

   }

}