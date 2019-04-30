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
    private Color[] colors = { Color.BLACK, Color.WHITE, Color.GRAY, Color.YELLOW, Color.GREEN, Color.ORANGE,
            Color.MAGENTA, Color.RED, Color.BLUE, new Color(230, 26, 40) };
    private String[] prefix = { "black_", "white_", "gray_", "yellow_", "green_", "orange_", "purple_", "red_", "blue_",
            "city_" };
    // Array of ints to control color changing in loop
    private int[] loop = { 7, 14, 58, 65, 72, 79, 86, 93, 100, 136 };

    // Vector for the buttons
    private Vector<CButton> buttonPaintList = new Vector<CButton>();
    // Vector for the names of the buttons
    private Vector<String> namePaintList = new Vector<String>();

    // For RMI
    private GameStub stub;

    // player name
    private String name;

    // current player's name
    private String currentPlayer;

    // For sending who's turn it is intially
    private boolean sendPlayer = true;

    // For changing destination card requirement from 2 to 1 after game start
    private boolean beginningCards = true;

    // Cards that the player has
    private ArrayList<String> cardsList = new ArrayList<String>();

    // Destination Cards that the player has
    private ArrayList<String> destinationList = new ArrayList<String>();

    /**
     * GameBoard constructor - creates then adds each button to the panel.
     * 
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

            getDestinationCards();

        } catch (RemoteException re) {
        }

        // Creating the timer to continually update the gameboard
        Timer time = new Timer();
        time.schedule(new GameboardUpdate(), 500, 1500);

        // set player name
        this.name = name;

        // Vector<Shape> to hold input from buttonlist.dat
        Vector<Shape> buttonList = null;

        setLayout(null);

        // Tries to load provided background image and button list vector object
        try {
            this.bgImage = ImageIO.read(getClass().getResource("resources/map.png"));

            ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("resources/buttonlist.dat"));
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
            CButton cButton = new CButton(s, buttonName, colors[j], ip, stubID, name);
            // Adding the CButton to the vector for buttonList
            buttonPaintList.add(cButton);
            // Adding to the JPanel
            add(cButton);
            // Incrementing and looping for the names and shapes
            i++;
            if (i == loop[j] && j < 9) {
                suff = 1;
                j++;
            }
        } // End button creation loop

        // Add End Turn Button
        JButton jbEndTurn = new JButton("End Turn");

        // Sizing requirements for Button
        Insets insets = getInsets();
        Dimension size = jbEndTurn.getPreferredSize();
        jbEndTurn.setBounds(25 + insets.left, 5 + insets.top, size.width, size.height);

        add(jbEndTurn);

        // Action listener for the end turn button
        jbEndTurn.addActionListener(e -> {
            // Calling the end turn method
            endTurn();
        });

    } // End GameBoard constructor

    /**
     * method to prompt user for destination cards
     */
    public void getDestinationCards() {
        // Getting the destination cards from the server
        try {
            JFrame jfDest = new JFrame();
            // Asking what destination cards the user wants to have
            JPanel panel = new JPanel();
            JPanel txtPanel = new JPanel();
            JPanel buttonPanel = new JPanel();
            // Array of checkboxes for validation
            ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();

            JButton jbOK = new JButton("Select");
            buttonPanel.add(jbOK);
            jbOK.addActionListener(e -> {
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
                            for (String s : destinationList) {
                                stub.removeDestinationCard(s);
                            }
                        } catch (RemoteException re) {
                        }
                    }
                } else if (!beginningCards) {
                    if (twoSelected >= 1) {
                        jfDest.dispose();
                        // Removing choosen cards from the server
                        try {
                            for (String s : destinationList) {
                                stub.removeDestinationCard(s);
                            }
                        } catch (RemoteException re) {
                        }
                    }
                }
                beginningCards = false;
            });

            ArrayList<String> dCardsFromServer = stub.getDestinationCards();

            txtPanel.add(new JLabel("Select two or more destination card(s)"));

            // Adding the cards to the panel of options
            for (String dCard : dCardsFromServer) {
                JCheckBox jcb = new JCheckBox(dCard);
                // Adding to the list
                checkBoxList.add(jcb);
                panel.add(jcb, BorderLayout.CENTER);
                jcb.addActionListener(new RadioActionListener());
            }

            jfDest.add(txtPanel, BorderLayout.NORTH);
            jfDest.add(panel, BorderLayout.CENTER);
            jfDest.add(buttonPanel, BorderLayout.SOUTH);

            jfDest.pack();
            jfDest.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            jfDest.setVisible(true);
            jfDest.setLocationRelativeTo(null);

        } catch (RemoteException re) {
        }
    }

    public void getTrainCards() {
        try {
            JFrame jfTrain = new JFrame();
            // Asking what destination cards the user wants to have
            JPanel panel = new JPanel();
            JPanel txtPanel = new JPanel();
            JPanel buttonPanel = new JPanel();
            // Array of checkboxes for validation
            ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();

            JButton jbOK = new JButton("Select");
            buttonPanel.add(jbOK);
            jbOK.addActionListener(e -> {
                // Making sure at least one button is selected
                int selectedTrains = 0;
                for (JCheckBox box : checkBoxList) {
                    if (box.isSelected()) {
                        selectedTrains++;
                    }
                }
                // if (beginningCards) {
                if (selectedTrains == 1) {
                    for (String s : cardsList) {
                        System.out.println(s);
                    }
                    jfTrain.dispose();
                    // Removing choosen card from the server
                    // try {
                    // stub.removeTrainCard();
                    // } catch (RemoteException re) { }
                }
            });

            ArrayList<String> visibleTrainCards = stub.getVisibleTrainCards();

            txtPanel.add(new JLabel("Select a train card"));

            // Adding the cards to the panel of options
            for (String trainCard : visibleTrainCards) {
                JCheckBox jcb = new JCheckBox(trainCard);
                // Adding to the list
                checkBoxList.add(jcb);
                panel.add(jcb, BorderLayout.CENTER);
                jcb.addActionListener(new TrainRadioActionListener());
            }

            JCheckBox jcbRandom = new JCheckBox("RANDOM");
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

        } catch (RemoteException re) {
        }

    }

    /**
     * Method that is called when the turn is started. Will loop over every key
     * (which is the name) of buttonList on the server and then take its color to
     * paint the button
     */
    public void startTurn() {
        // Checking if this is their last turn
        try {
            if (!stub.lastTurnStarted()) {
                // If it is their last turn
                // Will trigger the end game if true
                stub.isGameOver(currentPlayer);
            }
        } catch (RemoteException re) {
        }
        // Toggling the components on
        toggleComponents(true);
        try {
            if (sendPlayer) {
                // Sending a message out who's turn it is
                stub.sendMessage(currentPlayer + " is playing");
                // Getting all the new selected routes and setting them
                Hashtable<String, String> selectedFromServer = stub.updateRoutes();
                Set<String> keys = selectedFromServer.keySet();
                System.out.println("Routes from server: " + selectedFromServer.toString());
                // Iterating over the vector to see which ones need to be repainted
                for (String key : keys) {
                    // Parsing the id into color and names
                    // 0 is the color
                    String[] parsed = key.split("_");
                    // Finding the index of the button first using the
                    // namePaintList
                    int indexOfButton = namePaintList.indexOf(key);
                    // Getting the correct button in the buttonPaintList
                    CButton buttonToPaint = buttonPaintList.elementAt(indexOfButton);
                    // Setting the color to paint
                    String colorToPaint = parsed[0];

                    // Creating the default player index
                    int currentPlayerIndex = 0;

                    // getting the current players index for painting
                    Vector<String> players = stub.getPlayerNames();
                    for (int j = 0; j < players.size(); j++) {
                        if (players.get(j).equals(selectedFromServer.get(key))) {
                            currentPlayerIndex = j;
                        }
                    }

                    // iterate through the player names list to find the index
                    // of the current player, and set the color of the road
                    // to the corresponding color
                    System.out.println("Printing w player index: " + selectedFromServer.get(key));
                    // Calling the method to paint the color
                    // on the given CButton
                    buttonToPaint.colorButton("color" + currentPlayerIndex);
                }
                sendPlayer = false;
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    /**
     * Run at end of each users turn to send updates to the server.
     */
    public void endTurn() {
        // Turning the components off
        toggleComponents(false);
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
                        stub.sendMessage(
                                currentPlayer + " has less than 3 trains left everyone will get one more turn!");
                        // Keeping track of the last turn for everyone
                        stub.startLastTurnCounter(currentPlayer);
                    }
                }
            }
        } catch (RemoteException re) {
        }
    }

    /**
     * Used to toggle the components on or off at the start or end of turn.
     * 
     * @param state true or false to toggle
     */
    public void toggleComponents(boolean state) {
        Component[] components = getComponents();
        for (Component c : components) {
            c.setEnabled(state);
            // If the component is a CButton
            if (c instanceof CButton) {
                // Toggling the buttons on or off
                CButton cb = (CButton) c;
                cb.toggleButton(state);
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

    /**
     * Action Listener for the destination chooser.
     */
    public class RadioActionListener implements ActionListener {
        // for when a button is deselected
        private boolean turn = true;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (turn) {
                JCheckBox jrb = (JCheckBox) e.getSource();
                // Adding the destination to the destination list
                destinationList.add(jrb.getText());
                turn = false;
            } else {
                JCheckBox jrb = (JCheckBox) e.getSource();
                // Adding the destination to the destination list
                destinationList.remove(jrb.getText());
                turn = true;
            }
        }
    }

    /**
     * Action Listener for the destination chooser.
     */
    public class TrainRadioActionListener implements ActionListener {
        // for when a button is deselected
        private boolean turn = true;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (turn) {
                    JCheckBox jrb = (JCheckBox) e.getSource();
                    // Adding the destination to the destination list
                    String card = jrb.getText();

                    if (card.equals("RANDOM")) {
                        ArrayList<String> randomCard = stub.dealCards(1);
                        card = randomCard.get(0);
                    }
                    cardsList.add(card);
                    stub.removeTrainCard(card);
                    turn = false;

                } else {
                    JCheckBox jrb = (JCheckBox) e.getSource();
                    // Adding the destination to the destination list
                    String card = jrb.getText();
                    if (card.equals("RANDOM")) {
                        ArrayList<String> randomCard = stub.dealCards(1);
                        card = randomCard.get(0);
                    }
                    cardsList.remove(card);
                    turn = true;
                }
            } catch (RemoteException re) {
            }
        }
    }
} // end GameBoard class
