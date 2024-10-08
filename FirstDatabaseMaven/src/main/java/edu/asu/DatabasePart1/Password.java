package edu.asu.DatabasePart1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This static class provides methods to hash a password
 * 
 * @author Just Wise
 *
 */
public class Password {
	/**
	 * This should be the primary hashing function for passwords
	 * 
	 * @param password
	 * @param extra
	 * @return
	 */
	public static String hashFull(String password, String extra) {
		return hash(password + extra);
	}
	
	/**
	 * This function returns the SHA-256 hash of a given password
	 * 
	 * @param password
	 * @return
	 */
	public static String hash(String password) {
		//Create the string for the hash to be stored in
		String hash = "";
		try {
			//Create a SHA-256 hasher
			MessageDigest md = MessageDigest.getInstance("SHA-256");
		    //Add the password to the hasher
			md.update(password.getBytes());
			//Get the binary output of the hasher
		    byte[] digest = md.digest();
		    
		    //For every byte, convert it into the 2 hexidecimal characters of the string
		    for (byte i : digest) {
		    	hash += Integer.toHexString(byteToHexTop(i));
		    	hash += Integer.toHexString(byteToHexLow(i));
		    }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash;
	}
	
	/**
	 * Converts the first four bits of a byte into a single 4-bit number
	 * @param oneByte
	 * @return
	 */
	private static int byteToHexTop(byte oneByte) {
		return (oneByte >> 4) & 15;
	}
	
	/**
	 * Converts the last four bits of a byte into a single 4-bit number
	 * @param oneByte
	 * @return
	 */
	private static int byteToHexLow(byte oneByte) {
		return oneByte & 15;
	}
}