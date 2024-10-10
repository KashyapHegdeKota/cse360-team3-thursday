package edu.asu.DatabasePart1;

import java.sql.SQLException;
import java.util.Scanner;


public class StartCSE360 {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);

	public static void main( String[] args )
	{

		try { 
			
			databaseHelper.connectToDatabase();  // Connect to the database

			// Check if the database is empty (no users registered)
			if (databaseHelper.isDatabaseEmpty()) {
				System.out.println( "In-Memory Database  is empty" );
				//set up administrator access
				setupAdministrator();
			}
			else {
				//databaseHelper.displayUsersByAdmin();
				mainFlow();

			}
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			System.out.println("Good Bye!!");
			databaseHelper.closeConnection();
		}
	}
	
	
	static void mainFlow() throws SQLException {
		databaseHelper.displayUsersByUser();
		System.out.println( "If you are an administrator, then select A\nIf you are an user then select U\nSelect B to quit!\nEnter your choice:  " );
		String role = scanner.nextLine();

		switch (role) {
		case "U":
			userFlow();
			break;
		case "A":
			adminFlow();
			break;
		case "B":
			break;
		default:
			System.out.println("Invalid choice. Please select 'a', 'u'");
			databaseHelper.closeConnection();
	}}

	
	
	private static void setupAdministrator() throws SQLException {
		String e1 = null;
	    String Fn = null;
	    String Mn = null;
	    String Ln = null;
	    String Pn = null;
		System.out.println("Setting up the Administrator access.");
		System.out.print("Enter Admin Username: ");
		String email = scanner.nextLine();
		System.out.print("Enter Admin Password: ");
		String password = scanner.nextLine();
		System.out.print("Re-Enter Admin Password: ");
		String Rpassword = scanner.nextLine();
		System.out.print("Finish account setup: \n");
        System.out.print("Enter your email id: ");
        e1 = scanner.nextLine();
        System.out.print("Enter your first name: ");
        Fn = scanner.nextLine();
        System.out.print("Enter your middle name: ");
        Mn = scanner.nextLine();
        System.out.print("Enter your last name: ");
        Ln = scanner.nextLine();
        System.out.print("Enter your prefferd name (optional): ");
        Pn = scanner.nextLine();
		if(password.equals(Rpassword)) {
			DatabaseHelper.register(email, password, "admin", "1",  e1, Fn, Mn, Ln, Pn);
			System.out.println("Administrator setup completed.");
			adminFlow();
		}
		else {
			System.out.println("Administrator setup failed!");
		}
		

	}

	private static void userFlow() throws SQLException {
	    String email = null;
	    String password = null;
	    String rpassword = null;
	    String userOTP = null;
	    String adminOTP = null;
	    String userEmail = null;
	    String firstName = null;
	    String middleName = null;
	    String lastName = null;
	    String preferredName = null;
	    String role =null;

	    System.out.println("user flow");
	    System.out.print("What would you like to do \n1. Register\n 2. Login");
	    String choice = scanner.nextLine();
	    switch (choice) {
	        case "1":
	            System.out.println("Enter the OTP given by admin");
	            userOTP = scanner.nextLine();
	            adminOTP = DatabaseHelper.getOTP();
	            if (userOTP.equals(adminOTP)) {
	                // Collect registration details
	                System.out.print("Enter Username: ");
	                email = scanner.nextLine();
	                System.out.print("Enter User Password: ");
	                password = scanner.nextLine();
	                System.out.print("Re-enter User Password: ");
	                rpassword = scanner.nextLine();
	                System.out.print("Enter User Type 1. Instructor 2. Student: ");
		            role = scanner.nextLine();
		            if (role.equals("1")) {
		                role = "Instructor";
		            } else if (role.equals("2")) {
		                role = "Student";
		            }
		            System.out.print("Finish account setup: \n");
	                System.out.print("Enter User Email: ");
	                userEmail = scanner.nextLine();
	                System.out.print("Enter First Name: ");
	                firstName = scanner.nextLine();
	                System.out.print("Enter Middle Name (optional): ");
	                middleName = scanner.nextLine();
	                System.out.print("Enter Last Name: ");
	                lastName = scanner.nextLine();
	                System.out.print("Enter Preferred Name (optional): ");
	                preferredName = scanner.nextLine();

	                // Check if user already exists in the database
	                if (!databaseHelper.doesUserExist(email)) {
	                	if(password.equals(rpassword)) {
	                		databaseHelper.register(email, password, role, userOTP,userEmail, firstName, middleName, lastName, preferredName);
		                    System.out.println("User setup completed.");
		                    userFlow();
	                	}
	                	else {
	                		System.out.println("Passwords do not match. Rsgistration failed.");
	                	}
	                    
	                } else {
	                    System.out.println("User already exists.");
	                }
	                break;
	            } else {
	                System.out.println("The OTP does not match");
	            }
	            break;
	        case "2":
	            System.out.print("Enter Username: ");
	            email = scanner.nextLine();
	            System.out.print("Enter User Password: ");
	            password = scanner.nextLine();
	            System.out.print("Enter User Role: 1. Instructor 2. Student");
	            role= scanner.nextLine();
	            if(role.equals("1")) {
	            	role = "instructor";
	            }
	            else if(role.equals("2")) {
	            	role = "student";
	            }
	            else {
	            	System.out.println("Invalid user type selection!");
	            	return;
	            }
	            //role = scanner.nextLine();
	            if (databaseHelper.login(email, password, role)) {
	                System.out.println("User login successful.");
	                if ("instructor".equals(role)) {
	                    instructorFlow(email); // Call instructorFlow if the role is Instructor
	                } else {
	                    studentFlow(email); // Call studentFlow if the role is Student (implement student-specific actions)
	                }
	            } else {
	                System.out.println("Invalid user credentials. Try again!!");
	            }
	            break;	       
	        default:
	            System.out.println("Invalid choice.");
	            break;
	    }
	}
	
	
	
	private static void instructorFlow(String user) throws SQLException {
		databaseHelper.displayUserByEmail(user);
	    //System.out.println("Welcome, Instructor!");
	    boolean loggedIn = true;
	    while (loggedIn) {
	        System.out.println("1. Logout");
	        System.out.print("Enter your choice: ");
	        String choice = scanner.nextLine();
	        switch (choice) {
	            case "1":
	                loggedIn = false;
	                System.out.println("Logging out...");
	                mainFlow();
	            default:
	                System.out.println("Invalid choice. Please select '1' to Logout.");
	        }
	    }
	}
	
	private static void studentFlow(String user) throws SQLException {
	    databaseHelper.displayUserByEmail(user);
	    boolean loggedIn = true;
	    while (loggedIn) {
	        System.out.println("1. Logout");
	        System.out.print("Enter your choice: ");
	        String choice = scanner.nextLine();
	        switch (choice) {
	            case "1":
	                loggedIn = false;
	                System.out.println("Logging out...");
	                mainFlow(); // Call mainFlow() after logging out
	                break; // Exit the switch block after handling "1"
	            default:
	                System.out.println("Invalid choice. Please select '1' to Logout.");
	                break; // Exit the switch block for the default case
	        }
	    }
	}



	private static void adminFlow() throws SQLException {
	    System.out.println("admin flow");
	    System.out.print("Enter Admin Username: ");
	    String email = scanner.nextLine();
	    System.out.print("Enter Admin Password: ");
	    String password = scanner.nextLine();
	    if (databaseHelper.login(email, password, "admin")) {
	        System.out.println("Admin login successful.");
	        Admin.main(email);
	    } else {
	        System.out.println("Invalid admin credentials. Try again!!");
	    }
	}
}