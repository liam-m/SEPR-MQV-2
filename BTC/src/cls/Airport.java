package cls;

import java.io.File;

import scn.Demo;

import lib.jog.graphics;
import lib.jog.input;
import lib.jog.input.EventHandler;
import lib.jog.window;

public class Airport extends Waypoint implements EventHandler {

	private static double x_location = window.width()/2;
	private static double y_location = window.height()/2;		
	
	private static final int landing_radius = 128; 
	
	private boolean should_draw_landing_radius = false;
	public boolean is_active = false; // True if there is an aircraft Landing/Taking off
	private boolean clicked = false;
	
	public String name = "Mosbear Aiport";
	
	/**
	 * Time entered is directly related to the aircraft hangar and stores the time each aircraft entered the hangar
	 * this is used to determine score multiplier decrease if aircraft is in the hangar for too long
	 */
	public java.util.ArrayList<Aircraft> aircraft_hangar = new java.util.ArrayList<Aircraft>();
	public java.util.ArrayList<Double> time_entered = new java.util.ArrayList<Double>();
	private int hangar_size = 3;
	
	public Airport() { 
		super(x_location, y_location, true);
	}
	
	@Override
	public void draw() { 
		graphics.Image airport = graphics.newImage("gfx" + File.separator + "Airport.png");
		graphics.draw(airport, x_location-airport.width()/2, y_location-airport.height()/2);
		if (should_draw_landing_radius) {
			graphics.setColour(0, 128, 0, 128);
			graphics.circle(clicked, x_location, y_location, landing_radius, 32); // Filled if clicked, else not filled	
		}
	}
	
	public double getLongestTimeInHangar(double currentTime) {
		return aircraft_hangar.isEmpty() ? 0 : currentTime-time_entered.get(0);
	}
	
	public boolean isWithinRadius(Vector position) {
		double x = x_location - position.x();
		double y = y_location - position.y();
		return (x*x + y*y < landing_radius*landing_radius);
	}
	
	/**
	 * Adds aircraft to the back of the hangar and records the time in the time_entered list
	 * will only add the aircraft if the current size is less than the maximum denoted by hangar_size
	 * @param aircraft
	 */
	public void addToHangar(Aircraft aircraft) {
		if (aircraft_hangar.size() < hangar_size) {
			aircraft_hangar.add(aircraft);
			time_entered.add(Demo.getTime());
		}
	}
	
	public void signalTakeOff() {
		if (!aircraft_hangar.isEmpty()) {
			Aircraft aircraft = aircraft_hangar.remove(0);
			time_entered.remove(0);
			aircraft.takeOff();
		}	
	}
	  
	/** 
	 * decides whether to draw the radius around the airport by checking if any aircraft which are landing are close
	 * @param demo
	 */
	public void update(Demo demo) {
		should_draw_landing_radius = false;
		for (Aircraft aircraft : Demo.aircraftInAirspace) {
			if (this.isWithinRadius(aircraft.position()) && aircraft.currentTarget.equals(this.position())) {
				should_draw_landing_radius = true;
			}
		}	
	}
	
	public int getHangarSize() {
		return hangar_size;
	}

	@Override
	public void mousePressed(int key, int x, int y) {
		if (key == input.MOUSE_LEFT && isWithinRadius(new Vector(x, y, 0))) {
			clicked = true;
		}
	}


	@Override
	public void mouseReleased(int key, int x, int y) {
		clicked = false;
	}

	@Override
	public void keyPressed(int key) {
				
	}

	@Override
	public void keyReleased(int key) {
		
	}
	
	// Used in testing avoiding the need to have a demo instance
	@Deprecated
	public void signalTakeOffTesting() {
		if (aircraft_hangar.size() > 0) {
			aircraft_hangar.remove(0);
			time_entered.remove(0);
		}	
	}

	public void clear() {
		aircraft_hangar.clear();
		time_entered.clear();
	}
}
