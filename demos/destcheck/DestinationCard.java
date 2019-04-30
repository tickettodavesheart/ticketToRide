/**
     DestinationCard - a class to model a Ticket to Ride Destination Card.
     @author Joshua Bugryn
     @version 04292019
 */

public class DestinationCard {
  // first location on the card
   private String locationOne;
   // second location on the card
   private String locationTwo;
   
   /**
     DestinationCard constructor 
     @param locationOne - String containing the first city
     @param locationTwo - String containing the second city
    */
   public DestinationCard (String locationOne, String locationTwo) {
      this.locationOne = locationOne;
      this.locationTwo = locationTwo;
   }
   
   /**
      setLocationOne - sets location one for route
      @param String - new location one string value
    */
   public void setLocationOne(String newLocation) {
      this.locationOne = newLocation;
   }
    
    /**
      setLocationTwo - sets location one for route
      @param String - new location two string value
    */
   public void setLocationTwo(String newLocation) {
      this.locationTwo = newLocation;
   }
    
    /**
      getLocationOne - gets location one for route
      @return String - location one string value
    */
   public String getLocationOne() {
      return locationOne;
   }
    
    /**
      getLocationTne - sets location one for route
      @return String - location two string value
    */
   public String getLocationTwo() {
      return locationTwo;
   }
}