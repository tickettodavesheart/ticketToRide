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
   
   // Current players name
   private String currentPlayer;

   // boolean that is set to false after first on the timer
   // so that sending route to server only happens once
   private boolean sendRoutes = true;

   // boolean to limit the selection of a route to once
   private boolean selectedRoutesOnce = true;

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

         // find the current player
         currentPlayer = stub.getTockenOwner();

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
            if (!selected && selectedRoutesOnce) {
               try {
                     System.out.println("In if");
                  // Giving it the name and color
                  selectedName = nameButton;
						selected = true;
						// Giving it the name and color
						// getting the current players index for painting
						// grab the player names from the GameServer stub     
						Vector<String> playerNames = stub.getPlayerNames();
                                    currentPlayer = stub.getTockenOwner();
						// iterate through the player names list to find the index 
						// of the current player, and set the color of the road 
						// to the corresponding color
						for (int i = 0; i < playerNames.size(); i++) {
								if (playerNames.get(i).equals(currentPlayer)) {
									// Calling the method to paint the color on the given CButton
									colorButton("color" + i);
								} 
                                    }
                                    // Decrementing the player's trains
                                    stub.decrementPlayerTrains(currentPlayer, nameButton);
                  selectedRoutesOnce = false;
                  // Ending
                  // Ending the turn
                  endTurn();
               } catch (Exception re) {
                     System.out.println("Exception in clicked");
                }
            } else {
                  try {
                        System.out.println("Running else");
                        Vector<String> playerNames = stub.getPlayerNames();
                        for (int i = 0; i < playerNames.size(); i++) {
                              if (playerNames.get(i).equals(currentPlayer)) {
                                    // Calling the method to paint the color on the given CButton
                                    colorButton("color" + i);
                              } 
                        }
            } catch (Exception ree) { }
               // If the route is deselected then remove it from the list
               selected = false;
            }
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
         // Setting the boolean to send routes for the new turn to true
         sendRoutes = true;
      } else {
         removeMouseListener(ml);
         sendRoutes = false;
      }
   }

   /**
    * Method that is called when the user's turn is over.
    */
   public void endTurn() {
      if (sendRoutes) {
         try {
            // Sending the selected route and name to the 
            // server to paint on the next client
            stub.addRoute(currentPlayer, selectedName);
            System.out.println("Added: " + currentPlayer + " to route: " + selectedName);
				sendRoutes = false;
				// No longer can select a route
				toggleButton(false);
         } catch (Exception endTurnE) { }
      }
   }

   /**
    * Method to repaint the button with the given color.
    * @param color the color to paing the button
    */
   public void colorButton(String color) {
      // Creating a switch for the colors
      switch (color) {
         // color1
         case "color0":
            paintColor = colors[0];
            selected = true;
            // repainting the string
            repaint();
            break;
         // color2
         case "color1":
            paintColor = colors[1];
            selected = true;
            // repainting the string
            repaint();
            break;
         // color3
         case "color2":
            paintColor = colors[2];
            selected = true;
            // repainting the string
            repaint();
            break;
         // color4
         case "color3":
            paintColor = colors[3];
            selected = true;
            // repainting the string
            repaint();
            break;
      }
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

      if (selected) {
         // set the color to the new color
         g2d.setPaint(paintColor.darker());
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
