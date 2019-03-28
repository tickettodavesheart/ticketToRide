import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

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
    * Stub Method for getting all of the Clients playing.
    * @throws RemoteException when RMI does not work.
    */

   /**
    * Stub Method for who's turn it is.
    * @throws RemoteException when RMI does not work.
    */

   /**
    * Stub Method for number and type of train each play has.
    * @throws RemoteException when RMI does not work.
    */

   /**
    * Stub Method for number and type of cards each person has.
    * @throws RemoteException when RMI does not work.
    */

   /**
    * Stub Method for the destination cards left.
    * @throws RemoteException when RMI does not work.
    */

   /**
    * Stub Method for destination cards avialable.
    * @throws RemoteException when RMI does not work.
    */

   /**
    * Stub Method for scores for each player.
    * @throws RemoteException when RMI does not work.
    */

   /**
    * Stub Method for name, type and location of available train spots.
    * @throws RemoteException when RMI does not work.
    */

}
