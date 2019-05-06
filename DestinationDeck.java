import java.util.*;
import java.io.*;
  
  /**
     DestinationDeck - A deck of all DestinationCards for Ticket to Ride.
     @author Joshua Bugryn
     @version 05012019
   */
   
public class DestinationDeck {
  
  // input stream to read object file
   ObjectInputStream ois = null;
  // arraylist deck of cards
   ArrayList<DestinationCard> deck = null;
  // file to be read in as the deck
   private final String DECK_FILE = "resources/DestinationDeck.obj";
  
  /**
   * DestinationDeck constructor - reads in deck file and creates the deck.
   */
   public DestinationDeck() {
      try {
         this.ois = new ObjectInputStream(getClass().getResourceAsStream(DECK_FILE));
         this.deck = (ArrayList<DestinationCard>)ois.readObject();
      } catch(Exception e) {
         this.deck = new ArrayList<DestinationCard>();
         e.printStackTrace();
      }
   }
  
  /**
   * makeDeck - Returns the a new shuffled deck of DestinationCards
   *
   */
   public ArrayList<DestinationCard> makeDeck() {
      Collections.shuffle(deck);
      return deck;
   }
}