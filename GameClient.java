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
   
   // For RMI
   private GameStub stub;
   
   /**
    * Constructor for the ChatClient.
    * @param ip the IP adress for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name the name of the current game
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

      // Getting the players name
      String[] optionsName = {"OK"};
      JPanel panelName = new JPanel();
      JLabel lblName = new JLabel("Enter your unique nickname: ");
      JTextField txtName = new JTextField(10);
      panelName.add(lblName);
      panelName.add(txtName);

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
            "Nickname", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, optionsName, optionsName[0]);
            nickname = txtName.getText();
            System.out.println("Name Panel has been made. Nickname inputted: " + nickname);
            
            // default username
            if (nickname.length() < 1) {
               nickname = "Anonymous";
            }
            
            // check to see if it's already someone's name
            for (String player: playerNames) {
               System.out.println("Printing out the player names in for loop: "+player);
               // if name is taken, reprompt user
               if (player.equals(nickname)) {
                  System.out.println("When equal, repeat. player: " + player);
                  repeatPrompt = true;
               // otherwise, exit loop
               } else {
                  repeatPrompt = false;
               }
            }
         } while (repeatPrompt);
      } catch (RemoteException re) { } 

      setLayout(new BorderLayout(10, 10));
      
      // Sidebar JPanel
      JPanel sideBar = new JPanel();
      sideBar.setLayout(new BorderLayout(10, 10));
      add(sideBar, BorderLayout.EAST);
      
      Thread chatThread = new Thread(new Runnable() {
         public void run() {
            sideBar.add(new ChatClient(ip, stubID, name, nickname), BorderLayout.CENTER);
         }
      });
      Thread gameThread = new Thread(new Runnable() {
         public void run() {
            GameBoard gb = new GameBoard(ip, stubID, name, nickname);
            add(gb, BorderLayout.CENTER);
            gb.revalidate();
         }
      });
      
      // Starting the threads
      chatThread.start();
      gameThread.start();
      
      // Game Info in Sidebar
      JPanel gameInfo = new JPanel();
      gameInfo.setLayout(new GridLayout(0,2));
      gameInfo.setBorder(BorderFactory.createTitledBorder("Game Info"));
      
      JLabel labelCurrPlayer = new JLabel("Current Player: ");
      gameInfo.add(labelCurrPlayer);
      
      currPlayer = new JLabel();
      gameInfo.add(currPlayer);
      
      JLabel labelPlayerNames = new JLabel("Players: ");
      gameInfo.add(labelPlayerNames);
      
      playerNames = new JLabel();
      gameInfo.add(playerNames);
      
      // add gameInfo to sidebar
      sideBar.add(gameInfo, BorderLayout.NORTH);

      // Game Information Timer
      java.util.Timer time = new java.util.Timer();
      time.schedule(new InfoTimer(), 500, 1500);
      
      // Bottom JPanel
      JPanel bottomBar = new JPanel();
      bottomBar.setLayout(new BorderLayout(10,10));

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
      } catch (Exception ex) {System.out.println("Your shit don't work");}

      // Train Count
      JPanel cardInfo = new JPanel();
      JLabel trainIconLabel = new JLabel();

      // Set the icon for trainIconLabel
      ImageIcon trainIcon = new ImageIcon(trainImg);
      trainIconLabel.setIcon(trainIcon);
      
      // Train Card Count
      trainCardCount = new JLabel("45");

      cardInfo.add(trainIconLabel);
      cardInfo.add(trainCardCount);

      // Add cardInfo to bottomBar
      bottomBar.add(cardInfo, BorderLayout.WEST);

      // Train Card Stack
      JPanel trainsPanel = new JPanel();
      trainsPanel.setLayout(new GridLayout(0, 1));
      JLabel trains = new JLabel();
      trains.setIcon(resizeImage(trainDeck, 750, 100));

      trainsPanel.add(trains);

      bottomBar.add(trainsPanel, BorderLayout.CENTER);

      // cardDecks JPanel
      JPanel cardDecks = new JPanel();
      cardDecks.setLayout(new FlowLayout(FlowLayout.LEFT, 65,10));

      // Destination Card Deck Icon
      JLabel destDeckLabel = new JLabel();

      ImageIcon destDeckIcon = new ImageIcon(destDeckImg);
      destDeckLabel.setIcon(destDeckIcon);
      
      cardDecks.add(destDeckLabel);

      // Train Card Deck Icon
      JLabel trainDeckLabel = new JLabel();

      ImageIcon trainDeckIcon = new ImageIcon(trainDeckImg);
      trainDeckLabel.setIcon(trainDeckIcon);
      
      cardDecks.add(trainDeckLabel);

      // add CardDecks to bottomBar
      bottomBar.add(cardDecks,BorderLayout.EAST);
      
      // Set JFrame sizing
      setSize(1180, 740);
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
               System.out.println("Starting shutdown");

               // Locating the Registry
               Registry registry = LocateRegistry.getRegistry(ip);

               // Looking up the ServerStub class
               ServerStub serverStub = (ServerStub) 
                      registry.lookup("ServerStub");

               System.out.println("Connected to the server");

               // Calling the shutdown method on the server
               System.out.println(serverStub.shutdownClient(name));

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

   }

   public ImageIcon resizeImage(BufferedImage bg, int width, int height) {
      ImageIcon placeholder = new ImageIcon(bg);
      Image img = placeholder.getImage();
      Image resizedImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
      ImageIcon resizedIcon = new ImageIcon(resizedImg);
      return resizedIcon;
   }

   class InfoTimer extends TimerTask {
      public void run() {
         try {
            currPlayer.setText(stub.getTockenOwner());

            // Update playerlist
            Vector<String> playerList = stub.getPlayerNames();
            String players = "";
            for (int i = 0; i < playerList.size(); i++) {
               if (i < playerList.size()-1) {
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
   }


}
