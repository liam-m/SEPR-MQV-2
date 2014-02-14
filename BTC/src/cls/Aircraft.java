package cls;

import java.io.File;
import java.util.ArrayList;

import scn.Demo;
import lib.RandomNumber;
import lib.jog.audio;
import lib.jog.graphics;
import lib.jog.input;
import lib.jog.window;



/**
 * <h1>Aircraft</h1>
 * <p>Represents an in-game aircraft. Calculates velocity, route-following, etc.</p>
 */
public class Aircraft {

	/**
	 * The physical size of the plane in pixels. This determines crashes.
	 */
	public final static int RADIUS = 16;
	/**
	 * How far away (in pixels) the mouse can be from the plane but still select it.
	 */
	public final static int MOUSE_LENIANCY = 32; 
	/**
	 * How large to draw the bearing circle.
	 */
	public final static int COMPASS_RADIUS = 64;
	/**
	 * How far (in pixels) planes have to be away to not cause a separation violation.
	 */
	public static int separationRule = 64;
	/**
	 * How much the plane can turn per second, in radians.
	 */
	private double turnSpeed;
	/**
	 * The position of the plane.
	 */
	private Vector position;
	/**
	 * The velocity of the plane.
	 */
	private Vector velocity;
	/**
	 * Whether the plane is being manually controlled.
	 */
	private boolean isManuallyControlled;
	/**
	 * The flight name of the plane.
	 */
	private String flightName;
	/**
	 * The position the plane is currently flying towards (if not manually controlled).
	 */
	public Vector currentTarget;
	/**
	 * The target the player has told the plane to fly at when manually controlled.
	 */
	private double manualBearingTarget;
	/**
	 * The name of the location the plane is flying from.
	 */
	private String originName;
	/**
	 * The name of the location the plane is flying to.
	 */
	private String destinationName;
	/**
	 * An array of waypoints from the plane's origin to its destination.
	 */
	private Waypoint[] route;
	/**
	 * Returns the array of waypoints specific for a given plane.
	 * @return route
	 */
	public Waypoint[] getRoute() {
		return route;
	}
	/**
	 * The current stage the plane is at in its route.
	 */
	private int currentRouteStage;
	/**
	 * The point the plane will end up at before being removed.
	 */
	public Vector destination;
	/**
	 * The image to be drawn representing the plane.
	 */
	private graphics.Image image;
	/**
	 * Aircraft is landing if it has reached the destination
	 * if its destination is the airport it must also have been given the land command
	 */
	private boolean hasFinished;
	/**
	 * Whether the aircraft is waiting to land.
	 * False if the destination is not the airport. 
	 * If the destination is the airport, True until land() is called.
	 */
	public boolean is_waiting_to_land;
	/**
	 * The angle (radians) the plane is currently turning by.
	 */
	private double currentlyTurningBy;
	/**
	 * Holds a list of planes currently in violation of separation rules with this plane
	 */
	private java.util.ArrayList<Aircraft> planesTooNear = new java.util.ArrayList<Aircraft>();
	/**
	 * The current state of the plane's altitude, i.e. if the plane is climbing or falling
	 */
	private int altitudeState;
	/**
	 * The speed to climb or fall by. Default 300 for easy mode
	 */
	private int altitudeChangeSpeed = 300;
	
	/**
	 * Used to calculate how long an aircraft spent in the airspace
	 */
	private double timeOfCreation;
	/**
	 * Used to get (system) time when an aircraft was created.
	 * @return Time when aircraft was created.
	 */
	public double getTimeOfCreation() {
		return timeOfCreation;
	}
	/**
	 * Optimal time a plane needs to reach its exit point 
	 */
	private double optimalTime;
	/**
	 * Getter for optimal time.
	 * @return Optimal time for an aircraft to complete its path.
	 */
	public double getOptimalTime() {
		return optimalTime;
	}
	
	/**
	 * Static ints for use where altitude state is to be changed.
	 */
	public static final int ALTITUDE_CLIMB = 1;
	public static final int ALTITUDE_FALL = -1;
	public static final int ALTITUDE_LEVEL = 0;
	
	/**
	 * Flags whether the collision warning sound has been played before.
	 * If set, plane will not play warning again until it the separation violation involving it ends
	 */
	private boolean collisionWarningSoundFlag = false;
	
	/**
	 * A warning sound to be played when the plane enters separation violation.
	 */
	private final static audio.Sound WARNING_SOUND = audio.newSoundEffect("sfx" + File.separator + "beep.ogg"); 
	
	//Scoring bit
	
	/**
	 * Each plane has its own base score that user improves their score by when a plane successfully
	 *leaves the airspace.
	 */
	private int baseScore;
	private int individualScore;
	
	/**
	 * This variable increases the multiplierVariable when a plane successfully leaves the airspace.
	 */
	private int additionToMultiplier = 1; 
	
	/**
	 * Used to get a base score per plane outside of Aircraft class.
	 * @return baseScore
	 */
	public int getBaseScore() {
		return baseScore;
	}
	
	/**
	 * Gets the score for a specific aircraft.
	 */
	public int getScore() {
		return individualScore;
	}
	
	/**
	 * Sets the score for a specific aircraft.
	 */
	public void setScore(int score) {
		individualScore = score;
	}
	
	/**
	 * Used to get a planeBonusToMultiplier outside of Aircraft class.
	 * @return planeBonusToMultiplier
	*/
	public int getAdditionToMultiplier() {
		return additionToMultiplier;
	}
	
	/**
	 * Used to set planeBonusToMultiplier outside of Aircraft class.
	 * @param number
	 */
	public void setAdditionToMultiplier(int multiplierLevel) {
		switch (multiplierLevel) {
		case 1:
			additionToMultiplier = 64;
			break;
		case 2:
			additionToMultiplier = 32;
			break;
		case 3:
			additionToMultiplier = 32;
			break;
		case 4:
			additionToMultiplier = 16;
			break;
		case 5:
			additionToMultiplier = 8;
			break;
		}
	}
	
	/**
	 * Constructor for an aircraft.
	 * @param name the name of the flight.
	 * @param nameOrigin the name of the location from which the plane hails.
	 * @param nameDestination the name of the location to which the plane is going.
	 * @param originPoint the point to initialise the plane.
	 * @param destinationPoint the end point of the plane's route.
	 * @param img the image to draw to represent the plane.
	 * @param speed the speed the plane will travel at.
	 * @param sceneWaypoints the waypoints on the map.
	 * @param difficulty the difficulty the game is set to
	 */
	public Aircraft(String name, String nameDestination, String nameOrigin, Waypoint destinationPoint, Waypoint originPoint, graphics.Image img, double speed, Waypoint[] sceneWaypoints, int difficulty) {
		flightName = name;
		destinationName = nameDestination;
		originName = nameOrigin;
		image = img;
		timeOfCreation = System.currentTimeMillis()/1000; // System time when aircraft was created in seconds.
		
		// Find route
		route = findGreedyRoute(originPoint, destinationPoint, sceneWaypoints);
		destination = destinationPoint.position();
		// Place on spawn waypoint
		position = originPoint.position(); 
		
		int altitudeOffset = RandomNumber.randInclusiveInt(0, 1) == 0 ? 28000 : 30000;
		position = position.add(new Vector(0, 0, altitudeOffset));
		
		// Calculate initial velocity (direction)
		currentTarget = route[0].position();
		double x = currentTarget.x() - position.x();
		double y = currentTarget.y() - position.y();
		velocity = new Vector(x, y, 0).normalise().scaleBy(speed);
		
		isManuallyControlled = false;
		hasFinished = false;
		is_waiting_to_land = destination.equals(Demo.airport.position());
		currentRouteStage = 0;
		currentlyTurningBy = 0;
		manualBearingTarget = Double.NaN; 
		
		// Speed up plane for higher difficulties
		switch (difficulty) {
			// Adjust the aircraft's attributes according to the difficulty of the parent scene
			// 0 has the easiest attributes (slower aircraft, more forgiving separation rules)
			// 2 has the hardest attributes (faster aircraft, least forgiving separation rules)
			case Demo.DIFFICULTY_EASY:
				separationRule = 64;
				turnSpeed = Math.PI / 4;
				altitudeChangeSpeed = 400;
				baseScore = 60;
				optimalTime = totalDistanceInFlightPlan()/speed;
			break;
			
			case Demo.DIFFICULTY_MEDIUM:
				separationRule = 96;
				velocity = velocity.scaleBy(2);
				turnSpeed = Math.PI / 3;
				altitudeChangeSpeed = 200;
				baseScore = 150;
				optimalTime = totalDistanceInFlightPlan()/(speed * 2);
			break;
			
			case Demo.DIFFICULTY_HARD:
				separationRule = 128;
				velocity = velocity.scaleBy(3);
				// At high velocities, the aircraft is allowed to turn faster
				// this helps keep the aircraft on track.
				turnSpeed = Math.PI / 2;
				altitudeChangeSpeed = 100;
				baseScore = 240;
				optimalTime = totalDistanceInFlightPlan()/(speed * 3);
			break;
			
			default :
				Exception e = new Exception("Invalid Difficulty : " + difficulty + ".");
				e.printStackTrace();
		}
	}

	/**
	 * Allows access to the plane's current position.
	 * @return the plane's current position.
	 */
	public Vector position() {
		return position;
	}
	
	/**
	 * Allows access to the plane's name.
	 * @return the plane's name.
	 */
	public String getName() {
		return flightName;
	}
	
	/**
	 * Allows access to the name of the location from which this plane hails.
	 * @return the origin's name.
	 */
	public String getOriginName() {
		return originName;
	}
	
	/**
	 * Allows access to the name of the location to which this plane travels.
	 * @return the destination's name.
	 */
	public String getDestinationName() {
		return destinationName;
	}
	
	/**
	 * Allows access to whether the plane has reached its destination.
	 * @return true, if the plane is to be disposed. False, otherwise.
	 */
	public boolean isFinished() {
		return hasFinished;
	}
	
	/**
	 * Allows access to whether the plane is being manually controlled.
	 * @return true, if the plane is currently manually controlled. False, otherwise.
	 */
	public boolean isManuallyControlled() {
		return isManuallyControlled;
	}
	
	public int getAltitudeState() {
		return altitudeState;
	}

	/**
	 * Calculates the angle from the plane's position, to its current target.
	 * @return an angle in radians to the plane's current target.
	 */
	private double angleToTarget() {
		if (isManuallyControlled) {
			return (manualBearingTarget == Double.NaN) ? getBearing() : manualBearingTarget;
		} else {
			return Math.atan2(currentTarget.y() - position.y(), currentTarget.x() - position.x());
		}
	}
	
	/**
	 * Checks whether the plane lies outside of the airspace.
	 * @return true, if the plane is out of the airspace. False, otherwise.
	 */
	public boolean isOutOfBounds() {
		double x = position.x();
		double y = position.y();
		return (x < RADIUS || x > window.width() + RADIUS - 32 || y < RADIUS || y > window.height() + RADIUS - 176);
	}

	/**
	 * Calculates the angle at which the plane is travelling.
	 * @return the angle in radians of the plane's current velocity.
	 */
	public double getBearing() {
		return Math.atan2(velocity.y(), velocity.x());
	}
	
	/**
	 * Allows access to the magnitude of the plane's velocity. 
	 * @return the speed at which the plane is currently going.
	 */
	public double getSpeed() {
		return velocity.magnitude();
	}
	
	/**
	 * 
	 * @param point
	 * @return true, if the plane is near enough the point. False, otherwise.
	 */
	public boolean isAt(Vector point) {
		double dy = point.y() - position.y();
		double dx = point.x() - position.x();
		return dy*dy + dx*dx < 4*4;
	}
	
	/**
	 * Checks whether the angle at which the plane is turning is less than 0.
	 * @return true, if the plane is turning left (anti-clockwise). False, otherwise.
	 */
	public boolean isTurningLeft() {
		return currentlyTurningBy < 0;
	}
	
	/**
	 * Checks whether the angle at which the plane is turning is greater than 0.
	 * @return true, if the plane is turning right (clockwise). False, otherwise.
	 */
	public boolean isTurningRight() {
		return currentlyTurningBy > 0;
	}
	
	/**
	 * Checks the plane's route to see if a waypoint is included in it.
	 * @param waypoint the waypoint to check for.
	 * @return true, if the waypoint is in the plane's route. False, otherwise.
	 */
	public int flightPathContains(Waypoint waypoint) {
		int index = -1;
		for (int i = 0; i < route.length; i ++) {
			if (route[i] == waypoint) index = i;
		}
		return index;
	}
	
	/**
	 * Edits the plane's path by changing the waypoint it will go to at a certain stage
	 * in its route.
	 * @param routeStage the stage at which the new waypoint will replace the old.
	 * @param newWaypoint the new waypoint to travel to.
	 */
	public void alterPath(int routeStage, Waypoint newWaypoint) {
		if (routeStage > -1) {
			route[routeStage] = newWaypoint;
			if (!isManuallyControlled) resetBearing();
			if (routeStage == currentRouteStage) {
				currentTarget = newWaypoint.position();
				turnTowardsTarget(0);
			}
		}
	}
	
	/**
	 * Checks whether the mouse cursor is over this plane.
	 * @param mx the x coordinate of the mouse cursor.
	 * @param my the y coordinate of the mouse cursor.
	 * @return true, if the mouse is close enough to this plane. False, otherwise.
	 */
	public boolean isMouseOver(int mx, int my) {
		double dx = position.x() - mx;
		double dy = position.y() - my;
		return dx*dx + dy*dy < MOUSE_LENIANCY*MOUSE_LENIANCY;
	}
	/**
	 * Calls {@link isMouseOver()} using {@link input.mouseX()} and {@link input.mouseY()} as the arguments.
	 * @return true, if the mouse is close enough to this plane. False, otherwise.
	 */
	public boolean isMouseOver() { return isMouseOver(input.mouseX() - 16, input.mouseY() - 48); }
	
	private boolean is_landing = false;
	
	/**
	 * Updates the plane's position and bearing, the stage of its route, and whether it has finished its flight.
	 * @param time_difference
	 */
	public void update(double time_difference) {
		if (hasFinished) return;
		
		if (is_landing) {
			position.setZ(position.z()-2529*time_difference);
		} else {
			switch (altitudeState) {
			case -1:
				fall();
				break;
			case 0:
				break;
			case 1:
				climb();
				break;
			}
		}
		
		// Update position
		Vector dv = velocity.scaleBy(time_difference);
		position = position.add(dv);
		
		currentlyTurningBy = 0;
		
		// Update target		
		if (isAt(currentTarget)) {
			if (currentTarget.equals(destination)) { // At destination
				if (!is_waiting_to_land) { // Ready to land
					hasFinished = true;
					if (destination.equals(Demo.airport.position())) { // Landed at airport
						Demo.airport.is_active = false;
					}	
				}
			} else { // At target but not destination
				currentRouteStage++;
				 // Next target is the destination if you're at the end of the plan, otherwise it's the next waypoint
				currentTarget = currentRouteStage >= route.length ? destination : route[currentRouteStage].position();
			}
		}

		// Update bearing
		if ( Math.abs(angleToTarget() - getBearing()) > 0.01 ) {
			turnTowardsTarget(time_difference);
		}
	}
	/**
	 * Turns the plane left.
	 * @param time_difference the time elapsed since the last frame.
	 */
	public void turnLeft(double time_difference) {
		turnBy(time_difference * -turnSpeed);
		manualBearingTarget = Double.NaN;
	}
	
	/**
	 * Turns the plane right.
	 * @param time_difference the time elapsed since the last frame.
	 */
	public void turnRight(double time_difference) {
		turnBy(time_difference * turnSpeed);
		manualBearingTarget = Double.NaN;
	}

	/**
	 * Turns the plane by a certain angle (in radians). Positive angles turn the plane clockwise.
	 * @param angle the angle by which to turn.
	 */
	private void turnBy(double angle) {
		currentlyTurningBy = angle;
		double cosA = Math.cos(angle);
		double sinA = Math.sin(angle);
		double x = velocity.x();
		double y = velocity.y();
		velocity = new Vector(x*cosA - y*sinA, y*cosA + x*sinA, velocity.z());
	}

	/**
	 * Turns the plane towards its current target. How much it turns is determined by the plane's
	 * {@link turnSpeed}.
	 * @param time_difference the time elapsed since the last frame.
	 */
	private void turnTowardsTarget(double time_difference) {
		// Get difference in angle
		double angleDifference = (angleToTarget() % (2 * Math.PI)) - (getBearing() % (2 * Math.PI));
		boolean crossesPositiveNegativeDivide = angleDifference < -Math.PI * 7 / 8;
		// Correct difference
		angleDifference += Math.PI;
		angleDifference %= (2 * Math.PI);
		angleDifference -= Math.PI;
		// Get which way to turn.
		int angleDirection = (int)(angleDifference /= Math.abs(angleDifference));
		if (crossesPositiveNegativeDivide) angleDirection *= -1;  
		double angleMagnitude = Math.min(Math.abs((time_difference * turnSpeed)), Math.abs(angleDifference)); 
		turnBy(angleMagnitude * angleDirection);
	}
	
	/**
	 * Draws the plane and any warning circles if necessary. 
	 */
	public void draw(int controlAltitude) {
		double alpha = 255/((Math.abs(position.z() - controlAltitude) + 1000)/1000);
		double scale = 2*(position.z()/30000);
		graphics.setColour(128, 128, 128, alpha);
		graphics.draw(image, scale, position.x()-image.width()/2, position.y()-image.height()/2, getBearing(), 8, 8);
		graphics.setColour(128, 128, 128, alpha/2.5);
		graphics.print(String.format("%.0f", position.z()) + "£", position.x()+8, position.y()-8);
		drawWarningCircles();
	}
	
	/**
	 * Draws the compass around this plane
	 */
	public void drawCompass() {
		graphics.setColour(0, 128, 0);
		Double xpos = position.x()-image.width()/2; // Centre position of aircraft
		Double ypos = position.y()-image.height()/2;
		graphics.circle(false, xpos + 16, ypos + 48, COMPASS_RADIUS, 30);
		for (int i = 0; i < 360; i += 60) {
			double r = Math.toRadians(i - 90);
			double x = xpos + 16 + (1.1 * COMPASS_RADIUS * Math.cos(r));
			double y = ypos + 46 + (1.1 * COMPASS_RADIUS * Math.sin(r));
			if (i > 170) x -= 24;
			if (i == 180) x += 12;
			graphics.print(String.valueOf(i), x, y);
		}
		double x, y;
		if (isManuallyControlled && input.isMouseDown(input.MOUSE_RIGHT)) {
			// Draw new bearing
			graphics.setColour(0, 128, 0, 128);
			double r = Math.atan2(input.mouseY() - position.y(), input.mouseX() - position.x());
			x = 16 + xpos + (COMPASS_RADIUS * Math.cos(r));
			y = 48 + ypos + (COMPASS_RADIUS * Math.sin(r));
			graphics.line(xpos + 16, ypos + 48, x, y);
			graphics.line(xpos + 15, ypos + 48, x, y);
			graphics.line(xpos + 16, ypos + 47, x, y);
			graphics.line(xpos + 17, ypos + 48, x, y);
			graphics.line(xpos + 17, ypos + 49, x, y);
			graphics.setColour(0, 128, 0, 16);
		}
		x = 16 + xpos + (COMPASS_RADIUS * Math.cos(getBearing()));
		y = 48 + ypos + (COMPASS_RADIUS * Math.sin(getBearing()));
		// Draw current bearing
		graphics.line(xpos + 16, ypos + 48, x, y);
		graphics.line(xpos + 15, ypos + 48, x, y);
		graphics.line(xpos + 16, ypos + 47, x, y);
		graphics.line(xpos + 17, ypos + 48, x, y);
		graphics.line(xpos + 17, ypos + 49, x, y);
		
	}
	
	/**
	 * Draws warning circles around this plane and any others that are too near.
	 */
	private void drawWarningCircles() {
		for (Aircraft plane : planesTooNear) {
			Vector midPoint = position.add(plane.position).scaleBy(0.5);
			double radius = position.sub(midPoint).magnitude() * 2;
			graphics.setColour(128, 0, 0);
			graphics.circle(false, midPoint.x(), midPoint.y(), radius);
		}	
	}

	/**
	 * Draws lines starting from the plane, along its flight path to its destination.
	 */
	public void drawFlightPath(boolean is_selected) {
		if (is_selected) {
			graphics.setColour(0, 128, 128);
		} else {
			graphics.setColour(0, 128, 128, 128);
		}
		
		if (currentTarget != destination) {
			graphics.line(position.x()-image.width()/2, position.y()-image.height()/2, route[currentRouteStage].position().x(), route[currentRouteStage].position().y());
		}
		for (int i = currentRouteStage; i < route.length-1; i++) {
			graphics.line(route[i].position().x(), route[i].position().y(), route[i+1].position().x(), route[i+1].position().y());	
		}
		if (currentTarget == destination) {
			graphics.line(position.x()-image.width()/2, position.y()-image.height()/2, destination.x(), destination.y());
		} else {
			graphics.line(route[route.length-1].position().x(), route[route.length-1].position().y(), destination.x(), destination.y());
		}
	}
	
	/**
	 * Visually represents the pathpoint being moved.
	 * @param mouseX current position of mouse
	 * @param mouseY current position of mouse
	 */
	public void drawModifiedPath(int modified, double mouseX, double mouseY) {
		graphics.setColour(0, 128, 128, 128);
		if (currentRouteStage > modified-1) {
			graphics.line(position().x(), position().y(), mouseX, mouseY);
		} else {
			graphics.line(route[modified-1].position().x(), route[modified-1].position().y(), mouseX, mouseY);
		}
		if (currentTarget == destination) {
			graphics.line(mouseX, mouseY, destination.x(), destination.y());
		} else {
			int index = modified + 1;
			if (index == route.length) { // Modifying final waypoint in route
				// Line drawn to final waypoint
				graphics.line(mouseX, mouseY, destination.x(), destination.y());
			} else {
				graphics.line(mouseX, mouseY, route[index].position().x(), route[index].position().y());
			}
		}
	}
	
	/**
	 * Creates a sensible route from an origin to a destination from an array of waypoints. 
	 * Waypoint costs are considered according to distance from current aircraft location
	 * Costs are further weighted by distance from waypoint to destination.
	 * @param origin the waypoint from which to begin.
	 * @param destination the waypoint at which to end.
	 * @param waypoints the waypoints to be used.
	 * @return a sensible route between the origin and the destination, using a sensible amount of waypoint.
	 */
	public Waypoint[] findGreedyRoute(Waypoint origin, Waypoint destination, Waypoint[] waypoints) {
		// To hold the route as we generate it.
		ArrayList<Waypoint> selectedWaypoints = new ArrayList<Waypoint>();
		// Initialise the origin as the first point in the route.
		// SelectedWaypoints.add(origin);
		// To track our position as we generate the route. Initialise to the start of the route
		Waypoint currentPos = origin;

		// To track the closest next waypoint
		double cost = Double.MAX_VALUE;
		Waypoint cheapest = null;
		// To track if the route is complete
		boolean atDestination = false;
		
		while (! atDestination) {
			for (Waypoint point : waypoints) {
				boolean skip = false;
				
				for (Waypoint routePoints : selectedWaypoints) {
					// Check we have not already selected the waypoint
					// If we have, skip evaluating the point
					// This protects the aircraft from getting stuck looping between points
					if (routePoints.position().equals(point.position())) {
						skip = true; //flag to skip
						break; // no need to check rest of list, already found a match.
					}
				}
				// Do not consider the waypoint we are currently at or the origin
				// Do not consider offscreen waypoints which are not the destination
				// Also skip if flagged as a previously selected waypoint
				if (skip | point.position().equals(currentPos.position()) | point.position().equals(origin.position())
						| (point.isEntryOrExit() && !(point.position().equals(destination.position())))) {
					skip = false; //reset flag
					continue;
				}  else  {
					/* Get cost of visiting waypoint
					 * Compare cost vs current cheapest
					 * If smaller, replace */	
					if (point.getCost(currentPos) + 0.5 * Waypoint.getCostBetween(point, destination) < cost) {
						// Cheaper route found, update
						cheapest = point;
						cost = point.getCost(currentPos) + 0.5 * Waypoint.getCostBetween(point, destination);
					}
				}
				
			} // End for - evaluated all waypoints
			// The cheapest waypoint must have been found
			assert cheapest != null : "The cheapest waypoint was not found";

			if (cheapest.position().equals(destination.position())) {
				/* Route has reached destination 
				 * Break out of while loop*/
				atDestination = true;
			}
			// Update the selected route
			// Consider further points in route from the position of the selected point
			selectedWaypoints.add(cheapest);
			currentPos = cheapest;
			// Resaturate cost for next loop
			cost = Double.MAX_VALUE;

		} // End while
		// Create a Waypoint[] to hold the new route
		Waypoint[] route = new Waypoint[selectedWaypoints.size()];
		// Fill route with the selected waypoints
		for (int i = 0; i < selectedWaypoints.size(); i++) {
			route[i] = selectedWaypoints.get(i);
		}
		return route;
	}

	/**
	 * Updates the amount of planes that are too close, violating the separation rules,
	 * and also checks for crashes.
	 * @param time_difference the time elapsed since the last frame.
	 * @param scene the game scene object.
	 * @return 0 if no collisions, 1 if separation violation, 2 if crash
	 */
	public int updateCollisions(double time_difference, ArrayList<Aircraft> aircraftList, Score score) {
		planesTooNear.clear();
		for (int i = 0; i < aircraftList.size(); i ++) {
			Aircraft plane = aircraftList.get(i);
			if (plane != this && isWithin(plane, RADIUS)) {
				hasFinished = true;
				return i;
			} else if (plane != this && isWithin(plane, separationRule)) {
				planesTooNear.add(plane);
				score.increaseMeterFill(-1); // Punishment for breaching separation rules (applies to all aircraft involved - usually 2)
				if (collisionWarningSoundFlag == false) {
					collisionWarningSoundFlag = true;
					WARNING_SOUND.play();
				}
			}
		}
		if (planesTooNear.isEmpty()) {
			collisionWarningSoundFlag = false;
		}
		return -1;
	}
	
	/**
	 * Checks whether an aircraft is within a certain distance from this one.
	 * @param aircraft the aircraft to check.
	 * @param distance the distance within which to care about.
	 * @return true, if the aircraft is within the distance. False, otherwise.
	 */
	private boolean isWithin(Aircraft aircraft, int distance) {
		double dx = aircraft.position().x() - position.x();
		double dy = aircraft.position().y() - position.y();
		double dz = aircraft.position().z() - position.z();
		return dx*dx + dy*dy + dz*dz < distance*distance;
	}

	/**
	 * Toggles the state of whether this plane is manually controlled.
	 */
	public void toggleManualControl() {
		isManuallyControlled = !isManuallyControlled;
		if (isManuallyControlled) {
			setBearing(getBearing());
		}
		else {
			resetBearing();
		}
	}

	/**
	 * Changes the direction the plane is going towards.
	 * @param newHeading
	 */
	public void setBearing(double newHeading) {
		manualBearingTarget = newHeading;
	}

	/**
	 * Resets the direction towards which the plane will head.
	 */
	private void resetBearing() {
		if (currentRouteStage < route.length & route[currentRouteStage] != null) {
			currentTarget = route[currentRouteStage].position();
		}
		turnTowardsTarget(0);
	}
	
	/**
	 * Increases the plane's altitude.
	 */
	public void climb() {
		if (position.z() < 30000 && altitudeState == ALTITUDE_CLIMB)
			changeAltitude(altitudeChangeSpeed);
		if (position.z() >= 30000) {
			changeAltitude(0);
			altitudeState = ALTITUDE_LEVEL;
			position = new Vector(position.x(), position.y(), 30000);
		}
	}
	
	/**
	 * Decreases the plane's altitude.
	 */
	public void fall() {
		if (position.z() > 28000 && altitudeState == ALTITUDE_FALL)
			changeAltitude(-altitudeChangeSpeed);
		if (position.z() <= 28000) {
			changeAltitude(0);
			altitudeState = ALTITUDE_LEVEL;
			position = new Vector(position.x(), position.y(), 28000);
		}
	}
	
	public void land() {
		is_waiting_to_land = false;
		is_landing = true;
		Demo.airport.is_active = true;
	}
	
	public void takeOff() {
		Demo.airport.is_active = true;
		Demo.takeOffSequence(this);
	}
	/**
	 * Changes the plane's altitude by a given amount.
	 * @param height the height by which to change altitude.
	 */
	private void changeAltitude(int height) {
		//velocity = velocity.add(new Vector(0,0, height));
		velocity.setZ(height);
	}
	
	/**
	 * Sets the plane's altitude state, e.g. climbing or falling
	 * @param state
	 */
	public void setAltitudeState(int state) {
		this.altitudeState = state;
	}

	/**
	 * 	This function calculates optimal distance for a plane
	 * @return total distance a plane needs to pass based on its flight plan to get to its exit point
	 */	
	public int totalDistanceInFlightPlan() {
		int dist = 0;
		
		for (int i=0; i < route.length-1; i++) {
			dist += Waypoint.getCostBetween(route[i], route[i+1]);
		}
		
		return dist;
	}

	public boolean isCloseToEntry(Vector position) {
		double x = this.position().x() - position.x();
		double y = this.position().y() - position.y();
		return ((x * x + y * y) <= (200 * 200));
	}
	
}
