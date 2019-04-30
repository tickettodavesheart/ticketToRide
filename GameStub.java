import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

/**
 * The interface for RMI.
 * 
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public interface GameStub extends Remote {
  /**
   * Stub method for the Server to send new messages to the Client.
   * 
   * @return Messages to the Client.
   * @throws RemoteException when RMI does not work.
   */
  Vector<String> getMessages() throws RemoteException;

  /**
   * Stub method for the Client to send new messages to the Server.
   * 
   * @param message the msg to be added.
   * @throws RemoteException when RMI does not work.
   */
  void sendMessage(String message) throws RemoteException;

  /**
   * Stub Method for returning the playerlist.
   * 
   * @return player names in the given game
   * @throws RemoteException when RMI does not work.
   */
  Vector<String> getPlayerNames() throws RemoteException;

  /**
   * Stub Method for returning the token owner.
   * 
   * @return the owner of the token
   * @throws RemoteException when RMI does not work.
   */
  String getTockenOwner() throws RemoteException;

  /**
   * Stub Method for setting the token owner.
   * 
   * @param username the user to set the token to
   * @throws RemoteException when RMI does not work.
   */
  void setTokenOwner(String username) throws RemoteException;

  /**
   * Stub Method for setting the name of player.
   * 
   * @param nickname the name of the player
   * @throws RemoteException when RMI does not work.
   */
  void addName(String nickname) throws RemoteException;

  /**
   * Stub Method for number and type of train each play has.
   * 
   * @param name  the current players name
   * @param route the route to add
   * @throws RemoteException when RMI does not work.
   */
  void addRoute(String name, String route) throws RemoteException;

  /**
   * Method to change selected routes.
   * 
   * @return selectedRoutes the route selected and it's color
   * @throws RemoteException when RMI does not work.
   */
  Hashtable<String, String> updateRoutes() throws RemoteException;

  /**
   * Stub method to deal out cards to the player.
   * 
   * @param numCards - the number of cards to deal
   * @return starting cards the starting cards a player holds
   * @throws RemoteException when RMI does not work.
   */
  ArrayList<String> dealCards(int numCards) throws RemoteException;

  /**
   * Stub method to show the visible deck options for train cards
   * 
   * @return the arraylist of visible cards
   * @throws RemoteException when RMI does not work.
   */
  ArrayList<String> getVisibleTrainCards() throws RemoteException;

  /**
   * Stub method that adds the player's cards to the hashtable on the server.
   * 
   * @param player the player to add to
   * @param cards  the list of cards
   * @throws RemoteException when RMI does not work.
   */
  void addPlayerCards(String player, ArrayList<String> cards) throws RemoteException;

  /**
   * Stub method to get all of the players destination cards
   * 
   * @param player the player to get the list from
   * @return list of player's cards
   * @throws RemoteException when RMI does not work.
   */
  ArrayList<String> getPlayerTrainCards(String player) throws RemoteException;

  /**
   * Stub method to get random destination cards from the server.
   * 
   * @return destination cards
   * @throws RemoteException when RMI does not work
   */
  ArrayList<String> getDestinationCards() throws RemoteException;

  /**
   * Stub method to remove the destination card because it has been claimed.
   * 
   * @param choosenCard the cards that were selected
   * @throws RemoteException when RMI does not work
   */
  void removeDestinationCard(String choosenCard) throws RemoteException;

  /**
   * Stub method to remove the train card because it has been claimed.
   * 
   * @param choosenCard the cards that were selected
   * @throws RemoteException when RMI does not work
   */
  void removeTrainCard(String choosenCard) throws RemoteException;

  /**
   * Stub method to get all of the claimed routes in the hashtable with the key
   * being the player and the value their routes.
   * 
   * @return claimedRoutes the routes that were taken in a game
   * @throws RemoteException if RMI does not work
   */
  Hashtable<String, ArrayList<String>> getClaimedRoutes() throws RemoteException;

  /**
   * Stub method to decrement the number of trains that a player has.
   * 
   * @param player the player who needs to decrement
   * @param route  the route the player claimed
   * @throws RemoteException if RMI does not work
   */

  void decrementPlayerTrains(String player, String route) throws RemoteException;

  /**
   * Stub method to get the number of trains that a player has.
   * 
   * @param player the player to get the trains of
   * @return the number of trains they have left
   * @throws RemoteException if RMI does not work
   */
  int getPlayerTrains(String player) throws RemoteException;

  /**
   * Stub method to start the end of turn counter.
   * 
   * @param player the player that ended the game
   * @throws RemoteException if RMI does not work
   */
  void startLastTurnCounter(String player) throws RemoteException;

  /**
   * Stub method for checking that it is not the last turn.
   * 
   * @param player the player to check if they already took their last turn
   * @throws RemoteException if RMI does not work
   */
  void isGameOver(String player) throws RemoteException;

  /**
   * Stub method to check if the last turn has already been started.
   * 
   * @throws RemoteException if RMI does not work
   */
  boolean lastTurnStarted() throws RemoteException;

}
