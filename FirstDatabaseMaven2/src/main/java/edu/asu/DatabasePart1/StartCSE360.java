package edu.asu.DatabasePart1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class StartCSE360 extends Application {
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    private static String adminEmail;

    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CSE360 Database Application");
        showMainLayout(primaryStage);  // Initialize with the main layout
    }

    public static void showMainLayout(Stage primaryStage) {
        VBox mainLayout = new VBox(10);
        mainLayout.setStyle("-fx-padding: 10;");

        Label welcomeLabel = new Label("Welcome to the Database Application");
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Button quitButton = new Button("Quit");

        loginButton.setOnAction(e -> showLoginScreen(primaryStage));
        registerButton.setOnAction(e -> {
			try {
				showRegisterScreen(primaryStage);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}); // Updated for registration screen
        quitButton.setOnAction(e -> {
            databaseHelper.closeConnection();
            primaryStage.close();
        });

        mainLayout.getChildren().addAll(welcomeLabel, loginButton, registerButton, quitButton);
        primaryStage.setScene(new Scene(mainLayout, 400, 300));
        primaryStage.show();

        try {
            databaseHelper.connectToDatabase();
            if (databaseHelper.isDatabaseEmpty()) {
                setupAdministrator(primaryStage);
            }
        } catch (SQLException e) {
            showErrorDialog("Database Error", e.getMessage());
        }
    }

    private static void showLoginScreen(Stage primaryStage) {
        VBox loginLayout = new VBox(10);
        loginLayout.setStyle("-fx-padding: 10;");

        Label loginLabel = new Label("User Login");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button submitButton = new Button("Login");
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> StartCSE360.showMainLayout(primaryStage));

        submitButton.setOnAction(e -> {
            try {
                if (databaseHelper.login(emailField.getText(), passwordField.getText(), "student")) {
                    showMainScreen(primaryStage, "User");
                } else if (databaseHelper.login(emailField.getText(), passwordField.getText(), "admin")) {
                    Admin.main(primaryStage, emailField.getText());  // Open admin options
                } else if(databaseHelper.login(emailField.getText(),passwordField.getText(),"instructor")) {
                    Instructor.main(primaryStage,emailField.getText());
                }else {
                	showErrorDialog("Login Failed", "Invalid credentials");
                }
            } catch (SQLException ex) {
                showErrorDialog("Error", ex.getMessage());
            }
        });

        loginLayout.getChildren().addAll(loginLabel, emailField, passwordField, submitButton,backButton);
        primaryStage.setScene(new Scene(loginLayout, 400, 300));
    }

    private static void showRegisterScreen(Stage primaryStage) throws SQLException {
        VBox registerLayout = new VBox(10);
        registerLayout.setStyle("-fx-padding: 10;");

        Label registerLabel = new Label("User Registration");
        Optional<String> userOtp = showTextInputDialog("Enter the OTP:");
        String adminOtp = DatabaseHelper.getOTP();
        userOtp.ifPresent(otpInput -> {
            if (otpInput.equals(adminOtp)) {
	
		        TextField emailField = new TextField();
		        emailField.setPromptText("Email");
		        PasswordField passwordField = new PasswordField();
		        passwordField.setPromptText("Password");
		        PasswordField rePasswordField = new PasswordField();
		        rePasswordField.setPromptText("Re-enter Password");
		        
		        Button submitButton = new Button("Next");
		        Button backButton = new Button("Back");
		        backButton.setOnAction(e -> StartCSE360.showMainLayout(primaryStage));
		        submitButton.setOnAction(e -> {
		            String password = passwordField.getText();
		            String rePassword = rePasswordField.getText();
		
		            String passwordValidationResult = PasswordEvaluator.evaluatePassword(password);
		            if (!password.equals(rePassword)) {
		                showErrorDialog("Error", "Passwords do not match");
		            } else if (!"Success".equals(passwordValidationResult)) {
		                showErrorDialog("Invalid Password", "Password does not meet requirements: " + passwordValidationResult);
		            } else {
		                VBox setUpLayout = new VBox(10);
						setUpLayout.setStyle("-fx-padding:10;");
						Label setUpLabel = new Label("User Set-up");
						TextField firstField= new TextField();
						firstField.setPromptText("First Name");
						TextField middleField = new TextField();
						middleField.setPromptText("Middle Name");
						TextField lastField= new TextField();
						lastField.setPromptText("Last Name");
						TextField preferredField = new TextField();
						preferredField.setPromptText("Preferred Name");
						Button instructorButton = new Button("Instructor");
						Button studentButton = new Button("Student");
				        Button BackButton = new Button("Back");
				        BackButton.setOnAction(ev -> {
							try {
								StartCSE360.showRegisterScreen(primaryStage);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						});
						instructorButton.setOnAction(event ->{
							try {
								String firstName = firstField.getText();
								String middleName = middleField.getText();
								String lastName = lastField.getText();
								String preferredName = preferredField.getText();
								DatabaseHelper.register(emailField.getText(), password, "instructor", "1", emailField.getText(), firstName, middleName, lastName, preferredName);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						    showMainScreen(primaryStage, "Instructor");
						});
						studentButton.setOnAction(event ->{
							try {
								String firstName = firstField.getText();
								String middleName = middleField.getText();
								String lastName = lastField.getText();
								String preferredName = preferredField.getText();
								DatabaseHelper.register(emailField.getText(), password, "student", "1", emailField.getText(), firstName, middleName, lastName, preferredName);
							}catch (SQLException e1) {
								e1.printStackTrace();
							}
							showMainScreen(primaryStage,"Student");
						});
						setUpLayout.getChildren().addAll(setUpLabel, firstField, middleField, lastField, preferredField,instructorButton,studentButton,BackButton);
						primaryStage.setScene(new Scene(setUpLayout, 400, 300));
		            }
		        });
		
		        registerLayout.getChildren().addAll(registerLabel, emailField, passwordField, rePasswordField, submitButton,backButton);
		        primaryStage.setScene(new Scene(registerLayout, 400, 300));
            }else {
            	showErrorDialog("Error","OTP's don't match!");
            }});
        	
        
    }

    private static void setupAdministrator(Stage primaryStage) {
        VBox adminSetupLayout = new VBox(10);
        adminSetupLayout.setStyle("-fx-padding: 10;");

        Label setupLabel = new Label("Setup Administrator Access");
        TextField adminEmailField = new TextField();
        adminEmailField.setPromptText("Admin Email");
        PasswordField adminPasswordField = new PasswordField();
        adminPasswordField.setPromptText("Password");
        PasswordField adminRePasswordField = new PasswordField();
        adminRePasswordField.setPromptText("Re-enter Password");

        Button setupButton = new Button("Setup");
        setupButton.setOnAction(e -> {
            if (adminPasswordField.getText().equals(adminRePasswordField.getText())) {
				VBox setUpLayout = new VBox(10);
				setUpLayout.setStyle("-fx-padding:10;");
				Label setUpLabel = new Label("Admin Set-up");
				TextField firstField= new TextField();
				firstField.setPromptText("First Name");
				TextField middleField = new TextField();
				middleField.setPromptText("Middle Name");
				TextField lastField= new TextField();
				lastField.setPromptText("Last Name");
				TextField preferredField = new TextField();
				preferredField.setPromptText("Preferred Name");
				Button submitButton = new Button("Register");
				submitButton.setOnAction(event -> {
					try {
						String firstName = firstField.getText();
						String middleName = middleField.getText();
						String lastName = lastField.getText();
						String preferredName = preferredField.getText();
						DatabaseHelper.register(adminEmailField.getText(), adminPasswordField.getText(), "admin", "1",adminEmailField.getText(),firstName, middleName, lastName, preferredName);
					}catch(SQLException e1) {
						e1.printStackTrace();
					}
				    showMainScreen(primaryStage, "Admin");
				    Admin.main(primaryStage, adminEmailField.getText());
				});
				
			    setUpLayout.getChildren().addAll(setUpLabel, firstField, middleField, lastField, preferredField,submitButton);
				primaryStage.setScene(new Scene(setUpLayout, 400, 300));
			} else {
			    showErrorDialog("Setup Error", "Passwords do not match");
			}
        });

        adminSetupLayout.getChildren().addAll(setupLabel, adminEmailField, adminPasswordField, adminRePasswordField, setupButton);
        primaryStage.setScene(new Scene(adminSetupLayout, 400, 300));
    }

    private static void showMainScreen(Stage primaryStage, String role) {
        VBox mainScreenLayout = new VBox(10);
        mainScreenLayout.setStyle("-fx-padding: 10;");

        Label mainLabel = new Label("Welcome, " + role);
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showMainLayout(primaryStage));

        mainScreenLayout.getChildren().addAll(mainLabel, logoutButton);
        primaryStage.setScene(new Scene(mainScreenLayout, 400, 300));
    }
    
    @SuppressWarnings("unchecked")
	public static void showUsersByAdmin(Stage primaryStage) {
    	VBox showAdmin = new VBox(10);
    	showAdmin.setStyle("-fx-padding: 10;");
    	
    	Label mainLabel = new Label("User accounts");
    	
    	TableView<User> userTable = new TableView<>();
    	TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
    	idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

    	TableColumn<User, String> emailColumn = new TableColumn<>("Email");
    	emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

    	TableColumn<User, String> roleColumn = new TableColumn<>("Role");
    	roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    	
    	TableColumn<User,String> firstColumn = new TableColumn<>("First Name");
    	firstColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    	
    	TableColumn<User,String> middleColumn = new TableColumn<>("Middle Name");
    	middleColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
    	
    	TableColumn<User,String> lastColumn = new TableColumn<>("Last Name");
    	lastColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    	TableColumn<User,String> preferredColumn = new TableColumn<>("Preferred Name");
    	preferredColumn.setCellValueFactory(new PropertyValueFactory<>("preferredName"));

    	userTable.getColumns().addAll(idColumn, emailColumn, roleColumn,firstColumn,middleColumn,lastColumn,preferredColumn);
    	
        try {
        	ObservableList<User> userList = FXCollections.observableArrayList(DatabaseHelper.displayUsersByAdmin());
        	userTable.setItems(userList);
        } catch(SQLException e) {
        	System.out.print(e);
        }
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> StartCSE360.showMainLayout(primaryStage));

        showAdmin.getChildren().addAll(mainLabel, userTable, backButton);
        primaryStage.setScene(new Scene(showAdmin, 600, 400));
    }
    
    public static void createArticle(Stage primaryStage, int bool) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Create Article");

        // Set up the labels and fields in a grid layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorsField = new TextField();
        authorsField.setPromptText("Authors");
        TextField abstractField = new TextField();
        abstractField.setPromptText("Abstract");
        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Keywords");
        TextField bodyField = new TextField();
        bodyField.setPromptText("Body");
        TextField referencesField = new TextField();
        referencesField.setPromptText("References");
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group Name");

        // Adding each field to the grid with correct row indexes
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Authors:"), 0, 1);
        grid.add(authorsField, 1, 1);
        grid.add(new Label("Abstract:"), 0, 2);
        grid.add(abstractField, 1, 2);
        grid.add(new Label("Keywords:"), 0, 3);
        grid.add(keywordsField, 1, 3);
        grid.add(new Label("Body:"), 0, 4);
        grid.add(bodyField, 1, 4);
        grid.add(new Label("References:"), 0, 5);
        grid.add(referencesField, 1, 5);
        grid.add(new Label("Group Name (Separate by /):"), 0, 6);
        grid.add(groupNameField, 1, 6);
        
        // Radio buttons for article level
        Label levelLabel = new Label("Level:");
        RadioButton easyButton = new RadioButton("Easy");
        RadioButton intermediateButton = new RadioButton("Intermediate");
        RadioButton advancedButton = new RadioButton("Advanced");
        RadioButton expertButton = new RadioButton("Expert");
        
        ToggleGroup levelGroup = new ToggleGroup();
        easyButton.setToggleGroup(levelGroup);
        intermediateButton.setToggleGroup(levelGroup);
        advancedButton.setToggleGroup(levelGroup);
        expertButton.setToggleGroup(levelGroup);

        HBox levelBox = new HBox(10, easyButton, intermediateButton, advancedButton, expertButton);
        grid.add(levelLabel, 0, 7);  // Moved to row 7 to avoid overlap
        grid.add(levelBox, 1, 7);    // Moved to row 7

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                RadioButton selectedLevelButton = (RadioButton) levelGroup.getSelectedToggle();
                String level = selectedLevelButton != null ? selectedLevelButton.getText() : "Easy"; // Default to Easy if none selected

                return new String[]{
                    null, // ID (handled in addArticle if null)
                    level,
                    titleField.getText(),
                    authorsField.getText(),
                    abstractField.getText(),
                    keywordsField.getText(),
                    bodyField.getText(),
                    referencesField.getText(),
                    groupNameField.getText()
                };
            }
            return null;
        });

        dialog.showAndWait().ifPresent(articleArray -> {
            if (articleArray[2].isEmpty() || articleArray[3].isEmpty()) {
                showErrorDialog("Input Error", "Title and Authors are required.");
                return;
            }
            try {
                DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
                dbHelper.connectToDatabase();
                dbHelper.addArticle(articleArray);
                dbHelper.closeConnection();
                showInfoDialog("Article Created", "Article added successfully.");
            } catch (Exception e) {
                showErrorDialog("Error", "Unable to create article: " + e.getMessage());
            }
        });

        if (bool == 1) {
            Admin.showAdminOptions(primaryStage, adminEmail);
        } else {
            Instructor.main(primaryStage, adminEmail);
        }
    }

    
    public static void deleteArticle(Stage primaryStage, int bool) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Article");
        dialog.setHeaderText("Enter the article ID to delete:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
                dbHelper.connectToDatabase();
                dbHelper.removeArticle(id);
                dbHelper.closeConnection();
                showInfoDialog("Article Deleted", "Article deleted successfully.");
            } catch (Exception e) {
                showErrorDialog("Error", "Unable to delete article: " + e.getMessage());
            }
        });
        if(bool == 1) {
	        Admin.showAdminOptions(primaryStage, adminEmail);
        }else {
        	Instructor.main(primaryStage, adminEmail);
        }
    }
    
    public static void displayAllArticles(Stage primaryStage, int bool) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label mainLabel = new Label("All Articles");
        ListView<String> articleListView = new ListView<>();

        try {
            DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
            dbHelper.connectToDatabase();

            List<String> articles = dbHelper.listAllArticles();
            dbHelper.closeConnection();

            // Update the ListView on the JavaFX Application Thread
            Platform.runLater(() -> articleListView.getItems().setAll(articles));

        } catch (Exception e) {
            showErrorDialog("Database Error", "Unable to retrieve articles: " + e.getMessage());
        }

        Button backButton = new Button("Back");
        if (bool == 1) {
            backButton.setOnAction(e -> Admin.showAdminOptions(primaryStage, adminEmail));
        } else {
            backButton.setOnAction(e -> Instructor.main(primaryStage, adminEmail));
        }

        layout.getChildren().addAll(mainLabel, articleListView, backButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }

    public static void viewArticleById(Stage primaryStage, int bool) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label mainLabel = new Label("View Article by ID");
        TextField idField = new TextField();
        idField.setPromptText("Enter Article ID");
        
        Button viewButton = new Button("View Article");
        Label articleDetails = new Label();
        articleDetails.setWrapText(true); // Allows long text to wrap within the label

        viewButton.setOnAction(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                showErrorDialog("Input Error", "Please enter a valid article ID.");
                return;
            }

            try {
                DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
                dbHelper.connectToDatabase();
                String article = dbHelper.getArticleById(Long.parseLong(id));
                dbHelper.closeConnection();
                
                if (article != null) {
                    articleDetails.setText(article);
                } else {
                    showErrorDialog("Article Not Found", "No article found with the provided ID.");
                }
            } catch (Exception ex) {
                showErrorDialog("Database Error", "Unable to retrieve article: " + ex.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (bool == 1) {
                Admin.showAdminOptions(primaryStage, adminEmail);
            } else {
                Instructor.main(primaryStage, adminEmail);
            }
        });

        layout.getChildren().addAll(mainLabel, idField, viewButton, articleDetails, backButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }
    
    public static void backupArticles(Stage primaryStage, int bool) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label headerLabel = new Label("Backup Articles");
        Label instructionLabel = new Label("Choose whether to back up all articles or only articles from specific groups:");

        Button allArticlesButton = new Button("Backup All Articles");
        Button specificGroupsButton = new Button("Backup by Group");

        layout.getChildren().addAll(headerLabel, instructionLabel, allArticlesButton, specificGroupsButton);

        // Handler for backing up all articles
        allArticlesButton.setOnAction(e -> {
            TextInputDialog fileDialog = new TextInputDialog();
            fileDialog.setTitle("Backup All Articles");
            fileDialog.setHeaderText("Enter the file name to backup all articles:");

            fileDialog.showAndWait().ifPresent(fileName -> {
                try {
                    DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
                    dbHelper.connectToDatabase();
                    dbHelper.backupArticlesToFile(fileName);
                    dbHelper.closeConnection();
                    showInfoDialog("Backup Successful", "All articles backed up to " + fileName);
                } catch (Exception ex) {
                    showErrorDialog("Error", "Unable to backup articles: " + ex.getMessage());
                }
            });
        });

        // Handler for backing up articles by specific groups
        specificGroupsButton.setOnAction(e -> {
            TextInputDialog groupDialog = new TextInputDialog();
            groupDialog.setTitle("Backup Articles by Group");
            groupDialog.setHeaderText("Enter group names separated by / for backup (e.g., Python/Java):");

            groupDialog.showAndWait().ifPresent(groupNames -> {
                TextInputDialog fileDialog = new TextInputDialog();
                fileDialog.setTitle("Backup by Group");
                fileDialog.setHeaderText("Enter the file name to backup selected groups:");

                fileDialog.showAndWait().ifPresent(fileName -> {
                    try {
                        DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
                        dbHelper.connectToDatabase();
                        DatabaseHelper_Encryption.restoreGroupArticles(fileName, false, groupNames);
                        dbHelper.closeConnection();
                        showInfoDialog("Backup Successful", "Articles from groups (" + groupNames + ") backed up to " + fileName);
                    } catch (Exception ex) {
                        showErrorDialog("Error", "Unable to backup articles: " + ex.getMessage());
                    }
                });
            });
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (bool == 1) {
                Admin.showAdminOptions(primaryStage, adminEmail);
            } else {
                Instructor.main(primaryStage, adminEmail);
            }
        });

        layout.getChildren().add(backButton);
        primaryStage.setScene(new Scene(layout, 400, 250));
    }

    public static void restoreArticles(Stage primaryStage, int bool) {
        TextInputDialog fileDialog = new TextInputDialog();
        fileDialog.setTitle("Restore Articles");
        fileDialog.setHeaderText("Enter the file name to restore articles from:");

        fileDialog.showAndWait().ifPresent(fileName -> {
            // Ask if user wants to replace existing articles
            Alert replaceDialog = new Alert(Alert.AlertType.CONFIRMATION);
            replaceDialog.setTitle("Replace Existing Articles?");
            replaceDialog.setHeaderText("Do you want to replace existing articles?");
            replaceDialog.setContentText("Press OK to replace, or Cancel to keep existing articles.");

            boolean replaceExisting = replaceDialog.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .isPresent();

            TextInputDialog groupDialog = new TextInputDialog();
            groupDialog.setTitle("Restore by Group");
            groupDialog.setHeaderText("Enter group names separated by / to restore specific groups, or leave empty to restore all:");

            groupDialog.showAndWait().ifPresent(groupNames -> {
                try {
                    DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
                    dbHelper.connectToDatabase();
                    if (groupNames.isEmpty()) {
                        // Restore all articles if no group specified
                        dbHelper.restoreArticlesfromFile(fileName, replaceExisting, ""); // Pass empty string to signify all groups
                    } else {
                        // Restore only specific groups
                        dbHelper.restoreArticlesfromFile(fileName, replaceExisting, groupNames);
                    }
                    dbHelper.closeConnection();
                    showInfoDialog("Restore Successful", "Articles restored from " + fileName);
                } catch (Exception e) {
                    showErrorDialog("Error", "Unable to restore articles: " + e.getMessage());
                }
            });
        });

        if (bool == 1) {
            Admin.showAdminOptions(primaryStage, adminEmail);
        } else {
            Instructor.main(primaryStage, adminEmail);
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
        alert.setHeaderText("An Error Occurred");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }


    private static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void showListByGroupScreen(Stage primaryStage, int bool) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label mainLabel = new Label("List Articles by Group");
        TextField groupField = new TextField();
        groupField.setPromptText("Enter group name");

        Button listButton = new Button("List Articles");
        ListView<String> articleListView = new ListView<>();
        articleListView.setPrefHeight(300); // Set preferred height for better viewing

        listButton.setOnAction(e -> {
            String groupName = groupField.getText().trim();
            if (groupName.isEmpty()) {
                showErrorDialog("Input Error", "Please enter a valid group name.");
                return;
            }
            
            try {
            	DatabaseHelper_Encryption dbHelper = new DatabaseHelper_Encryption();
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
        backButton.setOnAction(e -> Admin.showAdminOptions(primaryStage, adminEmail));

        layout.getChildren().addAll(mainLabel, groupField, listButton, articleListView, backButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}