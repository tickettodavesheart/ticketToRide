import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

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

   
   
   /*
      CButton constructor
      
      @param button - A shape object to draw the button
      @param name - A string to assign a name to the button
      @param trainColor - The color the button will be filled
   */
   public CButton(Shape button, String nameButton, Color trainColor) {
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
                  //System.out.println(name + " clicked");
                  if(!selected) {
                     selected = true;
                  } else {
                     selected = false;
                  }
                  repaint();
               }
            }); // end MouseListener
   } // end CButton constructor
   
   
   // Hit detection for mouse actions
   @Override
    public boolean contains(int x, int y) {
      return area.contains((double) x, (double) y);
   }
   
   // Overriding default JButton size
   @Override
   public Dimension getPreferredSize() {
      return new Dimension(160,160);
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
      
      if(selected) {
         g2d.setPaint(new Color(0, 255, 255));
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