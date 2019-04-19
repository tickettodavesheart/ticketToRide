// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// For GUI
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import java.util.Timer;
import java.text.SimpleDateFormat;

/**
 * Class for the ChatClient.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class ChatClient extends JPanel {

   // For RMI
   private GameStub stub;

   // For GUI
   private JTextArea chatArea;
   private JTextField inputField;

   // For Client
   private String nickname;

   // For the game
   private String name = "";
   private String ip = "localhost";

   /**
    * Constructor for the ChatClient.
    * @param ip the IP adress for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name the name of the current game
    */
   public ChatClient(String ip, String stubID, String name) { 
      makeGUI();

      this.name = name;
      this.ip = ip;

      try {
         // Locating the Registry
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (GameStub) registry.lookup(stubID);

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }
   }

   /**
    * Method to create the Client GUI.
    */
   public void makeGUI() {

      String[] optionsName = {"OK"};
      JPanel panelName = new JPanel();
      JLabel lblName = new JLabel("Enter your nickname: ");
      JTextField txtName = new JTextField(10);
      panelName.add(lblName);
      panelName.add(txtName);
      JOptionPane.showOptionDialog(null, panelName, "Nickname", 
             JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, optionsName, optionsName[0]);
      nickname = txtName.getText();

      // Getting the username
      if (nickname.length() < 1) {
         nickname = "Anonymous";
      }

      // JTextArea for Chat
      chatArea = new JTextArea(20, 50);
      chatArea.setLineWrap(true);
      chatArea.setEditable(false);

      // Adding the chat area to the frame
      add(chatArea, "Center");

      // JScrollPane for ChatArea
      JScrollPane jsp = new JScrollPane(chatArea);
      add(jsp, "Center");

      // JPanel for input field and action button
      JPanel inputArea = new JPanel();
      inputArea.setLayout(new FlowLayout());

      // Input field
      inputField = new JTextField(20);
      inputArea.add(inputField);

      // Action button
      JButton actionButton = new JButton("Send");
      inputArea.add(actionButton);

      // Action Listener
      actionButton.addActionListener(e -> {
         sendMessage("(" + getTime() + " " + nickname + ") " 
                + inputField.getText());
         getAllMessages();
      });

      // Adding to the frame
      add(inputArea, "South");

      // // Send exit message
      // addWindowListener(new WindowAdapter() {
      //    @Override
      //    public void windowClosing(WindowEvent e) {
      //       sendMessage(nickname + " has left the chat");
      //       System.out.println("Starting shutdown");
      //       // Get the game that the client is running
      //       // create registry and bind to the main server
      //       // Creating the registry
      //       try {
      //          // Locating the Registry
      //          Registry registry = LocateRegistry.getRegistry(ip);

      //          // Looking up the ServerStub class
      //          ServerStub serverStub = (ServerStub) registry.lookup(
      //                 "ServerStub");

      //          System.out.println("Connected to the server");

      //          // Calling the shutdown method on the server
      //          System.out.println(serverStub.shutdownClient(name));

      //          // Creating the Lobby Again
      //          new Lobby(ip);

      //       } catch (Exception oe) {
      //          System.err.println("Client exception: " + oe.toString());
      //          oe.printStackTrace();
      //       }
      //    }
      // });

      // Adding the keylistener
      inputField.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent ke) {
            int key = ke.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
               sendMessage("(" + getTime() + " " + nickname + ") " 
                      + inputField.getText());
               getAllMessages();
            }
         }
      });

      // Setting the focus to the input field
      inputField.requestFocus();

      // Scrolling to the bottom of the JTextField
      chatArea.setCaretPosition(chatArea.getDocument().getLength());

      // Creating the timer to continually update the GUI
      Timer time = new Timer();
      time.schedule(new MessageTimer(), 500, 1500);
   } 

   /**
    * Method to send a message to the server.
    * @param msg the message to send
    */
   public void sendMessage(String msg) {
      // Creating the registry
      try {
         // Sending the message to the server
         stub.sendMessage(msg);
         inputField.setText("");
      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }
   } 
   
   /**
    * Method to get messages from the server.
    */
   public void getAllMessages() {
      // Creating the registry
      try {
         // Sending the message to the server
         Vector<String> messages = new Vector<String>(stub.getMessages());

         // Clearing the text
         chatArea.setText("");

         for (String msg : messages) {
            chatArea.append(msg + "\n\n");
         }

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }
   }

   /**
    * Class for the messageTimer class.
    */
   class MessageTimer extends TimerTask {
      /**
       * The run method for the ColorTimer task.
       */
      public void run() {
         getAllMessages();
      }
   }

   /**
    * Method that gets the current time in HH:MM:SS format.
    * 
    * @return timestamp
    */
   public String getTime() {
      String timeStamp = new SimpleDateFormat("HH:mm:ss").format(
             Calendar.getInstance().getTime());
      return timeStamp;
   }
}
