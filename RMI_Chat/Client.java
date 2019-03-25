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
 * Class for the Client.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class Client extends JFrame {

   // For RMI
   private ChatStub stub;

   // For GUI
   private JTextArea chatArea;
   private JTextField inputField;

   // For Client
   private String nickname;
   private String ipaddress;

   /**
    * Constructor for the Client.
    */
   private Client() { 
      super("Chat");
      makeGUI();

      // Creating the registry
      try {
         // Creating the Registry
         Registry registry = LocateRegistry.getRegistry(ipaddress);

         // Looking up the Stub class
         stub = (ChatStub) registry.lookup("ChatStub");

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }
   }

   /**
    * Method to create the Client GUI.
    */
   public void makeGUI() {
      // IP address input
      String[] options = {"OK"};
      JPanel panel = new JPanel();
      JLabel lbl = new JLabel("Enter the server IP address: ");
      JTextField txt = new JTextField(10);
      panel.add(lbl);
      panel.add(txt);
      JOptionPane.showOptionDialog(null, panel, "Connect", 
            JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
      ipaddress = txt.getText();

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
      inputField = new JTextField(50);
      inputArea.add(inputField);

      // Action button
      JButton actionButton = new JButton("Send");
      inputArea.add(actionButton);

      // Action Listener
      actionButton.addActionListener(e -> {
         send("(" + getTime() + " " + nickname + ") " 
                + inputField.getText());
         get();
      });

      // Adding to the frame
      add(inputArea, "South");

      // Send exit message
      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            send(nickname + " has left the chat");
         }
      });

      // Adding the keylistener
      inputField.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent ke) {
            int key = ke.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
               send("(" + getTime() + " " + nickname + ") " 
                      + inputField.getText());
               get();
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

      // Set JFrame sizing
      setSize(250, 250);
      pack();
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      setLocationRelativeTo(null);
   } 

   /**
    * Method to send a message to the server.
    * @param msg the message to send
    */
   public void send(String msg) {
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
   public void get() {
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
         get();
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

   /**
    * Main Method for the Client.
    * @param args for command line input
    */
   public static void main(String[] args) {
      new Client();
   }
}
