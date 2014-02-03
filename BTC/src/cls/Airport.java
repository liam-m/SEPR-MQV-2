package cls;

import java.io.File;

import lib.jog.graphics;
import lib.jog.window;

public class Airport extends Waypoint {

	private static double x_location = window.width()/2;
	private static double y_location = window.height()/2;		

	public static boolean land_pressed = false;
	public static boolean plane_can_land = false;
	
	public Airport() { 
		super(x_location, y_location, false);
	}
	
	@Override
	public void draw() { 
		graphics.Image airport = graphics.newImage("gfx" + File.separator + "Airport.png");
		graphics.draw(airport, x_location, y_location);
	}
}
