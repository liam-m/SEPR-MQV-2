package cls;

import java.io.File;

import scn.Demo;

import lib.jog.graphics;
import lib.jog.window;

public class Airport extends Waypoint {

	private static double x_location = window.width()/2;
	private static double y_location = window.height()/2;		
	
	private static final int landing_radius = 128; 
	
	private boolean should_draw_landing_radius = false;
	public boolean is_active = false; // True if there is an aircraft Landing/Taking off
	
	public Airport() { 
		super(x_location, y_location, true);
	}
	
	@Override
	public void draw() { 
		graphics.Image airport = graphics.newImage("gfx" + File.separator + "Airport.png");
		graphics.draw(airport, x_location, y_location);
		if (should_draw_landing_radius) {
			graphics.setColour(0, 128, 0, 128);
			graphics.circle(false, x_location, y_location, landing_radius);
		}
	}
	
	public boolean isWithinRadius(Vector position) {
		double x = x_location - position.x();
		double y = y_location - position.y();
		return (x*x + y*y < landing_radius*landing_radius);
	}
	
	public void update(Demo demo) {
		should_draw_landing_radius = false;
		for (Aircraft aircraft : demo.aircraftInAirspace) {
			if (this.isWithinRadius(aircraft.position()) && aircraft.destination.equals(this.position()) && aircraft.currentTarget.equals(this.position())) {
				should_draw_landing_radius = true;
			}
		}	
	}
}
