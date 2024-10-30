package edu.asu.DatabasePart1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * </p> DatabaseHelper Class </p>
 * 
 * Description: This class allows user registration, login, and other administration functionalities 
 * 
 * @authors Lynn Robert Carter - Blake Thilbin - Just Wise
 * 
 * @version 1.0	2024-10-9	An implementation of user registration and login features
 */
class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/firstDatabase";

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	
	public static String universalfirstName = "";
	public static String universalpreferredName = "";
	public static String universalRole = "";

	
	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	/**
	 * This establishes a connection to the database. It also creates an object statement and necessary tables.
	 * @throws SQLException if an error occurs.
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * Creates the necessary tables for users and invites if they don't already exist.
	 * @throws SQLException
	 */
	private void createTables() throws SQLException {
		//main user table
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "email VARCHAR(255), "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR(255), "
				+ "preferredName VARCHAR(255), "
				+ "role VARCHAR(255), "
				+ "random VARCHAR(255), "
				+ "userReset VARCHAR(255))";
		statement.execute(userTable); //initializes main table
		String inviteTable = "CREATE TABLE IF NOT EXISTS invite ("
				+ "invite VARCHAR(255), "
				+ "role VARCHAR(255))";
		statement.execute(inviteTable); //initializes invite table
		String articleTable = "CREATE TABLE IF NOT EXISTS articles ("
				+ "id LONG UNIQUE, "
				+ "title VARCHAR(255), "
				+ "groupName VARCHAR(255), "
				+ "authors VARCHAR(1000), "
				+ "abstract VARCHAR(10000), "
				+ "keywords VARCHAR(1000), "
				+ "body VARCHAR(100000000), "
				+ "references VARCHAR(10000), "
				+ "delete VARCHAR(1))";
		statement.execute(articleTable);
		String expireTable = "CREATE TABLE IF NOT EXISTS expire ("
				+ "email VARCHAR(255), "
				+ "password VARCHAR(255), "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(expireTable); //initializes expire table
		
		//these function checks all temp passwords to see if they expire and to delete them in 30 days
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable task = () -> {
		    try {
		        String deleteExpired = "DELETE FROM expire WHERE created_at < TIMESTAMPADD(DAY, -30, NOW())";
		        statement.execute(deleteExpired);
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		};
		scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.DAYS);
		
	}


	/**
	 * Checks if the database contains any users.
	 * @return true if the database is empty, otherwise it's false.
	 * @throws SQLException
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		
		return true;
	}
	
	/**
	 * This function creates a new account and adds it to the database
	 * @param email
	 * @param password
	 * @param roles
	 */
	public void register(String email, String password, String[] roles) {
		String insertUser = "INSERT INTO cse360users (email, password, firstName, middleName, lastName, preferredName, role, random, userReset) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String random = Password.generateRandomString(8);
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, Password.hashFull(password, random));
			pstmt.setString(3, "placeholder");
			pstmt.setString(4, "placeholder");
			pstmt.setString(5, "placeholder");
			pstmt.setString(6, "placeholder");
			pstmt.setString(7, Roles.ArrayToString(roles));
			pstmt.setString(8, random);
			pstmt.setString(9, "f");
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		insertUser = "INSERT INTO invite (invite, role) VALUES (?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, "invites");
			pstmt.setString(2, "roles");
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		insertUser = "INSERT INTO expire (email, password) VALUES(?,?)";
		
		//inserts expire user values equating to zero
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, "");
			pstmt.setString(2, "");
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this reset the user password with the temp one time password
	 * @param email
	 * @return
	 */
	public String resetPassword(String email) {
		
		//updates main user telling them that they do not need to reset anymore
		String sql = "UPDATE cse360users SET userReset = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, "f");
			statement.setString(2, email);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String pass = Password.generateRandomString(12);
		
		String query = "SELECT * FROM cse360users";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
		
			//gets the random for string which is a security measure
			String full = "";
			while(rs.next()) {
				if(rs.getString("email").equals(email)) {
					full = Password.hashFull(pass, rs.getString("random"));
				}
			}
		
			//updates the base password with the password hash full from password class hash security
			sql = "UPDATE cse360users SET password = ? WHERE email = ?";
			
			try(PreparedStatement statement = connection.prepareStatement(sql)){
				statement.setString(1, full);
				statement.setString(2, email);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return pass;
		
	}
	
	/**
	 * Sets the password of an account
	 * @param email
	 * @param password
	 */
	public void setPassword(String email, String password) {
		try {
			String query = "SELECT * FROM expire";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				if(rs.getString("email").equals(email)) {
					String sql = "UPDATE cse360users SET password = ? WHERE email = ?";
					PreparedStatement statement = connection.prepareStatement(sql);
					statement.setString(1, Password.hashFull(password, rs.getString("random")));
					statement.setString(2, email);
					statement.executeUpdate();
					return;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * asks for temp password to see if it is a valid one based off of the email inserted
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public String userReset(String email) throws SQLException{
		Scanner scanner = new Scanner(System.in);
		
		String query = "SELECT * FROM expire";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query); 
		
		System.out.println("Looks like your account is trying to be reset go ahead\n"
				+ "and insert the temp password you where given to reset your password!\n");
		
		//checks to see if it is valid and if so go to resetPassword function/method
		while(rs.next()) {
			if(rs.getString("email").equals(email)) {
				String pass = scanner.nextLine();
				if(rs.getString("password").equals(pass)) {
					String hold = resetPassword(email);
					
					String sql = "DELETE FROM expire WHERE password = ?";
					PreparedStatement statement = connection.prepareStatement(sql);
					statement.setString(1, pass);
					statement.executeUpdate();
					
					return hold;
				}
				else {
					System.out.println("That is not the password!");
				}
			}
		}
		return "";
		
	}
	
	/**
	 * This register function takes the database string representation and is used for the console-based version of the app
	 * @param email
	 * @param password
	 * @param roles
	 * @throws SQLException
	 */
	public void register(String email, String password, String roles) throws SQLException {
		String insertUser = "INSERT INTO cse360users (email, password, firstName, middleName, lastName, preferredName, role, random) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		String random = Password.generateRandomString(8);
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, Password.hashFull(password, random));
			pstmt.setString(3, "placeholder");
			pstmt.setString(4, "placeholder");
			pstmt.setString(5, "placeholder");
			pstmt.setString(6, "placeholder");
			pstmt.setString(7, roles);
			pstmt.setString(8, random);
			pstmt.executeUpdate();
		}
		insertUser = "INSERT INTO invite (invite, role) VALUES (?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, "invites");
			pstmt.setString(2, "roles");
			pstmt.executeUpdate();

		}
	}
	/*
	 * String save = "";
		String pass = "";
		
		while(rs.next()) {
			if(rs.getString("email").equals(email)) {
				pass = rs.getString("random");
			}
		}
		//resets it if i introduce it again
		rs = stmt.executeQuery(sql);
		int count = 0;
	
		while(rs.next()) { 
			if(rs.getString("email").equals(email) && rs.getString("firstName").equals("placeholder")) {
				System.out.println("Finish Setting up your account");
				finishRegistration(email);
			}
			if(rs.getString("email").equals(email) && rs.getString("password").equals(hashPassword.hash(password + pass))) {
				if(count == 0) {
					save = rs.getString("role");
					this.universalfirstName = rs.getString("firstName");
					this.universalpreferredName = rs.getString("preferredName");
				}
				else {
					System.out.println(rs.getString("role"));
				}
				this.universalfirstName = rs.getString("firstName");
				this.universalpreferredName = rs.getString("preferredName");
				count++;
			}
		} 
		
		
		if(count > 1) {
			System.out.println(save);
			System.out.println("Looks like you have more than one role!\nWhich one would you like to login as?");
			save = scanner.nextLine();
			this.universalRole = save;
		}
		
		System.out.println("HELLO, " + save);
	 */
	
	/**
	 * check to see if user must reset before they log on
	 * @param email
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public String doesUserReset(String email, String password) throws SQLException{
		String query = "SELECT * FROM cse360users";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query); 
		
		String newPassword = "";	
		
		//check if they must reset based off of the user reset value being t or true for example
		while(rs.next()) {
			if(rs.getString("userReset").equals("t") && rs.getString("email").equals(email) && rs.getString("password").equals(Password.hashFull(password,  rs.getString("random")))) {
				return newPassword = userReset(email);
			}
		}
		
		return newPassword;
		
	}
	
	/**
	 * Returns if a user needs to reset their password
	 * @param email
	 * @return
	 */
	public boolean shouldUserReset(String email) {
		String query = "SELECT * FROM cse360users";
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query); 
			
			//check if they must reset based off of the user reset value being t or true for example
			while(rs.next()) {
				if(rs.getString("userReset").equals("t") && rs.getString("email").equals(email)) {
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Authenticates a user by verifying the email and password. If successful, return the user's role.
	 * @return the user's role if login is successful, empty string otherwise
	 * @throws SQLException
	 */
	public String[] login(String email, String password) {
		String query = "SELECT * FROM cse360users";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query); 
			
			//The main changes for random
			
			while(rs.next()) {
				//big if determines if user is in and gives their role
				if(rs.getString("email").equals(email) && rs.getString("password").equals(Password.hashFull(password, rs.getString("random")))) {
					this.universalpreferredName = rs.getString("preferredName"); 
					this.universalfirstName = rs.getString("firstName");
					return Roles.stringToArray(rs.getString("role"));
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new String[] {};
	}
	
	public String getFirstName() {
		return this.universalfirstName;
	}
	public String getRole() {
		return this.universalRole;
	}
	public String[] getRoles() {
		return Roles.stringToArray(this.universalRole);
	}
	public String getpreferredName() {
		return this.universalpreferredName;
	}
	
	/**
	 * Checks if an invite code is valid or not
	 * @param invite
	 * @throws SQLException
	 */
	public boolean validateInviteCode(String invite) {
		try {
			String sql = "SELECT * FROM invite"; 
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql); 
	
			while(rs.next()) {
				if(rs.getString("invite").equals(Password.hash(invite))) {
					return true;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns the role of a given invite code
	 * @param invite
	 * @throws SQLException
	 */
	public String[] getInviteCodeRoles(String invite) {
		try {
			String sql = "SELECT * FROM invite"; 
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql); 
	
			while(rs.next()){
				if(rs.getString("invite").equals(Password.hash(invite))) {
					return Roles.stringToArray(rs.getString("role"));
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return new String[] {};
	}
	
	/**
	 * Validates an invite code, prompts for user credentials.
	 * Registers the user with the associated role.
	 * @throws SQLException
	 */
	public void inviteCode(String invite) throws SQLException{
		String sql = "SELECT * FROM invite"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		Scanner scanner = new Scanner(System.in);

		while(rs.next()){
			if(rs.getString("invite").equals(invite)) {
				System.out.println("Huzzah! It works give a username and password\nUsername: ");
				String email = scanner.nextLine();
				System.out.println("Password: ");
				String password = scanner.nextLine();
				register(email, password, rs.getString("role"));
				break;
			}
		}
		
	}
	
	/**
	 * Adds a new invite code with an associated role to the database.
	 * @throws SQLException
	 */
	public void addInviteUser(String invite, String[] roles) {
		String insertUser = "INSERT INTO invite (invite, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, Password.hash(invite));
			pstmt.setString(2, Roles.ArrayToString(roles));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns true if the user needs to setup their name information
	 * @param email
	 * @return
	 */
	public boolean checkFinish(String email) {
		try {
			String sql = "SELECT * FROM cse360users";
			Statement stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				if (rs.getString("firstName").equals("placeholder") && rs.getString("email").equals(email)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}	
	
	/**
	 * Writes the remaining data to the database
	 * @param email
	 * @param first
	 * @param middle
	 * @param last
	 * @param preferred
	 */
	public void finishRegistration(String email, String first, String middle, String last, String preferred) {
		
		String sql = "UPDATE cse360users SET preferredName = ? WHERE email = ?";
		System.out.println(email + " " + preferred);
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, preferred);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
			this.universalpreferredName = preferred;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		//to get middle and last just keeping repeating this code over and over
		sql = "UPDATE cse360users SET firstName = ? WHERE email = ?";
		this.universalfirstName = first;

		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, first);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
			this.universalfirstName = first;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sql = "UPDATE cse360users SET middleName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, middle);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sql = "UPDATE cse360users SET lastName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, last);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Returns if an email exists in the user database
	 * @param email
	 * @param role
	 * @return
	 */
	public boolean doesUserExist(String email, String role) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE email = ? AND role = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, email);
	        pstmt.setString(2, role);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	/**
	 * Deletes a user from the database
	 * @throws SQLException
	 */
	public void deleteUser() throws SQLException{
		String sql = "DELETE FROM cse360users WHERE email=?";
		Scanner scanner = new Scanner(System.in);

		PreparedStatement statement = connection.prepareStatement(sql);
		System.out.println("Choose a user to delete");
		String email = scanner.nextLine();
		statement.setString(1, email);
		
		System.out.println("Are you sure that you want to do this?");
		String answer = scanner.nextLine();
		if(answer.equals("Yes")) {
			statement.executeUpdate();
		}
		
	}
	
	/**
	 * Starts the process of deleting a user and returns the object needed to finish the delete
	 * @param email
	 * @return
	 */
	public PreparedStatement deleteUser(String email) {
		String sql = "DELETE FROM cse360users WHERE email=?";

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, email);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return statement;
	}
	
	/**
	 * Sets the roles of a user given their email
	 * @param email
	 * @param roles
	 */
	public void setUserRoles(String email, String[] roles) {
		try {
			String sql = "UPDATE cse360users SET role = ? WHERE email = ?";
			
			try(PreparedStatement statement = connection.prepareStatement(sql)){
				statement.setString(1, Roles.ArrayToString(roles));
				statement.setString(2, email);
				statement.executeUpdate();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add roles to a user
	 * @param email
	 * @param role
	 * @throws SQLException
	 */
	public void addUserRole(String email, String role) throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		String saved = "";
		
		//goes through searching through user based off of their email
		while(rs.next()) {
			if(rs.getString("email").equals(email)) {
				saved = rs.getString("role");
				System.out.print("Hello " + saved);
			}
		}
		if(saved.equals("")) {
			System.out.println("User does not exist!");
			return;
		}
		
		//updates user roles which is a full string
		sql = "UPDATE cse360users SET role = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, saved + "," + role);
			statement.setString(2, email);
			statement.executeUpdate();
		}
		
	}
	
	
	/**
	 * removes users role by updating thier role
	 * @param email
	 * @param role
	 * @throws SQLException
	 */
	public void removeUserRole(String email, String role) throws SQLException{		
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		String saved = "";
		
		//checks to see if email is valid
		while(rs.next()) {
			if(rs.getString("email").equals(email)) {
				saved = rs.getString("role");
			}
		}
		//checks if user even has roles if not they do not exist
		if(saved.equals("")) {
			System.out.println("User does not exist!");
			return;
		}
		
		saved = saved.replace(role, "");
		
		//updates and changes the role
		sql = "UPDATE cse360users SET role = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, saved);
			statement.setString(2, email);
			statement.executeUpdate();
		}
		
	}
	
	//This is used for the lambda in forEachUser
	public interface UserMethod {
		void doThing(int id, String email, String[] roles, String first, String middle, String last, String preferred);
	}
	
	/**
	 * Gets the string array of roles for a user
	 * @param email
	 * @return
	 */
	public String[] getRoles(String email) {
		String sql = "SELECT * FROM cse360users WHERE email = ?";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql); 
	
			while(rs.next()) {
				if (rs.getString("email").equals(email))
					return Roles.stringToArray(rs.getString("role"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return new String[] {};
	}
	
	/**
	 * This method performs a given action looping through every user
	 * @param method
	 */
	public void forEachUser(UserMethod method) {
		String sql = "SELECT * FROM cse360users";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql); 
	
			while(rs.next()) {
				// Retrieve by column name 
				int id  = rs.getInt("id");
				String  email = rs.getString("email");
				if (email.equals(""))
					continue;
				//String password = rs.getString("password");
				String role = rs.getString("role");
				String first = rs.getString("firstName");
				String middle = rs.getString("middleName");
				String last = rs.getString("lastName");
				String preferred = rs.getString("preferredName");
				method.doThing(id, email, Roles.stringToArray(role), first, middle, last, preferred);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A legacy console command for displaying all of the users
	 * @throws SQLException
	 */
	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  
			String first = rs.getString("firstName");
			String middle = rs.getString("middleName");
			String last = rs.getString("lastName");
			String preferred = rs.getString("preferredName");

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role); 
			System.out.println(", FirstName: " + first); 
			System.out.println(", MiddleName: " + middle); 
			System.out.println(", LastName: " + last); 
			System.out.println(", PreferredName: " + preferred); 

		} 
	}
	
	/**
	 * A legacy console command for displaying users
	 * @throws SQLException
	 */
	public void displayUsersByInstructor() throws SQLException{		//enhancement
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Age: " + email); 
			System.out.print(", First: " + password); 
			System.out.println(", Last: " + role); 
		} 
	}
	
	/**
	 * A legacy console command for displaying users
	 * @throws SQLException
	 */
	public void displayUsersByStudent() throws SQLException{		// enhancement
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Age: " + email); 
			System.out.print(", First: " + password); 
			System.out.println(", Last: " + role); 
		} 
	}
	
	//adds a new article
	public boolean addArticle(String title, String group, String authors, String abstrac, String keywords, String body, String references) throws Exception{
		//random unique long can change right limit for longer long
		Random random = new Random();
        long leftLimit = 1L;
        long rightLimit = 10000000L;
        long randomLong = leftLimit + (long) (random.nextDouble() * (rightLimit - leftLimit));
		String insertUser = "INSERT INTO  articles (id, title, groupName, authors, abstract, keywords, body, references, delete) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setLong(1, randomLong);
			pstmt.setString(2, title);
			pstmt.setString(3, group);
			pstmt.setString(4, authors);
			pstmt.setString(5, abstrac);
			pstmt.setString(6, keywords);
			pstmt.setString(7, body);
			pstmt.setString(8, references);
			pstmt.setString(9, "f");
			pstmt.executeUpdate();
		}
		
		return true;
	}
	//deletes articles
	public void deleteArticle(int id) throws Exception{
		String query = "DELETE FROM articles WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, id);
		statement.executeUpdate();
	}
	
	//updates articles
	public void updateArticle(int id, String title, String groupName, String authors, String abstrac, String keywords, String body, String references) {
		
		String sql = "UPDATE articles SET title = ?, groupName = ?, authors = ?, abstract = ?, keywords = ?, body = ?, references = ? WHERE id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, title);
			statement.setString(2, groupName);
			statement.setString(3, authors);
			statement.setString(4, abstrac);
			statement.setString(5, keywords);
			statement.setString(6, body);
			statement.setString(7, references);
			statement.setInt(8, id);
			int rowAffected = statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void displayArticleByAdmin() throws Exception{
		String sql = "SELECT * FROM articles"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			long id  = rs.getInt("id"); 
			String title = rs.getString("title"); 
			String group = rs.getString("groupName");
			String authors = rs.getString("authors");  
			String abstrac = rs.getString("abstract");
			String keywords = rs.getString("keywords");
			String encryptedBody = rs.getString("body");
			
			String references = rs.getString("references");

			System.out.println("ID: " + id);
			System.out.print("Title: " + title); 
			System.out.println("\nGroup: " + group);
			System.out.print(", Authors: " + authors); 
			System.out.println(", Abstract: " + abstrac); 
			System.out.print("keywords: " + keywords);
			System.out.print(" Encrypted body: " + encryptedBody);
			System.out.println("\nReferences: " + references); 
		} 
	}
	
	//lets a user see a specific article if they have they correct id
	public void seeArticle(int id) throws SQLException {
		String sql = "SELECT * FROM articles"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			if(rs.getInt("id") == id) {
			long ide  = rs.getInt("id"); 
			String title = rs.getString("title"); 
			String group = rs.getString("groupName");
			String authors = rs.getString("authors");  
			String abstrac = rs.getString("abstract");
			String keywords = rs.getString("keywords");
			String encryptedBody = rs.getString("body");
			
			String references = rs.getString("references");

			System.out.println("ID: " + ide);
			System.out.print("Title: " + title); 
			System.out.println("\nGroup: " + group);
			System.out.print(", Authors: " + authors); 
			System.out.println(", Abstract: " + abstrac); 
			System.out.print("keywords: " + keywords);
			System.out.print(" Encrypted body: " + encryptedBody);
			System.out.println("\nReferences: " + references); 
			}
		} 
	}
	
	//Backs articles up
	public void backupArticles(String filename) {
		try {
			Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM articles");
	
	        try (FileWriter writer = new FileWriter(filename)) {
	            while (rs.next()) {
	                String data = rs.getLong("id") + "|" +
	                			  rs.getString("title") + "|" +
	                			  rs.getString("groupName") + "|" +//each line gets added to the backup
	                              rs.getString("authors") + "|" +
	                              rs.getString("abstract") + "|" +
	                              rs.getString("keywords") + "|" +
	                              rs.getString("body") + "|" +
	                              rs.getString("references") + "|" +
	                              rs.getString("delete");
	                writer.write(data + "\n");
	            }
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//backs up articles based off of their group user inputted
	public boolean groupBackupArticles(String filename, String group) {
		try {
			Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM articles");
	        
	        boolean outputValid = false;
	        try (FileWriter writer = new FileWriter(filename)) {
	            while (rs.next()) {
	            	if(rs.getString("groupName").equals(group)) {
	            		outputValid = true;
	            		String data = rs.getLong("id") + "|" +
	            					  rs.getString("title") + "|" +
	            					  rs.getString("groupName") + "|" +//each line gets added to the backup
	            					  rs.getString("authors") + "|" +
	            					  rs.getString("abstract") + "|" +
	            					  rs.getString("keywords") + "|" +
	            					  rs.getString("body") + "|" +
	            					  rs.getString("references") + "|" +
	            					  rs.getString("delete");
	            		writer.write(data + "\n");
	            	}
	            }
	        }
			
	        return outputValid;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//merges instead of deleting all of the new articles
	public void mergeArticles(String filename) throws Exception{		    // Prepare SQL statements
	    String updateQuery = "UPDATE articles SET title = ?, groupName = ?, authors = ?, abstract = ?, keywords = ?, body = ?, references = ?, delete = ? WHERE id = ?";
	    String insertQuery = "INSERT INTO articles (id, title, groupName, authors, abstract, keywords, body, references, delete) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    
	    PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
	    PreparedStatement insertPstmt = connection.prepareStatement(insertQuery);

	    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] values = line.split("\\|");
	            if (values.length == 9) {
	                // Set parameters for update statement
	                for (int i = 1; i < 9; i++) {
	                    updatePstmt.setString(i, values[i]);
	                }
	                updatePstmt.setString(9, values[0]); // id is the last parameter
	                int affectedRows = updatePstmt.executeUpdate();

	                // If no rows were affected by update, insert new row
	                if (affectedRows == 0) {
	                    for (int i = 0; i < 9; i++) {
	                        insertPstmt.setString(i + 1, values[i]);
	                    }
	                    insertPstmt.executeUpdate();
	                }
	            } else {
	                System.err.println("Skipping line due to incorrect number of values: " + line);
	            }
	        }
	    } 

	}
	
	//restores articles depending on the file name
	public void restoreArticles(String filename) {
		try {
			//empties table as is required to back it up
			String query = "TRUNCATE TABLE articles";
			statement.executeUpdate(query);
		
	        PreparedStatement pstmt = connection.prepareStatement(
	            "INSERT INTO articles (id, title, groupName, authors, abstract, keywords, body, references, delete) VALUES (?, ?,?, ?, ?, ?, ?, ?, ?)"
	        );

	        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] values = line.split("\\|");
	                if (values.length == 9) {
	                    for (int i = 0; i < 9; i++) {
	                        pstmt.setString(i + 1, values[i]); //reads through the lines adding back to the table
	                    }
	                    pstmt.executeUpdate();
	                } else {
	                    System.err.println("Skipping line due to incorrect number of values: " + line);
	                }
	            }
	        } 
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Gets the connection object to the database
	 * @return
	 */
	public Connection getConnection() {
	    return connection;
	}
	
	/**
	 * Ends the connection to the database
	 */
	public void closeConnection() {
		System.out.println("Closing connection...");
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
