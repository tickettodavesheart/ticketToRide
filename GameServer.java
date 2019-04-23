import java.util.ArrayList;
import java.util.Vector;
import java.util.Hashtable;

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
   public String getTokenOwner() {
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
    * @param username the player that will hold the token.
    */
   @Override
   public void addName(String nickname) {
      playerNames.add(nickname);
   }

   /**
   * Method to add a new route to paint to the Server.
   * @param route, the route to paintColor
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
    * The Main method for the Server.
    * @param args for command line input
    */
   public static void main(String[] args) {

      System.out.println("Game server is ready");

   }
}
