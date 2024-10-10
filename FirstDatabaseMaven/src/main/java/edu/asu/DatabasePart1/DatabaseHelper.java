package edu.asu.DatabasePart1;
import java.sql.*;
import java.util.Scanner;

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
				+ "random VARCHAR(255))";
		statement.execute(userTable);
		String inviteTable = "CREATE TABLE IF NOT EXISTS invite ("
				+ "invite VARCHAR(255), "
				+ "role VARCHAR(255))";
		statement.execute(inviteTable);
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
	
	/**
	 * This function creates a new account and adds it to the database
	 * @param email
	 * @param password
	 * @param roles
	 */
	public void register(String email, String password, String[] roles) {
		String insertUser = "INSERT INTO cse360users (email, password, firstName, middleName, lastName, preferredName, role, random) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
	
	public String[] login(String email, String password) {
		String query = "SELECT * FROM cse360users";
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query); 
			
			//The main changes for random
			
			while(rs.next()) {
				//big if determines if user is in and gives their role
				if(rs.getString("email").equals(email) && rs.getString("password").equals(Password.hashFull(password,  rs.getString("random")))) {
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
				if(rs.getString("invite").equals(invite)) {
					return Roles.stringToArray(rs.getString("role"));
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return new String[] {};
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
				register(email, password, rs.getString("role"));
				break;
			}
		}
		
	}
	
	//new invite funcitons
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
