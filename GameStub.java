import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Hashtable;

/**
 * The interface for RMI.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public interface GameStub extends Remote {
   /**
    * Stub method for the Server to send new messages to the Client.
    * @return Messages to the Client.
    * @throws RemoteException when RMI does not work.
    */
   Vector<String> getMessages() throws RemoteException;

   /**
    * Stub method for the Client to send new messages to the Server.
    * @param message the msg to be added.
    * @throws RemoteException when RMI does not work.
    */
   void sendMessage(String message) throws RemoteException;
    
   /**
    * Stub Method for returning the playerlist
    * @throws RemoteException when RMI does not work.
    */
   Vector<String> getPlayerNames() throws RemoteException;
   
   /**
    * Stub Method for returning the tocken owner
    * @throws RemoteException when RMI does not work.
    */
   String getTockenOwner() throws RemoteException;
   
   /**
    * Stub Method for setting the tocken owner
    * @throws RemoteException when RMI does not work.
    */
   void setTokenOwner(String username) throws RemoteException;

   /**
    * Stub Method for setting the name of player
    * @throws RemoteException when RMI does not work.
    */
   void addName(String nickname) throws RemoteException;
   
   /**
    * Stub Method for number and type of train each play has.
    * @throws RemoteException when RMI does not work.
    */
   void addRoute(String route) throws RemoteException;

   /**
     * Method to change selected routes.
     * @return selectedRoutes the route selected and it's color
     * @throws RemoteException when RMI does not work.
     */
   Vector<String> updateRoutes() throws RemoteException;

}
