package edu.asu.DatabasePart1;

public class Roles {
	/**
	 * Takes an input of the form "admin,instructor,student" and returns a String[]
	 * @param input
	 * @return
	 */
	public static String[] stringToArray(String input) {
		if (input.length() == 0)
			return new String[0];
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
