import javax.swing.*;

import com.sun.javafx.scene.control.SelectedCellsMap;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
/**
 * CButton - A custom button that accepts a shape and creates a JButton
 *             of the given shape.
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

   /**
    * CButton constructor.
    * @param button - A shape object to draw the button
    * @param name - A string to assign a name to the button
    * @param trainColor - The color the button will be filled
    * @param ip the IP adress for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name the name of the current game
    */
   public CButton(Shape button, String nameButton, Color trainColor, String ip, String stubID, String name) {
      this.shape = button;
      this.area = new Area(shape);
      this.nameButton = nameButton;
      this.trainColor = trainColor;

      // remove JButton default styling
      setOpaque(false);
      setFocusPainted(false);
      setBorderPainted(false);

      // set bounds for null layout
      setBounds(0,0,850,618);

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

      // Anonymous inner class for mouse events
      addMouseListener(
            new MouseAdapter() {
               public void mouseEntered(MouseEvent e) {
                  //System.out.println(name + " entered");
                  entered = true;
                  repaint();
               }
               public void mouseExited(MouseEvent e) {
                  //System.out.println(name + " exited");
                  entered = false;
                  repaint();
               }
               public void mouseClicked(MouseEvent e) {
                  JOptionPane.showMessageDialog(null, nameButton, "Clicked", JOptionPane.INFORMATION_MESSAGE);
                  if(!selected) {
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
            }); // end MouseListener
   } // end CButton constructor

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
      System.out.println("In the CButton colorButton method");
      // Creating a switch for the colors
      switch (color.toLowerCase()) {
         case "gray":
            paintColor = colors[0];
            break;
         case "green":
            paintColor = colors[1];
            break;
         case "red":
            paintColor = colors[2];
            break;
         case "blue":
            paintColor = colors[3];
      }
      // If it gets repainted it should be disabled as it is already selected
      setEnabled(false);
      // repainting the string
      repaint();
      System.out.println("Repainted");
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
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
      //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));


      // if block to check if mouse has entered the button
      // changes outline color to highlight button if true
      if(entered) {
         g2d.setPaint(Color.YELLOW.brighter());
      } else {
         if(trainColor != Color.BLACK) {
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
      if(selected) {
         // TODO: will be the color of the player
         g2d.setPaint(Color.BLACK);
      } else {
         g2d.setPaint(trainColor);
      }

      // fills button with color
      g2d.fill(area);

   } // end paintComponent @Override

   /**
    * getButtonID - returns button ID name
    *
    * @return nameButton - String of button ID
    */
   public String getButtonID() {
      return nameButton;
   }
} // end CButton class
