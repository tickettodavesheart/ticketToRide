import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Timer;

// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

/**
 * GameBoard Class.
 * 
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
   private String[] prefix = {"black_", "white_", "grey_", "yellow_",
       "green_", "orange_", "purple_", "red_", "blue_", "city_"};
   // Array of ints to control color changing in loop
   private int[] loop = {7, 14, 58, 65, 72, 79, 86, 93, 100, 136};

   // Vector for the buttons
   private Vector<CButton> buttonPaintList = new Vector<CButton>();
   // Vector for the names of the buttons
   private Vector<String> namePaintList = new Vector<String>();

   // ArrayList for claimed routes
   private ArrayList<String> claimedRoutes = new ArrayList<String>();

   // selected routes from server
   private Hashtable<String, String> selectedFromServer = new Hashtable<String, String>();

   // For RMI
   private GameStub stub;
   
   // player name
   private String name;
   
   // current player's name
   private String currentPlayer;

   // For sending who's turn it is intially
   private boolean sendPlayer = true;
   private boolean endPlayerTurn = true;

   // For changing destination card requirement from 2 to 1 after game start
   private boolean beginningCards = true;
   private int turnNumber = 0;

   // Cards that the player has
   private ArrayList<String> cardsList = new ArrayList<String>();

   // Destination Cards that the player has
   private ArrayList<DestinationCard> destinationList = new ArrayList<DestinationCard>();

   // Cards that the player can choose from
   private ArrayList<String> visibleTrainCards = null;

   // The names of the players on the server
   private Vector<String> playerNames;

   // For Restricting Client's turn
   private int numCardsClaimed = 0;
   private int destCardTurns = 0;
   private boolean hasClaimedTrainCard = false;
   private boolean hasClaimedRoute = false;
   private boolean hasClaimedDestCard = false;
   private boolean checklistValid = false;
   private String currentClaimedRoute = "";

   /**
    * GameBoard constructor - creates then adds each button to the panel.
    * @param ip       the IP adress for the main server
    * @param stubID   the id for the GameStub for the specific server
    * @param name     the name of the current game
    * @param nickname the name of the player 
    */
   public GameBoard(String ip, String stubID, String name, String nickname) {
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
   
      // Setting the currentPlayer to their nickname
      currentPlayer = nickname;
   
      try {  
         // Checking if they are the first player
         // if they are starting the token with them
         if (stub.getPlayerNames().size() == 0) {
            stub.setTokenOwner(currentPlayer);
            System.out.println("Set token owner: " + stub.getTockenOwner());
         } else {
            System.out.println("Current token owner: " + stub.getTockenOwner());
         }
         // Adding the player to the list of players on the server
         stub.addName(nickname);
      
         // Getting their starting cards
         ArrayList<String> dealtCards = stub.dealCards(5);
         // Putting the cards on the server
         stub.addPlayerCards(nickname, dealtCards);
         for (String card : dealtCards) {
            cardsList.add(card);
            System.out.println("Card: " + card);
         }
      
      
      } catch (RemoteException re) { }
   
      // Creating the timer to continually update the gameboard
      Timer time = new Timer();
   
      // set player name
      this.name = name;
   
      // Vector<Shape> to hold input from buttonlist.dat
      Vector<Shape> buttonList = null;
   
      setLayout(null);
   
      // Tries to load provided background image and button list vector object
      try {
         this.bgImage = ImageIO.read(getClass().getResource(
                "resources/map.png"));
      
         ObjectInputStream ois = new ObjectInputStream(
                getClass().getResourceAsStream("resources/buttonlist.dat"));
         buttonList = (Vector<Shape>) ois.readObject();
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
         CButton cButton = new CButton(
                s, buttonName, colors[j], ip, stubID, name);
         // Adding the CButton to the vector for buttonList
         buttonPaintList.add(cButton);
         // Adding to the JPanel
         add(cButton);
         //cButton.toggleButton(true);
         // Incrementing and looping for the names and shapes
         i++;
         if (i == loop[j] && j < 9) {
            suff = 1;
            j++;
         }
      } // End button creation loop

      time.schedule(new GameboardUpdate(), 500, 1500);
   
      // Add End Turn Button
      JButton jbEndTurn = new JButton("End Turn");
   
      // Sizing requirements for Button
      Insets insets = getInsets();
      Dimension size = jbEndTurn.getPreferredSize();
      jbEndTurn.setBounds(25 + insets.left, 5 + insets.top, 
             size.width, size.height);
      
      add(jbEndTurn);
   
      // Action listener for the end turn button
      jbEndTurn.addActionListener(
         e -> {
         // Calling the end turn method
            endTurn();
         });
      getDestinationCards();
   } // End GameBoard constructor

   /**
    * method to prompt user for destination cards.
    */
   public void getDestinationCards() {
      System.out.println("Has Claimed Dest Card: " + hasClaimedDestCard);
      System.out.println("beginning Cards: " + beginningCards);
      System.out.println("turnNumber: " + turnNumber);
      // Getting the destination cards from the server
      if ((!hasClaimedDestCard && !hasClaimedRoute && !hasClaimedTrainCard) || (turnNumber < 1 && !hasClaimedRoute && !hasClaimedTrainCard && destCardTurns <= 1)) {
         try {
            JFrame jfDest = new JFrame();
            // Asking what destination cards the user wants to have
            JPanel panel = new JPanel();
            JPanel txtPanel = new JPanel();
            JPanel buttonPanel = new JPanel();
            // Array of checkboxes for validation
            ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
            ArrayList<DestinationCard> dCardsFromServer = stub.getDestinationCards();

            // Adding the cards to the panel of options
            for (DestinationCard dCard : dCardsFromServer) {
               JCheckBox jcb = new JCheckBox(dCard.toString());
               jcb.putClientProperty("card", dCard);
               // Adding to the list
               checkBoxList.add(jcb);
               panel.add(jcb, BorderLayout.CENTER);
               //jcb.addActionListener(new RadioActionListener());
            }

            JButton jbOK = new JButton("Select");
            buttonPanel.add(jbOK);
            jbOK.addActionListener(
               e -> {
               // Making sure at least one button is selected
                  int twoSelected = 0;
                  for (JCheckBox box : checkBoxList) {
                     if (box.isSelected()) {
                        twoSelected++;
                     }
                  }
                  if (beginningCards) {
                     if (twoSelected >= 2) {
                        jfDest.dispose();
                     // Removing choosen cards from the server
                        try {
                           for (DestinationCard dc : destinationList) {
                              stub.removeDestinationCard(dc);
                           }
                           checklistValid = true;
                        } catch (RemoteException re) { }
                     } else {
                        checklistValid = false;
                     }
                  } else if (!beginningCards) {
                     if (twoSelected >= 1) {
                        checklistValid = true;
                        jfDest.dispose();
                     // Removing choosen cards from the server
                        try {
                           for (DestinationCard dc : destinationList) {
                              stub.removeDestinationCard(dc);
                           }
                        } catch (RemoteException re) { }
                     } else {
                        checklistValid = false;
                     }
                  }
                  if (checklistValid) {
                     for (JCheckBox box : checkBoxList) {
                        if (box.isSelected()) {
                           //box.addActionListener(new RadioActionListener());
                           destinationList.add((DestinationCard)box.getClientProperty("card"));
                           hasClaimedDestCard = true;
                        }
                     }
                     destCardTurns++;                     
                  }
                  beginningCards = false;
               });
         
            
         
            txtPanel.add(new JLabel(
                  "Select two or more destination card(s)"));
         
            // // Adding the cards to the panel of options
            // for (DestinationCard dCard : dCardsFromServer) {
            //    JCheckBox jcb = new JCheckBox(dCard.toString());
            //    jcb.putClientProperty("card", dCard);
            //    // Adding to the list
            //    checkBoxList.add(jcb);
            //    panel.add(jcb, BorderLayout.CENTER);
            //    // jcb.addActionListener(new RadioActionListener());
            // }
         
            jfDest.add(txtPanel, BorderLayout.NORTH);
            jfDest.add(panel, BorderLayout.CENTER);
            jfDest.add(buttonPanel, BorderLayout.SOUTH);
         
            jfDest.pack();
            jfDest.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            jfDest.setVisible(true);
            jfDest.setLocationRelativeTo(null);
            
         } catch (RemoteException re) { }
      }
   }

   public void getTrainCards() {
      System.out.println("Has Claimed Dest Card: " + hasClaimedDestCard);
      System.out.println("beginning Cards: " + beginningCards);
      System.out.println("turnNumber: " + turnNumber);
      if ((!hasClaimedDestCard && !hasClaimedRoute && numCardsClaimed < 2) || (turnNumber < 1 && !hasClaimedRoute && numCardsClaimed < 2 && destCardTurns == 1)) {
         try {
            // add a swing InvokeLater here??
            JFrame jfTrain = new JFrame();
            // Asking what destination cards the user wants to have
            JPanel panel = new JPanel();
            JPanel txtPanel = new JPanel();
            JPanel buttonPanel = new JPanel();
            // Array of checkboxes for validation
            ArrayList<JRadioButton> checkBoxList = new ArrayList<JRadioButton>();
            
            JButton jbOK = new JButton("Select");
            buttonPanel.add(jbOK);
            jbOK.addActionListener(
               e -> {
               // Making sure at least one button is selected
                  int selectedTrains = 0;
                  for (JRadioButton box : checkBoxList) {
                     if (box.isSelected()) {
                        selectedTrains++;
                     }
                  }
               //if (beginningCards) {
                  if (selectedTrains == 1) {
                     for (String s : cardsList) {
                        System.out.println(s);
                     }
                     jfTrain.dispose();
                  // Removing choosen card from the server
                  // try {
                  //    stub.removeTrainCard();
                  // } catch (RemoteException re) { }
                  }
               });
         
            visibleTrainCards = stub.getVisibleTrainCards();
            System.out.println("Grabbed visibleTrainCards from server. Count: " + visibleTrainCards.size());
            int rainbowCount = 0;
            for (String s: visibleTrainCards) {
               if (s.equals("NEUTRAL")) {
                  rainbowCount++;
               }
            }
            if (rainbowCount >= 3) {
               for (int i = 0; i < rainbowCount; i++) {
                  visibleTrainCards.remove("NEUTRAL");
                  stub.removeTrainCard("NEUTRAL");
                  stub.setUsedVisibleTrainCards(visibleTrainCards);
               }
               visibleTrainCards.clear();
               visibleTrainCards = stub.getVisibleTrainCards();
               System.out.println("visibleTrainCards cleared. Reset to size: " + visibleTrainCards.size());
            }
            
         
            txtPanel.add(new JLabel(
                  "Select a train card"));
         
            // Adding the cards to the panel of options
            for (String trainCard : visibleTrainCards) {
               JRadioButton jcb = new JRadioButton(trainCard);
               // Adding to the list 
               checkBoxList.add(jcb);
               panel.add(jcb, BorderLayout.CENTER);
               jcb.addActionListener(new TrainRadioActionListener());
            }
            
            JRadioButton jcbRandom = new JRadioButton("RANDOM");
            checkBoxList.add(jcbRandom);
            panel.add(jcbRandom, BorderLayout.CENTER);
            jcbRandom.addActionListener(new TrainRadioActionListener());
         
            jfTrain.add(txtPanel, BorderLayout.NORTH);
            jfTrain.add(panel, BorderLayout.CENTER);
            jfTrain.add(buttonPanel, BorderLayout.SOUTH);
         
            jfTrain.pack();
            jfTrain.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            jfTrain.setVisible(true);
            jfTrain.setLocationRelativeTo(null);
            
         } catch (RemoteException re) { }
      }
   }

    /**
     * Method to get a player's destination cards
     * @return the destination cards a player owns
     */
    public ArrayList<DestinationCard> getDestinationCardsFromPlayer() {
        return destinationList;
     }

   /**
    * Method that is called when the turn is started.
    * Will loop over every key (which is the name) of buttonList
    * on the server and then take its color to paint the
    * button
    */
   public void startTurn() {

      endPlayerTurn = true;
      
      // Checking if this is their last turn
      try {
         if (!stub.lastTurnStarted()) {
            // If it is their last turn
            // Will trigger the end game if true
            stub.isItLastTurn(currentPlayer);
         }
      } catch (RemoteException re) { }
      // Toggling the components on 
      try {
         if (sendPlayer) {
            toggleComponents(true);
            System.out.println("SendPlayer If");
         
            // reset controls
            hasClaimedTrainCard = false;
            hasClaimedRoute = false;
            hasClaimedDestCard = false;
            numCardsClaimed = 0;
            // Sending a message out who's turn it is
            stub.sendMessage(currentPlayer + " is playing");
            // Getting all the new selected routes and setting them
            selectedFromServer = stub.updateRoutes();
            Object[] keys = selectedFromServer.keySet().toArray();
         
            // Creating the default player index
            int currentPlayerIndex = 0;

            System.out.println(selectedFromServer.toString());
            System.out.println("Keys length: " + keys.length);
         
            System.out.println("above for loop");
         
            // Iterating over the vector to see which ones need to be repainted
            for (Object key : keys) {
               System.out.println("in the t");
               System.out.println((String) key);
               // Parsing the id into color and names
               // 0 is the color
               String[] parsed = ((String) key).split("_");
               // Finding the index of the button first using the
               // namePaintList
               int indexOfButton = namePaintList.indexOf((String) key);
               // Getting the correct button in the buttonPaintList
               CButton buttonToPaint = buttonPaintList.elementAt(indexOfButton);
               // Setting the color to paint
               String colorToPaint = parsed[0];
            
               // getting the current players index for painting
               playerNames = stub.getPlayerNames();
               for (int j = 0; j < playerNames.size(); j++) {
                  System.out.println("we in the for");
                  if (playerNames.get(j).equals(selectedFromServer.get(key))) {
                     System.out.println("in the if");
                     currentPlayerIndex = j;
                  }
               }
            
               // iterate through the player names list to find the index 
               // of the current player, and set the color of the road 
               // to the corresponding color
               System.out.println("Printing w player index: " 
                     + selectedFromServer.get(key));
               // Calling the method to paint the color
               // on the given CButton
               buttonToPaint.colorButton("color" 
                        + currentPlayerIndex);
            }
         
         
            // If the last turn has not already started
            if (!stub.lastTurnStarted()) {
                // Checking if the game is over or not
               for (String p : playerNames) {
                    // Getting the number of trains a player has
                  int currentPlayerTrains = stub.getPlayerTrains(p);
                  if (currentPlayerTrains <= 3) {
                     stub.sendMessage(
                                currentPlayer + " has less than 3 trains left everyone will get one more turn!");
                        // Keeping track of the last turn for everyone
                     stub.startLastTurnCounter(currentPlayer);
                     try {
                        stub.calcScore(currentPlayer);
                     } catch (Exception ce) {
                        ce.printStackTrace();
                     }
                  }
               }
            }
            sendPlayer = false;
         }
      } catch (RemoteException re) {
          re.printStackTrace();
      }
   }

   /**
    * Run at end of each users turn to send updates to the server.
    */
   public void endTurn() {
      // increment the turn counter
      turnNumber++;
      System.out.println("\n\n\n\nEndPlayer Turn: " + endPlayerTurn);
      if (endPlayerTurn) {
         // sending route to server to be painted on other clients
         try {
            if(!currentClaimedRoute.equals("")) {
            stub.addRoute(currentPlayer, currentClaimedRoute );
            // adding route to player's local list of owned routes
            claimedRoutes.add(currentClaimedRoute);
            }
            System.out.println("your currently claimed routes are: " + claimedRoutes);
           // Turning the components off
           toggleComponents(false);
        
           setHasClaimedRoute(false, "");
        } catch(RemoteException re) {
           re.printStackTrace();
        }
        // Setting sendPlayer to true for the next turn
        sendPlayer = true;
        try {
            // grab the player name list from the server
            Vector<String> playerNames = stub.getPlayerNames();
            // find and store the current player's index in the list
            int playerIndex = 0;
            for (int i = 0; i < playerNames.size(); i++) {
                if (playerNames.get(i).equals(currentPlayer)) {
                  playerIndex = i;
                } 
            }
        
            // set the token owner to the next player
            if (playerIndex + 1 <= playerNames.size() - 1) {
                stub.setTokenOwner(playerNames.get(playerIndex + 1));
            } else {
                stub.setTokenOwner(playerNames.get(0));
            }
            System.out.println("Token changed to: " + stub.getTockenOwner());
        
            System.out.println("Current Player: " + currentPlayer);
        
            // If the last turn has not already started
            if (stub.lastTurnStarted()) {
                // Checking if the game is over or not
                for (String p : playerNames) {
                // Getting the number of trains a player has
                int currentPlayerTrains = stub.getPlayerTrains(p);
                if (currentPlayerTrains <= 3) {
                    stub.sendMessage(currentPlayer + " has less than 3 trains left everyone will get one more turn!");
                    // Keeping track of the last turn for everyone
                    stub.startLastTurnCounter(currentPlayer);
                }
                }
            }
        } catch (RemoteException re) {
        }
        endPlayerTurn = false;
      }
   }

    /**
     * Used to toggle the components on or off at the start or end of turn.
     * 
     * @param state true or false to toggle
     */
   public void toggleComponents(boolean state) throws RemoteException{
      Component[] components = getComponents();
      for (Component c : components) {
         c.setEnabled(state);
            // If the component is a CButton
         if (c instanceof CButton) {
                // Toggling the buttons on or off
            CButton cb = (CButton) c;
            cb.toggleButton(state, selectedFromServer);
         }
      }
   }

   // Override paintComponent to draw BackGround image
   @Override
   public void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.drawImage(bgImage, 0, 0, this);
   } // end paintComponent @Override

   /**
    * Action Listener for the destination chooser.
    */
   class TrainRadioActionListener implements ActionListener {
      // for when a button is deselected
      private boolean turn = true;
   
      @Override
      public void actionPerformed(ActionEvent e) {
         try {
            if (numCardsClaimed >= 2) {
               turn = false;
            }
         
            if (turn) {
               
               JRadioButton jrb = (JRadioButton) e.getSource();
               // Adding the destination to the destination list
               String card = jrb.getText();
            
               if (card.equals("RANDOM")) {
                  ArrayList<String> randomCard = stub.dealCards(1);
                  card = randomCard.get(0);
                  numCardsClaimed++;
                  System.out.println("numCardsClaimed incremented with random");
               } else if (card.equals("NEUTRAL")) {
                  if (numCardsClaimed >= 1) {
                     cardsList.remove(card);
                  } else {
                     numCardsClaimed = numCardsClaimed + 2;
                     System.out.println("numCardsClaimed incremented by 2");
                  }
               } else {
                  numCardsClaimed++;
                  System.out.println("numCardsClaimed incremented by default");
               
               }
               cardsList.add(card);
               stub.removeTrainCard(card);
               visibleTrainCards.remove(card);
               stub.setUsedVisibleTrainCards(visibleTrainCards);
               hasClaimedTrainCard = true;
               turn = false;
               System.out.println("numCardsClaimed: " + numCardsClaimed);
            
               // Adding to the server
               stub.addPlayerCards(currentPlayer, cardsList);
               
            } else {
               JRadioButton jrb = (JRadioButton) e.getSource();
               // Adding the destination to the destination list
               String card = jrb.getText();
               if (card.equals("RANDOM")) {
                  ArrayList<String> randomCard = stub.dealCards(1);
                  card = randomCard.get(0);
               }
               cardsList.remove(card);
               turn = true;
            }
         } catch (Exception ae) { }
      }
   }


   /**
    *  setHasClaimedRoute 
    *  @param toggle true or false to set to
    */
   public void setHasClaimedRoute(boolean toggle, String routeName) throws RemoteException {
      this.hasClaimedRoute = toggle;
      if(toggle) {
         currentClaimedRoute = routeName;
      } else {
         currentClaimedRoute = "";
      }
      System.out.println(currentPlayer + " has claimed " + routeName + " set to " + toggle);
   }

   /**
    * getHasClaimedRoute 
    * @return boolean true or false if a route has been chosen this turn
    */
    public boolean getHasClaimedRoute() {
      return this.hasClaimedRoute;
    }

    /**
    * getHasClaimedTrainCard 
    * @return boolean true or false if a train has been chosen this turn
    */
    public boolean getHasClaimedTrainCard() {
      return this.hasClaimedTrainCard;
    }

    /**
    * getHasClaimedDestCard 
    * @return boolean true or false if a destination card has been chosen this turn
    */
    public boolean getHasClaimedDestCard() {
       return this.hasClaimedDestCard;
    }

    /**
    * getTurnNumber 
    * @return int number of turns played
    */
    public int getTurnNumber() {
       return this.turnNumber;
    }
    /**
     * Class for the gameboard update timer.
     */
   class GameboardUpdate extends TimerTask {
        /**
         * The run method for the GameboardUpdate task.
         */
      public void run() {
         try {
                // check the server's account of the current player
            String serverCurrentPlayer = stub.getTockenOwner();
         
                // If it is not your turn
            if (!currentPlayer.equals(serverCurrentPlayer)) {
                    // Turning off the components
               toggleComponents(false);
            }
         
                // if the current player is now you, start your turn
            if (currentPlayer.equals(serverCurrentPlayer)) {
               startTurn();
            }
         } catch (RemoteException re) {
         } catch (NullPointerException npe) {
         }
      }
   }   

} // end GameBoard class