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
 * 
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class GameServer implements GameStub {

   // Vector for the messages on the server
   private Vector<String> messages = new Vector<String>();
   private Vector<String> playerNames = new Vector<String>();
   private String tokenOwner = "";
   // boolean for the visible train cards deck
   private boolean firstDeal = true;

   // Final score table
   private HashMap<String, Integer> finalScores = new HashMap<String, Integer>();
   // The visible train cards
   private ArrayList<String> visibleTrainCards = new ArrayList<String>();
   // Vector of arraylists with each route and color that it takes
   private Hashtable<String, String> selectedRoutes = new Hashtable<String, String>();
   // ArrayList of all cards available at the start of the game
   private ArrayList<String> cardsLeft = new ArrayList<String>(Arrays.asList("BLACK", "BLACK", "BLACK", "BLACK",
          "BLACK", "BLACK", "BLACK", "BLACK", "BLACK", "BLACK", "BLACK", "BLACK", "BLUE", "BLUE", "BLUE", "BLUE",
          "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", "BLUE", "GREEN", "GREEN", "GREEN", "GREEN", "GREEN",
          "GREEN", "GREEN", "GREEN", "GREEN", "GREEN", "GREEN", "GREEN", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL",
          "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "NEUTRAL", "ORANGE", "ORANGE",
          "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", "ORANGE", "PURPLE",
          "PURPLE", "PURPLE", "PURPLE", "PURPLE", "PURPLE", "PURPLE", "PURPLE", "PURPLE", "PURPLE", "PURPLE", "PURPLE", "RED", "RED", "RED",
          "RED", "RED", "RED", "RED", "RED", "RED", "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "WHITE", "WHITE",
          "WHITE", "WHITE", "WHITE", "WHITE", "WHITE", "WHITE", "WHITE", "YELLOW", "YELLOW", "YELLOW", "YELLOW",
          "YELLOW", "YELLOW", "YELLOW", "YELLOW", "YELLOW", "YELLOW", "YELLOW", "YELLOW"));

   // ArrayList of available destination cards
   private ArrayList<DestinationCard> destinationCardsLeft = new DestinationDeck().makeDeck();
   // Hashtable to hold given players claimed destination cards
   
   private Hashtable<String, ArrayList<DestinationCard>> playerDestinationCards = 
          new Hashtable<String, ArrayList<DestinationCard>>();
          
   // Hashtable to hold the given players claimed routes
   private Hashtable<String, ArrayList<String>> playerClaimedRoutes = new Hashtable<String, ArrayList<String>>();

   // Array of the trains that a player has left
   private int[] trainsLeft = new int[] { 45, 45, 45, 45, 45 };

   // Generate a Hashtable from the XML
   private Hashtable<String, Integer> hashedRoutes = new Hashtable<String, Integer>();

   // Hastable of player's train cards
   private Hashtable<String, ArrayList<String>> playerTrainCards = new Hashtable<String, ArrayList<String>>();

   // temp store of the color train cards to remove
   // removed after end turn method
   private ArrayList<String> tempColorsToRemove;

   // Document builders for XML parsing
   private DocumentBuilder builder;
   private XPath path;

   // Last turn counter
   private int lastTurnCounter = -1;
   private boolean lastTurnHasNotStarted = true;

   private ArrayList<String> usedVisibleTrainCards = new ArrayList<String>();

   private RoutePointsDict routeDict = new RoutePointsDict();


   /**
    * Method that returns a message.
    * 
    * @return String of the message.
    */
   @Override
   public Vector<String> getMessages() {
      return messages;
   }

   /**
    * Method that adds a message to the server.
    * 
    * @param message the message to be added.
    */
   @Override
   public void sendMessage(String message) {
      System.out.println("[INFO] Message from Chat: " + message);
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
    * 
    * @param username the player that will hold the token.
    */
   @Override
   public void setTokenOwner(String username) {
      tokenOwner = username;
   }

   /**
    * Method that returns the player names.
    * 
    * @param nickname the player that will hold the token.
    */
   @Override
   public void addName(String nickname) {
      playerNames.add(nickname);
   }

   /**
    * Method to add a new route to paint to the Server.
    * 
    * @param name  the name of the current player
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
    * 
    * @return selectedRoutes the route selected and it's color
    */
   @Override
   public Hashtable<String, String> updateRoutes() {
      return selectedRoutes;
   }

   /**
    * Method to deal the initial cards to a player.
    * 
    * @param numCards - the number of cards to deal
    * @return cards a list of cards a player gets
    */
   @Override
   public ArrayList<String> dealCards(int numCards) {
      ArrayList<String> dealtCards = new ArrayList<String>();
      // Generating a random number to choose the
      // index at
      Random rand = new Random();
      Integer[] cards = new Integer[numCards];
   
      for (int i = 0; i < numCards; i++) {
         // Generate the number in the range of the indicies
         cards[i] = rand.nextInt(cardsLeft.size() - 1);
         int card = cards[i];
         // Adding the card
         dealtCards.add(cardsLeft.get(card));
         // Removing the dealt card from the cardsLeft
         cardsLeft.remove(card); 
      }
      return dealtCards;
   }

           /**
           * A method to have the client tell the server how many cards to refill
           * @param usedList
           */
   public void setUsedVisibleTrainCards(ArrayList<String> usedList) {
      usedVisibleTrainCards = usedList;
   }

   /**
    * A method to show the visible deck options for train cards
    * 
    * @return the arraylist of visible cards
    */
   public ArrayList<String> getVisibleTrainCards() {
      int numCards = 5;
   
      Random rand = new Random();
      Integer[] cards = new Integer[numCards];
   
      if (firstDeal) {
         for (int i = 0; i < numCards; i++) {
            int card = rand.nextInt(cardsLeft.size() - 1);
            visibleTrainCards.add(cardsLeft.get(card));
            cardsLeft.remove(card);
         }
      } else {
         int visibleCardsLeft = usedVisibleTrainCards.size();
         for (int i = 0; i < 5 - visibleCardsLeft; i++) {
            int card = rand.nextInt(cardsLeft.size() - 1);
            usedVisibleTrainCards.add(cardsLeft.get(card));
            cardsLeft.remove(card);
         }
         visibleTrainCards.clear();
         visibleTrainCards = usedVisibleTrainCards;
      }
      firstDeal = false;
      return visibleTrainCards;
   }

   /**
    * Returns random destination cards the user can choose from.
    * 
    * @return destination cards the user can choose from
    */
   @Override
   public ArrayList<DestinationCard> getDestinationCards() {
      ArrayList<DestinationCard> destinationCards = new ArrayList<DestinationCard>();
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
    * Removes the choosen destination cards from the destination cards left.
    * 
    * @param choosenCard the cards that were selected
    */
   @Override
   public void removeDestinationCard(DestinationCard choosenCard) {
      destinationCardsLeft.remove(choosenCard);
   }

   /**
    * Removes the choosen train cards from the train cards left list.
    * 
    * @param choosenCard the cards that were selected
    */
   public void removeTrainCard(String choosenCard) {
      cardsLeft.remove(choosenCard);
   }

   /**
    * Gets all of the claimed routes in the hashtable with the key being the player
    * and the value their routes.
    * 
    * @return claimedRoutes the routes that were taken in a game
    * @throws RemoteException if RMI does not work
    */
   @Override
   public Hashtable<String, ArrayList<String>> getClaimedRoutes() {
      return playerClaimedRoutes;
   }

   /**
    * Gets all of the players destination cards
    * 
    * @return list of player's cards
    */
   @Override
   public void addPlayerCards(String player, ArrayList<String> cards) {
      playerTrainCards.put(player, cards);
   }

   /**
    * Stub method to get all of the players train cards
    * 
    * @param player the player to get the list from
    * @return list of player's cards
    */
   @Override
   public ArrayList<String> getPlayerTrainCards(String player) {
      ArrayList<String> returnList = null;
      Set<String> keys = playerTrainCards.keySet();
      for (String key : keys) {
         if (key.equals(player)) {
            returnList = playerTrainCards.get(key);
         }
      }
      return returnList;
   }
 
    /**
    * Method remove player train cards.
    * @param player the player to get the list from
    * @param colorToRemove the color to remove
    * @throws RemoteException when RMI does not work.
    */
    public void removePlayerTrainCards(String player) {
        Set<String> keys = playerTrainCards.keySet();
        System.out.println(keys);
        for (String key : keys) {
           System.out.println(key);
           if (key.equals(player)) {
              ArrayList<String> currentListOfTrainCards = playerTrainCards.get(key);
              for (String colorToRemove : tempColorsToRemove) {
                currentListOfTrainCards.remove(colorToRemove);  
                System.out.println("Removed: " + colorToRemove + " from " + player);     
              }
              System.out.println(playerTrainCards.get(key));
           }
        }
    }

    /**
    * Method to temp store the cards to remove from the train cards of a player.
    * @param colorsToRemove the color to remove
    * @throws RemoteException when RMI does not work.
    */
    public void tempStoreTrainColorCards(ArrayList colorsToRemove) {
        // Setting the temp color arraylist
        tempColorsToRemove = colorsToRemove;
        System.out.println("Colors to remove temp: " + tempColorsToRemove);
    }

   /**
    * First gets the weight of the route and decrements the number of trains that a
    * player has
    * 
    * @param player the player who needs to decrement
    * @param route  the route the player claimed
    */
   @Override
   public void decrementPlayerTrains(String player, String route) {
      // Getting the weight of the route
      int weight = routeDict.getRouteWeight(route);
   
      // Getting the index of the player's name
      try {
         int playerIndex = playerNames.indexOf(player);
         trainsLeft[playerIndex] = trainsLeft[playerIndex] - weight;
      } catch (Exception e) {
      }
   }

      /**
    * First gets the weight of the route and increments the number of trains that a
    * player has
    * 
    * @param player the player who needs to increment
    * @param route  the route the player claimed
    */
    @Override
    public void incrementPlayerTrains(String player, String route) {
       // Getting the weight of the route
       int weight = routeDict.getRouteWeight(route);
    
       // Getting the index of the player's name
       try {
          int playerIndex = playerNames.indexOf(player);
          trainsLeft[playerIndex] = trainsLeft[playerIndex] + weight;
       } catch (Exception e) {
       }
    }

   /**
    * Stub method to get the number of trains that a player has
    * 
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
      } catch (Exception e) {
      }
      return trainsToReturn;
   }

   /**
    * Stub method to start the end of turn counter.
    * 
    * @param player the player that ended the game
    * @throws RemoteException if RMI does not work
    */
   public void startLastTurnCounter(String player) {
      // Setting last turn to the
      lastTurnCounter = playerNames.indexOf(player);
      lastTurnHasNotStarted = false;
   }

   /**
    * Stub method for checking that it is not the last turn.
    * 
    * @param player the player to check if they already took their last turn
    * @throws RemoteException if RMI does not work
    */
   public void isItLastTurn(String player) {
      // TODO: change this to the end of game JFRame
      if (playerNames.indexOf(player) == lastTurnCounter) {
         System.exit(0);
         // End Game Here
      }
   }

   /**
    * Stub method to check if the last turn has already been started.
    * 
    * @throws RemoteException if RMI does not work
    */
   public boolean lastTurnStarted() {
      return lastTurnHasNotStarted;
   }

   /**
    * Stub method for checking that it is not the last turn.
    * 
    * @param player the player to check if they already took their last turn
    * 
    * @param player the player to check if they already took their last turn
    * @throws RemoteException if RMI does not work
    */
   public void isGameOver(String player) {
      // TODO: change this to the end of game JFRame
      if (playerNames.indexOf(player) == lastTurnCounter) {
         System.exit(0);
         // End Game Here
         // End Game Here
      }
   }

   /**
    * Given the route gets the weight of the route
    * 
    * @param route the route to check
    * @return the weight of the route
    */
   public int getRouteWeight(String route) {
      // Creating the hashtable of all the routes
      // and weights
      try {
         generateHashTable();
      } catch (Exception e) {
      }
   
      // Searching the hashtable for the supplied route
      // and returning the weight
      return hashedRoutes.get(route);
   }

   /**
    * Parses an XML file and generates a hashtable for it.
    * 
    * @throws SAXException                 for SAX error
    * @throws IOException                  for IO exception
    * @throws XPathExpressionException     for XPATH error
    * @throws ParserConfigurationException when can't configure the XML parser
    */
   public void generateHashTable()
           throws SAXException, IOException, XPathExpressionException, ParserConfigurationException {
      // Builing the parser
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      builder = dbfactory.newDocumentBuilder();
      XPathFactory xpfactory = XPathFactory.newInstance();
      path = xpfactory.newXPath();
   
      // Getting the XML to build the DOM
      File f = new File("data/routes.xml");
      Document doc = builder.parse(f);
   
      // Getting the city count
      int cityCount = Integer.parseInt(path.evaluate("count(/routes/city)", doc));
   
      // Strings
      int currentWeight = -1;
      String currentRoute = "";
   
      for (int i = 1; i <= cityCount; i++) {
         int routeCount = Integer.parseInt(path.evaluate("count(/routes/city[" + i + "]/route)", doc));
         for (int j = 1; j <= routeCount; j++) {
            currentRoute = path.evaluate("/routes/city[" + i + "]" + "/route[" + j + "]/id", doc);
            currentWeight = Integer
                   .parseInt(path.evaluate("/routes/city[" + i + "]" + "/route[" + j + "]/weight", doc));
         
            hashedRoutes.put(currentRoute, currentWeight);
         }
      
      }
   
   }
   
   
   /**
    * calcScore - Calculates a players score
    * @param player - player name to calculate score of
    * @return score - players calculated score
    */
   @Override
   public int calcScore(String player) throws Exception {
      // route points dictionary object to obtain weight scores
      RoutePointsDict rpd = new RoutePointsDict();
      // desination card check object to verify and add/subtract scores
      DestinationCardCheck dcc = new DestinationCardCheck();
      // score value to keep track of current calculation
      int score = 0;
      // ArrayList of the owned routes a player has claimed throughout the game
      ArrayList<String> ownedRoutes = getClaimedRoutes().get(player);
      

      // calculate score obtained from ownership of 
      // claimed routes
      for(String route : ownedRoutes) {
         if(route.contains("grey")) {
            score += rpd.getRouteWeight(route);
         } else {
            score += rpd.getRoutePoints(route);
         }
      }
      
      // get player's destination cards
      ArrayList<DestinationCard> ownedDestinationCards = playerDestinationCards.get(player);

      //iterate through the player's destination cards to verify each one
      // and award points
      for(DestinationCard dc : ownedDestinationCards) {
         dcc.newCard(dc, ownedRoutes);
      
         // verify current destination card
         // add points if it is completed
         // subtract points if it was not completed
         dcc.verify(dc.getLocationOne());
         if(dcc.isCardCompleted()) {
            score += dc.getPointValue();
         } else {
            score -= dc.getPointValue();
         }
      }
      
      // add score of player to the final scores table
      finalScores.put(player, new Integer(score));
      return score;
   }
   
   /**
    * addDestinationCard - Adds a selected DestinationCard to a players
    *                     list of owned cards.
    * @param player - player to assign the card to
    * @param card - DestinationCard object to add to the list
    */
   @Override
   public void addDestinationCard(String player, DestinationCard card) {
      ArrayList<DestinationCard> newCards;
     
      if(playerDestinationCards.get(player) != null) {
         newCards = playerDestinationCards.get(player);
      } else {
         newCards = new ArrayList<DestinationCard>();
      }
     // Adding new card to previous list
      newCards.add(card);
     // Adding the player and card to the Hashtable of players
     // and their cards
      playerDestinationCards.put(player, newCards);
   }

   /**
    * The Main method for the Server.
    * 
    * @param args for command line input
    */
   public static void main(String[] args) {
   
      System.out.println("Game server is ready");
   
   }
}
