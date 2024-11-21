package edu.asu.DatabasePart1;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//import Encryption.EncryptionHelper;
//import Encryption.EncryptionUtils;
import java.io.BufferedReader;
import java.io.FileReader;

class DatabaseHelper_Encryption {

    // JDBC driver name and database URL for H2 in-memory database
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/firstDatabase";

    // Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    // Connection and statement objects for executing SQL queries
    private static Connection connection = null;
    private static Statement statement = null;
    
    // Encryption helper for encrypting and decrypting article body content
    //private static EncryptionHelper encryptionHelper;

    // Constructor to initialize encryption helper
    public void DatabaseHelper_enc() throws Exception {
        //encryptionHelper = new EncryptionHelper();
    }
    
    private static EncryptionHelper encryptionHelper = new EncryptionHelper();
    
    // Establish connection to the database and create necessary tables
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            createTables();  // Create the article table if it doesn't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    // Create a table for storing articles if it doesn't already exist
    public static void createTables() throws SQLException {
        String articleTable = "CREATE TABLE IF NOT EXISTS cse360articles ("
                + "sequence_number INT AUTO_INCREMENT PRIMARY KEY, "  // Auto-increment primary key
                + "id BIGINT, "
                + "level VARCHAR(255),"
                + "title VARCHAR(255), "
                + "authors VARCHAR(255), "
                + "abstract VARCHAR(255),"
                + "setOfKeywords VARCHAR(255),"
                + "body TEXT,"  // Store the encrypted body as text
                + "referencesColumn VARCHAR(255), "  // Reference field
                + "groupOfArticles VARCHAR(255))"; // Group field, without an extra comma
       
        statement.execute(articleTable);  // Execute the query to create the table
    }

    // Check if the articles table is empty
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360articles";  // Query to count rows
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;  // Return true if no rows found
        }
        return true;
    }

    // Add a new article to the database
    public void addArticle(String[] articleArray) throws Exception {
        if (articleArray.length == 0) {
            System.out.println("Article provided was empty");
            return;
        }
        
        long id = articleArray[0] == null || articleArray[0].isEmpty() ? System.currentTimeMillis() : Long.parseLong(articleArray[0]);
        
        if (articleExists(id)) {
            System.out.println("Article with ID " + id + " already exists. Skipping addition.");
            return;
        }

        // Encrypt the body of the article before storing it
        String encryptionKey = "1234567890123456"; // Replace with your secure 16-byte key
        String encryptedBody = EncryptionHelper.encrypt(articleArray[6], encryptionKey);

        // Insert the article into the database, including the level
        String insertArticle = "INSERT INTO cse360articles (id, level, title, authors, abstract, setOfKeywords, body, referencesColumn,groupOfArticles) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
            pstmt.setLong(1, id);  // ID
            pstmt.setString(2, articleArray[1]);  // Level
            pstmt.setString(3, articleArray[2]);  // Title
            pstmt.setString(4, articleArray[3]);  // Authors
            pstmt.setString(5, articleArray[4]);  // Abstract
            pstmt.setString(6, articleArray[5]);  // Keywords
            pstmt.setString(7, encryptedBody);  // Encrypted body
            pstmt.setString(8, articleArray[7]);  // References
            pstmt.setString(9, articleArray[8]);
            pstmt.executeUpdate();  // Execute the update
        }
        System.out.println(articleArray[1] + " has been added.");  // Confirmation message
    }
    public List<String> listArticlesByGroups(String group) throws SQLException{
    	String query = "SELECT * FROM cse360articles WHERE groupOfArticles LIKE ?";
    	List<String> groups = new ArrayList<>();
    	try(PreparedStatement pstmt = connection.prepareStatement(query)){
    		pstmt.setString(1, "%" + group + "%");
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    			long id = rs.getLong("id");
    			String level = rs.getString("level");
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                String abstractText = rs.getString("abstract");
                String keywords = rs.getString("setOfKeywords");
                String body = rs.getString("body");
                String references = rs.getString("referencesColumn");

                String articleDetails = "ID: " + id + ", Level: "+level+", Title: " + title + ", Authors: " + authors +
                        "\nAbstract: " + abstractText + "\nKeywords: " + keywords + "\nBody: " + body + "\nReferences: " + references + "\n";
                groups.add(articleDetails);
    			
    			
    		}
    		
    	}catch(SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return groups;
    				
    }
    
    public List<String> listArticlesByLevel(String level) throws SQLException{
    	String query = "SELECT * FROM cse360articles WHERE level = ?";
    	List<String> levels = new ArrayList<>();
    	try(PreparedStatement pstmt = connection.prepareStatement(query)){
    		pstmt.setString(1, level);
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    			long id = rs.getLong("id");
    			String title = rs.getString("title");
    			String authors = rs.getString("authors");
    			String abstractText = rs.getString("abstract");
    			String keywords = rs.getString("setOfKeywords");
    			String body = rs.getString("body");
    			String references = rs.getString("referencesColumn");
    			
    			String articleDetails = "ID: " + id + ", Level: "+level+", Title: " + title + ", Authors: " + authors +
                        "\nAbstract: " + abstractText + "\nKeywords: " + keywords + "\nBody: " + body + "\nReferences: " + references + "\n";
                levels.add(articleDetails);
    		}
    	}catch(SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	return levels;
    }
    
    
    private boolean articleExists(long id) throws SQLException {
		String query = "SELECT COUNT(*) FROM cse360articles WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setLong(1,id);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) > 0;
			}
		}
		
		return false;
	}

	// List all articles stored in the database
    public List<String> listAllArticles() throws Exception {
    	List<String> articles = new ArrayList<>();
        if (!isDatabaseEmpty()) {
            String sql = "SELECT * FROM cse360articles";  // SQL query to select all articles
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Iterate through the result set and display each article's details
            while (rs.next()) {
                int sequenceNumber = rs.getInt("sequence_number");
                long id = rs.getLong("id");
                String level = rs.getString("level");
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                String group = rs.getString("groupOfArticles");
                System.out.print("Sequence Number: " + sequenceNumber);
                System.out.print(", Title: " + title);
                System.out.println(", Authors: " + authors);
                articles.add("Sequence Number: " + sequenceNumber + ", ID: " + id +", Level:"+level+ ", Title: " + title + ", Authors: " + authors+ ", Group: "+group);
                
            }
        } else {
            System.out.println("Database is empty.");
        }
        return articles;
    }
    
    public String getArticleById(long id, User user) throws Exception {
        String sql = "SELECT * FROM cse360articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String authors = rs.getString("authors");
                    String abstractText = rs.getString("abstract");
                    String keywords = rs.getString("setOfKeywords");
                    String encryptedBody = rs.getString("body");
                    String references = rs.getString("referencesColumn");

                    String body = user.canDecrypt() 
                        ? EncryptionHelper.decrypt(encryptedBody, "1234567890123456") 
                        : "Access Denied";

                    return "Title: " + title + "\nAuthors: " + authors + "\nAbstract: " + abstractText +
                           "\nKeywords: " + keywords + "\nBody: " + body + "\nReferences: " + references;
                }
            }
        }
        return null; // Article not found
    }

    
    public void backupArticlesToFile(String fileName) throws Exception {
        String sql = "SELECT * FROM cse360articles";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
        	System.out.print("Soham");
            // Iterate over each article and write its details to the file
            while (rs.next()) {
                long id = rs.getLong("id");
                String level = rs.getString("level");
                String title = rs.getString("title");
                String author = rs.getString("authors");
                String abst = rs.getString("abstract");
                String keywords = rs.getString("setOfKeywords");
                String body = rs.getString("body");
                String references = rs.getString("referencesColumn");
                String groups = rs.getString("groupOfArticles");
                // Write the article to the file
                writer.write(id + "," + level+","+ title + "," + author + "," + abst + "," + keywords + "," + body + "," + references+","+groups);
                writer.newLine();
            }

            System.out.println("Backup completed successfully.");
        } catch (IOException e) {
            System.err.println("Error while backing up articles: " + e.getMessage());
        }
    }
    public void restoreArticlesfromFile(String filename, boolean replace_existing) throws Exception {
    	if(replace_existing) {
    		dropTable();
    		createTables();
    	}
    	else {
    		try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        		String line;
        		while(((line = reader.readLine()) != null)) {
        			String[] parts = line.split(",");
        			long id = Long.parseLong(parts[0]);
        			String level = parts[1];
        			String title = parts[2];
        			String author = parts[3];
        			String abst = parts[4];
        			String keywords = parts[5];
        			String body = parts[6];
        			String references = parts[7];
        			String groups = parts[8];
        			if(articleExists(id)) {
        				System.out.print("Skipping");
        				continue;
        			}
        	        String insertSQL = "INSERT INTO cse360articles (id, level, title, authors, abstract, setOfKeywords, body, referencesColumn,groupOfArticles) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        		    try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
        		        pstmt.setLong(1, id);
        		        pstmt.setString(2,level);
        		        pstmt.setString(3, title);
        		        pstmt.setString(4, author);
        		        pstmt.setString(5, abst);
        		        pstmt.setString(6, keywords);
        		        pstmt.setString(7, body);
        		        pstmt.setString(8, references);
        		        pstmt.setString(8, groups);
        		        pstmt.executeUpdate();
        		    }
        			
        		}
        	}
    	}
    	
    }
    
    public static void restoreGroupArticles(String fileName, boolean replace_existing, String groupNames) throws Exception {
        if (replace_existing) {
            dropTable();
            createTables();
        } else {
            String[] groups = groupNames.split("/"); // Split multiple groups
            StringBuilder sql = new StringBuilder("SELECT * FROM cse360articles WHERE ");

            // Dynamically build the query for multiple groups
            for (int i = 0; i < groups.length; i++) {
                sql.append("groupOfArticles LIKE ?");
                if (i < groups.length - 1) {
                    sql.append(" OR ");
                }
            }

            try (PreparedStatement pstmt = connection.prepareStatement(sql.toString());
                 BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

                // Set each group as a parameter with wildcards
                for (int i = 0; i < groups.length; i++) {
                    pstmt.setString(i + 1, "%" + groups[i].trim() + "%");
                }

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String level = rs.getString("level");
                    String title = rs.getString("title");
                    String author = rs.getString("authors");
                    String abst = rs.getString("abstract");
                    String keywords = rs.getString("setOfKeywords");
                    String body = rs.getString("body");
                    String references = rs.getString("referencesColumn");
                    String groupsInDb = rs.getString("groupOfArticles");

                    // Write the article to the file
                    writer.write(id + "," + level + "," + title + "," + author + "," + abst + "," + keywords + "," + body + "," + references + "," + groupsInDb);
                    writer.newLine();
                }

                System.out.println("Backup completed successfully.");
            } catch (IOException e) {
                System.err.println("Error while backing up articles: " + e.getMessage());
            }
        }
    }

    public static boolean checkIfGroupExists(String groupName) throws SQLException {
        String query = "SELECT COUNT(*) FROM cse360articles WHERE groupOfArticles = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    public void restoreArticlesfromFile(String filename, boolean replace_existing, String groupNames) throws Exception {
        if (replace_existing) {
            dropTable();
            createTables();
        }

        String[] groups = groupNames.split("/"); // Split multiple groups for filtering
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 9) continue;  // Skip if line format is incorrect

                long id = Long.parseLong(parts[0]);
                String level = parts[1];
                String title = parts[2];
                String author = parts[3];
                String abst = parts[4];
                String keywords = parts[5];
                String body = parts[6];
                String references = parts[7];
                String groupsInFile = parts[8];

                // Check if any of the specified groups match the article's groups
                boolean matchesGroup = false;
                for (String group : groups) {
                    if (groupsInFile.contains(group.trim())) {
                        matchesGroup = true;
                        break;
                    }
                }

                // If it matches the group criteria, restore the article
                if (matchesGroup) {
                    if (articleExists(id)) {
                        System.out.println("Skipping article with ID " + id + " as it already exists.");
                        continue;
                    }

                    String insertSQL = "INSERT INTO cse360articles (id, level, title, authors, abstract, setOfKeywords, body, referencesColumn, groupOfArticles) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                        pstmt.setLong(1, id);
                        pstmt.setString(2, level);
                        pstmt.setString(3, title);
                        pstmt.setString(4, author);
                        pstmt.setString(5, abst);
                        pstmt.setString(6, keywords);
                        pstmt.setString(7, body);
                        pstmt.setString(8, references);
                        pstmt.setString(9, groupsInFile);
                        pstmt.executeUpdate();
                    }
                }
            }

            System.out.println("Restore completed successfully.");
        } catch (IOException e) {
            System.err.println("Error while restoring articles: " + e.getMessage());
        }
    }

    // Count the number of articles in the database
    public int count() throws Exception {
        int len = 0;
        String sql = "SELECT COUNT(*) FROM cse360articles";  // SQL query to count rows
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            len = rs.getInt(1);  // Retrieve the count from the first column
        }
        return len;
    }

    // Drop the article table from the database
    public static void dropTable() throws SQLException {
        String sql = "DROP TABLE IF EXISTS cse360articles";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);  // Execute the drop table command
            System.out.println("Table 'cse360articles' dropped successfully.");
        } catch (SQLException e) {
            System.out.println("Error while dropping table 'cse360articles'.");
            e.printStackTrace();
        }
    }

    // Remove an article from the database by its sequence number
    public void removeArticle(String id) throws Exception {
        String sql = "DELETE FROM cse360articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        	long id1 = Long.parseLong(id);
            pstmt.setLong(1, id1);  // Set the sequence number parameter
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

    // Close the database connection and statement
    public void closeConnection() {
        try {
            if (statement != null) statement.close();  // Close statement
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (connection != null) connection.close();  // Close connection
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}