import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * The interface for RMI.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public interface ChatStub extends Remote {
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
}
