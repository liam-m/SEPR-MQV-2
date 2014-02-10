package cls;

import scn.Demo;
import lib.jog.graphics;
import lib.jog.input;
import lib.jog.window;
import lib.jog.input.EventHandler;

public class AirportControlBox implements EventHandler{

	private Airport airport;
	
	private int number_of_divisions;
	
	private double positionX, positionY, width, height;
	
	private boolean clicked = false;
	/**
	 * Constructor for the control box
	 * @param x the x coordinate to draw at
	 * @param y the y coordinate to draw at
	 * @param w the width of the box
	 * @param h the height of the box
	 */
	public AirportControlBox(double x, double y, double w, double h, Airport airport) {
		positionX = x;
		positionY = y;
		width = w;
		height = h;
		this.airport = airport;
		number_of_divisions = airport.getHangarSize() + 1;
	}
	
	/**
	 * Draws the box to the screen
	 */
	public void draw() {
		drawBoxShell();
		drawLabels();
		if (clicked) {
			graphics.setColour(0, 128, 0);
			graphics.rectangle(true, positionX, (positionY + height) - (height /number_of_divisions), width, height/number_of_divisions);
		}
	}
	
	/**
	 * Draws the rectangle and the aircraft slots of the the box 
	 * (number of divisions is the hangar size of the airport + 1 for the button to signal take off)
	 */
	private void drawBoxShell() {
		// Outer shell
		graphics.setColour(0, 128, 0);
		graphics.rectangle(false, positionX, positionY, width, height);
		
		// Inner lines
		double y =  (window.height() - height / number_of_divisions) - (window.height() - (positionY + height)); 
		for (int i = 0; i < number_of_divisions; i++) {
			graphics.line(positionX, y, positionX + width, y);
			y -= height / number_of_divisions;
		}
	}
	
	private void drawLabels() {	
		// Take off Button
		double y =  (window.height() - height / number_of_divisions) - (window.height() - (positionY + height));
		if (!airport.is_active) {
			graphics.print("TAKE OFF", positionX + ((width - 70)/2), y + 12); // positioning values can be altered as seen fit
		} else {
			graphics.print("AIRPORT BUSY", positionX + ((width - 100)/2), y + 12); // positioning values can be altered as seen fit
		}
		
		//Airport Hangar
		double y_position = (y + 12);
		double percentage_complete;
		for (int i = 0; i < airport.aircraft_hangar.size(); i++) {
			y_position -= (height / number_of_divisions);
			
			graphics.setColour(0, 128, 0);
			graphics.print(airport.aircraft_hangar.get(i).getName(), positionX + ((width - 70)/2), y_position);
			
			percentage_complete = barProgress(airport.time_entered.get(i));
			
			if (percentage_complete == 1) {
				graphics.setColour(128, 0, 0);
				graphics.line(positionX, y_position + 10, positionX + (width * percentage_complete), y_position + 10);
			} else {
				graphics.setColour(128, 128, 0);
				graphics.line(positionX, y_position + 10, positionX + (width * percentage_complete), y_position + 10);
			}
			
		}
		
	}
	
	private double barProgress(double time_entered) {
		double time_elapsed = Demo.getTime() - time_entered;
		if (time_elapsed > 5) {
			return 1;
		} else {
			return time_elapsed/5;
		}
	}
	
	private boolean isMouseOverButton(int x, int y) {
		if (x < positionX || x > positionX + width) return false; 
		if (y < ((positionY + height) - (height /number_of_divisions)) ||y > (positionY + height)) return false;
		return true;		
	}
			
	@Override
	public void mousePressed(int key, int x, int y) {
		if (key == input.MOUSE_LEFT && isMouseOverButton(x, y)) {
			clicked = true;
		}
	}


	@Override
	public void mouseReleased(int key, int x, int y) {
		clicked = false;
		if (key == input.MOUSE_LEFT && isMouseOverButton(x, y)) {
			if (!airport.is_active) {
				airport.signalTakeOff();
			}
		}
	}


	@Override
	public void keyPressed(int key) {
		
	}


	@Override
	public void keyReleased(int key) {

		
	}
}
