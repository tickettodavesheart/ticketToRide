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

   // For RMI
   // private Stub stub;

   /**
    * Constructor for the ChatClient.
    * @param ip the IP adress for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name the name of the current game
    */
   public GameClient(String ip, String stubID, String name) { 

      setLayout(new GridLayout(0, 2));
      Thread chatThread = new Thread(new Runnable() {
         public void run() {
            add(new ChatClient(ip, stubID, name));
         }
      });
      Thread gameThread = new Thread(new Runnable() {
         public void run() {
            add(new GameBoard(ip, stubID, name));
         }
      });

      // Starting the threads
      chatThread.start();
      gameThread.start();

      // Set JFrame sizing
      setSize(600, 600);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      setVisible(true);
      setLocationRelativeTo(null);

   }
}
