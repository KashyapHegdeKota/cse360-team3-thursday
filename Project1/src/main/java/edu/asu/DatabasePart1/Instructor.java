package edu.asu.DatabasePart1;

import javafx.stage.Stage;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;


public class Instructor extends User{

	public Instructor(String email, String role, int id) {
		super(email, role, id, role, role, role, role);
	}
	
    public static void main(Stage primaryStage, String email) {
        showInstructionOptions(primaryStage, email);
    }
    
    private static void showInstructionOptions(Stage primaryStage, String email) {
    	Optional<String> choice = showTextInputDialog(
                "Enter your choice (1-6):\n" +
                "1. Add an article.\n" +
                "2. View an article by ID.\n" +
                "3. List all articles\n" +
                "4. Delete an article\n" +
                "5. Backup from an existing file\n" +
                "6. Restore to a file\n"+
                "7. Show articles by groups\n"+
                "8. Log out"
            );
        if (!choice.isPresent()) {
            // Cancel was pressed, go back to main menu
            StartCSE360.showMainLayout(primaryStage);
            return;
        }

        choice.ifPresent(selectedOption -> {
            switch (selectedOption) {
	            case "1":
	            	StartCSE360.createArticle(primaryStage,0);
	                break;
			    case "2":
			    	StartCSE360.viewArticleById(primaryStage,0);
			    	break;
			    case "3":
			    	StartCSE360.displayAllArticles(primaryStage,0);
			        break;
			    case "4":
			    	StartCSE360.deleteArticle(primaryStage,0);
			    	break;
			    case "5":
			    	StartCSE360.restoreArticles(primaryStage,0);
			    	break;
			    case "6":
			    	StartCSE360.backupArticles(primaryStage,0);
			    	break;
			    case "7":
                	StartCSE360.showListByGroupScreen(primaryStage,0);
                	break;
			    case "8":
			        StartCSE360.showMainLayout(primaryStage); // Log out and go back to main menu
			        return; // Stop further calls to showAdminOptions
			    default:
			        showErrorDialog("Invalid Choice", "Please select a valid option (1-6)");
			}
            
            // Only show options again if not logging out (option 6)
           
        });
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