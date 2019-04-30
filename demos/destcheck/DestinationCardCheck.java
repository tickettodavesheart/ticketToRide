import java.util.*;
import java.io.*;


/**
 Route Check - Tests if a destination card route has been completed by a player.
 @author Joshua Bugryn
 @version 04292019
 */
 
public class DestinationCardCheck {
  
   // Destination card object to check
   private DestinationCard destCard;
   // First city on destination card
   private String startingCity;
   // Second city on destination card
   private String endingCity;
   // boolean to check if second city is ever reached
   private boolean destTwoVisited;
  
  // ArrayList of routes controlled by the owner of the current destination card
   private ArrayList<String> ownedRoutes;
  
  // Master list of cities, their connected routes, 
  // and the routes destination and weight value
   private Hashtable<String, Hashtable<String, ArrayList<String>>> MASTER_LIST;
  
     /**
         DestinationCardCheck constructor - 
         
         //DestinationCard destCard, ArrayList<String> ownedRoutes
     */
   public DestinationCardCheck() {
      try {
         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("MAP_HASHED.obj")));
         MASTER_LIST = (Hashtable<String, Hashtable<String, ArrayList<String>>>)ois.readObject();
         ois.close();
         //System.out.println(MASTER_LIST);
         //System.out.println(MASTER_LIST.keySet().size());
      } catch(Exception E) {
         System.err.println("Cannot find MAP_HASHED.obj");
      }
   
      this.destCard = null;
      this.ownedRoutes = null;
      this.destTwoVisited = false;
      this.startingCity = "";
      this.endingCity = "";
   }
   
   /**
     newCard - reloads the DestinationCardCheck object with a new destination card and ownedRoutes
     @param destCard - a new DestinationCard object to be verified
     @param ownedRoutes - an ArrayList of strings containing the controlled routes
                         of the owner of the Destination Card
    */
   public void newCard(DestinationCard destCard, ArrayList<String> ownedRoutes) {
      this.destCard = destCard;
      this.ownedRoutes = ownedRoutes;
      this.destTwoVisited = false;
      this.startingCity = destCard.getLocationOne();
      this.endingCity = destCard.getLocationTwo();
   }
   
   
   /**
     isCardCompleted - returns the boolean containing the verification value.
     @return destTwoVisited - true if card is completed by player,
                              false if card was not completed by player
    */
   public boolean isCardCompleted() {
      return destTwoVisited;
   }
   
   
   /**
     verify - recursive function to check if the second destination
              of a DestinationCard object is reachable using its
              owner's controlled routes.
    */
   public void verify(String currentCity) {
      //System.out.println(MASTER_LIST.get(currentCity));
      Object[] currentRouteSet = MASTER_LIST.get(currentCity).keySet().toArray();
      for(int i = 0; i < currentRouteSet.length; i++) {
         String tempRoute = (String)currentRouteSet[i];
         String tempDest = MASTER_LIST.get(currentCity).get(tempRoute).get(0);
      
         
         if(ownedRoutes.indexOf(tempRoute) > -1) {
            //System.out.println("TempRoute: " + tempRoute);
            //System.out.println("TempDest: " + tempDest);
            ownedRoutes.remove(tempRoute);
            if(tempDest.equals(endingCity)) {
               destTwoVisited = true;
            }
            verify(tempDest);
         }
      }
   }
}