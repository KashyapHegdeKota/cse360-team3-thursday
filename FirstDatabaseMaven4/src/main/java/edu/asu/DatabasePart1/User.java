package edu.asu.DatabasePart1;

public class User {
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
    private String genericMessage;
    private String specificMessage;
    private int id;

    public User(String email, Role role, int id, String firstName,String middleName, String lastName, String preferredName, String genericMessage, String specificMessage) {
        this.email = email;
        this.role = role;
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.preferredName = preferredName;
        this.genericMessage = genericMessage;
        this.specificMessage = specificMessage;
        
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() { // Updated to follow naming conventions
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
    public String getGenericMessage() {
    	return genericMessage;
    }
    public String getSpecificMessage() {
    	return specificMessage;
    }
    public boolean canDecrypt() {
        return this.role == Role.instructor || this.role == Role.admin;
    }

    public boolean isAdmin() {
        return this.role == Role.admin;
    }
}