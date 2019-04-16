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
   private Hashtable<String, ArrayList<String>> hashedRoutes = new 
          Hashtable<String, ArrayList<String>>();
   // Document builders for XML parsing
   private DocumentBuilder builder;
   private XPath path;
   // ArrayList of visited edges
   private ArrayList<String> visitedRoutes = new 
          ArrayList<String>();

   /**
    * Defualt Constructor for the Longest Class.
    * @throws Exception all Exceptions
    */
   public Longest() throws Exception {

      // Creating the hashtable from the XML file
      generateHash();
      // Finding the longest trail in the XML file
      ArrayList<String> finalTrail = iterateTrails();
      System.out.println("\n\n------------ Results ------------");
      System.out.println("Trail: " + finalTrail);
      System.out.println("Length: " + finalTrail.size());

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

      for (int i = 1; i <= cityCount; i++) {
         // Declaring the name which is the Key for the Hash 
         String name = path.evaluate("/routes/city[" + i + "]/name", doc);
         // Deeclaring the route arraylist which is the value of the Hash
         ArrayList<String> route = new ArrayList<String>();
         // Adding all of the route in a key to the arraylist
         int routeCount = Integer.parseInt(path.evaluate(
                "count(/routes/city[" + i + "]/route)", doc));
         for (int j = 1; j <= routeCount; j++) { 
            String currentCity = path.evaluate("/routes/city[" + i + "]"
                   + "/route[" + j + "]/id", doc);
            route.add(currentCity);
         }
         hashedRoutes.put(name, route);
      }
   }

   /**
    * Iterate over HashMap for each route.
    * @return longestTrail the longest trail in the XML file
    */
   public ArrayList<String> iterateTrails() {
      // Getting the key set of the hashedRoutes
      Set<String> keys = hashedRoutes.keySet();
      // The arraylist for the longest trail
      ArrayList<String> longestTrail = new ArrayList<String>();
      for (String key: keys) {
         // Sending a city and it's citys to check if
         // it is the longest trail
         ArrayList<String> testtrail = findLongTrail(key);
         System.out.printf("%nVisited the following trail: %s %n\t with the key: %s", testtrail, key);
         // If the test trail is longer than the longest trail
         // set the new trail to the longes
         if (testtrail.size() > longestTrail.size()) {
            // Setting the new trail to the longest trail
            longestTrail = testtrail;
         }
      }
      // Returning the longest trail
      return longestTrail;
   }

   /**
    * Recurse each weight from citys from route
    * to find the longest route.
    * @param key the city name that is being started on
    * @return longestNewPath the longest path
    */
   public ArrayList<String> findLongTrail(String key) {
      // Getting the arraylist of values that a route stores
      ArrayList<String> currentRoutes = hashedRoutes.get(key);
      // The Arraylist of Keys for the longest NEW path
      ArrayList<String> longestNewPath = new ArrayList<String>();
      // Iterating over the current routes on the city
      for (String currentR : currentRoutes) {
         // Checking if the edge has already been visited
         // Passing the current destination's id to the method
         if (!edgeVisited(currentR)) {
            // Recursing all of the trails that have not been visited
            // to find the longest one
            ArrayList<String> newtrail = findLongTrail(key);
            // Checking if the newtrail is longer than the longest path
            if (newtrail.size() > longestNewPath.size()) {
               // Setting the new trail to the longestnewpath
               longestNewPath = newtrail;
            }
         }
      }
      // Checking if the path is empty
      if (longestNewPath.size() == 0) {
         // make this return the path that was traveresd
         return currentRoutes;
      }
      return longestNewPath;
   }

   /**
    * Helper method to check if route and city has been
    * visited.
    * @param id the id of the current city
    * @return visited returns true if the edge has been visited.
    */
   public boolean edgeVisited(String id) {
      // Checking the visitedRoutes arraylist to see if the edge
      // has been visited
      // Iterating over all of the elements in the list
      for (String visitId : visitedRoutes) {
         // Checking if the given id matches one in the list of visited
         // routes
         if (visitId.equals(id)) {
            // Returning true if the edge has been visited. 
            return true;
         }
      }
      // Adding the edge to the visitedRoutes arraylist
      visitedRoutes.add(id);
      // Returning false if the edge has not been visited. 
      return false;
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
