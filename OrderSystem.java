import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/*
   * @author Lucas Kohorst
   * @version 11-22-2019
   * HW 01 OrderSystem
   * A GUI where you can enter items name, cost and amount calculate the cost and save the items to a csv, you can also
   * load the csv and view the items in it with the Prev and Next buttons
   * ISTE120.01
   */
  
public class OrderSystem extends JFrame implements ActionListener {

   //Attributes
   //JLabels
   private JLabel jlName = new JLabel("Item name:", JLabel.RIGHT);
   private JLabel jlNumber = new JLabel("Number of:", JLabel.RIGHT);
   private JLabel jlCost = new JLabel("Cost:", JLabel.RIGHT);
   private JLabel jlAmount = new JLabel("Amount owed:", JLabel.RIGHT);

   //JtextFields
   private JTextField jtfName = new JTextField(10);
   private JTextField jtfNumber = new JTextField(10);
   private JTextField jtfCost = new JTextField(10);
   private JTextField jtfAmount = new JTextField(10);
   
   //JButtons
   private JButton jbCalc = new JButton("Calculate");
   private JButton jbSave = new JButton("Save");
   private JButton jbClear = new JButton("Clear");
   private JButton jbExit = new JButton("Exit");
   private JButton jbLoad = new JButton("Load");
   private JButton jbPrev = new JButton("<Prev");
   private JButton jbNext = new JButton("Next>"); 
   
   //File IO variables
   private String filename = "121Lab1.csv";
   private PrintWriter pw;
   private Scanner scan;
   
   //ArrayList used to store the lines of the .csv
   private ArrayList<String> lines = new ArrayList<String>();
   //in count used to display the number of items in the .csv
   private int count = 0;
   //int increment used to keepe count for to Prev and Next buttons
   private int increment = -1;

   //Main method that creates th view, positions it and sets the default close operation
   public static void main(String[] args) {
      OrderSystem jfMain = new OrderSystem();
      jfMain.setTitle("Lucas Kohorst Item Orders Calculator");
      jfMain.setLocationRelativeTo(null);
      jfMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jfMain.setVisible(true);
   }
   
   //Default Constructor, creates thee GUI with all nessecary buttons, labels, and textfields
   public OrderSystem() {
      //JPanel with gridlayout
      JPanel jpGrid = new JPanel(new GridLayout(0,2));
      
      //Adding the labels to the Grid Layout
      jpGrid.add(jlName);
      jpGrid.add(jtfName);
      
      jpGrid.add(jlNumber);
      jpGrid.add(jtfNumber);
      
      jpGrid.add(jlCost);
      jpGrid.add(jtfCost);
            
    
      jpGrid.add(jlAmount);
      jtfAmount.setEnabled(false);
      jpGrid.add(jtfAmount);
      
      //Adding the GridLayout to the main view in the NORTH position
      add(jpGrid, BorderLayout.NORTH); 
      //Packing the GUI
      pack();
      
      //New grid layouts, one for each set of buttons
      JPanel top = new JPanel(new FlowLayout(FlowLayout.TRAILING)); 
      JPanel bottom = new JPanel(new FlowLayout()); 
      
      //Adding the Calc, Save, Clear, and Exit button to the top flow layout with an action listener and with a tool tip
      jbCalc.addActionListener(this);
      jbCalc.setToolTipText("Calculate the cost of the current item");
      top.add(jbCalc);
      
      jbSave.addActionListener(this); 
      jbSave.setToolTipText("Save the inputted item for later viewing");     
      top.add(jbSave);      
      
      jbClear.addActionListener(this);   
      jbClear.setToolTipText("Clear the text fields");   
      top.add(jbClear);     
      
      jbExit.addActionListener(this);
      jbExit.setToolTipText("Exit the program");
      top.add(jbExit);
   
      //Adding the Load, Prev, Next, button to the bottom flow layout with an action listener and with a tool tip
      jbLoad.addActionListener(this);
      jbLoad.setToolTipText("Load the saved items to view");
      bottom.add(jbLoad);
   
      jbPrev.addActionListener(this);
      jbPrev.setToolTipText("View the previous saved item");
      bottom.add(jbPrev);
   
      jbNext.addActionListener(this);
      jbNext.setToolTipText("View the next saved item");
      bottom.add(jbNext);
      
      //Adding the top flow layout to the JPanel 
      add(top, BorderLayout.CENTER);
      //Adding the bottom flow layout to the JPanel
      add(bottom, BorderLayout.SOUTH);  
      //Packing the frame
      pack();
   }
   
   //ActionListener, added to a button and when clicked executes per button
   public void actionPerformed(ActionEvent ae) {
   
      //Declaring the actionObj to determine what button was pressed
      Object actionObj = ae.getSource();
    
      //Checking the Exit button
      if(actionObj.equals(jbExit)) {
        //Exiting the program
         System.exit(0);
      }
      
      //Checking the Clear button
      if(actionObj.equals(jbClear)) {
         //Clearing all of the text fields
         jtfName.setText("");
         jtfNumber.setText("");
         jtfCost.setText("");
         jtfAmount.setText("");
      }
      
      //Checking the equals button
      if(actionObj.equals(jbCalc)) {
         //Calling the equals() function
         equals();
      }
      
      //Checking the Save button
      if(actionObj.equals(jbSave)) {
         //Trying to run the equals() function and then write the results to a file with the PrintWriter 
         try {
            //Running the equals() function
            equals();
            //PrintWriter to print the text fields to a csv
            pw = new PrintWriter(new FileWriter(new File(filename), true));
            pw.printf("%s, %s, %s, %s\n", jtfName.getText(), jtfNumber.getText(), jtfCost.getText(), jtfAmount.getText());
            //Closing the file
            pw.close();
         }
         //Catching any IOException error and displaying it in a JOption Pane
         catch(IOException ioe) {
            JOptionPane.showMessageDialog(jbSave, "Error saving file");
         }
      }
   
      //Checking the Load button
      if(actionObj.equals(jbLoad)) {
         //Checking to see if the load function runs successfully, if not displaying an error message to the JOption pane 
         if(load() == -1) {
            JOptionPane.showMessageDialog(jbLoad, "Missing file or no data to read.");
         }
         //Otherwise displaying the number of items loaded
         else {
            JOptionPane.showMessageDialog(jbLoad, ("Items: " + count));
         }  
      }
   
      //Checking the Next button
      if(actionObj.equals(jbNext)) {
         //Increment starts at -1 so it can be incremented each time the next or prev button is pressed
         //Checking to see if increment is less than 0 if it is than resetting it back to -1
         if(increment < 0) {
            increment = 0;
         }
         //Incrementing by 1
         increment ++;
         //If the increment is greater than or equal to the size of the ArrayList then display "No data to display" to the JOPtion pane
         if(increment >= lines.size()) {
            JOptionPane.showMessageDialog(jbNext, ("No data to display"));
         }
         //Otherwise split the ArrayList (lines) by commas and then set the text of the the text fields appropriately
         else {
            String[] split = lines.get(increment).split(",");
            jtfName.setText(split[0]);
            jtfNumber.setText(split[1]); 
            jtfCost.setText(split[2]);       
            
         }
      }
      
      //Checking the Prev button
      if(actionObj.equals(jbPrev)) {
         //Increment starts at -1 so it can be incremented each time the next or prev button is pressed
         //Checking to see if the increment is greater than the length of the array if it is setting it to one more than the length
         if(increment >= lines.size()) {
            increment = lines.size() - 1;
         }
         //Decrementing by 1
         increment --;
         //If increment is less than 0 display "No data to display" to the JOption Pane
         if(increment < 0) {
            JOptionPane.showMessageDialog(jbPrev, ("No data to display"));
         }
         // Otherwise split the ArrayList (lines) by commas and then set the text of the the text fields appropriately
         else {
            String[] split = lines.get(increment).split(",");
            jtfName.setText(split[0]);
            jtfNumber.setText(split[1]); 
            jtfCost.setText(split[2]);       
         }
      }
   }
   
   //Equals function when called takes the numbers in the text fields and multiplys them then displays them in the Amount textfield
   public void equals() {
      String numberOf = jtfNumber.getText();
      double number = Double.parseDouble(numberOf);
        
      String costOf = jtfCost.getText();
      double cost = Double.parseDouble(costOf);
        
      double amountOwed = cost * number;
      String owed = String.format("$%.2f", amountOwed);
      jtfAmount.setText(owed);      
   } 
   
   //Load function loads the csv file if there is one to display to the user with the Prev and Next buttons
   //@return count, the number of items that are read in (or -1 if there is an error)
   public int load() {
      //Try to scan the file and add each line to an ArrayList (lines)
      try {
        //Creating the scanner
         scan = new Scanner(new File(filename));
        //While there is text in the file
         while(scan.hasNextLine()) {
         //Scan the next line 
            String name = scan.nextLine();
         //Add the line to the ArrayList (lines)
            lines.add(name);
         //Increment the count
            count ++;
         }     
        //Closing the file
         scan.close();
        //Returning the count
         return count;
      } 
      //Cathcing an IOException
      catch (IOException ioe) {
         return -1;
      }
   
   } 

}