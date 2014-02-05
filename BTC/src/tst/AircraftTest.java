package tst;

import static org.junit.Assert.*;

import org.junit.Test;

import btc.Main;
import scn.Demo;
import cls.Aircraft;
import cls.Waypoint;
import cls.Vector;

public class AircraftTest {

	// Create test aircraft
	private Aircraft generateTestAircraft()
	{
		Waypoint[] waypointList = new Waypoint[]{new Waypoint(0, 0, true), new Waypoint(100, 100, true), new Waypoint(25, 75, false), new Waypoint(75, 25, false), new Waypoint(50,50, false)};
		Aircraft testAircraft = new Aircraft("testAircraft", "Berlin", "Dublin", new Waypoint(100,100, true), new Waypoint(0,0, true), null, 10.0, waypointList, 1);
		return testAircraft;
	}

	// Test get functions
	// Test getPosition function
	@Test
	public void testGetPosition() {
		Aircraft testAircraft = generateTestAircraft();
		Vector resultPosition = testAircraft.position();
		assertTrue("x >= -128 and xy <= 27, y = 0, z = 28,000 or z = 30,000", ((0 == resultPosition.y()) && (128 >= resultPosition.x()) && (-128 <= resultPosition.x()) && ((28000 == resultPosition.z()) || (30000 == resultPosition.z()))));
	}
	// Test getName function
	@Test
	public void testGetName() {
		Aircraft testAircraft = generateTestAircraft();
		String name = testAircraft.name();
		assertTrue("Name = testAircraft", "testAircraft" == name);
	}
	// Test getOriginName function
	@Test
	public void testGetOriginName(){
		Aircraft testAircraft = generateTestAircraft();
		String name = testAircraft.originName();
		assertTrue("Origin name = Dublin", "Dublin" == name);
	}
	// Test getDestinationName function
	@Test
	public void testGetDestinationName(){
		Aircraft testAircraft = generateTestAircraft();
		String name = testAircraft.destinationName();
		assertTrue("Destination name = Berlin", "Berlin" == name);
	}
	// Test getIsFinished function
	@Test
	public void testGetIsFinishedName(){
		Aircraft testAircraft = generateTestAircraft();
		boolean status = testAircraft.isFinished();
		assertTrue("Finished = false", false == status);
	}
	// Test getIsManuallyControlled function
	@Test
	public void testIsManuallyControlled(){
		Aircraft testAircraft = generateTestAircraft();
		boolean status = testAircraft.isManuallyControlled();
		assertTrue("Manually controlled = false", false == status);
	}
	// Test getSpeed function
	@Test
	public void testGetSpeed(){
		Aircraft testAircraft = generateTestAircraft();
		double speed = (int) (testAircraft.speed() + 0.5);
		assertTrue("Speed = 20", speed == 20.0);
	}
	// Test getAltitudeState
	@Test
	public void testAltitudeState(){
		Aircraft testAircraft = generateTestAircraft();
		testAircraft.setAltitudeState(1);
		int altState = testAircraft.altitudeState();
		assertTrue("Altitude State = 1", altState == 1);
	}

	// Test outOfBounds
	@Test
	public void testOutOfBounds(){
		Waypoint[] waypointList = new Waypoint[]{new Waypoint(0, 0, true), new Waypoint(100, 100, true), new Waypoint(25, 75, false), new Waypoint(75, 25, false), new Waypoint(50,50, false)};
		Aircraft testAircraft = new Aircraft("testAircraft", "Berlin", "Dublin", new Waypoint(100,100, true), new Waypoint(0,0, true), null, 10.0, waypointList, 1);
		boolean x = testAircraft.outOfBounds();
		assertTrue("Out of bounds = false", x == true);
	}

	// Test set methods
	// Test setAltitudeState
	@Test
	public void testSetAltitudeState(){
		Aircraft testAircraft = generateTestAircraft();
		testAircraft.setAltitudeState(1);
		int altState = testAircraft.altitudeState();
		assertTrue("Altitude State = 1", altState == 1);
	}

	// Test is simplified (assuming difficulty sections had been implemented correctly)
	// Testing score feature 

	@Test
	public void testScore() {
		Demo testDemo = new Demo(1);
		testDemo.initializeAircraftArray();
		Aircraft testAircraft = generateTestAircraft(); //initialized in medium difficulty
		testDemo.aircraftList().add(testAircraft);
		Aircraft plane = testDemo.aircraftList().get(0);

		assertTrue(testDemo.getTotalScore() == 0);
		assertTrue(testDemo.multiplier == 1);
		assertTrue(testDemo.getMultiplierVariable() == 0);
		assertTrue(plane.getBaseScore() == 200);
		assertTrue(plane.getPlaneBonusToMultiplier() == 2);

		// Simulating Demo class' update from here (calling that function would otherwise interfere with testing):

		testDemo.updateMultiplierVariable(plane.getPlaneBonusToMultiplier());
		testDemo.updateMultiplier();
		testDemo.increaseTotalScore(testDemo.multiplier * plane.getBaseScore());

		assertTrue(testDemo.getTotalScore() == 200);
		assertTrue(testDemo.multiplier == 1);
		assertTrue(testDemo.getMultiplierVariable() == 2);	
	}

	// Testing multiplier 
	@Test
	public void testScoreMultiplier() {
		Demo testDemo = new Demo(1);
		int multVar = testDemo.getMultiplierVariable();

		assertTrue(testDemo.multiplier == 1);
		for (int i = 0; i < 132; i++) {

			if (multVar >= 0 && multVar < 10) 
				assertTrue(testDemo.multiplier == 1);

			if (multVar >= 10 && multVar < 40) 
				assertTrue(testDemo.multiplier == 2);

			if (multVar >= 40 && multVar < 80) 
				assertTrue(testDemo.multiplier == 3);

			if (multVar >= 80 && multVar < 130) 
				assertTrue(testDemo.multiplier == 4); 

			if (multVar >= 130) 
				assertTrue(testDemo.multiplier == 5);

			testDemo.updateMultiplierVariable(1);
			testDemo.updateMultiplier();
			multVar = testDemo.getMultiplierVariable();
		}

		assertTrue(testDemo.multiplier == 5);
		testDemo.resetMultiplier();
		assertTrue(testDemo.multiplier == 1);
	}

	// Testing totalDistanceInFlightPlan 
	@Test

	public void totalDistanceInFlightPlan() {
		Demo testDemo = new Demo(1);
		testDemo.initializeAircraftArray();
		Aircraft testAircraft = generateTestAircraft(); 
		testDemo.aircraftList().add(testAircraft);
		Aircraft plane = testDemo.aircraftList().get(0);
		int distance = 0;
		distance += Waypoint.getCostBetween(plane.getRoute()[0], plane.getRoute()[1]);
		assertTrue(distance == plane.totalDistanceInFlightPlan());
	}

}


