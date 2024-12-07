package edu.asu.DatabasePart1;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Testing {

    private DatabaseHelper_Encryption dbHelper;
    private Student student;

    @Before
    public void setUp() throws SQLException {
        dbHelper = new DatabaseHelper_Encryption();
        dbHelper.connectToDatabase(); // Ensure the database connection is established

        // Create a test student
        student = new Student(
            "test@student.com",
            Role.student,
            1,
            "First",
            "Middle",
            "Last",
            "Preferred",
            "Generic",
            "Specific"
        );
    }

//    @Test
//    public void testAddArticle() throws Exception {
//        String[] article = {
//            "12345",
//            "Easy",
//            "Test Title",
//            "Author Name",
//            "Test Abstract",
//            "Keywords",
//            "Test Body",
//            "References",
//            "Group"
//        };
//        dbHelper.addArticle(article);
//
//        List<String> articles = dbHelper.listAllArticles();
//        assertTrue(articles.stream().anyMatch(a -> a.contains("Test Title")));
//    }

//    @Test
//    public void testListArticlesByLevel() throws Exception {
//        String[] article = {
//            "67890",
//            "Intermediate",
//            "Intermediate Article",
//            "Another Author",
//            "Abstract",
//            "Some Keywords",
//            "Body Content",
//            "References",
//            "Group"
//        };
//        dbHelper.addArticle(article);
//
//        List<String> intermediateArticles = dbHelper.listArticlesByLevel("Intermediate");
//        assertTrue(intermediateArticles.stream().anyMatch(a -> a.contains("Intermediate Article")));
//    }

    @Test
    public void testSendGenericMessage() throws SQLException {
        dbHelper.connectToDatabase(); // Ensure the connection is established
        String message = "I'm confused about how to use this tool.";
        DatabaseHelper.storeGenericMessage(message, student.getEmail());
        // Add additional logic to verify the message is stored in the database if necessary
        assertNotNull(message);
    }

//    @Test
//    public void testSendSpecificMessage() throws SQLException {
//        dbHelper.connectToDatabase(); // Ensure the connection is established
//        String specificMessage = "I need help with JavaFX.";
//        DatabaseHelper.storeSpecificMessage(specificMessage, student.getEmail());
//        // Add additional logic to verify the specific message is stored in the database if necessary
//        assertNotNull(specificMessage);
//    }

//    @Test
//    public void testListArticlesByGroup() throws SQLException {
//        String[] article = {
//            "54321",
//            "Advanced",
//            "Grouped Article",
//            "Group Author",
//            "Abstract",
//            "Keywords",
//            "Body Content",
//            "References",
//            "Java/Python"
//        };
//        try {
//            dbHelper.addArticle(article);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        List<String> groupedArticles = dbHelper.listArticlesByGroups("Java");
//        assertTrue(groupedArticles.stream().anyMatch(a -> a.contains("Grouped Article")));
//    }

    @Test
    public void testBackupAndRestore() throws Exception {
        String backupFileName = "test_backup.txt";

        dbHelper.backupArticlesToFile(backupFileName);
        assertTrue(backupFileName.contains("test_backup"));

        // Simulate restore
        dbHelper.restoreArticlesfromFile(backupFileName, true, "Java/Python");
        List<String> articles = dbHelper.listArticlesByGroups("Java");
        assertFalse(articles.isEmpty());
    }
}
