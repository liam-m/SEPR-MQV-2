package cls;

public class FlightPlan {	
	private Waypoint[] route;
	private String originName;
	private String destinationName;
	private Vector destination;
	private Vector origin;
	
	FlightPlan(Waypoint[] route, String originName, String destinationName, Waypoint originPoint, Waypoint destinationPoint) {
		this.route = route;
		this.originName = originName;
		this.destinationName = destinationName;
		this.destination = destinationPoint.getLocation();
		this.origin = originPoint.getLocation();
	}
	
	public Waypoint[] getRoute() {
		return route;
	}
	
	public Vector getDestination() {
		return destination;
	}
	
	/**
	 * Edits the plane's path by changing the waypoint it will go to at a certain stage in its route.
	 * @param routeStage the stage at which the new waypoint will replace the old.
	 * @param newWaypoint the new waypoint to travel to.
	 */
	public void alterPath(int routeStage, Waypoint newWaypoint) {
		route[routeStage] = newWaypoint;
	}
	
	public String getDestinationName() {
		return destinationName;
	}
	
	public String getOriginName() {
		return originName;
	}
	
	/**
	 * Calculates optimal distance for a plane - Used for scoring
	 * @return total distance a plane needs to pass based on its flight plan to get to its exit point
	 */
	public int getTotalDistance() {
		int dist = 0;

		for (int i = 0; i < getRoute().length - 1; i++) {
			dist += Waypoint.getCostBetween(getRoute()[i], getRoute()[i + 1]);
		}

		return dist;
	}
}