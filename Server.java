// RMI
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Vector;

// For GUI
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import java.net.*;

/**
 * Server Class.
 * @author Lucas Kohorst
 * @version 3/25/19
 */
public class Server implements ServerStub {

   // Vector of all of the threads of games
   private Vector<Thread> threads = new Vector<Thread>();

   // Vector of the names of the current games
   private Vector<String> games = new Vector<String>();

   // Vector of the ports for each game
   private Vector<Integer> ports = new Vector<Integer>();

   // The starting port for the game servers
   private int GENERATED_PORT = 16789;

   // The starting object id for the game servers
   private int OBJ_ID = 0;

   // The last created game's stub_id
   private String stubID = "GameStub" + OBJ_ID;

   // The IP address of the machine
   private String CURRENT_IP = "localhost";

   // The name of the last created game
   private String nameOfGame = "New Ticket to Dave's Heart Game";

   /**
    * Method for getting the stubID for the last created game.
    * @return the stubID for the newely created game
    */
   public String getStubID()  {
      return stubID;
   }

   /**
    * Method for getting the server's IP.
    * @return the server's IP
    */
   public String getCurrentIP()  {
      return CURRENT_IP;
   }

   /**
    * Method for getting the name for the last created game.
    * @return the name for the newely created game
    */
   public String getNameOfGame()  {
      return nameOfGame;
   }

   /**
    * Method that returns a game names on the server.
    * @return games the names of games on the server.
    */
   @Override
   public Vector<String> getGames() {
      System.out.println("Games Sent out to Login");
      return games;
   }

   /**
    * Method that returns the number a game on the server.
    * @return games.size() the number of games on the server.
    */
   @Override
   public int getNumberActive() {
      System.out.println("Games size Sent out to LogIn");
      return games.size();
   }

   /**
    * Method that returns a ports on the server.
    * @return ports the ports of games on the server.
    */
   @Override
   public Vector<Integer> getPorts() {
      System.out.println("Ports Sent out to GameClient");
      return ports;
   }

   /**
    * Method to create a new game on the server.
    * @return if the creation of the games was successful
    */
   @Override
   public String newGame(String name) {
      // Creating a new game using the inner class
      ThreadServer ths = new ThreadServer(name);

      // Adding the game to the threads vector
      threads.add(ths);
      // Starting the thread
      ths.start();

      // Joining all of the game threads on the server
      for (Thread t : threads) {
         try {
            t.join();
         } catch (InterruptedException ie) { }
      }

      return "Game Created";

   }

   /**
    * Method to join a game on the server.
    * @return if the joining of the game was successful
    */
   @Override
   public String enterGame(String gameToEnter) {
      int gameIndex = 0;

      for (String curGame : games) {
         if (curGame.equals(gameToEnter)) {
            gameIndex = games.indexOf(curGame);
         }
      }

      int portToEnter = ports.elementAt(gameIndex);

      // Creating a new game using the inner class
      // Passing the port for the correct game
      ThreadServer ths = new ThreadServer(portToEnter);

      // Adding the game to the threads vector
      threads.add(ths);
      // Starting the thread
      ths.start();

      // Joining all of the game threads on the server
      for (Thread t : threads) {
         try {
            t.join();
         } catch (InterruptedException ie) { }
      }

      return "Game Joined";

   }

   /**
    * 
    */
   class ThreadServer extends Thread {

      // The port that the game is on
      private int port;

      // If true bind the stub to the registry with
      // the unique stubID
      private boolean toBind = false;

      /**
       * Defualt Constructor for ThreadServer.
       * @param name the name of the created game
       */
      ThreadServer(String name) { 
         // Setting the name of the game
         nameOfGame = name;

         // Incrementing the OBJ_ID and GENERATED_PORT
         // Bc the user did not specifiy a port to 
         // connect to
         OBJ_ID++;
         GENERATED_PORT++; 

         port = GENERATED_PORT;

         // Adding the name of the game and the port to the server
         games.add(nameOfGame);
         ports.add(port);

         // Making it so the stub is binded to the new port
         toBind = true;
      }

      /**
       * Constructor for ThreadServer with specified port.
       * @param port the port to connect to
       */
      ThreadServer(int port) { 
         // Settting the given port
         // and correct OBJ_ID
         this.port = port;
         OBJ_ID++;
         // Making it so the port is not binded again
         toBind = false;
      }

      /**
       * Method to be run when the thread is started.
       */
      public void run() {

         // Creating the stubID
         stubID = "GameStub" + port;

         try {
            // If a new game is being created, bind a stub to a
            // new port
            if (toBind) {
               // Starting the RMI registry at port as generated
               LocateRegistry.createRegistry(port);

               // Creating a new Server
               GameServer gameServerObj = new GameServer();

               // Casting the stub class to a remote object on the server
               // Exporting the ServerStub
               GameStub stub = (GameStub) UnicastRemoteObject.exportObject(
                      gameServerObj, OBJ_ID);

               // Locating the registry on the machine
               Registry registry = LocateRegistry.getRegistry();

               // Bind the remote object's stub in the registry
               registry.bind("GameStub" + GENERATED_PORT, stub);
            }   

            System.out.println("New Game Created");

            InetAddress iAddress = InetAddress.getLocalHost();
            CURRENT_IP = iAddress.getHostAddress();

            System.out.println("Everything set\nCURRENT_IP:" + CURRENT_IP + "\nstubID: " + stubID + "\nnameOfGame: " + nameOfGame);

            System.err.println("Threaded Server ready with GameStub");
         } catch (RemoteException re) {
            System.err.println("Server exception: " + re.toString());
            re.printStackTrace();
         } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
         }
      }
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
         Server serverObj = new Server();

         // Casting the stub class to a remote object on the server
         // Exporting the ServerStub
         ServerStub stub = (ServerStub) UnicastRemoteObject.exportObject(
                serverObj, 0);

         // Locating the registry on the machine
         Registry registry = LocateRegistry.getRegistry();

         // "Hello" to the remote object's stub in that registry,
         // what the Client can use to lookup the stub
         // Bind the remote object's stub in the registry
         registry.bind("ServerStub", stub);

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
