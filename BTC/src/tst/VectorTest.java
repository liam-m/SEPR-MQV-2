package tst;

import static org.junit.Assert.*;

import org.junit.Test;

import cls.Vector;

public class VectorTest {	
	// Test get functions
	// Test getX function
	@Test 
	public void testGetX() {
		Vector testVector = new Vector(1.0, 1.1, 1.2);
		assertTrue("x = 1.0", 1.0 == testVector.getX());
	}
	
	// Test getY function
	@Test 
	public void testGetY() {
		Vector testVector = new Vector(1.0, 1.1, 1.2);
		assertTrue("y = 1.1", 1.1 == testVector.getY());
		
	}
	
	// Test getZ function
	@Test 
	public void testGetZ() {
		Vector testVector = new Vector(1.0, 1.1, 1.2);
		assertTrue("z = 1.2", 1.2 == testVector.getZ());		
	}
	
	// Test magnitude function
	@Test 
	public void testMagnitude() {
		Vector testVector = new Vector(1.0, 2.0, 2.0);
		assertTrue("Magnitude = 3", 3.0 == testVector.magnitude());	
	}
	@Test 
	public void testMagnitude2() {
		Vector testVector = new Vector(12, 16, 21);
		assertTrue("Magnitude = 29", 29 == testVector.magnitude());	
	}
	
	// Test magnitudeSquared function
	@Test 
	public void testMagnitudeSquared() {
		Vector testVector = new Vector(1.0, 2.0, 2.0);
		assertTrue("Magnitude = 9", 9.0 == testVector.magnitudeSquared());	
	}
	@Test 
	public void testMagnitudeSquared2() {
		Vector testVector = new Vector(12, 16, 21);
		assertTrue("Magnitude = 841", 841 == testVector.magnitudeSquared());	
	}
	
	// Test equals function
	@Test 
	public void testEquals() {
		Vector testVector = new Vector(1.9, 2.2, 7.4);
		Vector testVector2 = new Vector(1.9, 2.2, 7.4);
		assertTrue("Equals = true", testVector.equals(testVector2));	
	}
	@Test 
	public void testEquals2() {
		Vector testVector = new Vector(9, 4.2, 5.1);
		Vector testVector2 = new Vector(9.0, 4.2, 5);
		assertTrue("Equals = false", !testVector.equals(testVector2));	
	}
	
	// Test addition function
	@Test 
	public void testAddition() {
		Vector testVector = new Vector(2.0, 2.0, 4.0);
		Vector testVector2 = new Vector(1.0, 3.0, 2.0);
		Vector resultVector = testVector.add(testVector2);
		assertTrue("Result =  3.0, 4.0, 6.0", (3.0 == resultVector.getX()) && (5.0 == resultVector.getY()) && (6.0 == resultVector.getZ()));	
	}
	@Test 
	public void testAddition2() {
		Vector testVector = new Vector(6.0, 8.1, 16);
		Vector testVector2 = new Vector(1.0, 2.0, 3.0);
		Vector resultVector = testVector.add(testVector2);
		assertTrue("Result =  7.0, 10.1, 19.0", (7.0 == resultVector.getX()) && (10.1 == resultVector.getY()) && (19.0 == resultVector.getZ()));	
	}
	
	// Test subtraction function
	@Test 
	public void testSubtraction() {
		Vector testVector = new Vector(2.0, 3.0, 4.0);
		Vector testVector2 = new Vector(1.0, 1.0, 2.0);
		Vector resultVector = testVector.sub(testVector2);
		assertTrue("Result = 1.0, 2.0, 2.0", (1.0 == resultVector.getX()) && (2.0 == resultVector.getY()) && (2.0 == resultVector.getZ()));	
	}
	@Test 
	public void testSubtraction2() {
		Vector testVector = new Vector(14.0, 6, 100);
		Vector testVector2 = new Vector(1.0, 6.0, 0);
		Vector resultVector = testVector.sub(testVector2);
		assertTrue("Result = 13.0, 0, 100.0", (13.0 == resultVector.getX()) && (0 == resultVector.getY()) && (100.0 == resultVector.getZ()));	
	}
	
	// Test scaleBy function
	@Test
	public void testScaleBy(){
		Vector testVector = new Vector(1, 2, 3);
		Vector resultVector = testVector.scaleBy(1.0);
		assertTrue("ScaledBy = (1 , 2, 3)",  (1 == resultVector.getX()) && (2 == resultVector.getY()) && (3 == resultVector.getZ()));
	}
	@Test
	public void testScaleBy2(){
		Vector testVector = new Vector(1, 2, 3);
		Vector resultVector = testVector.scaleBy(-2.0);
		assertTrue("ScaledBy = (-2 , -4, -6)",  (-2 == resultVector.getX()) && (-4 == resultVector.getY()) && (-6 == resultVector.getZ()));
	}
	
	// Test normalise function
	@Test 
	public void testNormalise() {
		Vector testVector = new Vector(1.0, 2.0, 2.0);
		Vector resultVector = testVector.normalise();
		assertTrue("Normalise = 1/3, 2/3, 2/3",  (1 == (resultVector.getX()* 3)) && (2 == (resultVector.getY()*3)) && (2 == (resultVector.getZ()*3)));
		
	}
	@Test 
	public void testNormalise2() {
		Vector testVector = new Vector(1, 4, 8);
		Vector resultVector = testVector.normalise();
		assertTrue("Normalise = 1/9, 4/9, 8/9",  (1 == (resultVector.getX()*9)) && (4 == (resultVector.getY()*9)) && (8 == (resultVector.getZ()*9)));	
	}
	
	
	// Test angle between function
	@Test 
	public void testAngle() {
		Vector testVector = new Vector(1, 0, 0);
		Vector testVector2 = new Vector(0, 1, 0);
		double angle = Math.PI / 2;
		assertTrue("Angle = pi/2", angle  ==  testVector.angleBetween(testVector2));	
	}
}
