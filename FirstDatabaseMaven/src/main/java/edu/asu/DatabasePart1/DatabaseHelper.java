package edu.asu.DatabasePart1;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  
	private static final Password hashPassword = new Password();

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	
	public static String firstName;
	public static String preferredName;
	public static String role;

	
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
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "email VARCHAR(255), "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR(255), "
				+ "preferredName VARCHAR(255), "
				+ "role VARCHAR(20), "
				+ "random VARCHAR(255))";
		statement.execute(userTable);
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
	
	public void register(String email, String password, String role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (email, password, firstName, middleName, lastName, preferredName, role, random) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		String random = hashPassword.generateRandomString(8);
		System.out.println(random);
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, hashPassword.hashFull(password, random));
			pstmt.setString(3, "placeholder");
			pstmt.setString(4, "placeholder");
			pstmt.setString(5, "placeholder");
			pstmt.setString(6, "placeholder");
			pstmt.setString(7, role);
			pstmt.setString(8, random);
			pstmt.executeUpdate();
		}
	}
	
	
	public boolean login(String email, String password) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE email = ? AND password = ? AND role = ?";
		Scanner scanner = new Scanner(System.in);
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		
		//The main changes for random
		String save = "";
		
		while(rs.next()) {
			if(rs.getString("email").equals(email)) {
				save = rs.getString("random");
			}
		}
		//resets it if i introduce it again
		rs = stmt.executeQuery(sql);
	/*	int count = 0;
	/*
		while(rs.next()) { 
			if(rs.getString("email").equals(email) && rs.getString("firstName").equals("placeholder")) {
				System.out.println("Finish Setting up your account");
				finishRegistration(email);
			}
			if(rs.getString("email").equals(email) && rs.getString("password").equals(password)) {
				if(count == 0) {
					save = rs.getString("role");
					this.firstName = rs.getString("firstName");
					this.preferredName = rs.getString("preferredName");
				}
				else {
					System.out.println(rs.getString("role"));
				}
				this.firstName = rs.getString("firstName");
				this.preferredName = rs.getString("preferredName");
				count++;
			}
		} 
		*/
		/*
		if(count > 1) {
			System.out.println(save);
			System.out.println("Looks like you have more than one role!\nWhich one would you like to login as?");
			save = scanner.nextLine();
		}
		*/

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			pstmt.setString(2, hashPassword.hash(password + save));
			pstmt.setString(3, "admin");
			try (ResultSet RS = pstmt.executeQuery()) {
				this.role = save;
				return RS.next();
			}
		}
		
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	public String getRole() {
		return this.role;
	}
	public String getpreferredName() {
		return this.preferredName;
	}
	
	public void inviteCode(String invite) throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		
		while(rs.next()){
			if(rs.getString("password").equals(invite)) {
				System.out.println("Huzzah! It works");
			}
		}
		
	}
	
	public void addInviteUser(String invite, String role) throws SQLException{
		String insertUser = "INSERT INTO cse360users (password, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, invite);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		}
	}
	
	public void finishRegistration(String email) throws SQLException{
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Insert first name: ");
		String first = scanner.nextLine();
		System.out.println("Insert middle: ");
		String middle = scanner.nextLine();
		
		System.out.println("Insert last: ");
		String last = scanner.nextLine();
		
		System.out.println("Do you have a preferred name? Y/N");
		String yn = scanner.nextLine();
		if(yn.equals("Y")) {
			System.out.println("Insert prefered: ");
			
			String preferred = scanner.nextLine();	
			String sql = "UPDATE cse360users SET preferredName = ? WHERE email = ?";
			
			try(PreparedStatement statement = connection.prepareStatement(sql)){
				statement.setString(1, preferred);
				statement.setString(2, email);
				int rowAffected = statement.executeUpdate();
				this.preferredName = preferred;

			}
		}
		//to get middle and last just keeping repeating this code over and over
		String sql = "UPDATE cse360users SET firstName = ? WHERE email = ?";
		this.firstName = first;

		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, first);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
			this.firstName = first;

		}
		
		sql = "UPDATE cse360users SET middleName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, middle);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
		}
		
		sql = "UPDATE cse360users SET lastName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, last);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
		}
		
		
	}
	
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
	
	public void addUserRole() throws SQLException{
		Scanner scanner = new Scanner(System.in);
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		System.out.println("Choose a user.");
		String email = scanner.nextLine();
		System.out.println("Choose a role to add.");
		String role = scanner.nextLine();
		
		String password = "";
		String first = "";
		String middle = "";
		String last = "";
		String preferred = "";
		
		while(rs.next()) {
			if(rs.getString("email").equals(email)) {
				password = rs.getString("password");
				first = rs.getString("firstName");
				middle = rs.getString("middleName");
				last = rs.getString("lastName");
				preferred = rs.getString("preferredName");

			}
		}
		
		String insertUser = "INSERT INTO cse360users (email, password, firstName, middleName, lastName, preferredName, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, first);
			pstmt.setString(4, middle);
			pstmt.setString(5, last);
			pstmt.setString(6, preferred);
			pstmt.setString(7, role);
			pstmt.executeUpdate();
		}
	}
	
	public void removeUserRole() throws SQLException{
		String sql = "DELETE FROM cse360users WHERE email = ? AND role = ?";
		Scanner scanner = new Scanner(System.in);
		PreparedStatement statement = connection.prepareStatement(sql);
		System.out.println("Choose a user.");
		String email = scanner.nextLine();
		System.out.println("Choose a role to delete.");
		String role = scanner.nextLine();
		
		System.out.println("Are you sure that you want to do this?");
		String answer = scanner.nextLine();
		if(answer.equals("Yes")) {
			statement.setString(1, email);
			statement.setString(2, role);
			statement.executeUpdate();
		}
		
	}
	
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
	
	//produces random string
	
	
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
