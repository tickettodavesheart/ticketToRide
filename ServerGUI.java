import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class ServerGUI extends JFrame {

   /**
    * ServerGUI Constructor
    */

   private JLabel localHostInfo;

   public ServerGUI(String localhost) {

      System.out.println(localhost);

      setLayout(new BorderLayout(10, 10));

      JPanel dataPanel = new JPanel();
      dataPanel.setLayout(new FlowLayout());
      dataPanel.setBorder(BorderFactory.createTitledBorder("Information"));
      add(dataPanel, BorderLayout.CENTER);

      JLabel localHostLabel = new JLabel("IP Address: ");
      dataPanel.add(localHostLabel);

      JLabel localHostInfo = new JLabel();
      localHostInfo.setText(localhost);
      dataPanel.add(localHostInfo);

      // Settings
      setTitle("Ticket to Ride Server");
      setVisible(true);
      setSize(400, 100);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      setLocationRelativeTo(null);
   }
   
}