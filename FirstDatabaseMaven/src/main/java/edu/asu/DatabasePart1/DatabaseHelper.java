package edu.asu.DatabasePart1;
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
	
	public void connectToDatabase() throws SQLException {
	    try {
	        Class.forName(JDBC_DRIVER);
	        System.out.println("Connecting to database...");
	        connection = DriverManager.getConnection(DB_URL, USER, PASS);
	        statement = connection.createStatement();
	        createTables();             // User and article tables
	        createSpecialGroupTable();   // Special group table
	    } catch (ClassNotFoundException e) {
	        System.err.println("JDBC Driver not found: " + e.getMessage());
	    }
	}
	
	private void createSpecialGroupTable() throws SQLException {
        String specialGroupMembersTable = "CREATE TABLE IF NOT EXISTS special_group_members ("
                + "group_name VARCHAR(255), "
                + "member_email VARCHAR(255), "
                + "role VARCHAR(20), " // Values could be 'instructor' or 'student'
                + "can_view_body BOOLEAN, "
                + "is_admin BOOLEAN, "
                + "PRIMARY KEY (group_name, member_email)"
                + ")";
        statement.execute(specialGroupMembersTable);
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
	
	public static void storeGenericMessage(String message, String email) {
		String query = "UPDATE cse360users SET genericMessage = ? WHERE email = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, message);
			pstmt.setString(2, email);
			pstmt.executeUpdate();
			System.out.println("Generic message stored for "+email);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("Error in storeGenericMessage");
		}
		
	}
	
	public static void storeSpecificMessage(String message, String email) {
		String query = "UPDATE cse360users SET specificMessage = ? where email = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, message);
			pstmt.setString(2, email);
			pstmt.executeUpdate();
			System.out.println("Specific message stored for "+ email);
			
		} catch(SQLException e){
			e.printStackTrace();
			System.out.print("Error in storeSpecificMessage");
			
		}
	}
	
	public static boolean isFirstInstructorInGroup(String groupName) throws SQLException {
        String query = "SELECT COUNT(*) FROM special_group_members WHERE group_name = ? AND role = 'instructor'";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0; // If count is 0, this is the first instructor
        }
    }
    public static void addInstructorToGroup(String groupName, String instructorEmail) throws SQLException {
        boolean isFirstInstructor = isFirstInstructorInGroup(groupName);
        addMemberToGroup(groupName, instructorEmail, Role.instructor, true, isFirstInstructor); // First instructor gets admin rights
    }

    public static void addStudentToGroup(String groupName, String studentEmail) throws SQLException {
        addMemberToGroup(groupName, studentEmail, Role.student, true, false);
    }
    public static void addMemberToGroup(String groupName, String memberEmail, Role role, boolean canViewBody, boolean isAdmin) throws SQLException {
        String query = "INSERT INTO special_group_members (group_name, member_email, role, can_view_body, is_admin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupName);
            pstmt.setString(2, memberEmail);
            pstmt.setString(3, role.toString());
            pstmt.setBoolean(4, canViewBody);
            pstmt.setBoolean(5, isAdmin);
            pstmt.executeUpdate();
        }
    }
    
    public static boolean doesGroupExist(String groupName) throws SQLException {
        String query = "SELECT COUNT(*) FROM special_group_members WHERE group_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    public static List<String> getInstructorsWithViewingRights(String groupName) throws SQLException {
        List<String> instructors = new ArrayList<>();
        String query = "SELECT member_email FROM special_group_members WHERE group_name = ? AND role = 'instructor' AND can_view_body = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                instructors.add(rs.getString("member_email"));
            }
        }
        return instructors;
    }
    
    public static List<String> getInstructorsWithAdminRights(String groupName, User user) throws SQLException {
        List<String> admins = new ArrayList<>();
        String query = "SELECT member_email, body FROM special_group_members " +
                       "INNER JOIN cse360articles ON group_name = ? AND role = 'instructor' AND is_admin = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupName);
            System.out.println("Executing query: " + pstmt.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("member_email");
                    String encryptedBody = rs.getString("body");
                    String decryptedBody = (user != null && user.canDecrypt())
                        ? EncryptionHelper.decrypt(encryptedBody, "1234567890123456")
                        : "Access Denied";
                    admins.add("Email: " + email + " | Body: " + decryptedBody);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return admins;
    }



    public static List<String> getStudentsWithViewingRights(String groupName, User user) throws SQLException {
        List<String> students = new ArrayList<>();
        String query = "SELECT member_email, body FROM special_group_members " +
                       "INNER JOIN cse360articles ON group_name = ? AND role = 'student' AND can_view_body = TRUE";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String email = rs.getString("member_email");
                    String encryptedBody = rs.getString("body");
                    String decryptedBody;
                    try {
                        decryptedBody = (user != null && user.canDecrypt() && encryptedBody != null)
                            ? EncryptionHelper.decrypt(encryptedBody, "1234567890123456")
                            : "Access Denied";
                    } catch (Exception e) {
                        e.printStackTrace();
                        decryptedBody = "Decryption Failed";
                    }
                    students.add("Email: " + email + " | Body: " + decryptedBody);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
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
	
	public static void register(String email, String password, Role role,String otp ,String userEmail, String firstName, String middleName, String lastName, String preferredName) throws SQLException {
		if (isEmailRegistered(email)) {
	        throw new SQLException("User with this email already exists.");
	    }
	    String evaluatePass = PasswordEvaluator.evaluatePassword(password);
	    if (evaluatePass.equals("Success")) {
	        String insertUser = "INSERT INTO cse360users (email, password, role, otp, userEmail, firstName, middleName, lastName, preferredName) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
	            pstmt.setString(1, email);
	            pstmt.setString(2, password);
	            pstmt.setString(3, role.name());
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

	public boolean login(String email, String password, Role role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE email = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			pstmt.setString(3, role.name());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	public static List<String> getAllMembersOfGroup(String groupName) throws SQLException {
	    List<String> members = new ArrayList<>();
	    String query = "SELECT member_email, role, can_view_body, is_admin FROM special_group_members WHERE group_name = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, groupName);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String email = rs.getString("member_email");
	            String role = rs.getString("role");
	            boolean canViewBody = rs.getBoolean("can_view_body");
	            boolean isAdmin = rs.getBoolean("is_admin");

	            // Format the member's details
	            String memberInfo = String.format("Email: %s | Role: %s | Can View: %b | Admin: %b", email, role, canViewBody, isAdmin);
	            members.add(memberInfo);
	        }
	    }
	    return members;
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
	public static boolean removeOTP() {
	    String updateRole = "UPDATE cse360users SET otp = NULL WHERE role=?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateRole)) {
	        pstmt.setString(1, Role.admin.toString());
	        int affectedRows = pstmt.executeUpdate(); // Use executeUpdate for updates
	        return affectedRows > 0; // Return true if at least one row was updated
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
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
			String roleStr = rs.getString("role"); // Get role as a String
		    Role role = Role.valueOf(roleStr); // Convert String to Role enum
			String firstName = rs.getString("firstName");
			String middleName = rs.getString("middleName");
			String lastName = rs.getString("lastName");
			String preferredName = rs.getString("preferredName");
			String genericMessage = rs.getString("genericMessage");
			String specificMessage = rs.getString("specificMessage");

			// Display values 
			User user = new User(email, role, id,firstName,middleName,lastName, preferredName,genericMessage, specificMessage);
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

	public List<Article> searchArticles(String title, String authors, String keywords, String ID) throws SQLException {
        List<Article> articles = new ArrayList<>();

        // Build the SQL query dynamically based on non-null search parameters
        StringBuilder query = new StringBuilder("SELECT * FROM cse360articles WHERE 1=1");
        
        if (title != null && !title.isEmpty()) {
            query.append(" AND title LIKE ?");
        }
        if (authors != null && !authors.isEmpty()) {
            query.append(" AND authors LIKE ?");
        }
        if (keywords != null && !keywords.isEmpty()) {
            query.append(" AND setOfKeywords LIKE ?");
        }
        if (ID != null && !ID.isEmpty()) {
            query.append(" AND id LIKE ?");
        }

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int index = 1;

            // Set parameters for the dynamic query
            if (title != null && !title.isEmpty()) {
                stmt.setString(index++, "%" + title + "%");
            }
            if (authors != null && !authors.isEmpty()) {
                stmt.setString(index++, "%" + authors + "%");
            }
            if (keywords != null && !keywords.isEmpty()) {
                stmt.setString(index++, "%" + keywords + "%");
            }
            if (ID != null && !ID.isEmpty()) {
            	stmt.setString(index++, "%" + ID + "%");
	        }

            ResultSet rs = stmt.executeQuery();

            // Loop through the result set and create Article objects
            while (rs.next()) {
                String articleID = rs.getString("id");
                String articlelevel = rs.getString("level");
                String articleTitle = rs.getString("title");
                String articleAuthors = rs.getString("authors");
                String articleabstract = rs.getString("abstract");
                String articlekeyword = rs.getString("setOfKeywords");
                String articlebody = rs.getString("body");
                String articlereferencesColumn = rs.getString("referencesColumn");
                String articlegroupOfArticles = rs.getString("groupOfArticles");



                Article article = new Article(articleID, articlelevel, articleTitle, articleAuthors, articleabstract, articlekeyword, articlebody, articlereferencesColumn, articlegroupOfArticles);
                articles.add(article);
            }
        }

        return articles;
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