package edu.asu.DatabasePart1;
import java.sql.SQLException;
import java.sql.Time;
import java.time.*;
import java.util.*;

public class Admin {
    static Scanner scan = new Scanner(System.in);

    public static void main(String email) { // Accept email as a parameter
        System.out.println("-----------------------------------------");
        System.out.printf("Welcome %s to the Admin Page\n", email);
        System.out.println("-----------------------------------------");

        String cont = "y";
        while (cont.equalsIgnoreCase("y")) { // Use equalsIgnoreCase for comparison
            int choice;
            System.out.println("What action would you like to perform?\n"
                    + "1. Invite an individual to join the application.\n"
                    + "2. Reset a user account.\n"
                    + "3. Delete a user account.\n"
                    + "4. List the user accounts.\n"
                    + "5. Add or remove a role from a user.\n"
                    + "6. Log out.\n");
            System.out.print("Enter your choice (1-6): ");
            choice = scan.nextInt();
            scan.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("The One Time Authentication Password has been generated.");
                    char[] otpGenerated = generateOTP();
                    String otpString = charToStringOTP(otpGenerated);
                    System.out.println("Generated OTP: " + otpString);
                    try {
                        addOTPToDB(otpString);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    reset();
                    break;
                case 3:
                    System.out.println("Enter the username you want to delete: ");
                    String username_del = scan.nextLine();
                    System.out.println("Are you sure? (yes if you want to continue): ");
                    String ans = scan.nextLine();
                    if (ans.equalsIgnoreCase("yes")) {
                        DatabaseHelper.deleteUserByEmail(username_del);
                        System.out.println("Successfully deleted the user.");
                    } else {
                        System.out.println("Admin changed their mind.");
                    }
                    break;
                case 4:
                    try {
                        DatabaseHelper.displayUsersByAdmin();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    // Implementation for adding or removing a role
                	
                   // System.out.println("Feature not implemented yet.");
                	addOrRemoveRoleByAdmin(email);
                    break;
                case 6:
                    System.out.println("Logging Out");
				try {
					StartCSE360.mainFlow();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Exit the method to log out
                default:
                    System.out.println("Invalid Choice.");
            }

            System.out.println("Do you want to continue? (y/Y for yes): ");
            cont = scan.nextLine();
        }
    }


    static char[] generateOTP() {
        char[] otp = new char[5];
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            otp[i] = (char) (r.nextInt(10) + '0'); // Generate digits
        }
        return otp;
    }

    static String charToStringOTP(char[] otp) {
        return new String(otp); // Use the String constructor for simplicity
    }

    static void addOTPToDB(String otp) throws SQLException {
        DatabaseHelper.addOTP(otp);
    }
    
    

    static void reset() {
    	int cond = 0;
        char[] otp = generateOTP();
        String otp_gen = new String(otp);
        System.out.println("Enter the username: ");
        String email = scan.nextLine();
        if (DatabaseHelper.doesUserExist(email)) {

            otp_gen = "";
            LocalTime otp_time = LocalTime.now();
            int minutes = otp_time.getMinute();

            for (int i = 0; i < 5; i++) {
                otp_gen = otp_gen + Character.toString(otp[i]);
            }
            System.out.println("Here's the OTP: " + otp_gen);

            System.out.println("Enter OTP: ");
            String otp_user = scan.nextLine();
            //		String str_otp_time= otp_time.toString();
            //		String str_otp_exp= otp_expiration.toString();
            //
            if (otp_user.equals(otp_gen)) {
                LocalTime curr_time = LocalTime.now();
                int curr_min = curr_time.getMinute();
                if (curr_min - minutes <= 5) {
                    System.out.println("Enter the new password: ");
                    String new_pass = scan.nextLine();

                    while (cond == 0) {
                        if (PasswordEvaluator.evaluatePassword(new_pass) ==
                            "Success") {
                            try {
                                DatabaseHelper.resetPassword(email, new_pass);
                            } catch (SQLException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            cond = 1;
                        } else {
                            cond = 0;
                        }
                    }
                }
            }
        }
    }
    
    
    
    static void addOrRemoveRoleByAdmin(String email) {
        while (true) { // Loop until a valid input is provided
            System.out.println("Enter the username: ");
            String user_email = scan.nextLine();
            System.out.println("Enter if you want to add or remove the role");
            String add_Or_Remove = scan.nextLine();

            if (add_Or_Remove.equalsIgnoreCase("add")) {
                System.out.println("Role: ");
                String role = scan.nextLine();
                if (role.equalsIgnoreCase("student") || role.equalsIgnoreCase("instructor")) {
                    DatabaseHelper.addRole(user_email, role.toLowerCase());
                    break; // Exit the loop after successful operation
                } else {
                    System.out.println("Enter a valid role!");
                }
            } else if (add_Or_Remove.equalsIgnoreCase("remove")) {
                DatabaseHelper.removeRole(user_email);
                break; // Exit the loop after successful operation
            } else {
                System.out.println("Invalid input. Try Again!");
            }
        }

        main(email); // Return to the main admin menu
    }

}
