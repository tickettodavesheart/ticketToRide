public class Route {
   private String locationOne;
   private String locationTwo;
   
   public Route (String locationOne, String locationTwo) {
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