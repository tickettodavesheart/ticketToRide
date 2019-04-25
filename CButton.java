import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
/**
 * CButton - A custom button that accepts a shape and creates a JButton
 * of the given shape.
 * @author Joshua Bugryn
 * @version 4/15/2019
 */

public class CButton extends JButton {
   // Shape object the button will use
   private Shape shape = null;
   // Area object for hit detection
   private Area area = null;
   // name of the button
   private String nameButton = "color_#";
   // Color object to hold button fill color
   private Color trainColor;
   // boolean to track mouse entry and exit
   private boolean entered = false;
   // boolean to track selection and deselection
   private boolean selected = false;

   // Array of colors for each player
   private Color[] colors = {Color.GRAY, Color.GREEN, Color.RED, Color.BLUE};

   // Attributes that are placeholders for the selected route
   private String selectedName = "";
   private String selectedColor = "";

   // Attributes that are given to be repainted with
   private Color paintColor = null;

   // For RMI
   private GameStub stub;

   // The mouse listner for custom button handling
   private MouseListener ml = null;

   /**
    * CButton constructor.
    * @param button - A shape object to draw the button
    * @param nameButton - A string to assign a name to the button
    * @param trainColor - The color the button will be filled
    * @param ip the IP address for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name the name of the current game
    */
   public CButton(Shape button, String nameButton, Color trainColor, 
          String ip, String stubID, String name) {
      this.shape = button;
      this.area = new Area(shape);
      this.nameButton = nameButton;
      this.trainColor = trainColor;

      // remove JButton default styling
      setOpaque(false);
      setFocusPainted(false);
      setBorderPainted(false);

      // set bounds for null layout
      setBounds(0, 0, 850, 618);

      // Bind to the GameServer
      try {
         // Locating the Registry
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (GameStub) registry.lookup(stubID);

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }

      ml = new MouseAdapter() {
         public void mouseEntered(MouseEvent e) {
            entered = true;
            repaint();
         } 

         public void mouseExited(MouseEvent e) {
            entered = false;
            repaint();
         }

         public void mouseClicked(MouseEvent e) {
            if (!selected) {
               try {
                  // Giving it the name and color
                  selectedName = nameButton;
                  selected = true;
                  // Ending the turn
                  endTurn();
               } catch (Exception re) { }
            } else {
               // If the route is deselected then remove it from the list
               selected = false;
            }
            repaint();
         }
      }; // end MouseListener
      
   } // end CButton constructor

   /**
    * Toggles the buttons custom handlers on or off.
    * @param state if they are on or off
    */
   public void toggleButton(boolean state) {
      if (state) {
         addMouseListener(ml);
      } else {
         removeMouseListener(ml);
         System.out.println("Buttons disabled");
      }
   }

   /**
    * Method that is called when the user's turn is over.
    */
   public void endTurn() {
      try {
         // Sending the selected route to the server
         stub.addRoute(selectedName);
      } catch (Exception endTurnE) { }
   }

   /**
    * Method to repaint the button with the given color.
    * @param color the color to paing the button
    */
   public void colorButton(String color) {
      // Creating a switch for the colors
      switch (color) {
         // gray
         case "gray":
            paintColor = colors[0];
            break;
         // green
         case "green":
            paintColor = colors[1];
            break;
         // red
         case "red":
            paintColor = colors[2];
            break;
         // blue
         case "blue":
            paintColor = colors[3];
         // color1
         case "color0":
            paintColor = colors[0];
            break;
         // color2
         case "color1":
            paintColor = colors[1];
            break;
         // color3
         case "color2":
            paintColor = colors[2];
            break;
         // color4
         case "color3":
            paintColor = colors[3];
            break;
      }
      // If it gets repainted it should be disabled as it is already selected
      setEnabled(false);
      // repainting the string
      repaint();
   }

   // Hit detection for mouse actions
   @Override
    public boolean contains(int x, int y) {
      return area.contains((double) x, (double) y);
   }

   // Overriding default JButton size
   @Override
   public Dimension getPreferredSize() {
      return new Dimension(160, 160);
   } // end getPreferredSize @Override


   /* Override paintComponent of JButton
      Draws outline and Fills each button

      Handles repaint() calls from mouseAdapter to change color:
          - on click
          - on mouse in
          - on mouse out
   */
   @Override
   public void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
             RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
            RenderingHints.VALUE_STROKE_PURE);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
             RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setStroke(new BasicStroke(1.5f, 
             BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));


      // if block to check if mouse has entered the button
      // changes outline color to highlight button if true
      if (entered) {
         g2d.setPaint(Color.YELLOW.brighter());
      } else {
         if (trainColor != Color.BLACK) {
            g2d.setPaint(Color.BLACK);
         } else {
            g2d.setPaint(Color.WHITE);
         }
      }
      // draw outline
      g2d.draw(shape);

      // if block to check if mouse click has selected or deselected button
      // changes fill color to highlight button selection or deselection
      // @param trainColor - Default color of the train route

      // FIXME: add the logic
      // TODO: remove this, put somewhere else
      // for when a user needs to repaint with the paintColor
      g2d.setPaint(paintColor);

      // TODO: need to disable the selection of buttons if
      // a client already has it selected on the server
      // display a message to the user they cannot click it
      // FIXME: commented out so that the button is not repainted over in else
      if (selected) {
         // TODO: will be the color of the player
         try {
            // grab the player names from the GameServer stub
            Vector<String> playerNames = stub.getPlayerNames();
            // find the current player
            String currentPlayer = stub.getTockenOwner();
            // iterate through the player names list to find the index 
            // of the current player, and set the color of the road 
            // to the corresponding color
            for (int i = 0; i < playerNames.size(); i++) {
               if (playerNames.get(i).equals(currentPlayer)) {
                  // sets paintColor to the correct color
                  colorButton(new String("color" + i));
               } else {
                  System.out.println("No color found!");
               }
            } 
            // set the color to the new color
            g2d.setPaint(paintColor);
         } catch (RemoteException re) { }
      } else {
         g2d.setPaint(trainColor);
      }

      // fills button with color
      g2d.fill(area);

   } // end paintComponent @Override

   /**
    * getButtonID - returns button ID name.
    * @return nameButton - String of button ID
    */
   public String getButtonID() {
      return nameButton;
   }
} // end CButton class
