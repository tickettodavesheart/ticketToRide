import java.util.Vector;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;

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
   private Vector<String> selectedRoutes =
          new Vector<String>();
   // Vector of all cards available at the start of the game
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
    * @param route the route to paintColor
    */
   @Override
   public void addRoute(String route) {
      // Adding the route and color to the selected routes.
      selectedRoutes.add(route);
   }

   /**
    * Method to change selected routes.
    * @return selectedRoutes the route selected and it's color
    */
   @Override
   public Vector<String> updateRoutes() {
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
    * The Main method for the Server.
    * @param args for command line input
    */
   public static void main(String[] args) {

      System.out.println("Game server is ready");

   }
}
