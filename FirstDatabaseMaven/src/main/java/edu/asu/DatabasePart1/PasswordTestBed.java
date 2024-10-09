package edu.asu.DatabasePart1;

public class PasswordTestBed {
	static int numPassed = 0;							//Number of passed test cases
	static int numFailed = 0;							//Number of failed test cases
	
	
	//The PasswordEvaluator class should correctly accept valid passwords and rejects invalid ones.
	//Therefore, it is intentional to set some test cases fail the test case for valid passwords and vice versa.
	public static void main(String[] args) {
		//printing separator above the Testing Automation Title 
		System.out.println("____________________________________________________________________________");
		System.out.println("\nTesting Automation");
		
		// call the performTestCase method to check if the predefined test cases pass or fail 
		performTestCase(1, "Aa!15678", true);					//Test case 1 is expected to succeed since it satisfies all conditions. The password is valid

		performTestCase(2, "A!", false);						//Test case 2 is expected to fail for being too short. The password is valid
		
		performTestCase(3, "Aa!15678", false);					//Test case 3 is expected to fail. The password is valid
		
		performTestCase(4, "A!", true);							//Test case 4 is expected to pass. The password is invalid
		performTestCase(5, "", true);							//Test case 5 is expected to pass. The password is invalid
		
		performTestCase(6, "Abcdefghijklmnop!12345678", true);  // Test case 6 is expected to pass. The password is valid
	    performTestCase(7, "Abcdefgh!", false);       			// Test case 7 is expected to fail (no numeric digit). The password is invalid
		
		//Print how many test cases passed and how many failed 
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}

	/**performTestCase is the method that executes test cases using the PasswordEvaluator class
	 * It handles the execution of individual test cases */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
		//Printing test case information
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		String resultText= Password.hashFull(inputText);		//Evaluate the password using PasswordEvaluator class
		
		System.out.println();
		
		//Check if the result case is not empty
		if (resultText != "") {
			//Failure if the password was valid, but the expected output that said "invalid"
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			//Success if the invalid password matches the expected result that said "invalid"
			else {			
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		//If resultText was empty
		else {	
			if (expectedPass) {	
				//If the password is valid, then this is a success 
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			else {
				//If the password is invalid, but was judged as valid then this is a failure. 
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		displayEvaluation();
	}
	
	/**Display whether each condition was satisfied or not satisfied on the screen*/
	private static void displayEvaluation() {
		if (Password.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		if (Password.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");
	

		if (Password.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		if (Password.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		if (Password.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");
	}
	
}

