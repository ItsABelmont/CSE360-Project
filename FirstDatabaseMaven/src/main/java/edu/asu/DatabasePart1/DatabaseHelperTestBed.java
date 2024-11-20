package edu.asu.DatabasePart1;

/**
 * <p> DatabaseHelperTestBed Class </p>
 * 
 * <p> Description: A Java demonstration for checking if DatabaseHelper Class lambda methods work as expected </p>
 * 
 * @author Reem Helal
 * 
 * @version 1.00	2024-10-09	A set of semi-automated tests
 * 
 */

public class DatabaseHelperTestBed {

    static int numPassed = 0; // Number of test cases passed
    static int numFailed = 0; // Number of test cases failed

    public static void main(String[] args) {
        // Print header for testing
        System.out.println();
        System.out.println("Testing Database Operations");

        DatabaseHelper dbHelper = new DatabaseHelper();

        // Connect to the database
        performTestCase(1, "Connect to Database", () -> {
            dbHelper.connectToDatabase(); // Let this throw any exception if it fails
            return dbHelper.getConnection() != null; // This will only be evaluated if the connection succeeds
        });

        // Insert a user into the database
        performTestCase(2, "Insert User", () -> {
            dbHelper.register("testuser@example.com", "password123", "student");
            return dbHelper.doesUserExist("testuser@example.com", "student");
        });

        // Check if user exists
        performTestCase(3, "Check User Existence", () -> {
            return dbHelper.doesUserExist("testuser@example.com", "student");
        });
        
        //Validates if the invite code allows a user to register and checks if the user is successfully added to the database.
        performTestCase(4, "Validate Invite Code", () -> {
            // Add the invite code and role to the invite table
            dbHelper.addInviteUser("inviteCode123", new String[]{"student"});
            
            // Call inviteCode, which will ask the user to input the username and password
            dbHelper.inviteCode("inviteCode123");

            // Check if the user was successfully registered based on the provided username
            System.out.println("Checking registration...");
            
            // Verify if the user was successfully registered with the given email and role.
            return dbHelper.doesUserExist("testuser@example.com", "student");
        });


        // Delete a user from the database
        performTestCase(5, "Delete User", () -> {
            dbHelper.deleteUser("testuser@example.com");
            return dbHelper.doesUserExist("testuser@example.com", "student");
        });
        
        //Add a new article
        performTestCase(6, "Admin Create Special Group", () -> {
            // Test setup
            String userEmail = "testuser@example.com";
            String title = "a";
            String role = "admin";
            String level = "b";
            String group = "c";
            String authors = "d";
            String abstrac = "e";
            String keywords = "test1, test2";
            String body = "Body";
            String references = "Reference 1";
            
            // Perform the operation
            return dbHelper.adminCreateSpecialGroup(userEmail, role, title, level, group, authors, abstrac, keywords, body, references);
            
        });
        
        //Add User Access
        performTestCase(7, "Check if user has that role", () -> {
       	String userEmail = "testuser@example.com";
       	String userRole = "student";
       	String group = "CSE360";
       	
        	return dbHelper.addUserAccessSpecial(userEmail, userRole, group);
       	
      });
     
        
        // Print the number of successful and failed test cases
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);

        dbHelper.closeConnection(); // Close the connection
        
    
    }

	/**
     * This is the method that performs test cases to make sure DatabaseHelper works properly
     * @param testCase Test case number
     * @param description A description of the testCase
     * @param testMethod The function that runs the test logic
     */
    private static void performTestCase(int testCase, String description, TestMethod testMethod) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Description: " + description);

        try {
            if (testMethod.run()) {
                System.out.println("***Success*** Test case " + testCase + " passed.");
                numPassed++;
            } else {
                System.out.println("***Failure*** Test case " + testCase + " failed.");
                numFailed++;
            }
        } catch (Exception e) {
            System.out.println("***Failure*** Test case " + testCase + " threw an exception: " + e.getMessage());
            numFailed++;
        }
    }

    /* Functional interface for lambda test methods*/
    interface TestMethod {
        boolean run() throws Exception;
    }
    
    
}
