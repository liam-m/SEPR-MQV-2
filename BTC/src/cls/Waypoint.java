package cls;

import lib.jog.graphics;

public class Waypoint {
	
	/**
	 * Leniancy to allow mouse input to be accepted in a small area around the waypoint
	 * For ease of use.
	 */
	public final static int MOUSE_LENIANCY = 32;
	
	final private int WAYPOINT_ICON_RADIUS = 8;
	private Vector waypoint_location;
	
	/**
	 * Marks whether the waypoint is a point where planes may enter and exit the game airspace
	 */
	private boolean entryOrExit; //#Please specify what True or False are (just one of them)
	
	/**
	 * Constructor for waypoints
	 * @param x the x coordinate of the waypoint
	 * @param y the y coordinate of the waypoint
	 * @param inputEntryOrExit whether the waypoint is a point where planes may enter and leave the airspace
	 */
	public Waypoint(double x, double y, boolean inputEntryOrExit) {
		waypoint_location  = new Vector(x, y, 0);
		entryOrExit = inputEntryOrExit;
	}
	
	public Vector getLocation() {
		return waypoint_location ;
	}
	
	/**
	 * Checks if the mouse is over the waypoint, within MOUSE_LENIANCY
	 * @param mx the mouse's x location
	 * @param my the mouse's y location
	 * @return whether the mouse is considered over the waypoint.
	 */
	public boolean isMouseOver(int mx, int my) {
		double dx = waypoint_location .getX() - mx;
		double dy = waypoint_location .getY() - my;
		return dx*dx + dy*dy < MOUSE_LENIANCY*MOUSE_LENIANCY;
	}
	
	/**
	 * Returns if the waypoint is an entry or exit point.
	 * @return Whether or not the waypoint is an entry or exit point.//#What does True mean?
	 */
	public boolean isEntryOrExit() {
		return this.entryOrExit;
	}
	
	/**
	 * Gets the cost of travelling between this waypoint and another - Used for pathfinding
	 * @param fromPoint The point to consider cost from, to this waypoint
	 * @return the distance(cost) between the two waypoints
	 */
	public double getCost(Waypoint fromPoint) {
		return waypoint_location .sub(fromPoint.getLocation()).magnitude();
	}
	
	/**
	 * Gets the cost between two waypoints
	 * @param source the source waypoint
	 * @param target the target waypoint
	 * @return the cost between source and target
	 */
	public static double getCostBetween(Waypoint source, Waypoint target) {
		return target.getCost(source);
	}
	
	/**
	 * draws the waypoint
	 * @param x the x location to draw at
	 * @param y the y location to draw at
	 */
	public void draw(double x, double y) {
		if (this.isEntryOrExit()) 
			graphics.setColour(64, 128, 0, 192);
		else
			graphics.setColour(128, 0, 0, 128);
		
		graphics.circle(false, x-WAYPOINT_ICON_RADIUS/2, y-WAYPOINT_ICON_RADIUS/2, WAYPOINT_ICON_RADIUS);
		graphics.circle(true, x-WAYPOINT_ICON_RADIUS/2, y-WAYPOINT_ICON_RADIUS/2, WAYPOINT_ICON_RADIUS - 2);
	}

	public void draw() {
		draw(waypoint_location .getX(), waypoint_location .getY());
	}
	
}
