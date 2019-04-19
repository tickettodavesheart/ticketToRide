import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

/**
 * GameBoard Class.
 * @author Joshua Bugryn
 * @version 4/15/2019
 */
public class GameBoard extends JPanel {

   // Image to paint as background of panel
   private BufferedImage bgImage = null;

   // Array of colors for each group of button
   private Color[] colors = {Color.BLACK, Color.WHITE, Color.GRAY,
       Color.YELLOW, Color.GREEN, Color.ORANGE, Color.MAGENTA,
       Color.RED, Color.BLUE, new Color(230, 26, 40)};
   private String[] prefix = {"black_", "white_", "gray_", "yellow_",
       "green_", "orange_", "purple_", "red_", "blue_", "city_"};
   // Array of ints to control color changing in loop
   private int[] loop = {7, 14, 58, 65, 72, 79, 86, 93, 100, 136};

   // Vector for the buttons
   private Vector<CButton> buttonPaintList = new Vector<CButton>();
   // Vector for the names of the buttons
   private Vector<String> namePaintList = new Vector<String>();

   // For RMI
   private GameStub stub;
   
   // player name
   private String name;

   /**
    * GameBoard constructor - creates then adds each button to the panel.
    * @param ip     the IP adress for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name   the name of the current game
    */
   public GameBoard(String ip, String stubID, String name) {
      // Bind to the GameServer
      
      this.name = name;
      try {
         // Locating the Registry
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (GameStub) registry.lookup(stubID);

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }

      // Vector<Shape> to hold input from buttonlist.dat
      Vector<Shape> buttonList = null;

      setLayout(null);

      // Tries to load provided background image and button list vector object
      try {
         this.bgImage = ImageIO.read(getClass().getResource("resources/map.png"));

         ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("resources/buttonlist.dat"));
         buttonList = (Vector<Shape>)ois.readObject();
         ois.close();
      } catch (IOException ioe) {
         ioe.printStackTrace();
      } catch (ClassNotFoundException cnfe) {
         cnfe.printStackTrace();
      }

      // Loop to create and add each Custom Button (CButton)
      int i = 0;
      int j = 0;
      int suff = 1;
      for (Shape s : buttonList) {
        // Creating the name of the button
         String buttonName = new String(prefix[j] + suff++);
         // Adding the buttonname to the vector
         namePaintList.add(buttonName);

         // Creating a new CButton for the game board
         CButton cButton= new CButton(s, buttonName, colors[j], ip, stubID, name);
         // Adding the CButton to the vector for buttonList
         buttonPaintList.add(cButton);
         // Adding to the JPanel
         add(cButton);
         // Incrementing and looping for the names and shapes
         i++;
         if (i == loop[j] && j < 9) {
            suff = 1;
            j++;
         }
      } // End button creation loop
      // Running the start of turn method
      startTurn();
   } // End GameBoard constructor

   /**
    * Method that is called when the turn is started.
    * Will loop over every key (which is the name) of buttonList
    * on the server and then take its color to paint the
    * button
    */
   public void startTurn() {
      System.out.println("In start turn");
      try {
         // Getting all the newely selected routes and setting them
         Vector<String> selectedFromServer = stub.updateRoutes();
         System.out.println("Routes from server: " + selectedFromServer);
         // Iterating over the vector to see which ones need to be repainted
         for (String key : selectedFromServer) {
            System.out.println("In for loop");
            // Parsing the id into color and names
            // 0 is the color
            String[] parsed = key.split("_");
            // Finding the index of the button first using the
            // namePaintList
            int indexOfButton = namePaintList.indexOf(key);
            // Getting the correct butotn in the buttonPaintList
            CButton buttonToPaint = buttonPaintList.elementAt(indexOfButton);
            // Setting the color to paint
            String colorToPaint = parsed[0];
            System.out.println("Color to paint: " + colorToPaint);
            // Calling the method to paint the color on the given CButton
            buttonToPaint.colorButton(colorToPaint);
            System.out.println("Called the method on the button");
         }
      } catch (Exception startTurne) { 
         System.out.println("Exception: " + startTurne);
      }
   }
   
   public void endTurn() {
   
      try {
         // grab the player name list from the server
         Vector<String> playerNames = stub.getPlayerNames();
         // find and store the current player's index in the list
         int playerIndex = 0;
         for(int i = 0; i < playerNames.size(); i++) {
            if(playerNames.get(i).equals(name)) {
               playerIndex = i;
            }
         }
         // set the token owner to the next player
         if (playerIndex + 1 <= playerNames.size() - 1) {
            stub.setTokenOwner(playerNames.get(playerIndex + 1));
         } else {
            stub.setTokenOwner(playerNames.get(0));
         }
      } catch (RemoteException re) { }
      
   }

   // Override paintComponent to draw BackGround image
   @Override
      public void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
             RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.drawImage(bgImage, 0, 0, this);
   } // end paintComponent @Override
} // end GameBoard class
