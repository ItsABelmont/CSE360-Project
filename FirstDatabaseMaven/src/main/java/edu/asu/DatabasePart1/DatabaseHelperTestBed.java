package edu.asu.DatabasePart1;

public class DatabaseHelperTestBed {

	static int numPassed = 0; //Number of test cases passed
	static int numFailed = 0; //Number of test cases failed
	
	public static void main(String[] args) {
		//Print header for testing 
		System.out.println();
		System.out.println("Testing Database Operations");
		
		DatabaseHelper dbHelper = new DatabaseHelper();
		
		//Connect to the database
		performTestCase(1, "Connect to Database", ()  -> {
			try {
				dbHelper.connectToDatabase();
				return dbHelper.getConnection() != null;
			} catch (Exception e) {
				return false;
			}
		});
		
		// Insert a user into the database
        performTestCase(2, "Insert User", () -> {
            try {
                dbHelper.register("testuser@example.com", "password123", "student");
                return dbHelper.doesUserExist("testuser@example.com", "student");
            } catch (Exception e) {
                return false;
            }
        });
		
		//Check if user exists
		performTestCase(3, "Check if user existence", () -> {
			try {
				return dbHelper.doesUserExist("testuser@example.com", "student");
			} catch (Exception e) {
				return false; 
			}
		});
		
		//Validate an invite code
		performTestCase(4, "Validate Invide Code",() -> {
			try {
				dbHelper.addInviteUser("invideCode123", "student");
				return dbHelper.validateInviteCode("invideCode123);");
			} catch (Exception e) {
				return false;
			}
		});
		
		//Delete a user from the database
		 performTestCase(5, "Delete User", () -> {
	            try {
	                dbHelper.deleteUser();
	                return !dbHelper.doesUserExist("testuser@example.com", "student");
	            } catch (Exception e) {
	                return false;
	            }
	       });
		
		//Print the number of successful and failed test cases
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
		
		dbHelper.closeConnection(); //Close the connection
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
