package edu.asu.DatabasePart1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * </p> Password Class </p> 
 * 
 * </p> Description: This static class provides methods to hash a password </p>
 * 
 * @author Just Wise
 *
 * @version 1.00	2024-10-08	This method deals with hashing a password
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
	
	/**
	 * Generates a random string of a given length
	 * @param length
	 * @return
	 */
	public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}




