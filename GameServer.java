import java.util.Vector;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.Hashtable;

import java.io.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;

/**
 * GameServer Class.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class GameServer implements GameStub {

   // Vector for the messages on the server
   private Vector<String> messages = new Vector<String>();
   private Vector<String> playerNames = new Vector<String>();
   private String tokenOwner = "";
   // Vector of arraylists with each route and color that it takes
   private Hashtable<String, String> selectedRoutes =
          new Hashtable<String, String>();
   // ArrayList of all cards available at the start of the game
   private ArrayList<String> cardsLeft = new ArrayList<String>(
       Arrays.asList("BLACK", "BLACK", "BLACK", "BLACK", "BLACK", "BLACK", 
             "BLACK", "BLACK", "BLACK", "BLACK", "BLACK", "BLACK", "BLUE", 
             "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", 
             "BLUE", "BLUE", "BLUE", "GREEN", "GREEN", "GREEN", "GREEN", 
             "GREEN", "GREEN", "GREEN", "GREEN", "GREEN", "GREEN", "GREEN", 
             "GREEN", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", 
             "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", 
             "NEUTRAL", "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", 
             "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", 
             "ORANGE", "ORANGE", "PINK", "PINK", "PINK", "PINK", 
             "PINK", "PINK", "PINK", "PINK", "PINK", "PINK", 
             "PINK", "PINK", "RED", "RED", "RED", "RED", 
             "RED", "RED", "RED", "RED", 
             "RED", "RED", "RED", "RED", "WHITE", "WHITE", 
             "WHITE", "WHITE", "WHITE", 
             "WHITE", "WHITE", "WHITE", "WHITE", "WHITE", 
             "WHITE", "WHITE", "YELLOW", 
             "YELLOW", "YELLOW", "YELLOW", "YELLOW", "YELLOW",
              "YELLOW", "YELLOW", 
             "YELLOW", "YELLOW", "YELLOW", "YELLOW" 
   ));

   // ArrayList of available destination cards
   private ArrayList<String> destinationCardsLeft = new ArrayList<String>(
       Arrays.asList("Los Angeles to New York City (21)", 
       "Duluth to Houston (8)", 
       "Sault Ste Marie to Nashville (8)", 
       "New York to Atlanta (6)", 
       "Portland to Nashville (17)", "Vancouver to Montréal (20)", 
       "Duluth to El Paso (10)", "Toronto to Miami (10)", 
       "Portland to Phoenix (11)",
       "Dallas to New York City (11)", "Calgary to Salt Lake City (7)", 
       "Calgary to Phoenix (13)", 
       "Los Angeles to Miami (20)", "Winnipeg to Little Rock (11)", 
       "San Francisco to Atlanta (17)", 
       "Kansas City to Houston (5)", "Los Angeles to Chicago (16)", 
       "Denver to Pittsburgh (11)", 
       "Chicago to Santa Fe (9)", "Vancouver to Santa Fe (13)", 
       "Boston to Miami (12)", 
       "Chicago to New Orleans (7)", "Montreal to Atlanta (9)", 
       "Seattle to New York (22)", 
       "Denver to El Paso (4)", "Helena to Los Angeles (8)", 
       "Winnipeg to Houston (12)", 
       "Montreal to New Orleans (13)", "Sault Ste. Marie to Oklahoma City (9)", 
       "Seattle to Los Angeles (9))"
   ));

   // Hashtable to hold the given players claimed routes
   private Hashtable<String, ArrayList<String>> playerClaimedRoutes = 
          new Hashtable<String, ArrayList<String>>();

   // Array of the trains that a player has left
   private int[] trainsLeft = new int[]{45, 45, 45, 45, 45}; 

   // Generate a Hashtable from the XML
   private Hashtable<String, Integer> hashedRoutes = new Hashtable<String, Integer>();

   // Document builders for XML parsing
   private DocumentBuilder builder;
   private XPath path;

   // Last turn counter
   private int lastTurnCounter = -1;
   private boolean lastTurnHasNotStarted = true;

   /**
    * Method that returns a message.
    * @return String of the message.
    */
   @Override
   public Vector<String> getMessages() {
      return messages;
   }

   /**
    * Method that adds a message to the server.
    * @param message the message to be added.
    */
   @Override
   public void sendMessage(String message) {
      System.out.println("New message: " + message);
      messages.add(message);
   }
   
   /**
    * Method that returns the player names.
    */
   @Override
   public Vector<String> getPlayerNames() {
      return playerNames;
   }
   
   /**
    * Method that returns the token owner.
    */
   @Override
   public String getTockenOwner() {
      return tokenOwner;
   }
   
   /**
    * Method that returns the player names.
    * @param username the player that will hold the token.
    */
   @Override
   public void setTokenOwner(String username) {
      tokenOwner = username;
   }
   
   /**
    * Method that returns the player names.
    * @param nickname the player that will hold the token.
    */
   @Override
   public void addName(String nickname) {
      playerNames.add(nickname);
   }

   /**
    * Method to add a new route to paint to the Server.
    * @param name the name of the current player
    * @param route the route to paintColor
    */
   @Override
   public void addRoute(String name, String route) {
      // Adding the route and color to the selected routes.
      selectedRoutes.put(route, name);
      
      ArrayList<String> newRoutes;
      if (playerClaimedRoutes.get(name) != null) {
         newRoutes = playerClaimedRoutes.get(name);
      } else {
         newRoutes = new ArrayList<String>();
      }
      // Adding the new route to the previous list
      newRoutes.add(route);
      // Adding the route and name to the Hashtable of players
      // and routes
      playerClaimedRoutes.put(name, newRoutes);
   }

   /**
    * Method to change selected routes.
    * @return selectedRoutes the route selected and it's color
    */
   @Override
   public Hashtable<String, String> updateRoutes() {
      return selectedRoutes;
   }

   /**
    * Method to deal the initial cards to a player.
    * @return cards a list of cards a player gets
    */
   @Override
   public ArrayList<String> dealCards() {
      ArrayList<String> dealtCards = new ArrayList<String>();
      // Generating a random number to choose the 
      // index at
      Random rand = new Random();
      // Generate the number in the range of the indicies
      int card1 = rand.nextInt(cardsLeft.size() - 1);
      // Adding the card
      dealtCards.add(cardsLeft.get(card1));
      // Removing the dealt card from the cardsLeft
      cardsLeft.remove(card1);
      int card2 = rand.nextInt(cardsLeft.size() - 1);
      // Adding the card
      dealtCards.add(cardsLeft.get(card2));
      // Removing the dealt card from the cardsLeft
      cardsLeft.remove(card2);
      int card3 = rand.nextInt(cardsLeft.size() - 1);
      // Adding the card
      dealtCards.add(cardsLeft.get(card3));
      // Removing the dealt card from the cardsLeft
      cardsLeft.remove(card3);
      int card4 = rand.nextInt(cardsLeft.size() - 1);
      // Adding the card
      dealtCards.add(cardsLeft.get(card4));
      // Removing the dealt card from the cardsLeft
      cardsLeft.remove(card4);
      int card5 = rand.nextInt(cardsLeft.size() - 1);
      // Adding the card
      dealtCards.add(cardsLeft.get(card5));
      // Removing the dealt card from the cardsLeft
      cardsLeft.remove(card5);

      return dealtCards;

   }

   /**
    * Returns random destination cards the user can choose from.
    * @return destination cards the user can choose from
    */
   @Override
   public ArrayList<String> getDestinationCards() {
      ArrayList<String> destinationCards = new ArrayList<String>();
      Random rand2 = new Random();
      // Generate the number in the range of the indicies
      int d1 = rand2.nextInt(destinationCardsLeft.size() - 1);
      // Adding the card
      destinationCards.add(destinationCardsLeft.get(d1));
      // Removing the dealt card from the cardsLeft
      destinationCardsLeft.remove(d1);
      int d2 = rand2.nextInt(destinationCardsLeft.size() - 1);
      // Adding the card
      destinationCards.add(destinationCardsLeft.get(d2));
      // Removing the dealt card from the cardsLeft
      destinationCardsLeft.remove(d2);
      int d3 = rand2.nextInt(destinationCardsLeft.size() - 1);
      // Adding the card
      destinationCards.add(destinationCardsLeft.get(d3));
      // Removing the dealt card from the cardsLeft
      destinationCardsLeft.remove(d3);

      return destinationCards;
   }

   /**
    * Removes the choosen destination cards from the 
    * destination cards left. 
    * @param choosenCard the cards that were selected
    */
   @Override
   public void removeDestinationCard(String choosenCard) {
      destinationCardsLeft.remove(choosenCard);
   }

   /**
    * Gets all of the claimed routes in the hashtable with the key
    * being the player and the value their routes.
    * @return claimedRoutes the routes that were taken in a game
    * @throws RemoteException if RMI does not work
    */
   @Override
   public Hashtable<String, ArrayList<String>> getClaimedRoutes()  {
      return playerClaimedRoutes;
   }

    /**
     * First gets the weight of the route and 
     * decrements the number of 
     * trains that a player has
     * @param player the player who needs to decrement
     * @param route the route the player claimed
     */ 
     @Override
     public void decrementPlayerTrains(String player, String route) {
        // Getting the weight of the route
        int weight = getRouteWeight(route);

        // Getting the index of the player's name
        try {
           int playerIndex = playerNames.indexOf(player);
           trainsLeft[playerIndex] = trainsLeft[playerIndex] - weight;
        } catch (Exception e) { }
     }

    /**
     * Stub method to get the number of trains that a player has
     * @param player the player to get the trains of
     * @return the number of trains they have left
     */
   @Override
   public int getPlayerTrains(String player) {
        int trainsToReturn = -1;
        // Getting the index of the player's name
        try {
           int playerIndex = playerNames.indexOf(player);
           trainsToReturn = trainsLeft[playerIndex];
        } catch (Exception e) { }
        return trainsToReturn;
   }

    /**
     * Stub method to start the end of turn counter.
     * @param player the player that ended the game
     * @throws RemoteException if RMI does not work
     */
    public void startLastTurnCounter(String player) {
        // Setting last turn to the 
        lastTurnCounter = playerNames.indexOf(player);
        lastTurnHasNotStarted = false;
        System.out.println("Started countdown");
    }

    /**
     * Stub method for checking that it is not the last turn.
     * @param player the player to check if they already
     * took their last turn
     * @throws RemoteException if RMI does not work
     */
    public void isItLastTurn(String player) {
        // TODO: change this to the end of game JFRame
        if (playerNames.indexOf(player) == lastTurnCounter) {
            System.out.println("They are equal exiting");
            System.exit(0);
            //End Game Here
        }
    }
 
    /**
     * Stub method to check if the last turn has already
     * been started.
     * @throws RemoteException if RMI does not work
     */
    public boolean lastTurnStarted() {
        return lastTurnHasNotStarted;
    }


   /**
    * Given the route gets the weight of the route
    * @param route the route to check
    * @return the weight of the route 
    */
    public int getRouteWeight(String route) {
        // Creating the hashtable of all the routes
        // and weights
        try {
            generateHashTable();
        } catch (Exception e) { }

        // Searching the hashtable for the supplied route
        // and returning the weight
        return hashedRoutes.get(route);
    } 

    /**
     * Parses an XML file and generates a hashtable for it.
     * @throws SAXException                 for SAX error
     * @throws IOException                  for IO exception
     * @throws XPathExpressionException     for XPATH error
     * @throws ParserConfigurationException when can't configure the XML parser
     */
    public void generateHashTable() throws SAXException,
    IOException, XPathExpressionException, ParserConfigurationException{
        // Builing the parser
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        builder = dbfactory.newDocumentBuilder();
        XPathFactory xpfactory = XPathFactory.newInstance();
        path = xpfactory.newXPath();

        // Getting the XML to build the DOM
        File f = new File("data/routes.xml");
        Document doc = builder.parse(f);

        // Getting the city count
        int cityCount = Integer.parseInt(path.evaluate(
                "count(/routes/city)", doc));

        // Strings
        int currentWeight = -1;
        String currentRoute = "";

        for (int i = 1; i <= cityCount; i++) {
            int routeCount = Integer.parseInt(path.evaluate(
                    "count(/routes/city[" + i + "]/route)", doc));
            for (int j = 1; j <= routeCount; j++) {
                currentRoute = path.evaluate("/routes/city[" + i + "]"
                        + "/route[" + j + "]/id", doc);
                currentWeight = Integer.parseInt(path.evaluate("/routes/city[" + i + "]"
                        + "/route[" + j + "]/weight", doc));

                hashedRoutes.put(currentRoute, currentWeight);
            }

        }

    }


   /**
    * The Main method for the Server.
    * @param args for command line input
    */
   public static void main(String[] args) {

      System.out.println("Game server is ready");

   }
}
