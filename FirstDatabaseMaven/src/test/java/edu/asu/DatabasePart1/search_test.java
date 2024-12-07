package edu.asu.DatabasePart1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class search_test {

    private DatabaseHelper dbHelper;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        
        connection = DriverManager.getConnection(DatabaseHelper.DB_URL, DatabaseHelper.USER, DatabaseHelper.PASS);
        Statement stmt = connection.createStatement();
        
        String createArticleTable = "CREATE TABLE IF NOT EXISTS cse360articles (" +
                "id VARCHAR(255) PRIMARY KEY, " +
                "level VARCHAR(255), " +
                "title VARCHAR(255), " +
                "authors VARCHAR(255), " +
                "abstract TEXT, " +
                "setOfKeywords TEXT, " +
                "body TEXT, " +
                "referencesColumn TEXT, " +
                "groupOfArticles TEXT" +
                ")";
        stmt.execute(createArticleTable);
        
        String insertArticles = "INSERT INTO cse360articles (id, level, title, authors, abstract, setOfKeywords, body, referencesColumn, groupOfArticles) VALUES " +
                "('1', 'Beginner', 'Test Title 1', 'Author A', 'Abstract A', 'Keyword1, Keyword2', 'Body A', 'Ref A', 'Group1')," +
                "('2', 'Intermediate', 'Test Title 2', 'Author B', 'Abstract B', 'Keyword3, Keyword4', 'Body B', 'Ref B', 'Group2')";
        stmt.execute(insertArticles);
        stmt.close();
    }

    @After
    public void tearDown() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DROP TABLE IF EXISTS cse360articles");
        stmt.close();
        connection.close();
        dbHelper.closeConnection();
    }

    @Test
    public void testSearchArticles() throws SQLException {
        List<Article> articles = dbHelper.searchArticles("Test Title 1", null, null, null);
        assertEquals(1, articles.size());
        assertEquals("Test Title 1", articles.get(0).getTitle());

        // Test searching by authors
        articles = dbHelper.searchArticles(null, "Author B", null, null);
        assertEquals(1, articles.size());
        assertEquals("Author B", articles.get(0).getAuthors());

        
     
        // Test searching by ID
        articles = dbHelper.searchArticles(null, null, null, "2");
        assertEquals(1, articles.size());
        assertEquals("2", articles.get(0).getId());
    }
}
