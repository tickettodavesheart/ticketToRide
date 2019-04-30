import java.util.*;
  
  /**
     DestTest - tester class for DestinationCardCheck.
     @author Joshua Bugryn
     @version 04292019
   */
   
public class DestTest {
  
   public static void main(String[] args) {
      DestinationCard destCard = new DestinationCard("San Francisco", "Santa Fe");
      ArrayList<String> ownedRoutes = new ArrayList<String>();
      ownedRoutes.add("white_1");
      ownedRoutes.add("green_1");
      ownedRoutes.add("yellow_1"); 
      ownedRoutes.add("orange_3");
      ownedRoutes.add("yellow_4");
      ownedRoutes.add("blue_1");
      ownedRoutes.add("grey_5");
      ownedRoutes.add("purple_2");
      ownedRoutes.add("grey_13");
      ownedRoutes.add("yellow_3");
      DestinationCardCheck dcc = new DestinationCardCheck();
      dcc.newCard(destCard, ownedRoutes);
      
      dcc.verify(destCard.getLocationOne());
      
      if(dcc.isCardCompleted()) {
         System.out.println("Destination Card Completed... Awarding points");
      } else {
         System.out.println("Destination Card Not Completed... No points Awarded");
      }
   }
}