package edu.asu.DatabasePart1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StartCSE360 {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);

	public static void main( String[] args )
	{

		try { 
			
			databaseHelper.connectToDatabase();  // Connect to the database

			// Check if the database is empty (no users registered)
			if (databaseHelper.isDatabaseEmpty()) {
				System.out.println( "In-Memory Database  is empty" );
				//set up admin access
				setupAdmin();		//enhancement
			}
			else {
				System.out.println( "If you are an admin, then select A\nIf you are an instructor then select I\nIf you are a student then select S\nEnter your choice:  " );
				String role = scanner.nextLine();

				switch (role) {
				case "I":
					instructorFlow();
					break;
				case "A":
					adminFlow();
					break;
				case "S":			//enhancement
					studentFlow();
					break;
				default:
					System.out.println("Invalid choice. Please select 'a', 'u'");
					databaseHelper.closeConnection();
				}

			}
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			System.out.println("Good Bye!!");
			databaseHelper.closeConnection();
		}
	}

	private static void setupAdmin() throws SQLException {		//enhancement
		System.out.println("Setting up the Admin access.");
		System.out.print("Enter Admin Email: ");
		String email = scanner.nextLine();
		System.out.print("Enter Admin Password: ");
		String password = scanner.nextLine();
		System.out.print("Enter your First Name: ");
		String firstName= scanner.nextLine();
		System.out.print("Enter your Middle Name: ");
		String middleName = scanner.nextLine();
		System.out.print("Enter your Last Name: ");
		String lastName = scanner.nextLine();
		System.out.print("Enter your Preferred Name: ");
		String preferredName = scanner.nextLine();
		databaseHelper.register(email, password, firstName, middleName, lastName, preferredName, "admin");
		System.out.println("Admin setup completed.");

	}

	private static void instructorFlow() throws SQLException {		//enhancement
		String email = null;
		String password = null;
		String firstName = null;
		String middleName = null; 
		String lastName = null;
		String preferredName = null;
		System.out.println("instructor flow");
		System.out.print("What would you like to do 1.Register 2.Login  ");
		String choice = scanner.nextLine();
		switch(choice) {
		case "1": 
			System.out.print("Enter Instructor Email: ");
			email = scanner.nextLine();
			System.out.print("Enter Instructor Password: ");
			password = scanner.nextLine(); 
			System.out.print("Enter your First Name:");
			firstName= scanner.nextLine();
			System.out.print("Enter your Middle Name:");
			middleName = scanner.nextLine();
			System.out.print("Enter your Last Name:");
			lastName = scanner.nextLine();
			System.out.print("Enter your Preferred Name:");
			preferredName = scanner.nextLine();
			// Check if user already exists in the database
		    if (!databaseHelper.doesUserExist(email)) {
		        databaseHelper.register(email, password, firstName, middleName, lastName, preferredName, "instructor");
		        System.out.println("Instructor setup completed.");
		    } else {
		        System.out.println("Instructor already exists.");
		    }
			break;
		case "2":
			System.out.print("Enter Instructor Email: ");
			email = scanner.nextLine();
			System.out.print("Enter Instructor Password: ");
			password = scanner.nextLine();
			
			if (databaseHelper.login(email, password, "instructor")) {
				System.out.println("Instructor login successful.");

			} else {
				System.out.println("Invalid instructor credentials. Try again!!");
			}
			break;
		}
	}
	

	private static void studentFlow() throws SQLException {		// enhancement 
		String email = null;
		String password = null;
		String firstName = null;
		String middleName = null;
		String lastName = null;
		String preferredName = null;
		System.out.println("student flow");
		System.out.print("What would you like to do 1.Register 2.Login  ");
		String choice = scanner.nextLine();
		switch(choice) {
		case "1": 
			System.out.print("Enter Student Email: ");
			email = scanner.nextLine();
			System.out.print("Enter Student Password: ");
			password = scanner.nextLine(); 
			System.out.print("Enter your First Name:");
			firstName= scanner.nextLine();
			System.out.print("Enter your Middle Name:");
			middleName = scanner.nextLine();
			System.out.print("Enter your Last Name:");
			lastName = scanner.nextLine();
			System.out.print("Enter your Preferred Name:");
			preferredName = scanner.nextLine();
			// Check if user already exists in the database
		    if (!databaseHelper.doesUserExist(email)) {
		        databaseHelper.register(email, password, firstName, middleName, lastName, preferredName, "student");
		        System.out.println("Student setup completed.");
		    } else {
		        System.out.println("Student already exists.");
		    }
			break;
		case "2":
			System.out.print("Enter Student Email: ");
			email = scanner.nextLine();
			System.out.print("Enter Student Password: ");
			password = scanner.nextLine();
			
			if (databaseHelper.login(email, password, "student")) {
				System.out.println("Student login successful.");

			} else {
				System.out.println("Invalid user credentials. Try again!!");
			}
			break;
		}
	}

	private static void adminFlow() throws SQLException {
	        System.out.println("Admin flow");
	        System.out.print("Enter Admin Email: ");
	        String email = scanner.nextLine();
	        System.out.print("Enter Admin Password: ");
	        String password = scanner.nextLine();
	        if (databaseHelper.login(email, password, "admin")) {
	            System.out.println("Admin login successful.");
	            completeAccountSetup(email); // Ensure the account setup is complete
	            databaseHelper.displayUsersByAdmin();
	        } else {
	            System.out.println("Invalid admin credentials. Try again!!");
	        }
	    }

	

	public static void completeAccountSetup(String email) throws SQLException {
        String query = "SELECT firstName, lastName FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = databaseHelper.getConnection().prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");

                    // If first or last name is missing, prompt the user to complete the profile
                    if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
                        System.out.println("Please complete your profile.");
                        System.out.print("Enter First Name: ");
                        firstName = scanner.nextLine();
                        System.out.print("Enter Last Name: ");
                        lastName = scanner.nextLine();

                        // Update the user's profile in the database
                        String updateQuery = "UPDATE cse360users SET firstName = ?, lastName = ? WHERE email = ?";
                        try (PreparedStatement updatePstmt = databaseHelper.getConnection().prepareStatement(updateQuery)) {
                            updatePstmt.setString(1, firstName);
                            updatePstmt.setString(2, lastName);
                            updatePstmt.setString(3, email);
                            updatePstmt.executeUpdate();
                        }
                        System.out.println("Profile updated successfully.");
                    }
                }
            }
        }
    }
}
