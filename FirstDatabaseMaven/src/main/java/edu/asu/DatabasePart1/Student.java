package edu.asu.DatabasePart1;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;


public class Student extends User{
	
	private static List<String> searchRequests;
	public Student(String email, Role role, int id, String firstName, String middleName, String lastName, String preferredName, String genericMessage, String specificMessage) {
	    super(email, role, id, firstName, middleName, lastName, preferredName, genericMessage, specificMessage);
	    Student.searchRequests = new ArrayList<>();

	}	
    public static void main(Stage primaryStage, String email) {
        showStudentOptions(primaryStage, email);
    }
    
    private static void showStudentOptions(Stage primaryStage, String email) {
    	Optional<String> choice = showTextInputDialog(
                "Enter your choice (1-6):\n" +
                "1. Search an article.\n" +
                "2. Send a generic message to help system\n"+
                "3. Send a specific message to help system\n"+
                "4. List articles by level\n"+
                "5. List articles by group\n"+
                "6. Log out"
            );
        if (!choice.isPresent()) {
            // Cancel was pressed, go back to main menu
            StartCSE360.showMainLayout(primaryStage);
            return;
        }

        choice.ifPresent(selectedOption -> {
            switch (selectedOption) {
	            case "1":
	            	StartCSE360.showArticleSearchScreen(primaryStage);
	                break;
			    case "2":
			    	//StartCSE360.viewArticleById(primaryStage,0);
			    	sendGenericHelpMessage(email);
			    	break;
			    case "3":
			    	//Send a generic message to the help system
			    	sendSpecificHelpMessage(email);
			    	break;
			    case "4":
			    	listArticlesByLevel(primaryStage, email);
			    	break;
			    case "5":
			    	showListByGroupScreen_Student(primaryStage, email);
			    	break;
			    case "6":
			    	//Quit
			    	StartCSE360.showMainLayout(primaryStage);
			    	break;
			    default:
			        showErrorDialog("Invalid Choice", "Please select a valid option (1-6)");
			}
            showStudentOptions(primaryStage, email);
            
            // Only show options again if not logging out (option 6)
           
        });
    }
    
    public static void showListByGroupScreen_Student(Stage primaryStage, String email) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label mainLabel = new Label("List Articles by Group");
        TextField groupField = new TextField();
        groupField.setPromptText("Enter group name");

        Button listButton = new Button("List Articles");
        ListView<String> articleListView = new ListView<>();
        articleListView.setPrefHeight(300); // Set preferred height for better viewing
        DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
        listButton.setOnAction(e -> {
            String groupName = groupField.getText().trim();
            if (groupField.getText() == null) {
                try {
					dbHelper.listAllArticles();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                return;
            }
            
            try {
            	
            	dbHelper.connectToDatabase();
                List<String> articles = dbHelper.listArticlesByGroups(groupName);
                ObservableList<String> articleItems = FXCollections.observableArrayList(articles);
                articleListView.setItems(articleItems);
                dbHelper.closeConnection();
            } catch (Exception ex) {
                showErrorDialog("Error", "Unable to retrieve articles: " + ex.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> StartCSE360.showStudentOptions(primaryStage, email));

        layout.getChildren().addAll(mainLabel, groupField, listButton, articleListView, backButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }
    
    public static void listArticlesByLevel(Stage primaryStage, String email) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("List Articles by Level");
        dialog.setHeaderText("Select the article level to filter:");

        String group;
        // Create the layout with radio buttons
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        ToggleGroup levelGroup = new ToggleGroup();
        RadioButton allButton = new RadioButton("All");
        RadioButton easyButton = new RadioButton("Easy");
        RadioButton intermediateButton = new RadioButton("Intermediate");
        RadioButton advancedButton = new RadioButton("Advanced");
        RadioButton expertButton = new RadioButton("Expert");

        // Add radio buttons to the group
        allButton.setToggleGroup(levelGroup);
        easyButton.setToggleGroup(levelGroup);
        intermediateButton.setToggleGroup(levelGroup);
        advancedButton.setToggleGroup(levelGroup);
        expertButton.setToggleGroup(levelGroup);

        // Set default selection to "All"
        allButton.setSelected(true);

        layout.getChildren().addAll(allButton, easyButton, intermediateButton, advancedButton, expertButton);

        // Add "OK" and "Cancel" buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(layout);

        // Handle the dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                RadioButton selectedButton = (RadioButton) levelGroup.getSelectedToggle();
                return selectedButton.getText();
            }
            return null; // If canceled
        });

        // Show dialog and process the selected level
        dialog.showAndWait().ifPresent(level -> {
            try {
                DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
                dbHelper.connectToDatabase();

                List<String> articles;
                if (level.equalsIgnoreCase("All")) {
                    articles = dbHelper.listAllArticles(); // Fetch all articles
                } else {
                    articles = dbHelper.listArticlesByLevel(level);
                }

                dbHelper.closeConnection();

                // Display articles in a new window
                VBox resultLayout = new VBox(10);
                resultLayout.setStyle("-fx-padding: 10;");

                Label mainLabel = new Label("Articles at Level: " + level);
                ListView<String> articleListView = new ListView<>(FXCollections.observableArrayList(articles));
                Button backButton = new Button("Back");
                backButton.setOnAction(e -> StartCSE360.showStudentOptions(primaryStage, email));

                resultLayout.getChildren().addAll(mainLabel, articleListView, backButton);
                primaryStage.setScene(new Scene(resultLayout, 600, 400));

            } catch (Exception e) {
                showErrorDialog("Error", "Unable to retrieve articles: " + e.getMessage());
            }
        });
    }
    
 
    
    public static void sendSpecificHelpMessage(String email) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Specific Help Request");
        dialog.setHeaderText("Enter what you need help with:");
        dialog.setContentText(null);

        Optional<String> helpRequest = dialog.showAndWait();
        helpRequest.ifPresent(request -> {
            if (request.isEmpty()) {
                showErrorDialog("Input Error", "Please specify what you need help with.");
            } else {
                DatabaseHelper.storeSpecificMessage(request, email);  // Store specific message in the database
                showInfoDialog("Specific Help Message", "Help message sent: 'I am unable to find information about: " + request + "'");
            }
        });
    }
    
    public static void sendGenericHelpMessage(String email) {
    	String message = "Generic Message: I'm confused about how to use this tool.";
        showInfoDialog("Generic Help Message", "Help message sent: 'I'm confused about how to use this tool.'");
        DatabaseHelper.storeGenericMessage(message, email);
        
        
    }
    
    
    public void displaySearchRequests() {
        if (searchRequests.isEmpty()) {
            System.out.println("No specific search requests made.");
        } else {
            System.out.println("Search requests made by this student:");
            searchRequests.forEach(request -> System.out.println("- " + request));
        }
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