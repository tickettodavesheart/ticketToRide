// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// GUI
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * Class for the GameClient.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class GameClient extends JFrame {

   // For RMI Registry
   private String ipaddress;

   // For Client
   private String nickname;

   // For RMI
   private GameStub stub;

   /**
    * Constructor for the ChatClient.
    * @param ip the IP adress for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name the name of the current game
    */
   public GameClient(String ip, String stubID, String name) { 

      setLayout(new BorderLayout(10,10));

      // JPanel for Sidebar

      Thread chatThread = new Thread(new Runnable() {
          public void run() {
            add(new ChatClient(ip, stubID, name), BorderLayout.EAST);
          }
      });
      Thread gameThread = new Thread(new Runnable() {
         public void run() {
            GameBoard gb = new GameBoard();
            add(gb, BorderLayout.CENTER);
            gb.revalidate();
         }
      });

      // Starting the threads
      chatThread.start();
      gameThread.start();

      try {
         // Locating the Registry
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (GameStub) registry.lookup(stubID);

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }

      // Set JFrame sizing
      setSize(1180, 680);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      setVisible(true);
      setLocationRelativeTo(null);

      String[] optionsName = { "OK" };
      JPanel panelName = new JPanel();
      JLabel lblName = new JLabel("Enter your nickname: ");
      JTextField txtName = new JTextField(10);
      panelName.add(lblName);
      panelName.add(txtName);
      JOptionPane.showOptionDialog(null, panelName, "Nickname", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, optionsName, optionsName[0]);
      nickname = txtName.getText();

      // Getting the username
      if (nickname.length() < 1) {
         nickname = "Anonymous";
      }

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
               ServerStub serverStub = (ServerStub) registry.lookup("ServerStub");

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
}
