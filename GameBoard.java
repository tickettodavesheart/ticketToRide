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
 * @version 4/15/2019
 */
public class GameBoard extends JPanel {

    // Image to paint as background of pan
   private BufferedImage bgImage = null;  
     
     // Array of colors for each group of button
   private Color[] colors = {Color.BLACK, Color.WHITE, Color.GRAY, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.RED, Color.BLUE, new Color(230, 26, 40)};
   private String[] prefix = {"black_", "white_", "gray_", "yellow_", "green_", "orange_", "purple_", "red_", "blue_", "city_"};    
     // Array of ints to control color changing in loop
   private int[] loop = {7, 14, 58, 65, 72, 79, 86, 93, 100, 136};
    
    /**
     * GameBoard constructor - accepts a background image, creates,
     *                       then adds each button.
     *
     * @param bgPath - relative path to the background image file
     */  
   public GameBoard() {
      
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
      for(Shape s : buttonList) {
         String name = new String(prefix[j] + suff++);
         
         add(new CButton(s, name, colors[j]));
         i++;
         if(i == loop[j] && j < 9) {
            suff = 1;
            j++;
         }
      } // End button creation loop
   } // End GameBoard constructor
   
   // Override paintComponent to draw BackGround image
   @Override
      public void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.drawImage(bgImage, 0 , 0, this);
   } // end paintComponent @Override
} // end GameBoard class