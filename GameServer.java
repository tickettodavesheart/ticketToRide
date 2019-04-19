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
