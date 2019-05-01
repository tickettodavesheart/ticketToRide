import java.io.Serializable;

/**
     DestinationCard - a class to model a Ticket to Ride Destination Card.
     @author Joshua Bugryn
     @version 04292019
 */

public class DestinationCard implements Serializable{
  // first location on the card
   private String locationOne;
   // second location on the card
   private String locationTwo;
   // point value
   private int pointValue;
   
   /**
     DestinationCard constructor 
     @param locationOne - String containing the first city
     @param locationTwo - String containing the second city
    */
   public DestinationCard (String locationOne, String locationTwo, int pointValue) {
      this.locationOne = locationOne;
      this.locationTwo = locationTwo;
      this.pointValue = pointValue;
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
      setPointValue - sets point value for the card
      @param int - new point value for the card
    */
   public void setPointValue(int pointValue) {
      this.pointValue = pointValue;
   }
    
    /**
      getLocationOne - gets location one for route
      @return String - location one string value
    */
   public String getLocationOne() {
      return locationOne;
   }
    
    /**
      getLocationTwo - sets location one for route
      @return String - location two string value
    */
   public String getLocationTwo() {
      return locationTwo;
   }
   
   /**
      getPointValue - gets point value for the card
      @return int - integer point value for the card
    */
   public int getPointValue() {
      return pointValue;
   }
   
   /**
     toString - pretty printed output of destination card info
     @return String - pretty preinted output of destination card info
    */
   public String toString() {
      return String.format(locationOne + " to " + locationTwo + " (" + pointValue + ")");
   }
}