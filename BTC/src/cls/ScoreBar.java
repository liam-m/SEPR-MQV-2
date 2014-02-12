package cls;

import java.util.Arrays;

import lib.jog.graphics;

public class ScoreBar {
	
	public final int MAX_DIGITS_IN_SCORE = 7;
	public final int MAX_SCORE = 9999999;
	
	private int current_digits_in_score;

	
	/**
	 * Records the total score the user has achieved at a given time.
	 */	
	private int totalScore = 0;
	
	/**
	 * Getter for total score in case it is needed outside the Demo class.
	 * @return totalScore
	 */	
	public int getTotalScore() {
		if (totalScore > MAX_SCORE) totalScore = MAX_SCORE;
		return totalScore;
	}
	
	/**
	 * Allows for increasing total score outside of Demo class.
	 * @param scoreDifference
	 */	
	public void increaseTotalScore(int amount) {
		if (amount > 0)
			totalScore += amount;
	}
	
	/**
	 * Initially set to 1. This is the main multiplier for score. As more planes leave airspace 
	 * it may be incremented based on the value of multiplierVariable (the interval it is currently in).
	 */	
	public int multiplier = 1; 
	
	/**
	 * Allows to reset multiplier to 1.
	 */	
	public void resetMultiplier() {
		this.multiplier = 1;
	} 
	
	/**
	 * This variable is used to increase main multiplier for score. Score multiplier varies based on 
	 * the immediate range this variable is in. I.e. When it is < 10 -> multiplier = 1, when 
	 *  10 <= multiplierVariable < 40 -> multiplier = 2, etc. 
	 */
	private int multiplierVariable = 0;
	
	/**
	 * Used to get multiplierVariable outside of Demo class.
	 * @return multiplierVariable
	 */	
	public int getMultiplierVariable() {
		return multiplierVariable;
	}
	
	// Necessary for testing
		
	/**
	 * This method should only be used publically for unit testing. Its purpose is to update multiplierVariable
	 * outside of Demo class. 
	 * @param difference
	 */
	public void increaseMultiplierVariable(int difference) {
		multiplierVariable += difference;
		updateMultiplier();
	}
	
	public void decreaseMultiplierVariable(int difference) {
		if (difference > multiplierVariable) {
			multiplierVariable = 0;
		} else {
			multiplierVariable -= difference;
		}
		updateMultiplier();
	}
		
	/**
	 * Updates multiplier based on the value of multiplierVariable and the interval it is 
	 * currently in.
	 */		
	private void updateMultiplier() {
		if (multiplierVariable < 10) {
			if (multiplierVariable < 0)
				multiplierVariable = 0;
			multiplier = 1;
		}
		else if (multiplierVariable < 40) { 
			multiplier = 3;
		}
		else if (multiplierVariable < 80) {
			multiplier = 5;
		}
		else if (multiplierVariable < 130) { 
			multiplier = 7;
		}
		else {
			multiplier = 10;
		}
	}
	
	public void drawScore() {
		/**
		 * Takes the maximum possible digits in the score and calculates how many of them are currently 0.
		 * 
		 */
		current_digits_in_score = (getTotalScore() != 0) ? (int)Math.log10(getTotalScore()) + 1 : 0; // exception as log10(0) is undefined.
		char[] chars = new char[MAX_DIGITS_IN_SCORE - current_digits_in_score];
		Arrays.fill(chars, '0');
		String zeros = new String(chars);
		
		/**
		 * Prints the unused score digits as 0s, and the current score.
		 */
		graphics.setColour(0, 128, 0, 128);
		graphics.print(zeros, 264, 3, 5);
		graphics.setColour(0, 128, 0);
		if (getTotalScore() != 0) graphics.printRight(String.valueOf(getTotalScore()), 544, 3, 5, 0);
		
		
	}
	
	public void drawMultiplier() {
		graphics.setColour(0, 128, 0, 64);
		
		int bar_segments = 16;
		int bar_segment_dif = 24;
		int bar_x_start = 608;
		int bar_y = 8;
		for (int i = 0; i <= bar_segments; i++) {
			graphics.rectangle(true, bar_x_start, bar_y, 16, 32);
			bar_x_start += bar_segment_dif;
		}
		graphics.setColour(0, 128, 0);
		
		bar_x_start += 16;
		String mul_var = String.format("%d", multiplier);
		graphics.print("x", bar_x_start, 18, 3);
		graphics.print(mul_var, bar_x_start + 16, 4, 5);
	}
}
