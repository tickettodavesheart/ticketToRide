import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * The interface for ServerStub so the LogIn can view all of the
 * active games and get all of the ports so the correct GameServer
 * can be connected to.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public interface ServerStub extends Remote { 

   /**
    * Stub method for getting the Vector of games on the server.
    * @return Games to the LogIn so the user can see what is availible
    * @throws RemoteException when RMI does not work.
    */
   Vector<String> getGames() throws RemoteException;

   /**
    * Stub method for getting number of active games on the server.
    * @return Number of Games to see how many are availible
    * @throws RemoteException when RMI does not work.
    */
   int getNumberActive() throws RemoteException;

   /**
    * Stub method for getting the Vector of ports on the server.
    * @return Ports to connect to the proper game
    * @throws RemoteException when RMI does not work.
    */
   Vector<Integer> getPorts() throws RemoteException;
   
   /**
    * Stub method for creating a new game on the server.
    * @return the status of the game's creation
    * @throws RemoteException when RMI does not work.
    */
   String newGame() throws RemoteException;

   /**
    * Stub method for joining a game on the server.
    * @param gameToEnter the name of the game to join
    * @return the status of the joining of the game
    * @throws RemoteException when RMI does not work.
    */
   String enterGame(String gameToEnter) throws RemoteException;

}
