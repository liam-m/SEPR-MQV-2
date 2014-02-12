package tst;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import btc.Main;
import scn.Demo;
import cls.Aircraft;
import cls.Waypoint;
import cls.Vector;
import cls.Score;

public class AircraftTest {	
	Aircraft testAircraft;
	Score testScore;
	
	@Before
	public void setUp() {
		Waypoint[] waypointList = new Waypoint[]{new Waypoint(0, 0, true), new Waypoint(100, 100, true), new Waypoint(25, 75, false), new Waypoint(75, 25, false), new Waypoint(50,50, false)};
		testAircraft = new Aircraft("testAircraft", "Berlin", "Dublin", new Waypoint(100,100, true), new Waypoint(0,0, true), null, 10.0, waypointList, 1);
		testScore = new Score();
	}
	
	// Test get functions
	// Test getPosition function
	@Test
	public void testGetPosition() {
		Vector resultPosition = testAircraft.position();
		assertTrue("x >= -128 and xy <= 27, y = 0, z = 28,000 or z = 30,000", ((0 == resultPosition.y()) && (128 >= resultPosition.x()) && (-128 <= resultPosition.x()) && ((28000 == resultPosition.z()) || (30000 == resultPosition.z()))));
	}
	
	// Test getName function
	@Test
	public void testGetName() {
		String name = testAircraft.getName();
		assertTrue("Name = testAircraft", "testAircraft" == name);
	}
	
	// Test getOriginName function
	@Test
	public void testGetOriginName(){
		String name = testAircraft.getOriginName();
		assertTrue("Origin name = Dublin", "Dublin" == name);
	}
	
	// Test getDestinationName function
	@Test
	public void testGetDestinationName(){
		String name = testAircraft.getDestinationName();
		assertTrue("Destination name = Berlin", "Berlin" == name);
	}
	
	// Test getIsFinished function
	@Test
	public void testGetIsFinishedName(){
		boolean status = testAircraft.isFinished();
		assertTrue("Finished = false", false == status);
	}
	
	// Test getIsManuallyControlled function
	@Test
	public void testIsManuallyControlled(){
		boolean status = testAircraft.isManuallyControlled();
		assertTrue("Manually controlled = false", false == status);
	}
	
	// Test getSpeed function
	@Test
	public void testGetSpeed(){
		double speed = (int) (testAircraft.getSpeed() + 0.5);
		assertTrue("Speed = 20", speed == 20.0);
	}
	
	// Test getAltitudeState
	@Test
	public void testAltitudeState(){
		testAircraft.setAltitudeState(1);
		int altState = testAircraft.getAltitudeState();
		assertTrue("Altitude State = 1", altState == 1);
	}
	
	// Test outOfBounds
	@Test
	public void testOutOfBounds(){
		Waypoint[] waypointList = new Waypoint[]{new Waypoint(0, 0, true), new Waypoint(100, 100, true), new Waypoint(25, 75, false), new Waypoint(75, 25, false), new Waypoint(50,50, false)};
		testAircraft = new Aircraft("testAircraft", "Berlin", "Dublin", new Waypoint(100,100, true), new Waypoint(0,0, true), null, 10.0, waypointList, 1);
		assertTrue("Out of bounds = false", testAircraft.isOutOfBounds());
	}
	
	// Test set methods
	// Test setAltitudeState
	@Test
	public void testSetAltitudeState(){
		testAircraft.setAltitudeState(1);
		int altState = testAircraft.getAltitudeState();
		assertTrue("Altitude State = 1", altState == 1);
	}

	// Test is simplified (assuming difficulty sections had been implemented correctly)
	// Testing score feature 

	@Test
	public void testScore() {
		Demo testDemo = new Demo(1);
		testDemo.initializeAircraftArray();
		testDemo.aircraftList().add(testAircraft);
		Aircraft plane = testDemo.aircraftList().get(0);

		assertTrue(testScore.getTotalScore() == 0);
		assertTrue(testScore.multiplier == 1);
		assertTrue(testScore.getMultiplierVariable() == 0);
		assertTrue(plane.getBaseScore() == 150);
		assertTrue(plane.getAdditionToMultiplier() == 2);

		// Simulating Demo class' update from here (calling that function would otherwise interfere with testing):

		testScore.increaseMultiplierVariable(plane.getAdditionToMultiplier());
		testScore.increaseTotalScore(testScore.multiplier * plane.getBaseScore());

		assertTrue(testScore.getTotalScore() == 150);
		assertTrue(testScore.multiplier == 1);
		assertTrue(testScore.getMultiplierVariable() == 2);	
	}

	// Testing multiplier 
	@Test
	public void testScoreMultiplier() {
		Demo testDemo = new Demo(1);
		int multVar = testScore.getMultiplierVariable();

		assertTrue(testScore.multiplier == 1);
		for (int i = 0; i < 132; i++) {

			if (multVar >= 0 && multVar < 10) 
				assertTrue(testScore.multiplier == 1);

			if (multVar >= 10 && multVar < 40) 
				assertTrue(testScore.multiplier == 3);

			if (multVar >= 40 && multVar < 80) 
				assertTrue(testScore.multiplier == 5);

			if (multVar >= 80 && multVar < 130) 
				assertTrue(testScore.multiplier == 7); 

			if (multVar >= 130) 
				assertTrue(testScore.multiplier == 10);

			testScore.increaseMultiplierVariable(1);
			multVar = testScore.getMultiplierVariable();
		}

		assertTrue(testScore.multiplier == 10);
		testScore.resetMultiplier();
		assertTrue(testScore.multiplier == 1);
	}

	// Testing totalDistanceInFlightPlan 
	@Test

	public void totalDistanceInFlightPlan() {
		Demo testDemo = new Demo(1);
		testDemo.initializeAircraftArray();
		testDemo.aircraftList().add(testAircraft);
		Aircraft plane = testDemo.aircraftList().get(0);
		int distance = 0;
		distance += Waypoint.getCostBetween(plane.getRoute()[0], plane.getRoute()[1]);
		assertTrue(distance == plane.totalDistanceInFlightPlan());
	}

}