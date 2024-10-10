package edu.asu.DatabasePart1;
import java.sql.*;

class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private static Connection connection;
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
	            + "email VARCHAR(255) UNIQUE, "
	            + "password VARCHAR(255), "
	            + "role VARCHAR(20), "
	            + "otp VARCHAR(20),"
	    		+ "userEmail VARCHAR(255) UNIQUE, "
	            + "firstName VARCHAR(255), "
	    		+ "lastName VARCHAR(255),"
	            + "middleName VARCHAR(255),"
	    		+ "preferredName VARCHAR(255))";
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
	
	public static void register(String email, String password, String role,String otp ,String userEmail, String firstName, String middleName, String lastName, String preferredName) throws SQLException {
	    String evaluatePass = PasswordEvaluator.evaluatePassword(password);
	    if (evaluatePass.equals("Success")) {
	        String insertUser = "INSERT INTO cse360users (email, password, role, otp, userEmail, firstName, middleName, lastName, preferredName) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
	            pstmt.setString(1, email);
	            pstmt.setString(2, password);
	            pstmt.setString(3, role.toLowerCase());
	            pstmt.setString(4, null);
	            pstmt.setString(5, userEmail);
	            pstmt.setString(6, firstName);
	            pstmt.setString(7, middleName);
	            pstmt.setString(8, lastName);
	            pstmt.setString(9, preferredName);
	            pstmt.executeUpdate();
	            System.out.println("User registered successfully.");
		            
	        }
	    } else {
	        System.out.println("Password does not meet the requirements: " + evaluatePass);
	    }
	}

	public boolean login(String email, String password, String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE email = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, role.toLowerCase());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	public static  boolean doesUserExist(String email) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
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
	
	public static boolean addOTP(String otp) throws SQLException {
	    String query = "UPDATE cse360users SET otp = ? WHERE role = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, otp);
	        pstmt.setString(2, "admin");
	        return pstmt.executeUpdate() > 0; // Use executeUpdate for UPDATE queries
	    }
	}
	public static String getOTP() throws SQLException{
		String query = "SELECT otp from cse360users WHERE role= ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, "admin");
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("otp"); // Retrieve the OTP value from the result set
	            }
	        }
	    }
	    return null;    
	}
	public static void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role); 
		} 
	}
	
	public void displayUsersByUser() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Email: " + email); 
			System.out.print(", Password: " + password); 
			System.out.println(", Role: " + role); 
		} 
	}

	public void displayUserByEmail(String email) throws SQLException {
	    String query = "SELECT * FROM cse360users WHERE email = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // Retrieve user information
	            //int id = rs.getInt("id");
	            String userEmail = rs.getString("email");
	            //String password = rs.getString("password");
	            String role = rs.getString("role");

	            // Display user information
	            System.out.println("Welcome! " + userEmail +"\n User type: "+ role );
	        } else {
	            System.out.println("No user found with the provided email: " + email);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public static boolean resetPassword(String email, String password) throws SQLException {
	    String query = "UPDATE cse360users SET password = ? WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setString(2, email);
	        int affectedRows = pstmt.executeUpdate(); // Use executeUpdate to perform the update
	        return affectedRows > 0; // Return true if at least one row was updated
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}	
	public static void deleteUserByEmail(String email) {
	    String deleteUser = "DELETE FROM cse360users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
	        pstmt.setString(1, email);
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("User deleted successfully.");
	        } else {
	            System.out.println("No user found with the provided email.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public static boolean removeRole(String email) {
	    String updateRole = "UPDATE cse360users SET role = NULL WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateRole)) {
	        pstmt.setString(1, email);
	        int affectedRows = pstmt.executeUpdate(); // Use executeUpdate for updates
	        return affectedRows > 0; // Return true if at least one row was updated
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public static boolean addRole(String email, String role_to_be_added) {
	    String updateRole = "UPDATE cse360users SET role = ? WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateRole)) {
	        pstmt.setString(1, role_to_be_added);
	        pstmt.setString(2, email);
	        int affectedRows = pstmt.executeUpdate(); // Use executeUpdate for updates
	        return affectedRows > 0; // Return true if at least one row was updated
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	public void closeConnection() {
		try { 
			if(statement != null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection != null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}


}