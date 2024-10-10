package edu.asu.DatabasePart1;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

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

        performTestCase(4, "Validate Invite Code", () -> {
            // Add the invite code and role to the invite table
            dbHelper.addInviteUser("inviteCode123", "student");

            // Simulate user input for username and password
            String simulatedInput = "testuser@example.com\npassword123\n";
            InputStream originalIn = System.in; // Save the original System.in

            try {
                System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); // Provide the simulated input

                // Call inviteCode and process input
                dbHelper.inviteCode("inviteCode123");
                
                // Check if the user was successfully registered
                return dbHelper.doesUserExist("testuser@example.com", "student");

            } finally {
                System.setIn(originalIn); // Restore the original System.in
            }
        });

        // Delete a user from the database
        performTestCase(5, "Delete User", () -> {
            dbHelper.deleteUser();
            return dbHelper.doesUserExist("testuser@example.com", "student");
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

    // Functional interface for lambda test methods
    @FunctionalInterface
    interface TestMethod {
        boolean run() throws Exception;
    }
}
