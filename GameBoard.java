import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

/**
 * GameBoard Class.
 * @author Joshua Bugryn
 * @version 4/6/2019
 */
public class GameBoard extends JFrame {

     // Object file holding the Vector<Shape> of shapes for each button
   private File buttonFile = new File("buttonlist.dat");
     
     // Vector<Shape> to hold input from buttonlist.dat
   private Vector<Shape> buttonList = null;
     
     // Array of colors for each group of button
   private Color[] colors = {Color.BLACK, Color.WHITE, Color.GRAY, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.RED, Color.BLUE, new Color(230, 26, 40)};
        
     // Array of ints to control color changing in loop
   private int[] loop = {6, 19, 50, 57, 64, 71, 78, 85, 92, 128};
      
   public GameBoard() {
      BufferedImage bgImage = null;
     
      // Width and Height of GameBoard window
      setSize(870, 638);
        
      try {
         bgImage = ImageIO.read(getClass().getResource("map.png"));
        
         if(buttonFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(buttonFile));
            buttonList = (Vector<Shape>)ois.readObject();
            ois.close();
         } else {
            buttonList = new Vector<Shape>();
         }
      } catch (IOException ioe) {
         ioe.printStackTrace();
      } catch (ClassNotFoundException cnfe) {
         cnfe.printStackTrace();
      }
      
      // Create the BackgroundPanel and read in Background image, and button list Vector
      BGPanel gamePanel = new BGPanel(bgImage);
      
      
      // Loop to create and add each Custom Button (CButton)
      int i = 0;
      int j = 0;
      for(Shape s : buttonList) {
         String name = new String("" + i);
         gamePanel.add(new CButton(s, name, colors[j]));
         i++;
         if(i == loop[j] && j < 9) {
            j++;
         }
      }
      
      add(gamePanel);
      
      setVisible(true);
      repaint();
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      
        
   }
   
   /**
    * CButton Class - A custom button that accepts a
    * shape and creates a JButton of the given shape.
    */
   public class CButton extends JButton {
     // Shape object the button will use
      private Shape shape = null;
     // Area object for hit detection
      private Area area = null;
     // name of the button
      private String name = "";
     // Color object to hold button fill color
      private Color trainColor;
     // boolean to track mouse entry and exit
      private boolean entered = false;
     // boolean to track selection and deselection
      private boolean selected = false;
   
      
      
      /**
       * CButton constructor.
       * 
       * @param button - A shape object to draw the button
       * @param name - A string to assign a name to the button
       * @param trainColor - The color the button will be filled
       */
      public CButton(Shape button, String name, Color trainColor) {
         this.shape = button;
         this.area = new Area(shape);
         this.name = name;
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
                  System.out.println(name + " entered");
                  entered = true;
                  repaint();
               }
               public void mouseExited(MouseEvent e) {
                  System.out.println(name + " exited");
                  entered = false;
                  repaint();
               }
               public void mouseClicked(MouseEvent e) {
                  //JOptionPane.showMessageDialog(null, "Click Event", "Clicked", JOptionPane.INFORMATION_MESSAGE);
                  System.out.println(name + " clicked");
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
   } // end CButton class
  
  
  /**
   * BGPanel - a custom JPanel that contains a BackGround image
   * @param bgImage - A BufferedImage to draw as the BackGround
   */ 
   public class BGPanel extends JPanel {
      private BufferedImage bgImage = null;
      
      /*
        BGPanel Constructor
        
        @param bgImage - BufferedImage to draw as BackGround
      */
      public BGPanel(BufferedImage bgImage) {
         this.bgImage = bgImage;
         setLayout(null);
      }
      
      // Override paintComponent to draw BackGround image
      @Override
      public void paintComponent(Graphics g) {
         Graphics2D g2d = (Graphics2D)g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
         g2d.drawImage(bgImage, 0 , 0, this);
      } // end paintComponent @Override
   } // end BGPanel class
   
   public static void main(String[] args) {
      new GameBoard();
   }
} // end GameBoard class