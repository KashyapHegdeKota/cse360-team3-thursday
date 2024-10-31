package edu.asu.DatabasePart1;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private static Connection connection;
	private static Statement statement = null; 
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
	public static void createArticleTables() throws SQLException{
		 String articleTable = "CREATE TABLE IF NOT EXISTS cse360articles ("
	                + "sequence_number INT AUTO_INCREMENT PRIMARY KEY, "  // Auto-increment primary key
	                + "id VARCHAR(255), "
	                + "title VARCHAR(255), "
	                + "authors VARCHAR(255), "
	                + "abstract VARCHAR(255),"
	                + "setOfKeywords VARCHAR(255),"
	                + "body TEXT,"  // Store the encrypted body as text
	                + "referencesColumn VARCHAR(255))";  // Reference field
	        statement.execute(articleTable);  // Execute the query to create the table

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
		if (isEmailRegistered(email)) {
	        throw new SQLException("User with this email already exists.");
	    }
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
	            System.out.println("User with first name "+firstName+ " and last name"+lastName+" has been registered");
		            
	        }
	    } else {
	        System.out.println("Password does not meet the requirements: " + evaluatePass);
	    }
	}
	private static boolean isEmailRegistered(String email) throws SQLException {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
	    PreparedStatement preparedStatement = connection.prepareStatement(query);
	    preparedStatement.setString(1, email);
	    ResultSet resultSet = preparedStatement.executeQuery();
	    resultSet.next();
	    return resultSet.getInt(1) > 0;
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
	public static List<User> displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		List<User> userList = new ArrayList<>();
		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  
			String firstName = rs.getString("firstName");
			String middleName = rs.getString("middleName");
			String lastName = rs.getString("lastName");
			String preferredName = rs.getString("preferredName");
			// Display values 
			User user = new User(email, role, id,firstName,middleName,lastName, preferredName);
			userList.add(user);
		} 
		return userList;
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
	public static boolean isArticleDBEmpty() throws SQLException{
		String query = "SELECT COUNT(*) AS count FROM cse360articles";  // Query to count rows
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;  // Return true if no rows found
        }
        return true;
    }
	 public static void addArticle(String[] articleArray) throws Exception {
	        if (articleArray.length == 0) {
	            System.out.println("Article provided was empty");
	            return;
	        }

	        // Encrypt the body of the article using title as the initialization vector (IV)
	        
	        // Insert the article into the database, replacing body with its encrypted version
	        String insertArticle = "INSERT INTO cse360articles (id, title, authors, abstract, setOfKeywords, body, referencesColumn) VALUES (?, ?, ?, ?, ?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
	            pstmt.setString(1, articleArray[0]);  // ID
	            pstmt.setString(2, articleArray[1]);  // Title
	            pstmt.setString(3, articleArray[2]);  // Authors
	            pstmt.setString(4, articleArray[3]);  // Abstract
	            pstmt.setString(5, articleArray[4]);  // Keywords
	            pstmt.setString(6, articleArray[5]);    // Encrypted body
	            pstmt.setString(7, articleArray[6]);  // References
	            pstmt.executeUpdate();  // Execute the update
	        }
	        System.out.println(articleArray[1] + " has been added.");  // Confirmation message
	  }
	 public static void listAllArticles() throws Exception {
	        if (!isArticleDBEmpty()) {
	            String sql = "SELECT * FROM cse360articles";  // SQL query to select all articles
	            Statement stmt = connection.createStatement();
	            ResultSet rs = stmt.executeQuery(sql);

	            // Iterate through the result set and display each article's details
	            while (rs.next()) {
	                int sequenceNumber = rs.getInt("sequence_number");
	                String title = rs.getString("title");
	                String authors = rs.getString("authors");
	                System.out.print("Sequence Number: " + sequenceNumber);
	                System.out.print(", Title: " + title);
	                System.out.println(", Authors: " + authors);
	            }
	        } else {
	            System.out.println("Database is empty.");
	        }
	    }
	 public static int count() throws Exception {
	        int len = 0;
	        String sql = "SELECT COUNT(*) FROM cse360articles";  // SQL query to count rows
	        Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(sql);

	        if (rs.next()) {
	            len = rs.getInt(1);  // Retrieve the count from the first column
	        }
	        return len;
	    }

	 public static void restoreToAFile(File fs) throws Exception{
		 try {
			 FileWriter writer = new FileWriter(fs);
			 int len = count();
			 for (int i=0;i<=len+1;i++) {
				 String sql = "SELECT * FROM cse360articles WHERE sequence_number = ?";
			      try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				      pstmt.setInt(1, i); 
				      ResultSet rs = pstmt.executeQuery();
				      if (rs.next()) {
				    	  String[] array = new String[8];
				    	  String id = rs.getString("id");
				    	  String title = rs.getString("title");
				    	  String authors = rs.getString("authors");
				    	  String ab = rs.getString("abstract");
				    	  String keywords = rs.getString("setOfKeywords");
				    	  String body = rs.getString("body");
				    	  String references = rs.getString("referencesColumn");
				    	  array[0] = String.valueOf(i);
				    	  array[1] = id;
			              array[2] = title;
			              array[3] = authors;
			              array[4] = ab;
			              array[5] = keywords;
			              array[6] = body;
			              array[7] = references;
			              if(array!=null) {
			            	  String articleData = String.join(",", array);
			            	  writer.write(articleData+"\n");
			              }
		
				      }
			      }
			 }
			 writer.close();
		 }catch(IOException e) {
			 System.out.println("IOException occured");
		 }
	 }
	 
	 public static void removeArticle(String seq) throws Exception {
	        String sql = "DELETE FROM cse360articles WHERE sequence_number = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setString(1, seq);  // Set the sequence number parameter
	            int rowsAffected = pstmt.executeUpdate();  // Execute the update
	            if (rowsAffected > 0) {
	                System.out.println("Article deleted successfully.");
	            } else {
	                System.out.println("No article found with the provided sequence number.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
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