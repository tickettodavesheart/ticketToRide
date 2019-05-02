import java.util.*;
import java.io.*;

public class RoutePointsDict {

   private Hashtable<Integer, Integer> routeValues;
   private Hashtable<String, Integer> routeWeights;
   private final File VALUES = new File("ROUTE_POINTS_TABLE.obj");
   private final File WEIGHTS = new File("ROUTE_WEIGHT_DICT.obj");
   
   public RoutePointsDict() {
      try {
          ObjectInputStream ois = new ObjectInputStream(new FileInputStream(VALUES));
      routeValues = (Hashtable<Integer, Integer>)ois.readObject();
      
      ois = new ObjectInputStream(new FileInputStream(WEIGHTS));
      routeWeights = (Hashtable<String, Integer>)ois.readObject();
      
      System.out.println("Values : " + routeValues);
      
      System.out.println("Weights: " + routeWeights);
      } catch (Exception e) {
          e.printStackTrace();
      }
      
   }
   
   /**
    *
    *
    */
   public int getRouteWeight(String route) {
      int weight;
      
      weight = routeWeights.get(route);
      return weight;
   }
    
   public int getRoutePoints(String route) {
      int points;
      points = routeValues.get(getRouteWeight(route));
      return points;
   }
   
   public static void main(String[] args) throws Exception {
      new RoutePointsDict();
   }
}