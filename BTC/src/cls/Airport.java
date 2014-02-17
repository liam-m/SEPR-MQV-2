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
	
	/**
	 * All location values are absolute and based on the current version of the airport image.
	 */
	private static double arrivals_x_location = x_location + 90;
	private static double arrivals_y_location = y_location + 83;
	private static double arrivals_width = 105;
	private static double arrivals_height = 52;
	
	private static double departures_x_location = x_location + 2;
	private static double departures_y_location = y_location + 50;
	private static double departures_width = 50;
	private static double departures_height = 36;
	
	public boolean is_active = false; // True if there is an aircraft Landing/Taking off
	private boolean is_arrivals_clicked = false;
	private boolean is_departures_clicked = false;
	
	public String name = "Mosbear Aiport";
	
	private graphics.Image airport = graphics.newImage("gfx" + File.separator + "Airport.png");
	
	public java.util.ArrayList<Aircraft> aircraft_waiting_to_land = new java.util.ArrayList<Aircraft>();
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
		// Draw the airport image
		graphics.draw(airport, x_location-airport.width()/2, y_location-airport.height()/2);
		
		//draw the hangar button if plane is waiting (departing flights)
		if (aircraft_hangar.size() > 0) {
			graphics.setColour(0, 128, 0, 128);
			graphics.rectangle(is_departures_clicked, departures_x_location-airport.width()/2, departures_y_location-airport.height()/2, departures_width, departures_height);
			if (Demo.getTime() - time_entered.get(0) >= 5) {
				graphics.setColour(128, 0, 0, 64);
			} else {
				graphics.setColour(128, 128, 0, 64);
			}
			graphics.rectangle(true, departures_x_location-airport.width()/2 + 1, departures_y_location-airport.height()/2 + 1, departures_width -2, departures_height -2);
			graphics.setColour(255, 255, 255, 128);
			graphics.print(Integer.toString(aircraft_hangar.size()), departures_x_location-airport.width()/2 + 23, departures_y_location-airport.height()/2 + 15);
		}
		graphics.setColour(0, 128, 0, 128);
		// draw the arrivals button if at least one plane is waiting (arriving flights)
		if (aircraft_waiting_to_land.size() > 0) {
			graphics.rectangle(is_arrivals_clicked, arrivals_x_location-airport.width()/2, arrivals_y_location-airport.height()/2, arrivals_width, arrivals_height);
			graphics.setColour(128, 128, 0, 64);
			graphics.rectangle(true, arrivals_x_location-airport.width()/2 + 1, arrivals_y_location-airport.height()/2 + 1, arrivals_width -2, arrivals_height -2);
			graphics.setColour(255, 255, 255, 128);
			graphics.print(Integer.toString(aircraft_waiting_to_land.size()), arrivals_x_location-airport.width()/2 + 50, arrivals_y_location-airport.height()/2 + 26);
		}
		
	}
	
	public double getLongestTimeInHangar(double currentTime) {
		return aircraft_hangar.isEmpty() ? 0 : currentTime-time_entered.get(0);
	}
	
	/**
	 *  Arrivals is the portion of the airport image which is used to issue the land command
	 * @param position is the mouse position to be tested
	 * @return true if mouse is within the rectangle that defines the arrivals portion of the airport
	 */
	public boolean isMouseOverArrivals(Vector position) {
		return isMouseInRect((int)position.x(), (int)position.y(),(int)(arrivals_x_location-airport.width()/2) + Demo.airspace_view_offset_x, (int)(arrivals_y_location-airport.height()/2) + Demo.airspace_view_offset_y, (int)arrivals_width, (int)arrivals_height);
	}
	
	/**
	 * Departures is the portion of the airport image which is used to issue the take off command
	 * @param position is the mouse position to be tested
	 * @return true if mouse is within the rectangle that defines the departures portion of the airport
	 */
	public boolean isMouseOverDepartures(Vector position) {
		return isMouseInRect((int)position.x(), (int)position.y(), (int)(departures_x_location-airport.width()/2) + Demo.airspace_view_offset_x, (int)(departures_y_location-airport.height()/2) + Demo.airspace_view_offset_y, (int)departures_width, (int)departures_height);

	}
	
	public boolean isMouseInRect (int test_x, int test_y, int x, int y, int width, int height) {
		return x <= test_x && test_x <= x + width && y <= test_y && test_y <= y + height;
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
		aircraft_waiting_to_land.clear();
		for (Aircraft a : demo.aircraftList()) {
			if (a.currentTarget.equals(this.position())) {
				aircraft_waiting_to_land.add(a);
			}
		}
	}
	
	public int getHangarSize() {
		return hangar_size;
	}

	@Override
	public void mousePressed(int key, int x, int y) {
		if (key == input.MOUSE_LEFT) { 
			if (isMouseOverArrivals(new Vector(x, y, 0))) {
				is_arrivals_clicked = true;
			} else if (isMouseOverDepartures(new Vector(x, y, 0))) {
				is_departures_clicked = true;
			}
		
		}
	}


	@Override
	public void mouseReleased(int key, int x, int y) {
		is_arrivals_clicked = false;
		is_departures_clicked = false;
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
