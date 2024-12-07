package edu.asu.DatabasePart1;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Testing_instructor {
	
	private DatabaseHelper_Encryption dbHelper;
    private Student student;

    @Before
    public void setUp() throws SQLException {
        dbHelper = new DatabaseHelper_Encryption();
        dbHelper.connectToDatabase();

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


    @Test
    public void testListArticlesByLevel() throws Exception {
        String[] article = {
            "67890",
            "Intermediate",
            "Intermediate Article",
            "Another Author",
            "Abstract",
            "Some Keywords",
            "Body Content",
            "References",
            "Group"
        };
        dbHelper.addArticle(article);

        List<String> intermediateArticles = dbHelper.listArticlesByLevel("Intermediate");
        assertTrue(intermediateArticles.stream().anyMatch(a -> a.contains("Intermediate Article")));
    }
    
    @Test
    public void testListArticlesByGroup() throws SQLException {
        String[] article = {
            "54321",
            "Advanced",
            "Grouped Article",
            "Group Author",
            "Abstract",
            "Keywords",
            "Body Content",
            "References",
            "Java/Python"
        };
        try {
			dbHelper.addArticle(article);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        List<String> groupedArticles = dbHelper.listArticlesByGroups("Java");
        assertTrue(groupedArticles.stream().anyMatch(a -> a.contains("Grouped Article")));
    }




}


