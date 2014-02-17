package scn;

import java.io.File;

import lib.RandomNumber;
import lib.jog.audio;
import lib.jog.graphics;
import lib.jog.input;
import lib.jog.window;
import cls.Aircraft;
import cls.Airport;
import cls.AirportControlBox;
import cls.Vector;
import cls.Waypoint;
import btc.Main;

public class Demo extends Scene {
	
	// Position of things drawn to window   
	private final int PLANE_INFO_X = 16;
	private final int PLANE_INFO_Y = window.height() - 120;
	private final int PLANE_INFO_W = window.width()/4 - 16;
	private final int PLANE_INFO_H = 112;
	
	private final int ALTIMETER_X = PLANE_INFO_X + PLANE_INFO_W + 8;
	private final int ALTIMETER_Y = window.height() - 120;
	private final int ALTIMETER_W = 244;
	private final int ALTIMETER_H = 112;
	
	private final int AIRPORT_CONTROL_X = ALTIMETER_X + ALTIMETER_W + 8;
	private final int AIRPORT_CONTROL_Y = window.height() - 120;
	private final int AIRPORT_CONTROL_W = 244;
	private final int AIRPORT_CONTROL_H = 112;
	
	private final int ORDERSBOX_X = AIRPORT_CONTROL_X + AIRPORT_CONTROL_W + 8;
	private final static int ORDERSBOX_Y = window.height() - 120;
	private final int ORDERSBOX_W = window.width() - (ORDERSBOX_X + 16);
	private final static int ORDERSBOX_H = 112;
	
	// Static Final Ints for difficulty settings
	// Difficulty of demo scene determined by difficulty selection scene
	public final static int DIFFICULTY_EASY = 0;
	public final static int DIFFICULTY_MEDIUM = 1;
	public final static int DIFFICULTY_HARD = 2;
	public static int difficulty = DIFFICULTY_EASY;
	
	// Necessary for testing
	
	/**
	 * This method should only be used for unit testing (avoiding instantiation of main class). Its purpose is to initialize array where
	 * aircraft are stored. 
	 */	
	@Deprecated
	public void initializeAircraftArray() {
		aircraftInAirspace = new java.util.ArrayList<Aircraft>();
	}
	
	// Additional constructor for testing purposes
	 
	/**
	 * This constructor should only be used for unit testing. Its purpose is to allow an instance
	 * of demo class to be created without an instance of Main class (effectively launching the game)
	 * @param difficulty
	 */	
	@Deprecated
	public Demo(int difficulty) {
		Demo.difficulty = difficulty;
	}
	
	// Scoring bit
	
	/**
	 * Records the total score the user has achieved at a given time.
	 */	
	private int totalScore = 0;
	
	/**
	 * Getter for total score in case it is needed outside the Demo class.
	 * @return totalScore
	 */	
	public int getTotalScore() {
		return totalScore;
	}
	
	/**
	 * Allows for increasing total score outside of Demo class.
	 * @param scoreDifference
	 */	
	public void increaseTotalScore(int amount) {
		if (amount > 0)
			totalScore += amount;
	}
	
	/**
	 * Initially set to 1. This is the main multiplier for score. As more planes leave airspace 
	 * it may be incremented based on the value of multiplierVariable (the interval it is currently in).
	 */	
	public int multiplier = 1; 
	
	/**
	 * Allows to reset multiplier to 1.
	 */	
	public void resetMultiplier() {
		this.multiplier = 1;
	} 
	
	/**
	 * This variable is used to increase main multiplier for score. Score multiplier varies based on 
	 * the immediate range this variable is in. I.e. When it is < 10 -> multiplier = 1, when 
	 *  10 <= multiplierVariable < 40 -> multiplier = 2, etc. 
	 */
	private int multiplierVariable = 0;
	
	/**
	 * Used to get multiplierVariable outside of Demo class.
	 * @return multiplierVariable
	 */	
	public int getMultiplierVariable() {
		return multiplierVariable;
	}
	
	// Necessary for testing
		
	/**
	 * This method should only be used publically for unit testing. Its purpose is to update multiplierVariable
	 * outside of Demo class. 
	 * @param difference
	 */
	public void increaseMultiplierVariable(int difference) {
		multiplierVariable += difference;
		updateMultiplier();
	}
	
	public void decreaseMultiplierVariable(int difference) {
		if (difference > multiplierVariable) {
			multiplierVariable = 0;
		} else {
			multiplierVariable -= difference;
		}
		updateMultiplier();
	}
		
	/**
	 * Updates multiplier based on the value of multiplierVariable and the interval it is 
	 * currently in.
	 */		
	private void updateMultiplier() {
		if (multiplierVariable < 10) {
			if (multiplierVariable < 0)
				multiplierVariable = 0;
			multiplier = 1;
		}
		else if (multiplierVariable < 40) { 
			multiplier = 3;
		}
		else if (multiplierVariable < 80) {
			multiplier = 5;
		}
		else if (multiplierVariable < 130) { 
			multiplier = 7;
		}
		else {
			multiplier = 10;
		}
	}
	
	/**
	 * Has the user been shown the message for leaving the aircraft in the hangar too long? 
	 */	
	private boolean shownAircraftWaitingMessage = false;
	
	/**
	 * Orders box to print orders from ACTO to aircraft to
	 */
	private cls.OrdersBox ordersBox;
	
	/**
	 * Time since the scene began
	 * Could be used for score
	 */
	private static double timeElapsed;
	/**
	 * The currently selected aircraft
	 */
	private Aircraft selectedAircraft;
	/**
	 * The currently selected waypoint
	 */
	private Waypoint selectedWaypoint;
	/**
	 * Selected path point, in an aircraft's route, used for altering the route
	 */
	private int selectedPathpoint;
	/**
	 * A list of aircraft present in the airspace
	 */
	public static java.util.ArrayList<Aircraft> aircraftInAirspace;
	
	/**
	 * An image to be used for aircraft
	 * Expand to list of images for multiple aircraft appearances
	 */
	private graphics.Image aircraftImage;
	
	/**
	 * A button to start and end manual control of an aircraft
	 */
	private lib.ButtonText manualOverrideButton;
	/**
	 * Tracks if manual heading compass of a manually controller aircraft has been dragged
	 */
	private boolean compassDragged;
	/**
	 * An altimeter to display aircraft altitidue, heading, etc.
	 */
	private cls.Altimeter altimeter;
	/**
	 * The interval in seconds to generate flights after
	 */
	private cls.AirportControlBox airport_control_box;
	private  double flightGenerationInterval = 60 /getMaxAircraft();
	/**
	 * The time eleapsed since the last flight was generated
	 */
	private double flightGenerationTimeElapsed = 6;
	
	/**
	 * This method provides maximum number of planes using value of multiplier
	 * @return maximum number of planes
	 */
	private int getMaxAircraft() {
		if (multiplier == 1) 
			return 3;
		else
			return multiplier;
	}
	/**
	 * The current control altitude of the ACTO
	 * initially 30,000
	 * only aircraft on or close to this altitude can be controlled
	 */
	private int controlAltitude = 30000;
	
	/**
	 * Music to play during the game scene
	 */
	private audio.Music music;
	/**
	 * The background to draw in the airspace.
	 */
	private graphics.Image background;
	
	/**
	 * Demo's instance of the airport class
	 */
	public static Airport airport = new Airport();
	
	/**
	 * A list of location names for waypoint flavour
	 */
	private final String[] LOCATION_NAMES = new String[] {
		"North West Top Leftonia",
		"100 Acre Woods",
		"City of Rightson",
		"South Sea",
		airport.name
	};
	
	/**
	 * The set of waypoints in the airspace which are origins / destinations
	 */
	public static Waypoint[] locationWaypoints = new Waypoint[] {
		/* A set of Waypoints which are origin / destination points */
		new Waypoint(8, 8, true), //top left
		new Waypoint(8, window.height() - ORDERSBOX_H - 72, true), //bottom left
		new Waypoint(window.width() - 40, 8, true), // top right
		new Waypoint(window.width() - 40, window.height() - ORDERSBOX_H - 72, true), //bottom right
		airport
	};

	/**
	 * All waypoints in the airspace, INCLUDING locationWaypoints.
	 */
	public static Waypoint[] airspaceWaypoints = new Waypoint[] {		
		/* All waypoints in the airspace, including location Way Points*/
	
		// Airspace waypoints
		new Waypoint(125, 70, false),   // 0
		new Waypoint(700, 100, false),  // 1
		new Waypoint(1040, 80, false),  // 2
		new Waypoint(670, 400, false),  // 3
		new Waypoint(1050, 400, false), // 4
		new Waypoint(250, 400, false),  // 5
		new Waypoint(200, 635, false),  // 6
		new Waypoint(500, 655, false),  // 7
		new Waypoint(800, 750, false),  // 8
		new Waypoint(1000, 750, false), // 9
		// Destination/origin waypoints - present in this list for pathfinding.
		locationWaypoints[0],           // 10
		locationWaypoints[1],           // 11
		locationWaypoints[2],           // 12
		locationWaypoints[3],           // 13
		locationWaypoints[4]			// 14
	};
	/**
	 * Constructor
	 * @param main the main containing the scene
	 * @param difficulty the difficulty the scene is to be initialised with
	 */
	public Demo(Main main, int difficulty) {
		super(main);
		Demo.difficulty = difficulty;
	}
	
	@Override
	/**
	 * Initialise and begin music, init background image and scene variables.
	 * Shorten flight generation timer according to difficulty
	 */
	public void start() {
		background = graphics.newImage("gfx" + File.separator + "map.png");
		music = audio.newMusic("sfx" + File.separator + "Gypsy_Shoegazer.ogg");
		music.play();
		ordersBox = new cls.OrdersBox(ORDERSBOX_X, ORDERSBOX_Y, ORDERSBOX_W, ORDERSBOX_H, 6);
		aircraftInAirspace = new java.util.ArrayList<Aircraft>();
		aircraftImage = graphics.newImage("gfx" + File.separator + "plane.png");
		lib.ButtonText.Action manual = new lib.ButtonText.Action() {
			@Override
			public void action() {
				// _selectedAircraft.manuallyControl();
				toggleManualControl();
			}
		};
		manualOverrideButton = new lib.ButtonText("Take Control", manual, (window.width() - 128) / 2, 32, 128, 64, 8, 4);
		timeElapsed = 0;
		compassDragged = false;
		selectedAircraft = null;
		selectedWaypoint = null;
		selectedPathpoint = -1;
		
		manualOverrideButton = new lib.ButtonText(" Take Control", manual, (window.width() - 128) / 2, 32, 128, 64, 8, 4);
		altimeter = new cls.Altimeter(ALTIMETER_X, ALTIMETER_Y, ALTIMETER_W, ALTIMETER_H);
		airport_control_box = new AirportControlBox(AIRPORT_CONTROL_X, AIRPORT_CONTROL_Y, AIRPORT_CONTROL_W, AIRPORT_CONTROL_H, airport);
		deselectAircraft();
	}
	
	/**
	 * Getter for aircraft list
	 * @return the arrayList of aircraft in the airspace
	 */
	public java.util.ArrayList<Aircraft> aircraftList() {
		return aircraftInAirspace;
	}
	
	/**
	 * Causes a selected aircraft to call methods to toggle manual control
	 */
	private void toggleManualControl() {
		if (selectedAircraft == null) return;
		selectedAircraft.toggleManualControl();
		manualOverrideButton.setText( (selectedAircraft.isManuallyControlled() ? "Remove" : " Take") + " Control");
	}
	
	/**
	 * Causes an aircraft to call methods to handle deselection
	 */
	private void deselectAircraft() {
		if (selectedAircraft != null && selectedAircraft.isManuallyControlled()) {
			selectedAircraft.toggleManualControl();
			manualOverrideButton.setText(" Take Control");
		}
		selectedAircraft = null;
		selectedWaypoint = null; 
		selectedPathpoint = -1;
		altimeter.hide();
	}
	
	/**
	 * Update all objects within the scene, ie aircraft, orders box altimeter.
	 * Cause collision detection to occur
	 * Generate a new flight if flight generation interval has been exceeded.
	 */
	@Override
	public void update(double time_difference) {
		timeElapsed += time_difference;
		
		if (airport.getLongestTimeInHangar(timeElapsed) > 5) {
			decreaseMultiplierVariable(2);
			if (!shownAircraftWaitingMessage) {
				ordersBox.addOrder(">>> Plane waiting to take off, multiplier decreasing");
				shownAircraftWaitingMessage = true;
			}
		} else {
			shownAircraftWaitingMessage = false;
		}
		
		ordersBox.update(time_difference);
		for (Aircraft plane : aircraftInAirspace) {
			plane.update(time_difference);
			if (plane.isFinished()) {
				increaseMultiplierVariable(plane.getPlaneBonusToMultiplier());
				double effiencyBonus =  Aircraft.efficiencyBonus(plane.getOptimalTime(), System.currentTimeMillis()/1000 - plane.getTimeOfCreation()); // Bonus multiplier to score of a particular plane based on its performance
				increaseTotalScore ((int)(multiplier * plane.getBaseScore() * effiencyBonus));
				System.out.println("Optimal time :" + plane.getOptimalTime() + "; Actual time spent: " + (System.currentTimeMillis()/1000 - plane.getTimeOfCreation())); // For debugging
				System.out.println("Total score: " + totalScore + "; Multiplier: " + multiplier + "; multiplierVariable: " + multiplierVariable + "\n "); // For debugging
				if (plane.getPlaneBonusToMultiplier() < 0)
					ordersBox.addOrder("<<< The plane has breached separation rules on its path, your multiplier may be reduced ");
				int totalEfficiencyBonus = (int) ((multiplier * plane.getBaseScore() * effiencyBonus) - multiplier * plane.getBaseScore()); // Used to show how many points were scored just for being efficient
				
				switch (RandomNumber.randInclusiveInt(0, 2)){
				case 0:
					ordersBox.addOrder("<<< Thank you Comrade");
					break;
				case 1:
					ordersBox.addOrder("<<< Well done Comrade");
					break;
				case 2:
					ordersBox.addOrder("<<< Many thanks Comrade");
					break;
				}
				ordersBox.addOrder("Plane successfully left airspace, bonus points: " + plane.getBaseScore() * multiplier);
				if (effiencyBonus > 1)
					ordersBox.addOrder("<<< Congrats, you scored extra " + totalEfficiencyBonus  + " points for efficiency!");
			}
		}
		checkCollisions(time_difference);
		for (int i = aircraftInAirspace.size()-1; i >=0; i --) {
			if (aircraftInAirspace.get(i).isFinished()) {
				if (aircraftInAirspace.get(i) == selectedAircraft) {
					deselectAircraft();
				}
				aircraftInAirspace.remove(i);
			}
		}
		altimeter.update(time_difference);
		airport.update(this);
		if (selectedAircraft != null && selectedAircraft.isManuallyControlled()) {
			if (input.isKeyDown(input.KEY_S)|| input.isKeyDown(input.KEY_DOWN)) {
				selectedAircraft.setAltitudeState(Aircraft.ALTITUDE_FALL);
			} else if (input.isKeyDown(input.KEY_W)|| input.isKeyDown(input.KEY_UP))
				selectedAircraft.setAltitudeState(Aircraft.ALTITUDE_CLIMB);
			if (input.isKeyDown(input.KEY_LEFT) || input.isKeyDown(input.KEY_A)) {
				selectedAircraft.turnLeft(time_difference);
			} else if (input.isKeyDown(input.KEY_RIGHT) || input.isKeyDown(input.KEY_D)) 
				selectedAircraft.turnRight(time_difference);
			if (selectedAircraft.isOutOfBounds()) {
				ordersBox.addOrder(">>> " + selectedAircraft.getName() + " out of bounds, returning to route");
				deselectAircraft();
			}
		}
		
		flightGenerationTimeElapsed += time_difference;
		if(flightGenerationTimeElapsed >= flightGenerationInterval){
			flightGenerationTimeElapsed -= flightGenerationInterval;
			if (aircraftInAirspace.size() < getMaxAircraft()){
				generateFlight();
			}
		}
		if (aircraftInAirspace.size() == 0)
			generateFlight();
	}
	
	/**
	 * Cause all planes in airspace to update collisions
	 * Catch and handle a resultant game over state
	 * @param time_difference delta time since last collision check
	 */
	private void checkCollisions(double time_difference) {
		for (Aircraft plane : aircraftInAirspace) {
			int collisionState = plane.updateCollisions(time_difference, aircraftList());
			if (collisionState >= 0) {
				gameOver(plane, aircraftList().get(collisionState));
				return;
			}
		}
	}
	
	@Override
	public void playSound(audio.Sound sound){
		sound.stop();
		sound.play();
	}
	
	/**
	 * Handle a game over caused by two planes colliding
	 * Create a gameOver scene and make it the current scene
	 * @param plane1 the first plane involved in the collision
	 * @param plane2 the second plane in the collision
	 */
	public void gameOver(Aircraft plane1, Aircraft plane2) {
		aircraftInAirspace.clear();
		airport.clear();
		playSound(audio.newSoundEffect("sfx" + File.separator + "crash.ogg"));
		main.closeScene();
		main.setScene(new GameOver(main, plane1, plane2));
	}
	
	/**
	 * Causes the scene to pause execution for the specified number of seconds
	 * @param seconds the number of seconds to wait.
	 */
	@Deprecated
	public void wait(int seconds){
		long startTime, endTime;
		startTime = System.currentTimeMillis();
		endTime = startTime + (seconds * 1000);
		
		while (startTime < endTime){
			startTime = System.currentTimeMillis();
		}
		
		return;
	}

	/**
	 * Handle mouse input
	 */
	@Override
	public void mousePressed(int key, int x, int y) {
		if (key == input.MOUSE_LEFT) {
			airport_control_box.mousePressed(key, x, y);
			airport.mousePressed(key, x, y);
			Aircraft newSelected = selectedAircraft;
			for (Aircraft a : aircraftInAirspace) {
				if (a.isMouseOver(x-16, y-48)) {
					newSelected = a;
					if (!a.isManuallyControlled())
						toggleManualControl();
				}
				
				if (airport.isWithinRadius(new Vector(x, y, 0)) && !airport.is_active && a.currentTarget.equals(airport.getWaypointLocation()) && a.is_waiting_to_land) {
					a.land();
				}
			}
			if (newSelected != selectedAircraft) {
				deselectAircraft();
				selectedAircraft = newSelected;
			}
			altimeter.show(selectedAircraft);
			if (selectedAircraft != null) {
				for (Waypoint w : airspaceWaypoints) {
					if (w.isMouseOver(x-16, y-48) && selectedAircraft.flightPathContains(w) > -1) {
						selectedWaypoint = w;
						selectedPathpoint = selectedAircraft.flightPathContains(w);
					}
				}
				if (selectedWaypoint == null && selectedAircraft.isManuallyControlled()) {
					// If mouse is over compass
					double dx = selectedAircraft.position().getX() - input.mouseX();
					double dy = selectedAircraft.position().getY() - input.mouseY();
					int r = Aircraft.COMPASS_RADIUS;
					if (dx*dx + dy*dy < r*r) {
						compassDragged = true;
					}
				}
			}
			
		}
		if (key == input.MOUSE_RIGHT) deselectAircraft();
		altimeter.mousePressed(key, x, y);
	}

	@Override
	public void mouseReleased(int key, int x, int y) {
		airport_control_box.mouseReleased(key, x, y);
		airport.mouseReleased(key, x, y);
		if (selectedAircraft != null && manualOverrideButton.isMouseOver(x, y)) manualOverrideButton.act();
		if (key == input.MOUSE_LEFT && selectedWaypoint != null) {
			if (selectedAircraft.isManuallyControlled() == true){
				return;
			} else {
				for (Waypoint w : airspaceWaypoints) {
					if (w.isMouseOver(x-16, y-48) && !w.isEntryOrExit()) {
						selectedAircraft.alterPath(selectedPathpoint, w);
						ordersBox.addOrder(">>> " + selectedAircraft.getName() + " please alter your course");
						ordersBox.addOrder("<<< Roger that. Altering course now.");
						selectedPathpoint = -1;
						selectedWaypoint = null;
					} else {
						selectedWaypoint = null;
					}
				}
			}
		}
		if (key == input.MOUSE_WHEEL_UP && controlAltitude < 30000)	controlAltitude += 2000;
		if (key == input.MOUSE_WHEEL_DOWN && controlAltitude > 28000) controlAltitude -= 2000;
		
		int altitudeState = 0;
		if (selectedAircraft != null) {
			altitudeState = selectedAircraft.getAltitudeState();
		}
		altimeter.mouseReleased(key, x, y);
		if (selectedAircraft != null) {
			if (altitudeState != selectedAircraft.getAltitudeState()) {
				ordersBox.addOrder(">>> " + selectedAircraft.getName() + ", please adjust your altitude");
				ordersBox.addOrder("<<< Roger that. Altering altitude now.");
			}
		}

		if (compassDragged && selectedAircraft != null) {
			double dx = input.mouseX() - selectedAircraft.position().getX();
			double dy = input.mouseY() - selectedAircraft.position().getY();
			double newHeading = Math.atan2(dy, dx);
			selectedAircraft.setBearing(newHeading);
		}
		compassDragged = false;
	}

	@Override
	public void keyPressed(int key) {
	}

	@Override
	/**
	 * handle keyboard input
	 */
	public void keyReleased(int key) {
		switch (key) {
		
			case input.KEY_SPACE :
				toggleManualControl();
			break;
			
			case input.KEY_LCRTL :
				generateFlight();
			break;
			
			case input.KEY_ESCAPE :
				aircraftInAirspace.clear();
				airport.clear();
				main.closeScene();
			break;
			
			case input.KEY_F5 :
				Aircraft a1 = createAircraft();
				Aircraft a2 = createAircraft();
				gameOver(a1, a2);
			break;
		}
	}
	
	/**
	 * Draw the scene GUI and all drawables within it, e.g. aircraft and waypoints
	 */
	@Override
	public void draw() {
		graphics.setColour(0, 128, 0);
		graphics.rectangle(false, 16, 48, window.width() - 32, window.height() - 176);
		
		graphics.setViewport(16, 48, window.width() - 32, window.height() - 176);
		graphics.setColour(255, 255, 255, 32);
		graphics.draw(background, 0, 0);
		drawMap();		
		graphics.setViewport();
		
		if (selectedAircraft != null && selectedAircraft.isManuallyControlled()) {
			selectedAircraft.drawCompass();
		}
		
		airport.drawAirportIcon();		
		ordersBox.draw();
		altimeter.draw();
		airport_control_box.draw();
		drawPlaneInfo();
		
		graphics.setColour(0, 128, 0);
		drawScore();
	}
	
	/**
	 * draw waypoints, and route of a selected aircraft between waypoints
	 * print waypoint names next to waypoints
	 */
	private void drawMap() {
		for (Waypoint waypoint : airspaceWaypoints) {
			if (!waypoint.equals(airport)) { // Skip the airport
				waypoint.drawAirportIcon();
			}
		}
		graphics.setColour(255, 255, 255);
		for (Aircraft aircraft : aircraftInAirspace) {
			aircraft.draw(controlAltitude);
			if (aircraft.isMouseOver()) {
				aircraft.drawFlightPath(false);
			}
		}
		
		if (selectedAircraft != null) {
			// Flight Path
			selectedAircraft.drawFlightPath(true);
			graphics.setColour(0, 128, 0);
			// Override Button
			graphics.setColour(0, 0, 0);
			graphics.rectangle(true, (window.width() - 128) / 2, 16, 128, 32);
			graphics.setColour(0, 128, 0);
			graphics.rectangle(false, (window.width() - 128) / 2, 16, 128, 32);
			manualOverrideButton.draw();
			
			selectedAircraft.drawFlightPath(true);
			graphics.setColour(0, 128, 0);
			
		}
		
		if (selectedWaypoint != null && selectedAircraft.isManuallyControlled() == false) {
			selectedAircraft.drawModifiedPath(selectedPathpoint, input.mouseX() - 16, input.mouseY() - 48);
		}
		
		graphics.setViewport();
		graphics.setColour(0, 128, 0);
		graphics.print(LOCATION_NAMES[0], locationWaypoints[0].getWaypointLocation().getX() + 25, locationWaypoints[0].getWaypointLocation().getY() + 42);
		graphics.print(LOCATION_NAMES[1], locationWaypoints[1].getWaypointLocation().getX() + 25, locationWaypoints[1].getWaypointLocation().getY() + 42);
		graphics.print(LOCATION_NAMES[2], locationWaypoints[2].getWaypointLocation().getX() - 125, locationWaypoints[2].getWaypointLocation().getY() + 42);
		graphics.print(LOCATION_NAMES[3], locationWaypoints[3].getWaypointLocation().getX() - 75, locationWaypoints[3].getWaypointLocation().getY() + 42);

	}
	
	/**
	 * draw the info of a selected plane in the scene GUI
	 */
	private void drawPlaneInfo() {
		graphics.setColour(0, 128, 0);
		graphics.rectangle(false, PLANE_INFO_X, PLANE_INFO_Y, PLANE_INFO_W, PLANE_INFO_H);
		if (selectedAircraft != null) {
			graphics.setViewport(PLANE_INFO_X, PLANE_INFO_Y, PLANE_INFO_W, PLANE_INFO_H);
			graphics.printCentred(selectedAircraft.getName(), 0, 5, 2, PLANE_INFO_W);
			// Altitude
			String altitude = String.format("%.0f", selectedAircraft.position().getZ()) + "£";
			graphics.print("Altitude:", 10, 40);
			graphics.print(altitude, PLANE_INFO_W - 10 - altitude.length()*8, 40);
			// Speed
			String speed = String.format("%.2f", selectedAircraft.getSpeed() * 1.687810) + "$";
			graphics.print("Speed:", 10, 55);
			graphics.print(speed, PLANE_INFO_W - 10 - speed.length()*8, 55);
			// Origin
			graphics.print("Origin:", 10, 70);
			graphics.print(selectedAircraft.getOriginName(), PLANE_INFO_W - 10 - selectedAircraft.getOriginName().length()*8, 70);
			// Destination
			graphics.print("Destination:", 10, 85);
			graphics.print(selectedAircraft.getDestinationName(), PLANE_INFO_W - 10 - selectedAircraft.getDestinationName().length()*8, 85);
			graphics.setViewport();
		}
	}
	
	/**
	 * draw a readout of the time the game has been played for, aircraft in the sky, etc.
	 * Hint: for assessment 3, this could be used to print the player's current score.
	 */
	private void drawScore() {
		int hours = (int)(timeElapsed / (60 * 60));
		int minutes = (int)(timeElapsed / 60);
		minutes %= 60;
		double seconds = timeElapsed % 60;
		java.text.DecimalFormat df = new java.text.DecimalFormat("00.00");
		String timePlayed = String.format("%d:%02d:", hours, minutes) + df.format(seconds); 
		graphics.print(timePlayed, window.width() - (timePlayed.length() * 8 + 32), 0);
		int planes = aircraftInAirspace.size();
		graphics.print(String.valueOf(aircraftInAirspace.size()) + " plane" + (planes == 1 ? "" : "s") + " in the sky.", 32, 0);
		graphics.print("Control Altitude: " + String.valueOf(controlAltitude), 544, 0);
	}
	
	/**
	 * Create a new aircraft object and introduce it to the airspace
	 */
	private void generateFlight() {
		Aircraft a = createAircraft();
		if (a.getOriginName().equals(airport.name)) {
			ordersBox.addOrder("<<< " + a.getName() + " is awaiting take off from " + a.getOriginName() + " heading towards " + a.getDestinationName() + ".");
			airport.addToHangar(a);
		} else {
			ordersBox.addOrder("<<< " + a.getName() + " incoming from " + a.getOriginName() + " heading towards " + a.getDestinationName() + ".");
			aircraftInAirspace.add(a);
		}
	}
	
	public static void takeOffSequence(Aircraft aircraft) {
		aircraftInAirspace.add(aircraft);
		// Space to implement some animation features?
		airport.is_active = false;
	}
	
	/**
	 * Handle nitty gritty of aircraft creating
	 * including randomisation of entry, exit, altitude, etc.
	 * @return the create aircraft object
	 */
	private Aircraft createAircraft() {
		// Origin and Destination
		int o = RandomNumber.randInclusiveInt(0, locationWaypoints.length - 1);
		int d = RandomNumber.randInclusiveInt(0, locationWaypoints.length - 1);
		while (LOCATION_NAMES[d] == LOCATION_NAMES[o]){
			d = RandomNumber.randInclusiveInt(0, locationWaypoints.length - 1);
		}
		String originName = LOCATION_NAMES[o];
		String destinationName = LOCATION_NAMES[d];
		Waypoint originPoint = locationWaypoints[o];
		Waypoint destinationPoint = locationWaypoints[d];
		
		// Name
		String name = "";
		boolean nameTaken = true;
		while (nameTaken) {
			name = "Flight " + (int)(900 * Math.random() + 100);
			nameTaken = false;
			for (Aircraft a : aircraftInAirspace) {
				if (a.getName() == name) nameTaken = true;
			}
		}
		return new Aircraft(name, destinationName, originName, destinationPoint, originPoint, aircraftImage, 32 + (int)(10 * Math.random()), airspaceWaypoints, difficulty);
	}
	
	@Override
	/**
	 * cleanly exit by stopping the scene's music
	 */
	public void close() {
		music.stop();
	}

	public static double getTime() {
		return timeElapsed;
	}
}
