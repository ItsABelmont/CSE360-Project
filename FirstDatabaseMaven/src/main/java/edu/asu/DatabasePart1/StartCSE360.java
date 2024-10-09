package edu.asu.DatabasePart1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import java.util.Random;

public class StartCSE360 {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);
	private static final Password hashPassword = new Password();

	public static void main( String[] args )
	{
		try {
			databaseHelper.connectToDatabase();
		}
		catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		
		GUI.start(databaseHelper);
		
		
		/*try { 
			int length = 8;
			databaseHelper.connectToDatabase();  // Connect to the database
			
			// Check if the database is empty (no users registered)
			if (databaseHelper.isDatabaseEmpty()) {
				System.out.println( "In-Memory Database  is empty" );
				//set up admin access
				setupAdmin();		//enhancement
			}
			else {
				System.out.print("What would you like to do 1.Register 2.Login  ");
				String choice = scanner.nextLine();

				switch (choice) {
				case "1":
					System.out.println("Insert invite code.");
					String choose = scanner.nextLine();
					databaseHelper.inviteCode(choose);
					
//					if(choose.equals("1")) {
//						setupAdmin();
//					}
//					else if(choose.equals("2")) {
//						instructorFlow();
//					}
//					else {
//						studentFlow();
//					}
					
					break;
				case "2":
					System.out.print("Enter Email: ");
					String email = scanner.nextLine();
					System.out.print("Enter Password: ");
					String password = scanner.nextLine();
					if (databaseHelper.login(email, password).equals("admin")) {
						System.out.println("Login successful!");
						adminFlow();
						
					} else {
						System.out.println("Invalid credentials. Try again!!");
					}
					
					break;
				default:
					System.out.println("Invalid choice. Please select '1', '2'");
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
		}*/
	}

	private static void setupAdmin() throws SQLException {		//enhancement
		System.out.println("Setting up the Admin access.");
		System.out.print("Enter Admin Email: ");
		String email = scanner.nextLine();
		System.out.print("Enter Admin Password: ");
		String password = scanner.nextLine();
		String password2 = "";

		while(!password.equals(password2)) {
			System.out.print("Please enter the password again");
			password2 = scanner.nextLine();
			if(password.equals(password2)) {
				System.out.println("They Match!");
			}
			else {
				System.out.println("They do not match! Try again");
			}
		}

		if (databaseHelper.doesUserExist(email, "admin")) {
	        System.out.println("You already exist with this role.");
	    } else {
	    	databaseHelper.register(email, password, "admin");
			System.out.println("Instructor setup completed.");
	    }

	}

	private static void instructorFlow() throws SQLException {		//enhancement
		String email = null;
		String password = null;

		System.out.println("instructor flow");
		
		System.out.print("Enter Instructor Email: ");
		email = scanner.nextLine();
		System.out.print("Enter Instructor Password: ");
		password = scanner.nextLine();
		String password2 = "";
			
		while(!password.equals(password2)) {
			System.out.print("Please enter the password again");
			password2 = scanner.nextLine();
			if(password.equals(password2)) {
				System.out.println("They Match!");
			}
			else {
				System.out.println("They do not match! Try again");
			}
		}
		
		if (databaseHelper.doesUserExist(email, "instructor")) {
	        System.out.println("You already exist with this role.");
	    } else {
	    	databaseHelper.register(email, password, "instructor");
			System.out.println("Instructor setup completed.");
	    }
	
	}
	

	private static void studentFlow() throws SQLException {		// enhancement 
		String email = null;
		String password = null;

		System.out.println("student flow");
		
		System.out.print("Enter Student Email: ");
		email = scanner.nextLine();
		System.out.print("Enter Student Password: ");
		password = scanner.nextLine();
		String password2 = "";
			
		while(!password.equals(password2)) {
			System.out.print("Please enter the password again");
			password2 = scanner.nextLine();
			if(password.equals(password2)) {
				System.out.println("They Match!");
			}
			else {
				System.out.println("They do not match! Try again");
			}
		}
		
		if (databaseHelper.doesUserExist(email, "student")) {
	        System.out.println("You already exist with this role.");
	    } else {
	    	databaseHelper.register(email, password, "student");
			System.out.println("Instructor setup completed.");
	    }
	}

	private static void adminFlow() throws SQLException {
        databaseHelper.displayUsersByAdmin();

		if(!databaseHelper.getpreferredName().equals("placeholder")) {
			System.out.println("Hello " + databaseHelper.getpreferredName() + " What would you like to do?");
		}
		else if(databaseHelper.getpreferredName().equals("placeholder") && databaseHelper.getFirstName().equals("placeholder")){
			System.out.println("Hello, What would you like to do?");
		}
		else {
			System.out.println("Hello " + databaseHelper.getFirstName() + " What would you like to do?");
		}
		
		while(true) {
		System.out.println("1. Print Users\n2. Delete User\n3. Add user role\n4. Remove user role\n5. Generate Invite Code\n6. Logout");
		String choice = scanner.nextLine();
			if(choice.equals("1"))
				databaseHelper.displayUsersByAdmin();
			else if(choice.equals("2"))
				databaseHelper.deleteUser();
			else if(choice.equals("3"))
				databaseHelper.addUserRole();
			else if(choice.equals("4"))
				databaseHelper.removeUserRole();
			else if(choice.equals("5"))
				generateInviteCode();
			else if(choice.equals("6"))
				return;
			else
				System.out.println("Not valid choose a number");
		}
	}
	
	//makes random strings for new hashs

	
	//add random string to end of the password
	
	public static void generateInviteCode() {
		String role = "";
		int length = 8;
		
		System.out.println("What role would you like the user to have?");
		System.out.println("1. Admin\n2.Instructor\n3.Student");
		
		String choose = scanner.nextLine();
		
		if(choose.equals("1"))
			role = "admin";
		else if(choose.equals("2"))
			role = "instructor";
		else 
			role = "student";
		
		String invite = hashPassword.hash(hashPassword.generateRandomString(length));
		System.out.println("Here is the invite code.");
		System.out.println(invite);
		try {
			databaseHelper.addInviteUser(invite, role);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}


}
