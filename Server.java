import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Vector;
      
/**
 * Server Class.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class Server implements Stub {
        
   private Vector<String> messages = new Vector<String>();

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
    * The Main method for the Server.
    * @param args for command line input
    */
   public static void main(String[] args) {
      
      try {
         // Starting the RMI registry at port 1099 (reserved)
         LocateRegistry.createRegistry(1099);

         // Creating a new Server
         Server obj = new Server();

         // Casting the stub class to a remote object on the server
         Stub stub = (Stub) UnicastRemoteObject.exportObject(obj, 0);

         // Locating the registry on the machine
         Registry registry = LocateRegistry.getRegistry();

         // "Hello" to the remote object's stub in that registry, 
         // what the Client can use to lookup the stub
         // Bind the remote object's stub in the registry
         registry.bind("Stub", stub);

         System.err.println("Server ready");
      } catch (RemoteException re) {
         System.err.println("Server exception: " + re.toString());
         re.printStackTrace();
      } catch (Exception e) {
         System.err.println("Server exception: " + e.toString());
         e.printStackTrace();
      }

   }
}
