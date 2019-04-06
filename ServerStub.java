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
    * @param name the name for the newe game
    * @return the status of the game's creation
    * @throws RemoteException when RMI does not work.
    */
   String newGame(String name) throws RemoteException;

   /**
    * Stub method for getting the stubID for the last created game.
    * @return the stubID for the newely created game
    * @throws RemoteException when RMI does not work.
    */
   String getStubID() throws RemoteException;

   /**
    * Stub method for getting the server's IP.
    * @return the server's IP
    * @throws RemoteException when RMI does not work.
    */
   String getCurrentIP() throws RemoteException;

   /**
    * Stub method for getting the name for the last created game.
    * @return the name for the newely created game
    * @throws RemoteException when RMI does not work.
    */
   String getNameOfGame() throws RemoteException;

   /**
    * Stub method for getting number of players in a game.
    * @param gameName the name of the game to get the players
    * @return the number of players in a game
    * @throws RemoteException when RMI does not work.
    */
   int getNumPlayers(String gameName) throws RemoteException;

   /**
    * Stub method for incrementing number of players in a game.
    * @param gameName the name of the game to get the players
    * @throws RemoteException when RMI does not work.
    */
   void incNumPlayers(String gameName) throws RemoteException;

   /**
    * Stub method for adding number of players in a game.
    * @throws RemoteException when RMI does not work.
    */
   void addIntialPlayer() throws RemoteException;

   /**
    * Stub method for joining a game on the server.
    * @param gameToEnter the name of the game to join
    * @return the status of the joining of the game
    * @throws RemoteException when RMI does not work.
    */
   String enterGame(String gameToEnter) throws RemoteException;

   /**
    * Stub method that is implemented when a client is shutdown.
    * 
    * @param game the game in which the client is running
    * @return the status of the termination
    * @throws RemoteException when RMI does not work.
    */
   String shutdownClient(String game) throws RemoteException;

}
