package edu.asu.DatabasePart1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Encryption.EncryptionUtils;

class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  
	private static final Password hashPassword = new Password();

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	
	public String universalfirstName = "";
	public String universalpreferredName = "";
	public String universalRole = "";

	
	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

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
				+ "authors VARCHAR(1000), "
				+ "abstract VARCHAR(10000), "
				+ "keywords VARCHAR(1000), "
				+ "body VARCHAR(100000000), "
				+ "references VARCHAR(10000), "
				+ "delete VARCHAR(1))";
		statement.execute(articleTable); //initializes articleTable
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


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		
		return true;
	}
	
	//registers users putting in placeholder values for users that need to finish registration
	public void register(String email, String password, String role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (email, password, firstName, middleName, lastName, preferredName, role, random, userReset) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)";
		String random = hashPassword.generateRandomString(8);
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, hashPassword.hashFull(password, random));
			pstmt.setString(3, "placeholder");
			pstmt.setString(4, "placeholder");
			pstmt.setString(5, "placeholder");
			pstmt.setString(6, "placeholder");
			pstmt.setString(7, role);
			pstmt.setString(8, random);
			pstmt.setString(9, "f");
			pstmt.executeUpdate();
		}
		insertUser = "INSERT INTO invite (invite, role) VALUES (?, ?)";
		
		//inserts role and password for the users
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, "invites");
			pstmt.setString(2, "roles");
			pstmt.executeUpdate();

		}
		insertUser = "INSERT INTO expire (email, password) VALUES(?,?)";
		
		//inserts expire user values equating to zero
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, "");
			pstmt.setString(2, "");
			pstmt.executeUpdate();

		}
	}
	
	
	//this reset the user password with the temp one time password
	public String resetPassword(String email)throws SQLException {
		
		Scanner scanner = new Scanner(System.in);
		
		//updates main user telling them that they do not need to reset anymore
		String sql = "UPDATE cse360users SET userReset = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, "f");
			statement.setString(2, email);
			statement.executeUpdate();
		}
		
		System.out.println("Insert your new Password");
		String pass = scanner.nextLine();
		
		String query = "SELECT * FROM cse360users";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query); 
		
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
		
		return pass;
		
	}
	
	//asks for temp password to see if it is a valid one based off of the email inserted
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
	
	//sets up user so next they log on they must us the temp password to reset their own password
	
	public void passwordReset(String email, String password) throws SQLException{
		String sql = "UPDATE cse360users SET userReset = ? WHERE email = ?";
		//change main to t to show that it is being reset
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, "t");
			statement.setString(2, email);
			statement.executeUpdate();
		}
		
		//inserts values into expire table
		String insertUser = "INSERT INTO expire (email, password) VALUES (?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.executeUpdate();
		}

	}
	
	//check to see if user must reset before they log on
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
	
	//main login function checks to see if user is logged in then to see if the credentials are valid or not
	public String login(String email, String password) throws SQLException {
		String query = "SELECT * FROM cse360users";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query); 
		
		String newPassword = "";	
		
		while(rs.next()) {
			if(rs.getString("userReset").equals("t") && rs.getString("email").equals(email) && rs.getString("password").equals(Password.hashFull(password,  rs.getString("random")))) {
				newPassword = userReset(email);
			}
		}
		
		rs = stmt.executeQuery(query); 
		
		//checks to see if user exists
		while(rs.next()) {
			if(rs.getString("email").equals(email) && rs.getString("password").equals(Password.hashFull(password,  rs.getString("random")))) {
				this.universalpreferredName = rs.getString("preferredName"); 
				this.universalfirstName = rs.getString("firstName");
				return rs.getString("role");
			}
		}
		
		return "";
	}
	
	//just getters and stuff
	public String getFirstName() {
		return this.universalfirstName;
	}
	public String getRole() {
		return this.universalRole;
	}
	public String getpreferredName() {
		return this.universalpreferredName;
	}
	
	//new invite checks invite code
	public void inviteCode(String invite) throws SQLException{
		
		String sql = "SELECT * FROM invite"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		Scanner scanner = new Scanner(System.in);
		
		//checks all invite code to see the on that matches
		while(rs.next()){
			if(rs.getString("invite").equals(invite)) {
				System.out.println("Give a username and password\nUsername: ");
				String email = scanner.nextLine();
				System.out.println("Password: ");
				String password = scanner.nextLine();
				
				//Delete the line get rid of temp password	
				sql = "DELETE FROM invite WHERE password=?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setString(1, invite);
				statement.executeUpdate();
				
				//finally registers them in the system
				register(email, password, rs.getString("role"));
				break;
			}
		}
		
	}
	
	//new invite functions add invite to the user 
	public void addInviteUser(String invite, String role) throws SQLException{
		String insertUser = "INSERT INTO invite (invite, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, invite);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		}
	}
	
	//asks for all these in order to properly finish the registration
	public void finishRegistration(String email, String first, String middle, String last, String preferred) throws SQLException{
		
		//gets the user preferred name
		String sql = "UPDATE cse360users SET preferredName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, preferred);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
			this.universalpreferredName = preferred;

		}
		
		//updates first name
		sql = "UPDATE cse360users SET firstName = ? WHERE email = ?";
		this.universalfirstName = first;

		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, first);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
			this.universalfirstName = first;

		}
		
		//updates middle name
		sql = "UPDATE cse360users SET middleName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, middle);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
		}
		
		//updates last name
		sql = "UPDATE cse360users SET lastName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, last);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
		}
		
		
	}
	
	//checks for user existence
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
	
	//Deletes user
	public void deleteUser() throws SQLException{
		String sql = "DELETE FROM cse360users WHERE email=?";
		Scanner scanner = new Scanner(System.in);
		
		//gets user to delete
		PreparedStatement statement = connection.prepareStatement(sql);
		System.out.println("Choose a user to delete");
		String email = scanner.nextLine();
		statement.setString(1, email);
		
		//clarifies user deletion
		System.out.println("Are you sure that you want to do this?\n"
				+ "insert \"Yes\" to do so");
		String answer = scanner.nextLine();
		if(answer.equals("Yes")) {
			statement.executeUpdate();
		}
		
	}
	
	//delete user
	public void deleteArticle(int id) throws Exception{
		String query = "DELETE FROM articles WHERE id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setInt(1, id);
		statement.executeUpdate();
	}
	
	//add users
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
	
	
	//removes users role by updating thier role
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
	
	public boolean addArticle(String title, String authors, String abstrac, String keywords, String body, String references) throws Exception{
		Random random = new Random();
        long leftLimit = 1L;
        long rightLimit = 10000000L;
        long randomLong = leftLimit + (long) (random.nextDouble() * (rightLimit - leftLimit));
		String insertUser = "INSERT INTO  articles (id, title, authors, abstract, keywords, body, references, delete) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setLong(1, randomLong);
			pstmt.setString(2, title);
			pstmt.setString(3, authors);
			pstmt.setString(4, abstrac);
			pstmt.setString(5, keywords);
			pstmt.setString(6, body);
			pstmt.setString(7, references);
			pstmt.setString(8, "f");
			pstmt.executeUpdate();
		}
		
		return true;
	}
	
	//Prints out user info
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
			String userReset = rs.getString("userReset");
			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role); 
			System.out.println(", FirstName: " + first); 
			System.out.println(", MiddleName: " + middle); 
			System.out.println(", LastName: " + last); 
			System.out.println(", PreferredName: " + preferred); 
			System.out.println(", userReset: " + userReset);

		} 
	}
	
	public void displayArticleByAdmin() throws Exception{
		String sql = "SELECT * FROM articles"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			long id  = rs.getInt("id"); 
			String title = rs.getString("title"); 
			String authors = rs.getString("authors");  
			String abstrac = rs.getString("abstract");
			String keywords = rs.getString("keywords");
			String encryptedBody = rs.getString("body");
			
			String references = rs.getString("references");

			System.out.println("ID: " + id);
			System.out.print("Title: " + title); 
			System.out.print(", Authors: " + authors); 
			System.out.println(", Abstract: " + abstrac); 
			System.out.print("keywords: " + keywords);
			System.out.print(" Encrypted body: " + encryptedBody);
			System.out.println("\nReferences: " + references); 
		} 
	}
	
	public void seeArticle(int id) {
		
	}
	
	public void backupArticles(String filename) throws Exception{
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM articles");

        try (FileWriter writer = new FileWriter(filename)) {
            while (rs.next()) {
                String data = rs.getLong("id") + "," +
                			  rs.getString("title") + "," + //each line gets added to the backup
                              rs.getString("authors") + "," +
                              rs.getString("abstract") + "," +
                              rs.getString("keywords") + "," +
                              rs.getString("body") + "," +
                              rs.getString("references") + "," +
                              rs.getString("delete");
                writer.write(data + "\n");
            }
        } 
	}
	
	public void mergeArticles(String filename) throws Exception{		    // Prepare SQL statements
		    String updateQuery = "UPDATE articles SET title = ?, authors = ?, abstract = ?, keywords = ?, body = ?, references = ?, delete = ? WHERE id = ?";
		    String insertQuery = "INSERT INTO articles (id, title, authors, abstract, keywords, body, references, delete) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		    
		    PreparedStatement updatePstmt = connection.prepareStatement(updateQuery);
		    PreparedStatement insertPstmt = connection.prepareStatement(insertQuery);

		    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
		        String line;
		        while ((line = reader.readLine()) != null) {
		            String[] values = line.split(",");
		            if (values.length == 8) {
		                // Set parameters for update statement
		                for (int i = 1; i < 8; i++) {
		                    updatePstmt.setString(i, values[i]);
		                }
		                updatePstmt.setString(8, values[0]); // id is the last parameter
		                int affectedRows = updatePstmt.executeUpdate();

		                // If no rows were affected by update, insert new row
		                if (affectedRows == 0) {
		                    for (int i = 0; i < 8; i++) {
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
	public void restoreArticles(String filename) throws Exception{
		//empties table as is required to back it up
		String query = "TRUNCATE TABLE articles";
		statement.executeUpdate(query);
		
	        PreparedStatement pstmt = connection.prepareStatement(
	            "INSERT INTO articles (id, title, authors, abstract, keywords, body, references, delete) VALUES (?,?, ?, ?, ?, ?, ?, ?)"
	        );

	        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] values = line.split(",");
	                if (values.length == 8) {
	                    for (int i = 0; i < 8; i++) {
	                        pstmt.setString(i + 1, values[i]); //reads through the lines adding back to the table
	                    }
	                    pstmt.executeUpdate();
	                } else {
	                    System.err.println("Skipping line due to incorrect number of values: " + line);
	                }
	            }
	        } 
	}
	
	public Connection getConnection() {
	    return connection;
	}
	
	public void closeConnection() {
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
