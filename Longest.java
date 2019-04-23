
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;

/**
 * Method to find the longest path.
 * @author Lucas Kohorst
 * @version 4/15/19
 */
public class Longest {

   // Generate a Hashtable from the XML
   private Hashtable<String, Hashtable<String, ArrayList<String>>> hashedRoutes = new
          Hashtable<String, Hashtable<String, ArrayList<String>>>();
   // Document builders for XML parsing
   private DocumentBuilder builder;
   private XPath path;
   // ArrayList of visited edges
   private ArrayList<String> visitedRoutes = new
          ArrayList<String>();

   /**
    * Defualt Constructor for the LongestV2 Class.
    * @throws Exception all Exceptions
    */
   public Longest() throws Exception {

      // Creating the hashtable from the XML file
      generateHash();
      // Finding the longest trail in the XML file
      ArrayList<String> finalTrail = iterateTrails();
      System.out.println("\n\n------------ Results ------------");
      System.out.println("Trail: " + finalTrail);
      System.out.println("Length: " + finalTrail.size()/2);

      int weight = 0;
      for(int i = 0; i < finalTrail.size() - 1; i++) {
         if(i % 2 == 1) {
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
      File f = new File("data/test.xml");
      Document doc = builder.parse(f);

      // Getting the city count
      int cityCount = Integer.parseInt(path.evaluate(
                "count(/routes/city)", doc));

      // Strings
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
            currentRoute = path.evaluate("/routes/city[" + i + "]"
                   + "/route[" + j + "]/id", doc);
            currentWeight = path.evaluate("/routes/city[" + i + "]"
                   + "/route[" + j + "]/weight", doc);
            currentDest = path.evaluate("/routes/city[" + i + "]"
                   + "/route[" + j + "]/destination", doc);

            //System.out.printf("C:%s%nR:%s%n",name, currentRoute);
            destWeight = new ArrayList<String>();
            destWeight.add(currentDest);
            destWeight.add(currentWeight);
            //System.out.println(destWeight + "\n");
            values.put(currentRoute, destWeight);
         }

         //System.out.println(values + "\n");

         hashedRoutes.put(name, values);
      }
      //System.out.println("\n" + hashedRoutes);
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
         Hashtable<String, ArrayList<String>> visitedDestinations = hashedRoutes.get(key);


         //System.out.println("vD: " + visitedDestinations);
         //System.out.println("visitedDestinations" + visitedDestinations);
         // Iterating over each destination
         //for (String dest : visitedDestinations.keySet()) {
         //System.out.println(key);


            // Sending a city and it's routes to check if
            // it is the longest trail
         ArrayList<String> testtrail = findLongTrail(hashedRoutes, key,
                   new ArrayList<String>());
         //System.out.printf("%nVisited the following trail: %s %n\t with the key: %s", testtrail, key);
            // If the test trail is longer than the longest trail
            // set the new trail to the longes
         if (testtrail.size() > longestTrail.size()) {
               // Setting the new trail to the longest trail
            longestTrail = testtrail;
         }
         //}
      }
      // Returning the longest trail
      return longestTrail;
   }

   /**
    * Recurse each weight from citys from route
    * to find the longest route.
    * @param key the city name that is being started on
    * @param dest the destination to check
    * @param path an arraylist of the path
    * @return longestNewPath the longest path
    */
   public ArrayList<String> findLongTrail(Hashtable graph, String start,
          ArrayList<String> path) {
      // ArryList that holds the path of the current path
      ArrayList<String> currentPathPlaceholder = path;
      // Adding all of the previous path to the current placeholder

      //currentPathPlaceholder = path;

      System.out.println("Currt Path" +path);
      // Adding the given destination to the current path
      //currentPathPlaceholder.add();

      //System.out.println("\nCurrent Path Holder from " + start + ": " + currentPathPlaceholder + "\n");
      // Getting the Hashtable of routes that a city node stores
      Hashtable<String, ArrayList<String>> currentRoutes = hashedRoutes.get(start);

      //System.out.println("Routes :" + currentRoutes);
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


            //System.out.println("NT: " + newtrail);


            // Checking if the newtrail is longer than the longest path
            if (!newtrail.isEmpty() && newtrail.size() > longestNewPath.size()) {
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
    * @param id the id of the current city
    * @return visited returns true if the edge has been visited.
    */
   public boolean edgeVisited(ArrayList<String> path, String route) {

      // Checking the visitedRoutes arraylist to see if the edge
      // has been visited
      // Iterating over all of the elements in the list

      if (path.indexOf(route) >= 0) {
         //System.out.println("Route already used");
            // Returning true if the edge has been visited.
         return true;
      } else {
      // Returning false if the edge has not been visited.
         //System.out.println("New route adding to path");
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
      new Longest();
   }

}
