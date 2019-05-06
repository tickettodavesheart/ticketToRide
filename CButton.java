import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.Hashtable;

// RMI
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
/**
 * CButton - A custom button that accepts a shape and creates a JButton
 * of the given shape.
 * @author Joshua Bugryn
 * @version 4/15/2019
 */

public class CButton extends JButton {
   // Shape object the button will use
   private Shape shape = null;
   // Area object for hit detection
   private Area area = null;
   // name of the button
   private String nameButton = "color_#";
   // Color object to hold button fill color
   private Color trainColor;
   // boolean to track mouse entry and exit
   private boolean entered = false;
   // boolean to track selection and deselection
   private boolean selected = false;

   // Array of colors for each player
   private Color[] colors = {new Color(124, 33, 48), new Color(34, 119, 80), new Color(79, 76, 186), new Color(124, 108, 33)};

   // Attributes that are placeholders for the selected route
   private String selectedName = "";
   private String selectedColor = "";

   // Attributes that are given to be repainted with
   private Color paintColor = null;

   // For RMI
   private GameStub stub;

   // The mouse listner for custom button handling
   private MouseListener ml = null;
   
   // Current players name
   private String currentPlayer;

   // boolean that is set to false after first on the timer
   // so that sending route to server only happens once
   private boolean sendRoutes = true;

   // boolean to limit the selection of a route to once
   private boolean routeClaimed = false;


   /**
    * CButton constructor.
    * @param button - A shape object to draw the button
    * @param nameButton - A string to assign a name to the button
    * @param trainColor - The color the button will be filled
    * @param ip the IP address for the main server
    * @param stubID the id for the GameStub for the specific server
    * @param name the name of the current game
    */
   public CButton(Shape button, String nameButton, Color trainColor, 
          String ip, String stubID, String name) {
      this.shape = button;
      this.area = new Area(shape);
      this.nameButton = nameButton;
      this.trainColor = trainColor;

      // remove JButton default styling
      setOpaque(false);
      setFocusPainted(false);
      setBorderPainted(false);

      // set bounds for null layout
      setBounds(0, 0, 850, 618);

      // Bind to the GameServer
      try {
         // Locating the Registry
         Registry registry = LocateRegistry.getRegistry(ip);

         // Looking up the Stub class
         stub = (GameStub) registry.lookup(stubID);

         // find the current player
         currentPlayer = stub.getTockenOwner();

      } catch (Exception e) {
         System.err.println("Client exception: " + e.toString());
         e.printStackTrace();
      }

      MouseListener[] mla = getMouseListeners();
      if (mla.length > 0) {
         for (int j = 0; j < mla.length; j++) {
               removeMouseListener(mla[j]);
       }
      }
      
   } // end CButton constructor

   /**
    * Toggles the buttons custom handlers on or off.
    * @param state if they are on or off
    */
   public void toggleButton(boolean state, Hashtable<String, String> selectedFromServer) {
         ml = new RouteAdapter(this, selectedName, stub);
         Hashtable<String, String> routes = null;
         try {
            routes = stub.updateRoutes();
            //System.out.print(routes);
         } catch (Exception e) {
            System.out.println(e);
         }
         ArrayList<String> selectedRoutes = new ArrayList<String>();
         Object[] c = routes.keySet().toArray();

         for(Object o : c) {
            selectedRoutes.add((String) o);
         }

         System.out.println(selectedRoutes);
         System.out.println(getButtonID() + "index of = " + selectedRoutes.indexOf(getButtonID()));
      if (state && !routeClaimed && (selectedRoutes.indexOf(getButtonID()) == -1)) {
         MouseListener[] mla = getMouseListeners();
         if (mla.length > 0) {
            for (int j = 0; j < mla.length; j++) {
                  removeMouseListener(mla[j]);
          }
      }
         addMouseListener(ml);
         // Setting the boolean to send routes for the new turn to true
         sendRoutes = true;
      } else {
            MouseListener[] mla = getMouseListeners();
            if (mla.length > 0) {
               for (int j = 0; j < mla.length; j++) {
                     removeMouseListener(mla[j]);
             }
            sendRoutes = false;
         }
      }
   }

   /**
    * Method that is called when the user's turn is over.
    */
   public void endTurn(String routeName, String playerToAdd) {
         try {
            if(getSelected() && !this.routeClaimed){
               this.routeClaimed = true;
            }
            // Sending the selected route and name to the 
            // server to paint on the next client
            stub.addRoute(playerToAdd, routeName);
				sendRoutes = false;
				// No longer can select a route
            toggleButton(false, new Hashtable<String, String>());
         } catch (Exception endTurnE) { 
            endTurnE.printStackTrace();
         }
   }

   /**
    * Method to repaint the button with the given color.
    * @param color the color to paing the button
    */
   public void colorButton(String color) {
      // Creating a switch for the colors
      switch (color) {
         // color1
         case "color0":
            paintColor = colors[0];
            selected = true;
            // repainting the string
            repaint();
            break;
         // color2
         case "color1":
            paintColor = colors[1];
            selected = true;
            // repainting the string
            repaint();
            break;
         // color3
         case "color2":
            paintColor = colors[2];
            selected = true;
            // repainting the string
            repaint();
            break;
         // color4
         case "color3":
            paintColor = colors[3];
            selected = true;
            // repainting the string
            repaint();
            break;
      }
   }

   // Hit detection for mouse actions
   @Override
    public boolean contains(int x, int y) {
      return area.contains((double) x, (double) y);
   }

   // Overriding default JButton size
   @Override
   public Dimension getPreferredSize() {
      return new Dimension(160, 160);
   } // end getPreferredSize @Override


   /* Override paintComponent of JButton
      Draws outline and Fills each button

      Handles repaint() calls from mouseAdapter to change color:
          - on click
          - on mouse in
          - on mouse out
   */
   @Override
   public void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
             RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
            RenderingHints.VALUE_STROKE_PURE);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
             RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setStroke(new BasicStroke(1.5f, 
             BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));


      // if block to check if mouse has entered the button
      // changes outline color to highlight button if true
      if (entered) {
         g2d.setPaint(Color.YELLOW.brighter());
      } else {
         if (trainColor != Color.BLACK) {
            g2d.setPaint(Color.BLACK);
         } else {
            g2d.setPaint(Color.WHITE);
         }
      }
      // draw outline
      g2d.draw(shape);

      if (selected) {
         // set the color to the new color
         g2d.setPaint(paintColor);
      } else {
         g2d.setPaint(trainColor);
      }

      // fills button with color
      g2d.fill(area);

   } // end paintComponent @Override

   /**
    * getButtonID - returns button ID name.
    * @return nameButton - String of button ID
    */
   public String getButtonID() {
      return nameButton;
   }

   /**
    * toggleEntered - toggles boolean state of entered.
    * 
    */
      public void toggleEntered(boolean toggle) {
            entered = toggle;
      }
      /**
       * toggleSelected - toggles boolean state of selected
       * 
       */
      public void toggleSelected(boolean toggle) {
            selected = toggle;
      }

      /**
       * toggleSelectedOnce - toggles boolean state of routeClaimed
       * 
       */
      public void toggleRouteClaimed(boolean toggle) {
            routeClaimed = toggle;
      }

      /**
       * getEntered - gets value of entered 
       * @return entered - boolean state of entered
       */
      public boolean getEntered() {
            return entered;
      }

      /**
       * getSelected - gets value of selected
       * @return selected - boolean state of selected
       */
      public boolean getSelected() {
            return selected;
      }

      /**
       * isRouteClaimed - gets value of routeClaimed
       * @return routeClaimed - boolean state of routeClaimed
       */
      public boolean isRouteClaimed() {
            return routeClaimed;
		}
		
		/** 
		 * Gets the name of the button.
		 * @return name of the button
		 */
	   public String getButtonName() {
			return nameButton;
		}
		
		/** 
		 * Gets the current player name.
		 * @return name of the player
		 */
	   public String getCurrentPlayer() {
			return currentPlayer;
		}


} // end CButton class

class RouteAdapter extends MouseAdapter { 

      private CButton btn;
      private String selectedName;
      private GameStub stub;
		private String currentPlayer;
		private String nameButton;

      public RouteAdapter(CButton btn, String selectedName, GameStub stub) {
            this.btn = btn;
            this.selectedName = selectedName;
				this.stub = stub; 
				this.nameButton = btn.getButtonName();
				this.currentPlayer = btn.getCurrentPlayer();
      }

      public void mouseEntered(MouseEvent e) {
         btn.toggleEntered(true);
         btn.repaint();
      } 

      public void mouseExited(MouseEvent e) {
         btn.toggleEntered(false);
         btn.repaint();
      }

      public void mouseClicked(MouseEvent e) {
			// if you haven't done anything else this turn, you can select a route
			
			// First checking if you can claim the route
			if (canClaimRoute()) {
				if( (!( (GameBoard) btn.getParent() ).getHasClaimedRoute() 
						&& !( (GameBoard) btn.getParent() ).getHasClaimedTrainCard()
						&& !( (GameBoard) btn.getParent() ).getHasClaimedDestCard())
						|| (!( (GameBoard) btn.getParent() ).getHasClaimedRoute() 
						&& !( (GameBoard) btn.getParent() ).getHasClaimedTrainCard()
						&& ( (GameBoard) btn.getParent() ).getTurnNumber() < 1)) {
					try {
						// Giving it the name and color
						btn.toggleRouteClaimed(true);
						selectedName = btn.getButtonID();
						btn.toggleSelected(true);
						((GameBoard) btn.getParent() ).setHasClaimedRoute(true, btn.getButtonID());
						// Giving it the name and color
						// getting the current players index for painting
						// grab the player names from the GameServer stub     
						Vector<String> playerNames = stub.getPlayerNames();
						currentPlayer = stub.getTockenOwner();
						// iterate through the player names list to find the index 
						// of the current player, and set the color of the road 
						// to the corresponding color
						for (int i = 0; i < playerNames.size(); i++) {
							if (playerNames.get(i).equals(currentPlayer)) {
								// Calling the method to paint the color on the given CButton
								btn.colorButton("color" + i);
							} 
						}
						// Decrementing the player's trains
						stub.decrementPlayerTrains(currentPlayer, btn.getButtonID());
						////btn.toggleSelectedOnce(false);
						// Ending
						// Ending the turn
						//btn.endTurn(btn.getButtonID(), stub.getTockenOwner());
					} catch (Exception re) {
							re.printStackTrace();
							System.err.println("[Exception]: A RemoteException has occurred - " + re);
						}
				} else if(btn.getSelected()) {
					try {
						stub.incrementPlayerTrains(currentPlayer, btn.getButtonID());
						btn.toggleSelected(false);
						((GameBoard) btn.getParent() ).setHasClaimedRoute(false, "");
						btn.toggleRouteClaimed(false);
						btn.repaint();
					} catch (RemoteException re) {
						re.printStackTrace();
					}
				}
			}
         // if you haven't done anything else this turn, you can select a route
         if( (!( (GameBoard) btn.getParent() ).getHasClaimedRoute() 
               && !( (GameBoard) btn.getParent() ).getHasClaimedTrainCard()
               && !( (GameBoard) btn.getParent() ).getHasClaimedDestCard())
               || (!( (GameBoard) btn.getParent() ).getHasClaimedRoute() 
               && !( (GameBoard) btn.getParent() ).getHasClaimedTrainCard()
               && ( (GameBoard) btn.getParent() ).getTurnNumber() < 1)) {
            try {
               // Giving it the name and color
               btn.toggleRouteClaimed(true);
               selectedName = btn.getButtonID();
               btn.toggleSelected(true);
               ((GameBoard) btn.getParent() ).setHasClaimedRoute(true, btn.getButtonID());
               // Giving it the name and color
               // getting the current players index for painting
               // grab the player names from the GameServer stub     
               Vector<String> playerNames = stub.getPlayerNames();
               currentPlayer = stub.getTockenOwner();
               // iterate through the player names list to find the index 
               // of the current player, and set the color of the road 
               // to the corresponding color
               for (int i = 0; i < playerNames.size(); i++) {
                  if (playerNames.get(i).equals(currentPlayer)) {
                     // Calling the method to paint the color on the given CButton
                     btn.colorButton("color" + i);
                  } 
               }
               // Decrementing the player's trains
               stub.decrementPlayerTrains(currentPlayer, btn.getButtonID());
               ////btn.toggleSelectedOnce(false);
               // Ending
               // Ending the turn
               //btn.endTurn(btn.getButtonID(), stub.getTockenOwner());
            } catch (Exception re) {
                  re.printStackTrace();
                  System.err.println("[Exception]: A RemoteException has occurred - " + re);
               }
         } else if(btn.getSelected()) {
            try {
               stub.incrementPlayerTrains(currentPlayer, btn.getButtonID());
               btn.toggleSelected(false);
               ((GameBoard) btn.getParent() ).setHasClaimedRoute(false, "");
               btn.toggleRouteClaimed(false);
               btn.repaint();
               btn.revalidate();
            } catch (RemoteException re) {
               re.printStackTrace();
            }
         }
      }

      /**
       * Checks to see if the user can claim
       * the given route.
       * @return if they can claim the route
       */
      public boolean canClaimRoute() {
			
			// boolean to return
			boolean canClaim = false;
         // The counts of each color
         int blackCount = 0;
			int blueCount = 0;
			int greenCount = 0;
			int neutralCount = 0;
			int orangeCount = 0;
			int PURPLECount = 0;
			int redCount = 0;
			int whiteCount = 0;
			int yellowCount = 0;
         // Getting the current player's trains
         try {
				ArrayList<String> currentListOfTrainCards = stub.getPlayerTrainCards(currentPlayer);
            for (String color : currentListOfTrainCards) {
               switch (color) {
                  case "BLACK":
                     blackCount++;
                     break;
                  case "BLUE":
                     blueCount++;
                     break;
                  case "GREEN":
                     greenCount++;
                     break;
                  case "NEUTRAL":
                     neutralCount++;
                     break;
                  case "ORANGE":
                     orangeCount++;
                     break;
                  case "PURPLE":
                     PURPLECount++;
                     break;
                  case "RED":
                     redCount++;
                     break;
                  case "WHITE":
                     whiteCount++;
                     break;
                  case "YELLOW":
                     yellowCount++;
                     break;
               }
            }
			} catch (RemoteException re) { }

			// Adding the counts to an arraylist
			Hashtable<String, Integer> trainColorCountList = new Hashtable<String, Integer>();
			trainColorCountList.put("BLACK", blackCount);
			trainColorCountList.put("BLUE", blueCount);
			trainColorCountList.put("GREEN", greenCount);
			trainColorCountList.put("ORANGE", orangeCount);
			trainColorCountList.put("PURPLE", PURPLECount);
			trainColorCountList.put("RED", redCount);
			trainColorCountList.put("WHITE", whiteCount);
			trainColorCountList.put("YELLOW", yellowCount);
			
			// Getting the weights of the routes
			RoutePointsDict rpd = new RoutePointsDict();
			int routeWeight = rpd.getRouteWeight(nameButton);
			// Getting the route color
			String[] buttonNameSplit = nameButton.split("_");
			String buttonWeightColor = buttonNameSplit[0];
			ArrayList<String> tempColors = new ArrayList<String>();
			switch (buttonWeightColor.toUpperCase()) {
				case "BLACK":
					if ((blackCount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || blackCount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("BLACK");
							}
						} else {
							int neutralColorsNeeded = routeWeight - blackCount;
							for (int i = 0; i < blackCount; i++) {
								tempColors.add("BLACK");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "BLUE":
					if ((blueCount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || blueCount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("BLUE");
							}
						} else {
							int neutralColorsNeeded = routeWeight - blueCount;
							for (int i = 0; i < blueCount; i++) {
								tempColors.add("BLUE");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "GREEN":
					if ((greenCount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || greenCount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("GREEN");
							}
						} else {
							int neutralColorsNeeded = routeWeight - blackCount;
							for (int i = 0; i < greenCount; i++) {
								tempColors.add("GREEN");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "ORANGE":
					if ((orangeCount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || orangeCount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("ORANGE");
							}
						} else {
							int neutralColorsNeeded = routeWeight - blackCount;
							for (int i = 0; i < orangeCount; i++) {
								tempColors.add("ORANGE");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "PURPLE":
					if ((PURPLECount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || PURPLECount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("PURPLE");
							}
						} else {
							int neutralColorsNeeded = routeWeight - PURPLECount;
							for (int i = 0; i < PURPLECount; i++) {
								tempColors.add("PURPLE");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "RED":
					if ((redCount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || redCount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("RED");
							}
						} else {
							int neutralColorsNeeded = routeWeight - redCount;
							for (int i = 0; i < redCount; i++) {
								tempColors.add("RED");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "WHITE":
					if ((whiteCount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || whiteCount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("WHITE");
							}
						} else {
							int neutralColorsNeeded = routeWeight - whiteCount;
							for (int i = 0; i < whiteCount; i++) {
								tempColors.add("WHITE");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "YELLOW":
					if ((yellowCount + neutralCount) >= routeWeight) {
						canClaim = true;
						if (neutralCount == 0 || yellowCount == routeWeight) {
							for (int i = 0; i < routeWeight; i++) {
								tempColors.add("YELLOW");
							}
						} else {
							int neutralColorsNeeded = routeWeight - yellowCount;
							for (int i = 0; i < yellowCount; i++) {
								tempColors.add("YELLOW");
							}
							for (int i = 0; i < neutralColorsNeeded; i++) {
								tempColors.add("NEUTRAL");
							}
						}
					}
					break;
				case "GREY":
					JFrame jfTrainCard = new JFrame();
					JPanel jpTrainCard = new JPanel();
					JPanel buttonPanel = new JPanel();
					ButtonGroup bgroup = new ButtonGroup();

					boolean displayFrame = true;
					int matching = 0;

					Set<String> keys = trainColorCountList.keySet();
					for (String colorCount : keys) {
							if ((trainColorCountList.get(colorCount)) >= routeWeight) {
								canClaim = true;
								System.out.println("in first");
								JRadioButton jrbColorCount = new JRadioButton(colorCount);
								jrbColorCount.setActionCommand(colorCount);
								bgroup.add(jrbColorCount);
								jpTrainCard.add(jrbColorCount);
								matching += 1;
							}
							else if ((trainColorCountList.get(colorCount) + neutralCount) >= routeWeight) {
								System.out.println("in second");
								canClaim = true;
								JRadioButton jrbColorCount = new JRadioButton(colorCount + " and NEUTRAL");
								jrbColorCount.setActionCommand(colorCount + " and NEUTRAL");
								bgroup.add(jrbColorCount);
								jpTrainCard.add(jrbColorCount);
								matching += 1;
							}
					}

					if (matching == 0) {
						displayFrame = false;
					}

					JButton jbOK = new JButton("Select");
					buttonPanel.add(jbOK);

					// Action listener for the okay button
					jbOK.addActionListener(
						e -> {
							// Getting the choosen button in the group
							String selectedText = bgroup.getSelection().getActionCommand();
							// Getting the main color to take from 
							String[] jrbSplitSelection = selectedText.split(" ");
							String mainColor = jrbSplitSelection[0];
							int mainNumber = 0;
							// Finding the main color in the arralylist of colors
							Set<String> jrbKeys = trainColorCountList.keySet();
							for (String currentCount : jrbKeys) {
									if (currentCount.equals(mainColor)) {
										mainNumber = trainColorCountList.get(currentCount);
									}
							}	
							if (selectedText.contains("NEUTRAL")) {
								int neutralColorsNeeded = routeWeight - mainNumber;
								for (int i = 0; i < mainNumber; i++) {
									tempColors.add(mainColor);
								}
								for (int i = 0; i < neutralColorsNeeded; i++) {
									tempColors.add("NEUTRAL");
								}
							} else {
								for (int i = 0; i < routeWeight; i++) {
									tempColors.add(mainColor);
								}
							}
							jfTrainCard.dispose();

							// Adding to the temp color
							try {
								stub.tempStoreTrainColorCards(tempColors);
							} catch (RemoteException re) { }
						});

					jfTrainCard.add(buttonPanel, BorderLayout.SOUTH);
					jfTrainCard.add(jpTrainCard, BorderLayout.CENTER);

					System.out.println("Display" + displayFrame);
					if (displayFrame) {
						jfTrainCard.pack();
						jfTrainCard.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						jfTrainCard.setVisible(true);
						jfTrainCard.setLocationRelativeTo(null);
					}

					break;
			}

			// Adding to the temp color
			try {
				stub.tempStoreTrainColorCards(tempColors);
			} catch (RemoteException re) { }

			return canClaim;

      }
   } // end MouseListener