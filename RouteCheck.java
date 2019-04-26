import java.io.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;

// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

/**
 * Method to find the longest path.
 * @author Lucas Kohorst
 * @version 4/15/19
 */
public class RouteCheck {

   // Generate a Hashtable from the XML
   private Hashtable<String, Hashtable<String, 
          ArrayList<String>>> hashedRoutes = new
          Hashtable<String, Hashtable<String, ArrayList<String>>>();
   // Document builders for XML parsing
   private DocumentBuilder builder;
   private XPath path;
   // ArrayList of visited edges
   private ArrayList<String> visitedRoutes = new
          ArrayList<String>();
   // ArrayList of visited Cities
   private ArrayList<String> visitedCities = new 
          ArrayList<String>();
   // Route card currently being checked
   private Route routeCard;
   // boolean for route completion
   private boolean completed;
   // For RMI
   private GameStub stub;
   private String ip;
   private String stubID;

   /**
    * Default Constructor for the LongestV2 Class.
    * @param routeCard the card to check
    * @param ip        the IP adress for the main server
    * @param stubID    the id for the GameStub for the specific server
    * @throws Exception all Exceptions
    */
   public RouteCheck(Route routeCard, String ip, String stubID) throws Exception {
      this.routeCard = routeCard;
      this.ip = ip;
      this.stubID = stubID;
   
      // Creating the hashtable from the XML file
      generateHash();
      // Finding the longest trail in the XML file
      ArrayList<String> finalTrail = iterateTrails();
      System.out.println("\n\n------------ Results ------------");
      System.out.println("Trail: " + finalTrail);
      System.out.println("Length: " + finalTrail.size() / 2);
      System.out.println("Cities Visited: " + visitedCities);
   
      if(visitedCities.indexOf(routeCard.getLocationOne()) != -1 && visitedCities.indexOf(routeCard.getLocationTwo()) != -1){
         completed = true;
         System.out.println("Route Completed? : " + completed);
      } else {
         completed = false;
         System.out.println("Route Completed? : " + completed);
      }
   
      int weight = 0;
      for (int i = 0; i < finalTrail.size() - 1; i++) {
         if (i % 2 == 1) {
            try {
               weight += Integer.parseInt(finalTrail.get(i));
            } catch (NumberFormatException nfe) {
               System.err.println("---------- End ----------");
               System.exit(0);
            }
         }
      }
      System.out.println("Weight: " + weight);
   
   }

   /**
    * Parses an XML file and generates a hashtable for it.
    * @throws SAXException                 for SAX error
    * @throws IOException                  for IO exception
    * @throws XPathExpressionException     for XPATH error
    * @throws ParserConfigurationException when can't configure the XML parser
    */
   public void generateHash() throws SAXException,
          IOException, XPathExpressionException, ParserConfigurationException {
      // Builing the parser
      DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
      builder = dbfactory.newDocumentBuilder();
      XPathFactory xpfactory = XPathFactory.newInstance();
      path = xpfactory.newXPath();
   
      // Getting the XML to build the DOM
      File f = new File("data/routes.xml");
      Document doc = builder.parse(f);

      // Connect to GameServer to check the routes
      try {
         // Locating the Registry
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (GameStub) registry.lookup(stubID);

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }
   
      // Getting the city count
      int cityCount = Integer.parseInt(path.evaluate(
                "count(/routes/city)", doc));
   
      // Strings
      String currentCity = "";
      String currentRoute = "";
      String currentWeight = "";
      String currentDest = "";
      // list of route's [0] destination, [1] weight
      ArrayList<String> destWeight = null;
      // Hastable of routes : [destination, weight]
      Hashtable<String, ArrayList<String>> values = null;
   
      for (int i = 1; i <= cityCount; i++) {
         values = new Hashtable<String, ArrayList<String>>();
         // Declaring the name which is the Key for the Hash
         String name = path.evaluate("/routes/city[" + i + "]/name", doc);
         // Deeclaring the route arraylist which is the value of the Hash
         ArrayList<String> route = new ArrayList<String>();
         // Adding all of the route in a key to the arraylist
         int routeCount = Integer.parseInt(path.evaluate(
                "count(/routes/city[" + i + "]/route)", doc));
         for (int j = 1; j <= routeCount; j++) {
            // Logic here to check if it was a city on the server 
            // that was claimed
            try {
               // Getting the claimed routes from the server
               Hashtable<String, ArrayList<String>> 
                      claimedRoutes = stub.getClaimedRoutes();
               // Getting the keys of this
               Set<String> keys = claimedRoutes.keySet();
               // Getting the id in the XML to check
               currentRoute = path.evaluate("/routes/city[" + i + "]" 
                      + "/route[" + j + "]/id", doc);
               // Iterating over each name in the hastable
               for (String key: keys) {
                  // Iterating over the arraylists of the current name
                  for (String r : claimedRoutes.get(key)) {
                     // Checking if the current iterative route is 
                     // equal in the XML
                     if (currentRoute.equals(r)) {
                        // Getting the rest of the data and 
                        // adding it to the built hashtable
                        currentCity = path.evaluate("/routes/city[" + i + "]" 
                               + "/name", doc);
                        System.out.println(currentCity);
                        currentWeight = path.evaluate("/routes/city[" + i + "]" 
                               + "/route[" + j + "]/weight", doc);
                        currentDest = path.evaluate("/routes/city[" + i + "]" 
                               + "/route[" + j + "]/destination", doc);

                        destWeight = new ArrayList<String>();
                        destWeight.add(currentDest);
                        destWeight.add(currentWeight);
                        values.put(currentRoute, destWeight);

                        hashedRoutes.put(name, values);
                     }
                  }   
               }

            } catch (RemoteException re) { }

         }
      
      }
   
   }

   /**
    * Iterate over HashMap for each route.
    * @return longestTrail the longest trail in the XML file
    */
   public ArrayList<String> iterateTrails() {
      // The arraylist for the longest trail
      ArrayList<String> longestTrail = new ArrayList<String>();
   
      for (String key: hashedRoutes.keySet()) {
         // Getting the list of destinations from the hash
         if(visitedCities.indexOf(key) == -1) {
            visitedCities.add(key.trim());
         }
         Hashtable<String, ArrayList<String>> visitedDestinations 
                = hashedRoutes.get(key);
      
         // Sending a city and it's routes to check if
         // it is the longest trail
         ArrayList<String> testtrail = findLongTrail(hashedRoutes, key,
                   new ArrayList<String>());
         // If the test trail is longer than the longest trail
         // set the new trail to the longest
         if (testtrail.size() > longestTrail.size()) {
            longestTrail = testtrail;
         }
      
      }
      // Returning the longest trail
      return longestTrail;
   }

   /**
    * Recurse each weight from citys from route
    * to find the longest route.
    * @param graph the graph to check
    * @param start where to start on the graph
    * @param path an arraylist of the path
    * @return longestNewPath the longest path
    */
   public ArrayList<String> findLongTrail(Hashtable graph, String start,
          ArrayList<String> path) {
      // ArryList that holds the path of the current path
      ArrayList<String> currentPathPlaceholder = path;
      // Adding all of the previous path to the current placeholder
      //System.out.println("Currt Path" + path);
      // Adding the given destination to the current path
   
      // Getting the Hashtable of routes that a city node stores
      Hashtable<String, ArrayList<String>> currentRoutes 
             = hashedRoutes.get(start);
   
      // The Arraylist of Routes for the longest NEW path
      ArrayList<String> longestNewPath = new ArrayList<String>();
   
      // Iterating over the current routes on the city
      for (String currentR : currentRoutes.keySet()) {
      
         // Checking if the edge has already been visited
         // Passing the current destination's id to the method
         if (!edgeVisited(currentPathPlaceholder, currentR)) {
            // Recursing all of the trails that have not been visited
            // to find the longest one
         
            // Destination of current route
            String dest = currentRoutes.get(currentR).get(0);
            // Weight of current route
            String weight = currentRoutes.get(currentR).get(1);
         
            // Adding route and weight to path
            currentPathPlaceholder.add(currentR);
            currentPathPlaceholder.add(weight);
         
         
            ArrayList<String> newtrail = findLongTrail(hashedRoutes, dest,
                    currentPathPlaceholder);
         
            // Checking if the newtrail is longer than the longest path
            if (!newtrail.isEmpty() && newtrail.size() 
                   > longestNewPath.size()) {
               // Setting the new trail to the longestnewpath
               longestNewPath = newtrail;
            }
         }
      }
   
      // Checking if the path is empty
      if (longestNewPath.size() == 0) {
         // make this return the path that was traveresd
         return currentPathPlaceholder;
      }
      return longestNewPath;
   }

   /**
    * Helper method to check if route and city has been
    * visited.
    * @param path the current path
    * @param route the route to visit
    * @return visited returns true if the edge has been visited.
    */
   public boolean edgeVisited(ArrayList<String> path, String route) {
   
      // Checking the visitedRoutes arraylist to see if the edge
      // has been visited
      // Iterating over all of the elements in the list
   
      if (path.indexOf(route) >= 0) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Check if the given route and city produces the longest
    * path.
    * @param args the command line input
    * @throws Exception for all exception
    */
   public static void main(String[] args) throws Exception {
      // WHEN we start using this it will be called as a 
      // new object with the IP and stubID so that it can 
      // connect to the server right now these are just test 
      // vars and the Hashtable should contain nothing 
      // if it is not apart of a current game

      // This will also only run if you start the initial server on localhost
      // and create a new game
      // the gamestub: GameStub16790 is the starting game stub for the 
      // first game created
      new RouteCheck(new Route("A", "E"), "localhost", "GameStub16790");
   }

}