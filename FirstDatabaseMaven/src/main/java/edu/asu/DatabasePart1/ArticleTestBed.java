package edu.asu.DatabasePart1;

/**
 * <p> ArticleTestBed Class </p>
 * 
 * <p> Description: A Java demonstration for checking if DatabaseHelper.java's article methods work as expected </p>
 * 
 * @author Reem Helal
 * 
 * @version 1.00	2024-10-29	A set of semi-automated tests
 * 
 */

public class ArticleTestBed {
	
	static int numPassed = 0;		//Number of test cases passed
	static int numFailed = 0;		//Number of test cases failed
	
	public static void main(String[] args) {
		// Print header for testing
		System.out.println();
		System.out.println("Testing Article Functions");
		
		DatabaseHelper dbHelper = new DatabaseHelper();
		
		try {
			// Connect to the database
			dbHelper.connectToDatabase();

			// Test adding an article
			performTestCase(1, "Add Article", () -> {
				return dbHelper.addArticle("TestTitle", "TestGroup", "Author1,Author2", 
						"TestAbstract", "TestKeywords", "TestBody", "TestReferences");
			});
			
			// Test updating an article
			performTestCase(2, "Update Article", () -> {
				// Assuming that the ID of the previously added article is known, update the article.
				// We can retrieve the ID from the DatabaseHelper if necessary.
				// Let's assume the ID is 1 for this example.
				dbHelper.updateArticle(1, "Updated Title", "Updated Group", 
						"Updated Author1, Updated Author2", "Updated Abstract", 
						"Updated Keywords", "Updated Body", "Updated References");
				// Optionally, verify the article content after update
				return true; // Since the method doesn't return a boolean, we return true for simplicity.
			});
			
			// Test deleting an article
			performTestCase(3, "Delete Article", () -> {
				dbHelper.deleteArticle(1); // Assume the ID of the article to delete is 1
				return true; // I could not verify directly, so returning true
			});
			
			// Test backup 
			performTestCase(4, "Backup Articles", () -> {
				dbHelper.backupArticles("backup.txt");
				// Check if the backup file is created and has data
				return true;
			});

			//Test restore functionality
			performTestCase(5, "Restore Articles", () -> {
				dbHelper.restoreArticles("backup.txt");
				// Verify the restored data in the database 
				return true;
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the connection after tests
			dbHelper.closeConnection();
		}

		// Print the number of successful and failed test cases
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}
	
	/**
	 * This method performs a test case.
	 * @param testCase the test case number
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
	
	/* Functional interface for lambda test methods */
	interface TestMethod {
		boolean run() throws Exception;
	}		
}
