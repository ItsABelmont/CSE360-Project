package edu.asu.DatabasePart1;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * <p> StartCSE360 Class </p>
 * 
 * <p> Description: This Java class is the entry point into the program </p>
 * 
 * @authors Lynn Robert Carter - Blake Philbin - Just Wise - Reem Helal
 * 
 * @version 1.00  2024-10-08  Initial creation of the file
 * 
 */

public class StartCSE360 {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);
	private static final Password hashPassword = new Password();
	public static String userRole = "";
	public static String email = "";
	public static String password = "";

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
		
		databaseHelper.closeConnection();
		
		System.exit(0);
		
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
					String email2 = scanner.nextLine();
					email = email2;
					System.out.print("Enter Password: ");
					String password2 = scanner.nextLine();
					password = password2;
					
					userRole = Roles.ArrayToString(databaseHelper.login(email, password));
					
					//if a user has no roles than they do not exist and can not login
					if (userRole.equals(""))
						System.out.println("Invalid credentials. Try again!!");
					else
						checkRoles();
					
					
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
	
	/** Sets up admin account by prompting for email and password, ensuring they match, and registering the admin 
	 * @throws SQLException 
	 */
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
	
	/** Handles instructor account by prompting for email, password, and account registration 
	 * @throws SQLException
	 */
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
	
	/** Handles instructor login and account setup by prompting for first, middle, last and preferred names
	 * @throws SQLException
	 */
	private static void instructorLogin() throws SQLException {		// enhancement 
		
		if(!databaseHelper.getpreferredName().equals("placeholder")) {
			System.out.println("Hello instructor " + databaseHelper.getpreferredName() + " What would you like to do?");
		}
		else if(databaseHelper.getpreferredName().equals("placeholder") && databaseHelper.getFirstName().equals("placeholder")){
			System.out.println("Looks lik you need to finish setting"
					+ "up your account!");
			
			String preferred = "placeholder";
			System.out.println("Insert first name: ");
			String first = scanner.nextLine();
			System.out.println("Insert middle: ");
			String middle = scanner.nextLine();
			
			System.out.println("Insert last: ");
			String last = scanner.nextLine();
			
			System.out.println("Do you have a preferred name? Y/N");
			String yn = scanner.nextLine();
			if(yn.equals("Y")) {
				System.out.println("Insert preferred: ");
				
				preferred = scanner.nextLine();					
			}
			
			databaseHelper.finishRegistration(email, first, middle, last, preferred);
			System.out.println("Hello, What would you like to do?");
		}
		else {
			System.out.println("Hello instructor " + databaseHelper.getFirstName() + " What would you like to do?");
		}
		
		while(true) {
			System.out.println("1. Logout");
			String choice = scanner.nextLine();
				if(choice.equals("1"))
					return;
		}
		
	}
	
	/** Handles student account by prompting for e-mail, password, password confirmation, and account registration
	 * @throws SQLException
	 */
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
	
	/** Handles student login and account setup by prompting for first, middle, last and preferred names
	 * @throws SQLException
	 */
	private static void studentLogin() throws SQLException {		// enhancement 
		
		if(!databaseHelper.getpreferredName().equals("placeholder")) {
			System.out.println("Hello student " + databaseHelper.getpreferredName() + " What would you like to do?");
		}
		else if(databaseHelper.getpreferredName().equals("placeholder") && databaseHelper.getFirstName().equals("placeholder")){
			System.out.println("Looks lik you need to finish setting"
					+ "up your account!");
			
			String preferred = "placeholder";
			System.out.println("Insert first name: ");
			String first = scanner.nextLine();
			System.out.println("Insert middle: ");
			String middle = scanner.nextLine();
			
			System.out.println("Insert last: ");
			String last = scanner.nextLine();
			
			System.out.println("Do you have a preferred name? Y/N");
			String yn = scanner.nextLine();
			if(yn.equals("Y")) {
				System.out.println("Insert preferred: ");
				
				preferred = scanner.nextLine();					
			}
			
			databaseHelper.finishRegistration(email, first, middle, last, preferred);
			System.out.println("Hello, What would you like to do?");
		}
		else {
			System.out.println("Hello student " + databaseHelper.getFirstName() + " What would you like to do?");
		}
		
		while(true) {
			System.out.println("1. Logout");
			String choice = scanner.nextLine();
				if(choice.equals("1"))
					return;
		}
		
	}
	
	/** Handles admin account setup and management, and prompts for first, middle, last and preferred names 
	 * @throws SQLException
	 */
	private static void adminFlow() throws SQLException {
       //just test thingie databaseHelper.displayUsersByAdmin();
		if(!databaseHelper.getpreferredName().equals("placeholder")) {
			System.out.println("Hello " + databaseHelper.getpreferredName() + " What would you like to do?");
		}
		else if(databaseHelper.getpreferredName().equals("placeholder") && databaseHelper.getFirstName().equals("placeholder")){
			System.out.println("Looks lik you need to finish setting"
					+ "up your account!");
			
			String preferred = "placeholder";
			System.out.println("Insert first name: ");
			String first = scanner.nextLine();
			System.out.println("Insert middle: ");
			String middle = scanner.nextLine();
			
			System.out.println("Insert last: ");
			String last = scanner.nextLine();
			
			System.out.println("Do you have a preferred name? Y/N");
			String yn = scanner.nextLine();
			if(yn.equals("Y")) {
				System.out.println("Insert preferred: ");
				
				preferred = scanner.nextLine();					
			}
			
			databaseHelper.finishRegistration(email, first, middle, last, preferred);
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
			else if(choice.equals("3")) {
				System.out.println("Please choose a user email");
				String email = scanner.nextLine();
				System.out.println("Please choose the role to add");
				String role = scanner.nextLine();
				if(!role.equals("admin") && !role.equals("student") && !role.equals("instructor")) {
					System.out.println("That choice is not allowed choose again");
				}
				else
					databaseHelper.addUserRole(email, role);
			}
			else if(choice.equals("4")) {
				System.out.println("Please choose a user email");
				String email = scanner.nextLine();
				System.out.println("Please choose the role to remove");
				String role = scanner.nextLine();
				if(!role.equals("admin") && !role.equals("student") && !role.equals("instructor")) {
					System.out.println("That choice is not allowed choose again");
				}
				else
					databaseHelper.removeUserRole(email, role);
			}
			else if(choice.equals("5"))
				generateInviteCode();
			else if(choice.equals("6"))
				return;
			else
				System.out.println("Not valid choose a number");
		}
	}
	
	/** Creates random strings for new hashes and adds random string to end of the password */
	public static void generateInviteCode() {
		String role = "";
		int length = 12;
		
		System.out.println("What role would you like the user to have?");
		System.out.println("1. Admin\n2.Instructor\n3.Student");
		
		String choose = scanner.nextLine();
		
		if(choose.equals("1"))
			role = "admin";
		else if(choose.equals("2"))
			role = "instructor";
		else 
			role = "student";
		
		String invite = Password.generateRandomString(length);
		System.out.println("Here is the invite code.");
		System.out.println(invite);
		
		databaseHelper.addInviteUser(invite, new String[] {role});

		
	}
	
	/** Checks user roles and redirects them to either admin, instructor or student login flow */
	public static void checkRoles() {
		
		String[] arr = userRole.split(",", 0);
		
		boolean admin = false;
		boolean instructor = false;
		boolean student = false;
		
		int count = arr.length;
		
		for(int i = 0; i < arr.length; i++) {   
		    if(arr[i].equals("admin")) 
		    	admin = true;
		    if(arr[i].equals("instructor"))
		    	instructor = true;
		    if(arr[i].equals("student")) 
		    	student = true;
		} 
		
		if(count > 1) {
			while(true) {
				System.out.println("Looks like you have multiple role!\nChoose one to login as");
				if(admin)
					System.out.println("Admin");
				if(instructor)
					System.out.println("Instructor");
				if(student)
					System.out.println("Student");
				String choice = scanner.nextLine();
				//checks choice
				if(admin && choice.equals("admin") || choice.equals("Admin"))
					try {
						adminFlow();
						return;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				else if(instructor && choice.equals("instructor") || choice.equals("Instructor"))
					try {
						instructorLogin();
						return;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				else if(student && choice.equals("student") || choice.equals("Student"))
					try {
						studentLogin();
						return;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				else
					System.out.println("Not a valid choice!");
			}
		}
		else {
			if(admin)
				try {
					adminFlow();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			else if(instructor)
				try {
					instructorFlow();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			else
				try {
					studentLogin();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}


}
