package edu.asu.DatabasePart1;

/**
 * <p> PasswordTestBed Class </p>
 * 
 * <p> Description: A Java demonstration for comparing the hash of two passwords </p>
 * 
 * @author Reem Helal
 * 
 * @version 1.00	2024-10-08	A set of semi-automated test cases
 * 
 */

public class PasswordTestBed {
    static int numPassed = 0; // Number of passed test cases
    static int numFailed = 0; // Number of failed test cases

    public static void main(String[] args) {
        // Print header for testing
        System.out.println("\nTesting Password Hashing");

        // Test cases: perform hashing and compare hashes
        performTestCase(1, "password123", "password123");   // Test case 1, same password, expected to pass
        performTestCase(2, "Password!@", "Password!@");   // Test case 2, same password with special chars, expected to pass
        performTestCase(3, "mypassword", "differentPassword"); // Test case 3, different passwords, expected to fail
        performTestCase(4, "12345", "12345");                // Test case 4, numeric passwords, expected to pass
        performTestCase(5, "samePassword", "samePassword");  // Test case 5, identical passwords, expected to pass
        
        // Print how many tests passed and how many failed
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    /**
     * performTestCase compares the hash of two passwords and check if they are the same.
     * 
     * @param testCase   Test case number
     * @param password1  First password to hash
     * @param password2  Second password to hash
     */
    private static void performTestCase(int testCase, String password1, String password2) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Password 1: \"" + password1 + "\"");
        System.out.println("Password 2: \"" + password2 + "\"");

        // Hash the passwords
        String hash1 = Password.hash(password1);
        String hash2 = Password.hash(password2);

        // Compare the two hashes
        if (hash1.equals(hash2)) {
            // Success if the hashes match
            System.out.println("***Success*** The passwords <" + password1 + "> and <" + password2 + "> produce the same hash.");
            numPassed++;
        } else {
            //Failure if the hashes don't match 
        	//When Failure happens, print out hash2 and hash2
            System.out.println("***Failure*** The passwords <" + password1 + "> and <" + password2 + "> do not produce the same hash.");
            System.out.println("Hash 1: " + hash1);
            System.out.println("Hash 2: " + hash2);
            numFailed++;
        }
    }
}