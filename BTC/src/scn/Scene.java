package scn;

import lib.jog.audio;

public abstract class Scene implements lib.jog.input.EventHandler {


	protected btc.Main main;

	/**
	 * Top level Constructor for a scene
	 * @param main the main class holding the scene, ie the running instance of the game
	 */
	protected Scene(btc.Main main) {
		this.main = main; 
	}

	// Additional constructor for testing purposes

	/**
	 * This constructor should only be used for unit testing. Its purpuse is to allow an instance
	 * of demo class to be created without an instance of Main class (effectively launching the game)
	 */

	@Deprecated
	protected Scene() {

	}

	/**
	 * Handles initialisation of a scene. Runs once only at the start of the scene
	 */
	abstract public void start();

	/**
	 * Handles updates of all objects requiring updates in the scene
	 * Runs regularly when called by Main
	 * @param time_difference the delta time since the last update was carried out
	 */
	abstract public void update(double time_difference);

	/**
	 * Handles drawing of all drawable objects in the scene to the window
	 * Runs regularly when called by main
	 */
	abstract public void draw();

	/**
	 * Used to cleanly exit a scene, e.g. halting the scene's music so it does not overlap the next scene's music
	 * Runs once only when a scene is closed
	 */
	abstract public void close();

	/**
	 * Plays a sound effect
	 * Can be used by a scene's object to request a sound to be played, e.g. aircraft can request a warning beep
	 * when their separation radius is infringed.
	 * @param sound the sound to be played
	 */
	abstract public void playSound(audio.Sound sound);

}



