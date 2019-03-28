// GUI
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

import java.util.*;
import java.util.Vector;
import java.util.Timer;

/**
 * LogIn Class.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class Login extends JFrame {

   // Global Variables
   // GUI
   private JLabel jlGames = new JLabel("Available Games");
   private JList<String> jlstGame;

   // RMI
   private ServerStub stub;

   // Vectors locally
   private Vector<String> games;
   private Vector<Integer> ports;

   // The game that the user selected to join
   private String selectedGame = null;

   /**
    * Main Constructor for the ClientGUI Class. Creates thee GUI window.
    */
   public Login() {
      super("Ticket to Ride");

      // IP address input
      String[] options = {"Connect"};
      JPanel panel = new JPanel();
      JLabel lbl = new JLabel("Enter the main server's IP address: ");
      JTextField txt = new JTextField(10);
      panel.add(lbl);
      panel.add(txt);
      JOptionPane.showOptionDialog(null, panel, "Connect", 
            JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
      String ip = txt.getText();

      // Connecting to the main Server
      connectMainServer(ip);

      // Getting all of the names of games on the
      // main server
      queryGames();

      // Creating the JPanel then adding it to the frame
      JPanel jp = new JPanel();
      jp.setLayout(new GridLayout(0, 1));

      // Creating the join button.
      JButton jbJoin = new JButton("Join");

      // Adding the action listener for the join button
      jbJoin.addActionListener(e -> {
         // Checking if the selected game is null or the intial message
         if (selectedGame == null || selectedGame.equals("No games available")) {
            // Display a message to the user that there are no games
            // that they are able to join
            JOptionPane.showMessageDialog(null, "No games available to join");
         } else {
            // Joining the selected game
            joinGame(selectedGame);
         }  
      });

      // Creating the create game button.
      JButton jbCreate = new JButton("Create Game");

      // Adding the action listener for the create button
      jbCreate.addActionListener(e -> {
         // Creating a new game on the server
         createGame();
      });

      // Creating the list
      jlstGame = new JList(games);

      // Adding the selection listener to the list
      jlstGame.addListSelectionListener(e -> {
         try {
            selectedGame = jlstGame.getSelectedValue().toString();
         } catch (ArrayIndexOutOfBoundsException aroobe) { }   
      });

      // Adding the elements to the JPanel
      jp.add(jlGames);
      jp.add(new JScrollPane(jlstGame));
      jp.add(jbJoin);
      jp.add(jbCreate);

      // Adding the JPanel to the frame
      add(jp, "Center");

      // Creating the timer to continually update the list of games
      Timer time = new Timer();
      time.schedule(new GameListTimer(), 50, 100);

      // Set JFrame sizing
      setSize(250, 250);
      // pack();
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      setLocationRelativeTo(null);
   }

   /**
    * Class for the GameListTimer class.
    */
   class GameListTimer extends TimerTask {
      /**
       * The run method for the GameListTimer task.
       */
      public void run() {
         queryGames();
      }
   }

   /**
    * Method to get all of the game names on the server.
    */
   public void queryGames() {
      try {
         games = stub.getGames();

         if (stub.getNumberActive() < 1) {
            games = new Vector<String>();
            games.addElement("No games available");
         } else {
            // Creating the game list from the game Vector
            jlstGame.setListData(games);
         }

      } catch (RemoteException re) {
         System.out.println("Remote Exception: " +   re);
      }   
   }

   /**
    * Method to get all of the ports of games on the server.
    */
   public void queryPorts() {
      try {
         ports = stub.getPorts();
      } catch (RemoteException re) {
         System.out.println("Remote Exception: " + re);
      }
   }
   
   /**
    * Method to create a new game on the server.
    */
   public void createGame() {
      try {
         System.out.println(stub.newGame());
      } catch (RemoteException re) {
         System.out.println("Remote Exception: " + re);
      }
   }
   
   /**
    * Method to join a given game on the server.
    * @param gameToEnter the name of the game to join
    */
   public void joinGame(String gameToEnter) {
      try {
         System.out.println(stub.enterGame(gameToEnter));
      } catch (RemoteException re) {
         System.out.println("Remote Exception: " + re);
      }
   }

   /**
    * Method to connect to the main server.
    * @param ip the IP address for the main server.
    */
   public void connectMainServer(String ip) {
      // Creating the registry
      try {
         // Creating the Registry with the specific port of the game
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (ServerStub) registry.lookup("ServerStub");

         System.out.println("Connected to Main Server");

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
         JOptionPane.showMessageDialog(null, "Client exception: "
                + e.toString(), "Connection Failed", 
                JOptionPane.INFORMATION_MESSAGE);
      }
   } 

   /**
    * Main Method for the ClientGUI Class.
    * @param args for command line input.
    */
   public static void main(String[] args) {
      new Login();
   }

}
