import java.util.Vector;

/**
 * GameServer Class.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class GameServer implements GameStub {

   private Vector<String> messages = new Vector<String>();
   private Vector<String> playerNames = new Vector<String>();
   private String tockenOwner = "";

   /**
    * Method that returns a message.
    * @return String of the message.
    */
   @Override
   public Vector<String> getMessages() {
      System.out.println("Messages sent out");
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
    * Method that returns the tocken owner.
    */
   @Override
   public String getTockenOwner() {
      return tockenOwner;
   }
   
   /**
    * Method that returns the player names.
    * @param username the player that will hold the tocken.
    */
   @Override
   public void setTockenOwner(String username) {
      tockenOwner = username;
   }

   /**
    * The Main method for the Server.
    * @param args for command line input
    */
   public static void main(String[] args) {

      System.out.println("Game server is ready");

   }
}
