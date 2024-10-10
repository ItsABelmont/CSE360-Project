package edu.asu.DatabasePart1;

/**
 * </p> Roles Class </p>
 * 
 * <p> Description: This class provides utility methods for converting between a string separated by commas of roles and an array. </p>
 * 
 * @authors Just Wise
 * 
 * @version 1.0	2024-10-9	
 */
public class Roles {
	/**
	 * Takes an input of the form "admin,instructor,student" and returns a String[]
	 * @param input
	 * @return
	 */
	public static String[] stringToArray(String input) {
		return input.split(",");
	}
	
	/**
	 * Takes an input of a String array and returns a single string with each role separated by commas
	 * @param input
	 * @return
	 */
	public static String ArrayToString(String[] input) {
		return String.join(",", input);
	}
}
