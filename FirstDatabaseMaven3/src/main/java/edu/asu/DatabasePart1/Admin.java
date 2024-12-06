package edu.asu.DatabasePart1;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Admin {

    public static void main(Stage primaryStage, String email) {
        showAdminOptions(primaryStage, email);
    }

    static void showAdminOptions(Stage primaryStage, String email) {
    	Optional<String> choice = showTextInputDialog(
    	        "Enter your choice (1-15):\n" +
    	        "1. Invite an individual to join the application\n" +
    	        "2. Reset a user account\n" +
    	        "3. Delete a user account\n" +
    	        "4. List the user accounts\n" +
    	        "5. Add or remove a role from a user\n" +
    	        "6. Log out\n" +
    	        "7. Create an article\n" +
    	        "8. View articles by id\n" +
    	        "9. List all articles\n" +
    	        "10. Delete an article\n" +
    	        "11. Backup articles to file\n" +
    	        "12. Restore articles from file\n"+
    	        "13. Show articles by Groups\n"+
    	        "14. Show Instructors with viewing rights\n"+
    	        "15. View all members of a group"
    	        
        );

        // Check if the user clicked cancel
        if (!choice.isPresent()) {
            // Cancel was pressed, go back to main menu
            StartCSE360.showMainLayout(primaryStage);
            return;
        }

        choice.ifPresent(selectedOption -> {
            try {
                switch (selectedOption) {
                    case "1":
                        generateAndStoreOTP();
                        break;
                    case "2":
                        resetUser();
                        break;
                    case "3":
                        deleteUser();
                        break;
                    case "4":
                        StartCSE360.showUsersByAdmin(primaryStage);
                        break;
                    case "5":
                        addOrRemoveRoleByAdmin(email);
                        break;
                    case "6":
                        StartCSE360.showMainLayout(primaryStage); // Log out and go back to main menu
                        return;
                    case "7":
                    	int bool = calledFromAdmin();
                    	StartCSE360.createArticle(primaryStage, bool);
                    	break;
                    case "8":
                    	int bool1 = calledFromAdmin();
                    	StartCSE360.viewArticleById(primaryStage,bool1);
                    	break;
                    case "9":
                    	int bool2 = calledFromAdmin();
                    	StartCSE360.displayAllArticles(primaryStage,bool2);
                    	break;
                    case "10":
                    	//Delete am article
                    	int bool3 = calledFromAdmin();
                    	StartCSE360.deleteArticle(primaryStage,bool3);
                    	break;
                    case "11":
                    	//Back up and restore an article
                    	int bool4 = calledFromAdmin();
                    	StartCSE360.backupArticles(primaryStage,bool4);
                    	break;
                    case "12":
                    	//List all help articles
                    	int bool5 = calledFromAdmin();
                    	StartCSE360.restoreArticles(primaryStage,bool5);
                    	break;
                    case "13":
                    	int bool6 = calledFromAdmin();
                    	StartCSE360.showListByGroupScreen(primaryStage,bool6);
                    	break;
                    case "14": // Option to show list of instructors with viewing rights
                        showInstructorsWithViewingRights(primaryStage);
                        break;
                    case "15": // Option to show list of instructors with admin rights
                    	showAllMembersOfGroup(primaryStage); 
                    	break;
                    case "16": // Option to show list of students with viewing rights
                        //showStudentsWithViewingRights(primaryStage);
                        break;

                    default:
                        showErrorDialog("Invalid Choice", "Please select a valid option (1-13)");
                }
            } catch (SQLException e) {
                showErrorDialog("Database Error", e.getMessage());
            }
            
            // Only show options again if not logging out (option 6)
//            if (!"6".equals(selectedOption)) {
//                showAdminOptions(primaryStage, email);
//            }
        });
    }
    public static void showInstructorsWithViewingRights(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label header = new Label("Instructors with Viewing Rights");
        ListView<String> listView = new ListView<>();

        // Ask for the group name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Group Name");
        dialog.setHeaderText("Enter the group name to list instructors with viewing rights:");
        dialog.showAndWait().ifPresent(groupName -> {
            try {
                List<String> instructors = DatabaseHelper.getInstructorsWithViewingRights(groupName);
                listView.getItems().setAll(instructors);
            } catch (SQLException e) {
                showErrorDialog("Database Error", e.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> Admin.showAdminOptions(primaryStage, null));

        layout.getChildren().addAll(header, listView, backButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }


    private static void generateAndStoreOTP() throws SQLException {
        char[] otpGenerated = generateOTP();
        String otpString = charToStringOTP(otpGenerated);
        DatabaseHelper.addOTP(otpString);
        showInfoDialog("OTP Generated", "The One-Time Password is: " + otpString);
    }

    private static void resetUser() throws SQLException {
        Optional<String> email = showTextInputDialog("Enter the username to reset:");
        email.ifPresent(userEmail -> {
            if (DatabaseHelper.doesUserExist(userEmail)) {
                char[] otp = generateOTP();
                String otpString = charToStringOTP(otp);
                showInfoDialog("OTP", "Generated OTP: " + otpString);

                Optional<String> userOtp = showTextInputDialog("Enter the OTP:");
                userOtp.ifPresent(otpInput -> {
                    if (otpInput.equals(otpString)) {
                        Optional<String> newPassword = showTextInputDialog("Enter new password:");
                        newPassword.ifPresent(password -> {
                            try {
                                DatabaseHelper.resetPassword(userEmail, password);
                                showInfoDialog("Password Reset", "Password has been reset successfully.");
                            } catch (SQLException e) {
                                showErrorDialog("Database Error", e.getMessage());
                            }
                        });
                    } else {
                        showErrorDialog("OTP Error", "Invalid OTP entered.");
                    }
                });
            } else {
                showErrorDialog("User Not Found", "No user found with the given email.");
            }
        });
    }

    static void deleteUser() throws SQLException {
        Optional<String> email = showTextInputDialog("Enter the username you want to delete:");
        email.ifPresent(userEmail -> {
            Optional<String> confirmation = showTextInputDialog("Are you sure? Type 'yes' to confirm:");
            confirmation.ifPresent(response -> {
                if (response.equalsIgnoreCase("yes")) {
                    DatabaseHelper.deleteUserByEmail(userEmail);
                    showInfoDialog("User Deleted", "User successfully deleted.");
                } else {
                    showInfoDialog("Deletion Canceled", "User deletion canceled by admin.");
                }
            });
        });
    }

    private static void addOrRemoveRoleByAdmin(String adminEmail) throws SQLException {
        Optional<String> email = showTextInputDialog("Enter the username:");
        email.ifPresent(userEmail -> {
            Optional<String> action = showTextInputDialog("Enter 'add' to add a role or 'remove' to remove the role:");
            action.ifPresent(addOrRemove -> {
                if (addOrRemove.equalsIgnoreCase("add")) {
                    Optional<String> role = showTextInputDialog("Enter role (student/instructor):");
                    role.ifPresent(userRole -> {
                        if (userRole.equalsIgnoreCase("student") || userRole.equalsIgnoreCase("instructor")) {
                            DatabaseHelper.addRole(userEmail, userRole.toLowerCase());
                            showInfoDialog("Role Added", "Role successfully added to user.");
                        } else {
                            showErrorDialog("Invalid Role", "Please enter a valid role (student/instructor).");
                        }
                    });
                } else if (addOrRemove.equalsIgnoreCase("remove")) {
                    DatabaseHelper.removeRole(userEmail);
                    showInfoDialog("Role Removed", "Role successfully removed from user.");
                } else {
                    showErrorDialog("Invalid Action", "Please enter 'add' or 'remove'.");
                }
            });
        });
    }
    public static void showAllMembersOfGroup(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label header = new Label("All Members of Group");
        ListView<String> listView = new ListView<>();

        // Prompt for the group name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Group Name");
        dialog.setHeaderText("Enter the group name to view all members:");
        dialog.showAndWait().ifPresent(groupName -> {
            try {
                // Fetch all members of the group from the database
                List<String> members = DatabaseHelper.getAllMembersOfGroup(groupName);
                listView.getItems().setAll(members);
            } catch (SQLException e) {
                showErrorDialog("Database Error", e.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> Admin.showAdminOptions(primaryStage, null));

        layout.getChildren().addAll(header, listView, backButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }



    static char[] generateOTP() {
        char[] otp = new char[5];
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            otp[i] = (char) (r.nextInt(10) + '0');
        }
        return otp;
    }
    public static int calledFromAdmin() {
    	return 1;
    }

    static String charToStringOTP(char[] otp) {
        return new String(otp);
    }

    private static Optional<String> showTextInputDialog(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(prompt);
        dialog.setContentText(null);
        return dialog.showAndWait();
    }

    private static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
