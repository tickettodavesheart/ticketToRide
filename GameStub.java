import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

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
     * Stub Method for returning the playerlist.
     * @return player names in the given game
     * @throws RemoteException when RMI does not work.
     */
   Vector<String> getPlayerNames() throws RemoteException;
   
   /**
    * Stub Method for returning the token owner.
    * @return the owner of the token
    * @throws RemoteException when RMI does not work.
    */
   String getTockenOwner() throws RemoteException;
   
   /**
    * Stub Method for setting the token owner.
    * @param username the user to set the token to
    * @throws RemoteException when RMI does not work.
    */
   void setTokenOwner(String username) throws RemoteException;

   /**
    * Stub Method for setting the name of player.
    * @param nickname the name of the player
    * @throws RemoteException when RMI does not work.
    */
   void addName(String nickname) throws RemoteException;
   
   /**
    * Stub Method for number and type of train each play has.
    * @param route the route to add
    * @throws RemoteException when RMI does not work.
    */
   void addRoute(String route) throws RemoteException;

   /**
     * Method to change selected routes.
     * @return selectedRoutes the route selected and it's color
     * @throws RemoteException when RMI does not work.
     */
   Vector<String> updateRoutes() throws RemoteException;

   /**
    * Stub method to deal out cards to the player.
    * @return starting cards the starting cards a player holds
    * @throws RemoteException when RMI does not work.
    */
   ArrayList<String> dealCards() throws RemoteException;

}
