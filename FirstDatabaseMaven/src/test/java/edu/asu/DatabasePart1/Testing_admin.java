package edu.asu.DatabasePart1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class Testing_admin {
    private DatabaseHelper db;
    private DatabaseHelper_Encryption dbEncryption;

    @Before
    public void setUp() throws SQLException {
        db = new DatabaseHelper();
        db.connectToDatabase();
        db.createTables();
    }


    @Test
    public void testDeleteUser() throws SQLException {
        // Register a user
        DatabaseHelper.register("userToDelete@example.com", "Password@123", Role.user, null,
            "userToDelete@example.com", "Jane", "Smith", "", "", null, null);

        // Delete the user
        DatabaseHelper.deleteUserByEmail("userToDelete@example.com");

        // Validate user no longer exists
        assertFalse(DatabaseHelper.doesUserExist("userToDelete@example.com"));
    }

   
    @Test
    public void testViewAllMembersOfGroup() throws SQLException {
        
        String groupName = "TestGroup";
        String instructorEmail = "instructor@example.com";
        String studentEmail = "student@example.com";

        
        DatabaseHelper.addInstructorToGroup(groupName, instructorEmail);
        DatabaseHelper.addStudentToGroup(groupName, studentEmail);

        List<String> members = DatabaseHelper.getAllMembersOfGroup(groupName);

        assertTrue(members.stream().anyMatch(member -> member.contains("Email: " + instructorEmail)));
        assertTrue(members.stream().anyMatch(member -> member.contains("Email: " + studentEmail)));
    }
    
    @Test
    public void testShowInstructorsWithViewingRights() throws SQLException {
  
        String groupName = "TestGroupForInstructors";
        String instructorEmail1 = "instructor1@example.com";
        String instructorEmail2 = "instructor2@example.com";
        String studentEmail = "student@example.com";

        DatabaseHelper.addMemberToGroup(groupName, instructorEmail1, Role.instructor, true, false);
        DatabaseHelper.addMemberToGroup(groupName, instructorEmail2, Role.instructor, true, false);
        DatabaseHelper.addMemberToGroup(groupName, studentEmail, Role.student, false, false);

        List<String> instructorsWithRights = DatabaseHelper.getInstructorsWithViewingRights(groupName);

        assertNotNull(instructorsWithRights);
        assertEquals(2, instructorsWithRights.size());
        assertTrue(instructorsWithRights.contains(instructorEmail1));
        assertTrue(instructorsWithRights.contains(instructorEmail2));
        assertFalse(instructorsWithRights.contains(studentEmail)); // Ensure students aren't included
    }
    
    @After
    public void tearDown() throws SQLException {
        // Clear the database after each test
        db.connection.createStatement().execute("DELETE FROM special_group_members");
        db.connection.createStatement().execute("DELETE FROM cse360users");
    }


}
