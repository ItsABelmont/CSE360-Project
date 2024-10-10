package edu.asu.DatabasePart1;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  
	private static final Password hashPassword = new Password();

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	
	public static String universalfirstName = "";
	public static String universalpreferredName = "";
	public static String universalRole = "";

	
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
				+ "role VARCHAR(255), "
				+ "random VARCHAR(255), "
				+ "userReset VARCHAR(255))";
		statement.execute(userTable);
		String inviteTable = "CREATE TABLE IF NOT EXISTS invite ("
				+ "invite VARCHAR(255), "
				+ "role VARCHAR(255))";
		statement.execute(inviteTable);
		String expireTable = "CREATE TABLE IF NOT EXISTS expire ("
				+ "email VARCHAR(255), "
				+ "password VARCHAR(255), "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(expireTable);
		
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

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, "invites");
			pstmt.setString(2, "roles");
			pstmt.executeUpdate();

		}
		insertUser = "INSERT INTO expire (email, password) VALUES(?,?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, "");
			pstmt.setString(2, "");
			pstmt.executeUpdate();

		}
	}
	
	public String resetPassword(String email)throws SQLException {
		
		Scanner scanner = new Scanner(System.in);
		
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
		
		String full = "";
		while(rs.next()) {
			if(rs.getString("email").equals(email)) {
				full = Password.hashFull(pass, rs.getString("random"));
			}
		}
		
		sql = "UPDATE cse360users SET password = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, full);
			statement.setString(2, email);
			statement.executeUpdate();
		}
		
		
		
		return pass;
		
	}
	
	public String userReset(String email) throws SQLException{
		Scanner scanner = new Scanner(System.in);
		
		String query = "SELECT * FROM expire";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query); 
		
		System.out.println("Looks like your account is trying to be reset go ahead\n"
				+ "and insert the temp password you where given to reset your password!\n");
		
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
	
	public String doesUserReset(String email, String password) throws SQLException{
		String query = "SELECT * FROM cse360users";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query); 
		
		String newPassword = "";	
		
		while(rs.next()) {
			if(rs.getString("userReset").equals("t") && rs.getString("email").equals(email) && rs.getString("password").equals(Password.hashFull(password,  rs.getString("random")))) {
				return newPassword = userReset(email);
			}
		}
		
		return newPassword;
		
	}
	
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
		
		while(rs.next()) {
			//big if determines if user is in and gives their role
			System.out.println("OUt");
			if(rs.getString("email").equals(email) && rs.getString("password").equals(Password.hashFull(password,  rs.getString("random")))) {
				System.out.println("in");

				this.universalpreferredName = rs.getString("preferredName"); 
				this.universalfirstName = rs.getString("firstName");
				return rs.getString("role");
			}
		}
		
		return "";
	}
	
	public String getFirstName() {
		return this.universalfirstName;
	}
	public String getRole() {
		return this.universalRole;
	}
	public String getpreferredName() {
		return this.universalpreferredName;
	}
	
	//new invite functions
	public void inviteCode(String invite) throws SQLException{
		String sql = "SELECT * FROM invite"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		Scanner scanner = new Scanner(System.in);

		while(rs.next()){
			if(rs.getString("invite").equals(invite)) {
				System.out.println("Huzzah! It works give a unsername and password\nUsername: ");
				String email = scanner.nextLine();
				System.out.println("Password: ");
				String password = scanner.nextLine();
				//Delete the line
				
				sql = "DELETE FROM invite WHERE password=?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setString(1, invite);
				statement.executeUpdate();
					
				register(email, password, rs.getString("role"));
				break;
			}
		}
		
	}
	
	//new invite funcitons
	public void addInviteUser(String invite, String role) throws SQLException{
		String insertUser = "INSERT INTO invite (invite, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)){
			pstmt.setString(1, invite);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		}
	}
	
	public void finishRegistration(String email, String first, String middle, String last, String preferred) throws SQLException{
		
		String sql = "UPDATE cse360users SET preferredName = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, preferred);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
			this.universalpreferredName = preferred;

		}
		//to get middle and last just keeping repeating this code over and over
		sql = "UPDATE cse360users SET firstName = ? WHERE email = ?";
		this.universalfirstName = first;

		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, first);
			statement.setString(2, email);
			int rowAffected = statement.executeUpdate();
			this.universalfirstName = first;

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
	
	public void addUserRole(String email, String role) throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		String saved = "";
		
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
		
		sql = "UPDATE cse360users SET role = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, saved + "," + role);
			statement.setString(2, email);
			statement.executeUpdate();
		}
		
	}
	
	public void removeUserRole(String email, String role) throws SQLException{		
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		String saved = "";
		
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
		
		saved = saved.replace(role, "");
		
		sql = "UPDATE cse360users SET role = ? WHERE email = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			statement.setString(1, saved);
			statement.setString(2, email);
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
