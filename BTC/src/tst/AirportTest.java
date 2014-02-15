package tst;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import cls.Aircraft;
import cls.Airport;
import cls.Vector;
import cls.Waypoint;

public class AirportTest {
	Airport test_airport;
	Aircraft test_aircraft;
	
	@Before
	public void setUp() {
		test_airport = new Airport();
		Waypoint[] waypointList = new Waypoint[]{new Waypoint(0, 0, true), new Waypoint(100, 100, true), new Waypoint(25, 75, false), new Waypoint(75, 25, false), new Waypoint(50,50, false)};
		test_aircraft = new Aircraft("testAircraft", "Berlin", "Dublin", new Waypoint(100,100, true), new Waypoint(0,0, true), null, 10.0, waypointList, 1);			
	}
	
	// TODO write tests for new methods (isMouseOver...)
	
	@Test
	public void testAddToHangar() {
		// Dependant on hangar_size = 3
		test_airport.addToHangar(test_aircraft);
		assertTrue("The size of the hanger = 1", test_airport.aircraft_hangar.size() == 1);
		
		test_airport.addToHangar(test_aircraft);
		assertTrue("The size of the hanger = 2", test_airport.aircraft_hangar.size() == 2);
		
		test_airport.addToHangar(test_aircraft);
		assertTrue("The size of the hanger = 3", test_airport.aircraft_hangar.size() == 3);
		
		//this should also be 3 because of the maximum size
		test_airport.addToHangar(test_aircraft);
		assertTrue("The size of the hanger = 3", test_airport.aircraft_hangar.size() == 3);
	}
	
	@Test
	public void testSignalTakeOff() {
		test_airport.signalTakeOffTesting();
		assertTrue("The size of the hanger = 0", test_airport.aircraft_hangar.size() == 0);
		
		test_airport.addToHangar(test_aircraft);
		test_airport.signalTakeOffTesting();
		assertTrue("The size of the hanger = 0", test_airport.aircraft_hangar.size() == 0);
	}
}
