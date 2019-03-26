// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class for the GameClient.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class GameClient {

   // For RMI Registry
   private String ipaddress;

   // For RMI
   // private Stub stub;

   /**
    * Constructor for the ChatClient.
    */
   public GameClient() {

      // Creating the registry
      try {
         // Creating the Registry
         Registry registry = LocateRegistry.getRegistry(ipaddress);

         // Looking up the Stub class
         Stub stub = (Stub) registry.lookup("Stub");

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }
   }

   /**
    * The Main Method for the GameClient class.
      * @param args for Command Line Input
      */
   public static void main(String[] args) {
      new GameClient();
      new ChatClient();
   }

}
