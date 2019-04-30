import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class testTrain extends JFrame {

   public testTrain() {
    
      JPanel tp = new JPanel(null);
      BufferedImage bgImage = null;
      try {
         bgImage = ImageIO.read(getClass().getResource(
                "resources/cards/Train cards/GR.jpg"));
      } catch (Exception e) {}
      JLabel train = new JLabel(new ImageIcon(bgImage));
      train.setBounds(0, 0, 500, 318);
      int greenC = 5;
      JLabel trainCount = new JLabel("<html><font color=red>" + greenC + "</font>", JLabel.CENTER);
      trainCount.setFont(new Font("Arial", Font.BOLD, 100));
      trainCount.setBackground(Color.BLACK);
      trainCount.setOpaque(true);
      trainCount.setBounds(420, 218, 80, 100);
      tp.add(trainCount);
      tp.add(train);
    
      add(tp);
    
      setSize(516,357);
    
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
   }
    
   public static void main(String[] args) {
      new testTrain();
   }
}