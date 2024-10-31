package edu.asu.DatabasePart1;

public class User {
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
    private int id;

    public User(String email, String role, int id, String firstName,String middleName, String lastName, String preferredName) {
        this.email = email;
        this.role = role;
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.preferredName = preferredName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() { // Updated to follow naming conventions
        return role;
    }

    public int getId() { // Updated to follow naming conventions
        return id;
    }
    public String getFirstName() {
    	return firstName;
    }
    public String getLastName() {
    	return lastName;
    }
    public String getMiddleName() {
    	return middleName;
    }
    public String getPreferredName() {
    	return preferredName;
    }
}
