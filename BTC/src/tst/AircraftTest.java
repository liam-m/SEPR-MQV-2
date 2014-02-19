package tst;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import scn.Demo;
import cls.Aircraft;
import cls.Waypoint;
import cls.Vector;
import cls.Score;

@SuppressWarnings("deprecation")

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
		Vector resultPosition = testAircraft.getPosition();
		assertTrue("x >= -128 and xy <= 27, y = 0, z = 28,000 or z = 30,000", ((0 == resultPosition.getY()) && (128 >= resultPosition.getX()) && (-128 <= resultPosition.getX()) && ((28000 == resultPosition.getZ()) || (30000 == resultPosition.getZ()))));
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
		String name = testAircraft.getFlightPlan().getOriginName();
		assertTrue("Origin name = Dublin", "Dublin" == name);
	}
	
	// Test getDestinationName function
	@Test
	public void testGetDestinationName(){
		String name = testAircraft.getFlightPlan().getDestinationName();
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
		assertTrue(testScore.getMultiplier() == 1);
		assertTrue(testScore.getMultiplierLevel() == 1);
		assertTrue(plane.getBaseScore() == 150);

		// Simulating Demo class' update from here (calling that function would otherwise interfere with testing):

		testScore.increaseTotalScore(testScore.getMultiplier() * plane.getBaseScore());
		while(testScore.getTargetScore() != testScore.getTotalScore()) testScore.update();
		
		assertTrue(testScore.getTotalScore() == 150);
	}

	// Testing multiplier 
	@Test
	public void testScoreMultiplier() {
		Demo testDemo = new Demo(1);
		testDemo.initializeAircraftArray();
		testDemo.aircraftList().add(testAircraft);
		
		assertTrue(testScore.getMultiplierLevel() == 1);
		assertTrue(testScore.getMultiplier() == 1);
		assertTrue(testScore.getMeterFill() == 0);
		
		// tests the meter will not decrease below 0 at multiplier_level 1
		testScore.increaseMeterFill(-1);
		while(testScore.getTargetMeterFill() != testScore.getMeterFill()) testScore.update();
		
		assertTrue(testScore.getMultiplierLevel() == 1);
		assertTrue(testScore.getMultiplier() == 1);
		assertTrue(testScore.getMeterFill() == 0);
		
		// tests increasing the multipler_level at max meter_fill
		testScore.increaseMeterFill(256);
		while(testScore.getTargetMeterFill() != testScore.getMeterFill()) testScore.update();

		assertTrue(testScore.getMultiplierLevel() == 2);
		assertTrue(testScore.getMultiplier() == 3);
		assertTrue(testScore.getMeterFill() == 0);
		
		// tests an increase beyond the bound of the meter
		testScore.increaseMeterFill(257);
		while(testScore.getTargetMeterFill() != testScore.getMeterFill()) testScore.update();
		
		assertTrue(testScore.getMultiplierLevel() == 3);
		assertTrue(testScore.getMultiplier() == 5);
		assertTrue(testScore.getMeterFill() == 1);
		
		// sets the meter and multiplier_level to their max values
		testScore.increaseMeterFill(-1);
		testScore.increaseMeterFill(3*256);
		while(testScore.getTargetMeterFill() != testScore.getMeterFill()) testScore.update();
		
		assertTrue(testScore.getMultiplierLevel() == 5);
		assertTrue(testScore.getMultiplier() == 10);
		assertTrue(testScore.getMeterFill() == 256);
		
		// tests the meter will not increase beyond 256 at multiplier_level 5
		testScore.increaseMeterFill(1);
		while(testScore.getTargetMeterFill() != testScore.getMeterFill()) testScore.update();
		
		assertTrue(testScore.getMultiplierLevel() == 5);
		assertTrue(testScore.getMultiplier() == 10);
		assertTrue(testScore.getMeterFill() == 256);
		
		// tests decreasing the meter to it's lower bound
		testScore.increaseMeterFill(-256);
		while(testScore.getTargetMeterFill() != testScore.getMeterFill()) testScore.update();
		
		assertTrue(testScore.getMultiplierLevel() == 5);
		assertTrue(testScore.getMultiplier() == 10);
		assertTrue(testScore.getMeterFill() == 0);
		
		// tests decreasing the meter beyond it's lower bound and lowering the multiplier_level
		testScore.increaseMeterFill(-1);
		while(testScore.getTargetMeterFill() != testScore.getMeterFill()) testScore.update();
		
		assertTrue(testScore.getMultiplierLevel() == 4);
		assertTrue(testScore.getMultiplier() == 7);
		assertTrue(testScore.getMeterFill() == 255);
		
		
	}

	// Testing totalDistanceInFlightPlan 
	@Test

	public void totalDistanceInFlightPlan() {
		Demo testDemo = new Demo(1);
		testDemo.initializeAircraftArray();
		testDemo.aircraftList().add(testAircraft);
		Aircraft plane = testDemo.aircraftList().get(0);
		int distance = 0;
		distance += Waypoint.getCostBetween(plane.getFlightPlan().getRoute()[0], plane.getFlightPlan().getRoute()[1]);
		assertTrue(distance == plane.getFlightPlan().getTotalDistance());
	}
	
	//Testing isCloseToEntry
	@Test
		
	public void isCloseToEntry() {
		Waypoint[] waypointList = new Waypoint[]{new Waypoint(0, 0, true), new Waypoint(100, 100, true), new Waypoint(25, 75, false), new Waypoint(675, 125, false), new Waypoint(530,520, false)};
		testAircraft = new Aircraft("testAircraft", "Berlin", "Dublin", new Waypoint(100,100, true), new Waypoint(0,0, true), null, 10.0, waypointList, 1);
		assertTrue(testAircraft.isCloseToEntry(waypointList[0].getLocation()));			
		assertTrue(testAircraft.isCloseToEntry(waypointList[1].getLocation()));
		assertTrue(testAircraft.isCloseToEntry(waypointList[2].getLocation()));
		assertFalse(testAircraft.isCloseToEntry(waypointList[3].getLocation()));
		assertFalse(testAircraft.isCloseToEntry(waypointList[4].getLocation()));
	}


}