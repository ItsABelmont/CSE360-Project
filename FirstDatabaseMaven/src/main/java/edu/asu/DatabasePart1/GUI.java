package edu.asu.DatabasePart1;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * </p> GUI Class </p>
 * 
 * </p> Description: This class contains the definition for the graphical interface </p>
 * 
 * @author Just Wise
 *
 * @version 1.00	2024-10-9	Functions defining use cases for system login
 * @version 1.1		2024-10-29  Functions defining use cases for article management 
 * 
 */
public class GUI extends Application {
	private static Stage appStage;
	private static DatabaseHelper databaseHelper;
	
	private static int numGroups;
	
	public static String loginPreferredName;
	public static String currentEmail;
	
	public static final double WINDOW_WIDTH = 512;
	public static final double WINDOW_HEIGHT = 384;
	public static final String FONT_NAME = "Arial";
	
	private static ArrayList<String> searchHistory = new ArrayList<String>();
	
	/**
	 * This is used for routing into the main runtime from StartCSE360
	 * @param args
	 */
	public static void start(DatabaseHelper dbh) {
		databaseHelper = dbh;
		launch();
	}
	
	/**
	 * This is what is run by JavaFX to begin the application
	 */
	@Override
	public void start(Stage stage) throws Exception {
		appStage = stage;
		
		appStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/edu/asu/DatabasePart1/Icon.png")));
		
		//Prevent the window from being resized
		appStage.setResizable(false);
		
		//Add a title to the window
		appStage.setTitle("Help System");
		
		try {
			//If the database is empty, start in the account creation page
			if (databaseHelper.isDatabaseEmpty()) {
				setCreateNewAccountPage();
			} else {//Otherwise start in the login page
				setLoginPage();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method sets up the login page
	 */
	public static void setLoginPage() {
		Pane root = new Pane();
		
		//This error message text will only show if text is added to it
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 170);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Help System Login", 50, 512, Pos.CENTER, 0, 0);
		
		//Text and an input field for the email
		Label emailTitle = createLabel("Email:", 15, 512, Pos.CENTER, 0, 70);
		TextField emailInput = createTextField("", 15, 256, Pos.CENTER, 128, 90);
		
		//Text and an input field for the password
		Label passwordTitle = createLabel("Password:", 15, 512, Pos.CENTER, 0, 120);
		TextField passwordInput = createPasswordField(15, 256, Pos.CENTER, 128, 140);
		
		//Pressing this button will cause the system to try logging in with the credentials in the text fields
		Button loginButton = createButton(
				(event) -> {
					//Try logging in to the database and get the array of possible roles the user has
					String[] roles = databaseHelper.login(emailInput.getText(), passwordInput.getText());
					if (roles.length > 0) {
						currentEmail = emailInput.getText();
						if (databaseHelper.shouldUserReset(currentEmail) || databaseHelper.checkFinish(currentEmail)) {
							setSetupAccountPage();
						}
						else
							setLoggingInPage(roles);
					} else {//If there are no roles, the login failed and there was no login with those credentials
						failCreateAccount(errorMessage, "Invalid email or password");
					}
				},
			"Login", 15, 96, Pos.CENTER, 210, 190);
		
		Button createAccountButton = createButton(
				(event) -> {
					setCreateNewAccountPage();
				},
			"New User", 13, 64, Pos.CENTER, 218, 240);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, emailTitle, emailInput, passwordTitle, passwordInput,
				loginButton, createAccountButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method sets up the page to ask for a user's login role
	 */
	public static void setLoggingInPage(String[] roles) {
		loginPreferredName = databaseHelper.getpreferredName();
		
		//If there is only one role, skip this page
		if (roles.length == 1) {
			if (roles[0].equals("admin")) {
				setAdminPage();
				return;
			} else if (roles[0].equals("instructor")) {
				setInstructorPage();
				return;
			} else if (roles[0].equals("student")) {
				setStudentPage();
				return;
			} else {
				return;
			}
		}
		
		Pane root = new Pane();
		
		//Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 170);
		//errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Select a Role", 50, 512, Pos.CENTER, 0, 0);
		
		//Login as an admin
		Button adminButton = createButton(
				(event) -> {
					setAdminPage();
				},
			"Admin", 15, 96, Pos.CENTER, 202, 70);
		
		//Login as an instructor
		Button instructorButton = createButton(
				(event) -> {
					setInstructorPage();
				},
			"Instructor", 15, 96, Pos.CENTER, 202, 120);
		
		//Login as a student
		Button studentButton = createButton(
				(event) -> {
					setStudentPage();
				},
			"Student", 15, 96, Pos.CENTER, 202, 170);
		
		//Go back to the login page
		Button backButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Back", 15, 76, Pos.CENTER, 212, 250);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, backButton);
		for (String s : roles) {//Only add each button if the user has the role for it
			if (s.equals("admin")) {
				root.getChildren().add(adminButton);
			} else if (s.equals("instructor")) {
				root.getChildren().add(instructorButton);
			} else if (s.equals("student")) {
				root.getChildren().add(studentButton);
			}
		}
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method sets up the admin page after logging in
	 */
	public static void setAdminPage() {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 320);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("HELLO, " + loginPreferredName, 30, 512, Pos.CENTER, 0, 0);
		
		//Generate an invite code
		Button generateCodeButton = createButton(
				(event) -> {
					setGenerateInviteCodePage();
				},
			"Generate Invite Code", 13, 158, Pos.CENTER, 180, 70);
		
		//Go to the list users page
		Button listUsersButton = createButton(
				(event) -> {
					setListUsersPage("admin");
				},
			"List Users", 13, 158, Pos.CENTER, 180, 120);
		
		//Go to the article page
		Button articlesButton = createButton(
				(event) -> {
					setArticleModPage("admin");
				},
			"Articles", 13, 158, Pos.CENTER, 180, 170);
		
		//Go to the restore page
		Button backupButton = createButton(
				(event) -> {
					setBackupPage("admin");
				},
			"Backup System", 13, 158, Pos.CENTER, 180, 220);
		
		//Go to the restore page
		Button restoreButton = createButton(
				(event) -> {
					setRestorePage("admin");
				},
			"Restore System", 13, 158, Pos.CENTER, 180, 270);
		
		//Logout button
		Button logoutButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Logout", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, generateCodeButton, listUsersButton, articlesButton, backupButton, restoreButton, logoutButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Creates the page for packing up articles and groups
	 */
	public static void setBackupPage(String type) {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Backup System", 30, 512, Pos.CENTER, 0, 0);
		
		Label groupName = createLabel("Group Name", 15, 512, Pos.CENTER, 0, 70);
		TextField groupField = createTextField("", 15, 256, Pos.CENTER, 128, 90);
		
		Label fileName = createLabel("Export File Name:", 15, 512, Pos.CENTER, 0, 120);
		TextField fileNameField = createTextField("", 15, 256, Pos.CENTER, 128, 140);
		
		//Backs up the whole article system to a file
		Button wholeSystemButton = createButton(
				(event) -> {
					String file = fileNameField.getText();
					if (file.equals("")) {
						errorMessage.setText("File must have a name!");
					} else {
						databaseHelper.backupArticles(fileNameField.getText());
						if (type.equals("admin"))
							setAdminPage();
						else if (type.equals("instructor"))
							setInstructorPage();
						else setStudentPage();
					}
				},
				"Backup Whole System", 13, 158, Pos.CENTER, 180, 170);
		
		//Update an article's info
		Button groupButton = createButton(
				(event) -> {
					String group = groupField.getText();
					String file = fileNameField.getText();
					if (group.equals("") || file.equals("")) {
						errorMessage.setText("Group and file must have a name!");
					} else {
						if (!databaseHelper.groupBackupArticles(file, group)) {
							errorMessage.setText("Group name \"" + group + "\" does not exist!");
						} else {
							if (type.equals("admin"))
								setAdminPage();
							else if (type.equals("instructor"))
								setInstructorPage();
							else setStudentPage();
						}
					}
				},
			"Backup Group", 13, 158, Pos.CENTER, 180, 220);
		
		//Back button
		Button backButton;
		backButton = createButton(
				(event) -> {
					setStudentPage();
				},
				"Back", 15, 64, Pos.CENTER, 428, 40);
		
		if (type.equals("admin")) {
			backButton.setOnAction((event) -> {
				setAdminPage();
			});
		} else if (type.equals("instructor")) {
			backButton.setOnAction((event) -> {
				setInstructorPage();
			});
		}
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, groupName, groupField, fileName, fileNameField, wholeSystemButton, groupButton, backButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Creates the page for packing up articles and groups
	 */
	public static void setRestorePage(String type) {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Backup System", 30, 512, Pos.CENTER, 0, 0);
		
		Label fileName = createLabel("Import file:", 15, 512, Pos.CENTER, 0, 120);
		Label fileNameImported = createLabel("", 15, 512, Pos.CENTER, 0, 140);
		FileChooser fileNameChooser = new FileChooser();
		
		//Backs up the whole article system to a file
		Button fileNameField = createButton(
				(event) -> {
					File file = fileNameChooser.showOpenDialog(appStage);
					fileNameImported.setText(file.getName());
				},
				"Select file", 13, 158, Pos.CENTER, 180, 160);
		
		CheckBox mergeArticles = createCheckBox("Merge articles with preexisting database\n(no checkmark will replace the database with the file)", 15, 96, Pos.CENTER, 110, 190);
		mergeArticles.setSelected(true);
		
		//Restores the system from a button click
		Button wholeSystemButton = createButton(
				(event) -> {
					if (!fileNameImported.getText().equals("")) {
						boolean success;
						if (!mergeArticles.isSelected()) {
							success = databaseHelper.restoreArticles(fileNameImported.getText());
						} else {
							success = databaseHelper.mergeArticles(fileNameImported.getText());
						}
						if (!success) {
							errorMessage.setText("File does not exist!");
						} else {
							if (type.equals("admin"))
								setAdminPage();
							else if (type.equals("instructor"))
								setInstructorPage();
							else setStudentPage();
						}
					} else {
						errorMessage.setText("Must select a file to restore from");
					}
				},
				"Restore from file", 13, 158, Pos.CENTER, 180, 250);
		
		//Back button
		Button backButton;
		backButton = createButton(
				(event) -> {
					setStudentPage();
				},
				"Back", 15, 64, Pos.CENTER, 428, 40);
		
		if (type.equals("admin")) {
			backButton.setOnAction((event) -> {
				setAdminPage();
			});
		} else if (type.equals("instructor")) {
			backButton.setOnAction((event) -> {
				setInstructorPage();
			});
		}
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, fileName, fileNameImported, fileNameField, mergeArticles, wholeSystemButton, backButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Creates the page for interacting with the article database
	 */
	public static void setArticleModPage(String type) {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Article System", 30, 512, Pos.CENTER, 0, 0);
		
		//Create a new article
		Button createButton = createButton(
				(event) -> {
					setCreateArticlePage(type);
				},
			"Create Article", 13, 158, Pos.CENTER, 180, 70);
		
		//View an article
		Button viewButton = createButton(
				(event) -> {
					setViewArticlesStartPage(type);
				},
			"View Articles", 13, 158, Pos.CENTER, 180, 150);
		
		//Back button
		Button backButton;
		backButton = createButton(
				(event) -> {
					setStudentPage();
				},
				"Back", 15, 64, Pos.CENTER, 428, 40);
		if (type.equals("admin")) {
			backButton.setOnAction((event) -> {
				setAdminPage();
			});
		} else if (type.equals("instructor")) {
			backButton.setOnAction((event) -> {
				setInstructorPage();
			});
		}
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, createButton, viewButton, backButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This sets the page for creating a new article
	 * @param type
	 */
	public static void setCreateArticlePage(String type) {
		Pane root = new Pane();
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(root);
		
		Label errorMessage = createLabel("", 15, 500, Pos.CENTER, 6, 560);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Create Article", 30, 500, Pos.CENTER, 6, 0);
		
		Label titleName = createLabel("Title:", 12, 500, Pos.CENTER, 6, 40);
		TextField titleNameField = createTextField("", 15, 500, Pos.CENTER, 6, 55);
		
		Label groupName = createLabel("Group:", 12, 500, Pos.CENTER, 6, 90);
		TextField groupNameField = createTextField("", 15, 500, Pos.CENTER, 6, 105);
		
		CheckBox specialGroup = createCheckBox("Special Group", 12, 128, Pos.CENTER, 6, 85);
		specialGroup.setOnAction((event)-> {
			numGroups = 0;
			databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
				if (group.equals(groupNameField.getText())) {
					numGroups = 1;
				}
			});
			databaseHelper.forEachSpecialArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
				if (group.equals(groupNameField.getText())) {
					numGroups = 2;
				}
			});
			if (numGroups == 1)
				specialGroup.setSelected(false);
			if (numGroups == 2)
				specialGroup.setSelected(true);
		});
		
		groupNameField.textProperty().addListener((event, oldString, newString) -> {
			numGroups = 0;
			databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
				if (group.equals(newString)) {
					numGroups = 1;
				}
			});
			databaseHelper.forEachSpecialArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
				if (group.equals(newString)) {
					numGroups = 2;
				}
			});
			if (numGroups == 1)
				specialGroup.setSelected(false);
			if (numGroups == 2)
				specialGroup.setSelected(true);
		});
		
		Label authorsName = createLabel("Authors:", 12, 500, Pos.CENTER, 6, 130);
		TextField authorsNameField = createTextField("", 15, 500, Pos.CENTER, 6, 145);
		
		Label abstractName = createLabel("Abstract:", 12, 500, Pos.CENTER, 6, 170);
		TextField abstractNameField = createTextField("", 15, 500, Pos.CENTER, 6, 185);
		
		Label keywordsName = createLabel("Keywords:", 12, 500, Pos.CENTER, 6, 210);
		TextField keywordsNameField = createTextField("", 15, 500, Pos.CENTER, 6, 225);
		
		//The body is unique and therefore has it's own TextArea
		Label bodyName = createLabel("Body:", 12, 500, Pos.CENTER, 6, 250);
		TextArea bodyNameField = createTextArea("", 15, 300, true, 6, 270);
		bodyNameField.setMaxWidth(512);
		bodyNameField.setMinHeight(70);
		
		Label referencesName = createLabel("References:", 12, 500, Pos.CENTER, 6, 480);
		TextField referencesNameField = createTextField("", 15, 500, Pos.CENTER, 6, 495);
		
		
		//Restores the system from a button click
		Button createButton = createButton(
				(event) -> {
					if (titleNameField.getText().equals("") || groupNameField.getText().equals("") || authorsNameField.getText().equals("") || abstractNameField.getText().equals("") || keywordsNameField.getText().equals("") || bodyNameField.getText().equals("") || referencesNameField.getText().equals("")) {
						errorMessage.setText("Cannot leave fields blank");
					} else {
						if (specialGroup.isSelected()) {
							numGroups = 0;
							databaseHelper.forEachSpecialArticle((id, titleArticle, group, author, abstrac, keywords, body, references, i) -> {
								if (group == groupNameField.getText())
									numGroups = 1;
							});
							boolean success = false;
							if (numGroups == 0 || databaseHelper.doesHaveAdminAccessSpecial(currentEmail, type, groupNameField.getText())) {
								if (numGroups == 0)
									success = databaseHelper.adminCreateSpecialGroup(currentEmail, type, titleNameField.getText(), "", groupNameField.getText(), authorsNameField.getText(), abstractNameField.getText(), keywordsNameField.getText(), bodyNameField.getText(), referencesNameField.getText());
								else
									success = databaseHelper.addToSpecialGroup(currentEmail, titleNameField.getText(), "", groupNameField.getText(), authorsNameField.getText(), abstractNameField.getText(), keywordsNameField.getText(), bodyNameField.getText(), referencesNameField.getText());
								System.out.println(numGroups);
								if (success) {
										setArticleModPage(type);
								} else {
									errorMessage.setText("Error creating article");
								}
							} else {
								errorMessage.setText("No access to this group");
							}
						} else {
							if (databaseHelper.addArticle(titleNameField.getText(), groupNameField.getText(), authorsNameField.getText(), abstractNameField.getText(), keywordsNameField.getText(), bodyNameField.getText(), referencesNameField.getText())) {
								setArticleModPage(type);
							} else {
								errorMessage.setText("Error creating article");
							}
						}
					}
				},
				"Create article", 13, 158, Pos.CENTER, 180, 530);
		
		//Back button
		Button backButton;
		backButton = createButton(
				(event) -> {
					setArticleModPage(type);
				},
				"Back", 15, 64, Pos.CENTER, 428, 20);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, titleName, titleNameField, groupName, 
				groupNameField, authorsName, authorsNameField, abstractName, abstractNameField, 
				keywordsName, keywordsNameField, bodyName, bodyNameField, 
				referencesName, referencesNameField, 
				createButton, specialGroup, backButton, errorMessage);
		
		Scene scene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This page polls the user as to which groups they want to filter by
	 * @param type
	 */
	public static void setViewArticlesStartPage(String type) {
		Pane root = new Pane();
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(root);
		
//		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 560);
//		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Displayed Groups:", 30, 512, Pos.CENTER, 0, 0);
		
		Label groupsName = createLabel("Type the name of each group you want to display, separated by commas:", 12, 512, Pos.CENTER, 0, 40);
		
		//Get how many total groups are there
		numGroups = 0;
		databaseHelper.forEachArticle((id, titleName, group, author, abstrac, keywords, body, references, i) -> {
			numGroups++;
		});
		databaseHelper.forEachSpecialArticle((id, titleName, group, author, abstrac, keywords, body, references, i) -> {
			numGroups++;
		}, currentEmail, type);
		//If there are no groups, skip this page
		if (numGroups <= 0) {
			setViewArticlesPage(type, null);
			return;
		}
		String[] allGroups = new String[numGroups];
		CheckBox[] groupChecks = new CheckBox[numGroups];
		
		//Now loop through each group and add each unique group to the GUI
		numGroups = 0;
		databaseHelper.forEachArticle((id, titleName, group, author, abstrac, keywords, body, references, i) -> {
			boolean duplicate = false;
			for (int u = 0; u < numGroups; u++) {
				if (group.equals(allGroups[u])) {
					duplicate = true;
				}
			}
			
			if (!duplicate) {
				allGroups[numGroups] = group;
				
				CheckBox groupName = createCheckBox(group, 25, 256, Pos.TOP_LEFT, 156, numGroups * 45 + 60);
				groupName.setSelected(true);
				
				//Add the CheckBox to a reference array for later
				groupChecks[numGroups] = groupName;
				
				root.getChildren().addAll(groupName);
				
				numGroups++;
			}
		});
		databaseHelper.forEachSpecialArticle((id, titleName, group, author, abstrac, keywords, body, references, i) -> {
			boolean duplicate = false;
			for (int u = 0; u < numGroups; u++) {
				if (group.equals(allGroups[u])) {
					duplicate = true;
				}
			}
			
			if (!duplicate) {
				allGroups[numGroups] = group;
				
				CheckBox groupName = createCheckBox(group, 25, 256, Pos.TOP_LEFT, 156, numGroups * 45 + 60);
				groupName.setSelected(true);
				
				//Add the CheckBox to a reference array for later
				groupChecks[numGroups] = groupName;
				
				root.getChildren().addAll(groupName);
				
				numGroups++;
			}
		}, currentEmail, type);
		
		//Restores the system from a button click
		Button createButton = createButton(
			(event) -> {
				String[] finalGroups;
				int groupNum = 0;
				int u = 0;
				//Determine how many groups are selected
				while (u < groupChecks.length && groupChecks[u] != null) {
					if (groupChecks[u].isSelected())
						groupNum++;
					u++;
				}
				//Make an array of the correct length
				finalGroups = new String[groupNum];
				u = 0;
				int o = 0;
				//Now loop through the created array and add every checked group to it
				while (u < groupChecks.length && groupChecks[u] != null) {
					if (groupChecks[u].isSelected()) {
						finalGroups[o] = groupChecks[u].getText();
						o++;
					}
					u++;
				}
				
				//Start the view page with the String[] of which groups to display
				setViewArticlesPage(type, finalGroups);
			},
			"Continue", 13, 158, Pos.CENTER, 180, 45 * numGroups + 90);
		
		//Back button
		Button backButton;
		backButton = createButton(
				(event) -> {
					setArticleModPage(type);
				},
				"Back", 15, 64, Pos.CENTER, 428, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, groupsName,
				createButton, backButton);
		
		Scene scene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Views all articles regardless of group
	 * @param type
	 */
	public static void setViewArticlesPage(String type) {
		setViewArticlesPage(type, null);
	}
	
	/**
	 * Sets up the page where every article in the system is displayed
	 */
	public static void setViewArticlesPage(String type, String[] groups) {
		Pane root = new Pane();
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(root);
		
		Pane promptPane = new Pane();
		promptPane.setVisible(false);
		Button noButton = createButton((event) -> {promptPane.setVisible(false);}, "BACK", 30, 256, Pos.CENTER, 128, 162);
		Button deleteButton = createButton((event) -> {}, "DELETE", 30, 256, Pos.CENTER, 128, 232);
		promptPane.getChildren().addAll(noButton, deleteButton);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 0);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Users:", 30, 512, Pos.CENTER, 0, 0);
		
		numGroups = 0;
		//Go through each user and create a label for the name and email, and give them buttons
		databaseHelper.forEachArticle((id, titleName, group, author, abstrac, keywords, body, references, i) -> {
			boolean found = false;
			if (groups != null) {
				for (int u = 0; u < groups.length; u++) {
					if (groups[u].equals(group))
						found = true;
				}
			}
			if (groups == null || found) {
				Label user = createLabel(titleName/* + " (By " + author + ")"*/, 13, 256, Pos.TOP_LEFT, 30, numGroups * 30 + 50);
				Label groupName = createLabel("Group: " + group, 13, 256, Pos.TOP_LEFT, 156, numGroups * 30 + 50);
				
				Button viewButton = createButton(//View button
						(event) -> {
							setArticleViewPage(type, id, false);
						},
					"View", 15, 64, Pos.CENTER, 278, numGroups * 30 + 50);
				
				Button updateButton = createButton(//Edit button
						(event) -> {
							setArticleUpdatePage(type, id, false);
						},
					"Update", 15, 64, Pos.CENTER, 358, numGroups * 30 + 50);
				
				Button deleteArticleButton = createButton(//Delete button
						(event) -> {},
					"Delete", 15, 64, Pos.CENTER, 438, numGroups * 30 + 50);
				//Delete the article on click and stop displaying it in the GUI
				deleteArticleButton.setOnAction((event) -> {
					if (databaseHelper.deleteArticle(id)) {
						user.setVisible(false);
						groupName.setVisible(false);
						viewButton.setVisible(false);
						updateButton.setVisible(false);
						deleteArticleButton.setVisible(false);
					} else {
						errorMessage.setText("Error deleting article");
					}
				});
				
				root.getChildren().addAll(user, groupName, viewButton, updateButton, deleteArticleButton);
				
				numGroups++;
			}
		});
		databaseHelper.forEachSpecialArticle((id, titleName, group, author, abstrac, keywords, body, references, i) -> {
			boolean found = false;
			if (groups != null) {
				for (int u = 0; u < groups.length; u++) {
					if (groups[u].equals(group))
						found = true;
				}
			}
			if (groups == null || found) {
				Label user = createLabel(titleName/* + " (By " + author + ")"*/, 13, 256, Pos.TOP_LEFT, 30, numGroups * 30 + 50);
				Label groupName = createLabel("Group: " + group, 13, 256, Pos.TOP_LEFT, 156, numGroups * 30 + 50);
				
				Button viewButton = createButton(//View button
						(event) -> {
							setArticleViewPage(type, id, true);
						},
					"View", 15, 64, Pos.CENTER, 278, numGroups * 30 + 50);
				
				Button updateButton = createButton(//Edit button
						(event) -> {
							setArticleUpdatePage(type, id, true);
						},
					"Update", 15, 64, Pos.CENTER, 358, numGroups * 30 + 50);
				
				Button deleteArticleButton = createButton(//Delete button
						(event) -> {},
					"Delete", 15, 64, Pos.CENTER, 438, numGroups * 30 + 50);
				//Delete the article on click and stop displaying it in the GUI
				deleteArticleButton.setOnAction((event) -> {
					if (databaseHelper.deleteSpecialArticle(currentEmail, type, id)) {
						user.setVisible(false);
						groupName.setVisible(false);
						viewButton.setVisible(false);
						updateButton.setVisible(false);
						deleteArticleButton.setVisible(false);
					} else {
						errorMessage.setText("Error deleting article");
					}
				});
				
				root.getChildren().addAll(user, groupName, viewButton);
				if (databaseHelper.doesHaveAdminAccessSpecial(currentEmail, type, group)) {
					root.getChildren().addAll(updateButton, deleteArticleButton);
				}
				
				numGroups++;
			}
		}, currentEmail, type);
		
		//Go back to the admin page with a button press
		Button backButton = createButton(
				(event) -> {
					setArticleModPage(type);
				},
			"Back", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, backButton, errorMessage, promptPane);
		
		Scene scene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This page allows a user to edit an article
	 * @param type
	 * @param id
	 */
	public static void setArticleUpdatePage(String type, long id, boolean special) {
		Article article;
		if (special) {
			article = databaseHelper.getSpecialArticle(id);
		} else
			article = databaseHelper.getArticle(id);
		
		Pane root = new Pane();
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(root);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 560);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Update Article", 30, 512, Pos.CENTER, 0, 0);
		
		Label titleName = createLabel("Title:", 12, 512, Pos.CENTER, 0, 40);
		TextField titleNameField = createTextField(article.title, 15, 512, Pos.CENTER, 0, 55);
		
		Label groupName = createLabel("Group:", 12, 512, Pos.CENTER, 0, 90);
		TextField groupNameField = createTextField(article.group, 15, 512, Pos.CENTER, 0, 105);
		
		Label authorsName = createLabel("Authors:", 12, 512, Pos.CENTER, 0, 130);
		TextField authorsNameField = createTextField(article.authors, 15, 512, Pos.CENTER, 0, 145);
		
		Label abstractName = createLabel("Abstract:", 12, 512, Pos.CENTER, 0, 170);
		TextField abstractNameField = createTextField(article.abstrac, 15, 512, Pos.CENTER, 0, 185);
		
		Label keywordsName = createLabel("Keywords:", 12, 512, Pos.CENTER, 0, 210);
		TextField keywordsNameField = createTextField(article.keywords, 15, 512, Pos.CENTER, 0, 225);
		
		Label bodyName = createLabel("Body:", 12, 512, Pos.CENTER, 0, 250);
		TextArea bodyNameField = createTextArea(article.body, 15, 312, true, 0, 270);
		bodyNameField.setMaxWidth(512);
		bodyNameField.setMinHeight(70);
		
		Label referencesName = createLabel("References:", 12, 512, Pos.CENTER, 0, 480);
		TextField referencesNameField = createTextField(article.references, 15, 512, Pos.CENTER, 0, 495);
		
		
		//Saves the newly created article
		Button createButton = createButton(
				(event) -> {
					boolean success = false;
					if (special) {
						success = databaseHelper.updateSpecialArticle(currentEmail, type, id, titleNameField.getText(), groupNameField.getText(), authorsNameField.getText(), abstractNameField.getText(), keywordsNameField.getText(), bodyNameField.getText(), referencesNameField.getText());
					} else
						success = databaseHelper.updateArticle(id, titleNameField.getText(), groupNameField.getText(), authorsNameField.getText(), abstractNameField.getText(), keywordsNameField.getText(), bodyNameField.getText(), referencesNameField.getText());
					if (success) {
						setViewArticlesPage(type);
					} else {
						errorMessage.setText("Error updating article" + (special ? " (you may not have access)" : ""));
					}
				},
				"Save", 13, 158, Pos.CENTER, 180, 530);
		
		//Back button
		Button backButton;
		backButton = createButton(
				(event) -> {
					setViewArticlesPage(type);
				},
				"Back", 15, 64, Pos.CENTER, 428, 20);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, titleName, titleNameField, groupName, 
				groupNameField, authorsName, authorsNameField, abstractName, abstractNameField, 
				keywordsName, keywordsNameField, bodyName, bodyNameField, 
				referencesName, referencesNameField, 
				createButton, backButton, errorMessage);
		
		Scene scene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This page is very similar to the update page, the text fields are just not editable
	 * @param type
	 * @param id
	 */
	public static void setArticleViewPage(String type, long id, boolean special) {
		Article article;
		if (special) {
			article = databaseHelper.getSpecialArticle(id);
		} else
			article = databaseHelper.getArticle(id);
		
		Pane root = new Pane();
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(root);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 530);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("View Article", 30, 512, Pos.CENTER, 0, 0);
		
		//Label titleName = createLabel("Title:", 12, 512, Pos.CENTER, 0, 40);
		TextField titleNameField = createText(article.title, 15, 512, Pos.CENTER, 0, 55);
		
		//Label groupName = createLabel("Group:", 12, 512, Pos.CENTER, 0, 90);
		TextField groupNameField = createText(article.group, 15, 512, Pos.CENTER, 0, 105);
		
		//Label authorsName = createLabel("Authors:", 12, 512, Pos.CENTER, 0, 130);
		TextField authorsNameField = createText(article.authors, 15, 512, Pos.CENTER, 0, 135);
		
		//Label abstractName = createLabel("Abstract:", 12, 512, Pos.CENTER, 0, 170);
		TextField abstractNameField = createText(article.abstrac, 15, 512, Pos.CENTER, 0, 165);
		
		//Label keywordsName = createLabel("Keywords:", 12, 512, Pos.CENTER, 0, 210);
		TextField keywordsNameField = createText(article.keywords, 15, 512, Pos.CENTER, 0, 195);
		
		//Label bodyName = createLabel("Body:", 12, 512, Pos.CENTER, 0, 250);
		TextArea bodyNameField = createTextArea(article.body, 15, 312, true, 0, 230);
		bodyNameField.setEditable(false);
		bodyNameField.setMaxWidth(512);
		bodyNameField.setMinHeight(70);
		
		//Label referencesName = createLabel("References:", 12, 512, Pos.CENTER, 0, 480);
		TextField referencesNameField = createText(article.references, 15, 512, Pos.CENTER, 0, 445);
		
		
		//Back button
		Button backButton;
		backButton = createButton(
				(event) -> {
					if (type.equals("admin") || type.equals("instructor"))
						setViewArticlesPage(type);
					else if (type.equals("student"))
						setSearchPage(type);
				},
				"Back", 15, 64, Pos.CENTER, 428, 20);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, titleNameField, 
				groupNameField, authorsNameField, abstractNameField, 
				keywordsNameField, bodyNameField, 
				referencesNameField, backButton, errorMessage);
		
		Scene scene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Setup the page where you create invite codes as an admin
	 */
	public static void setGenerateInviteCodePage() {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Generate Invite Code", 30, 512, Pos.CENTER, 0, 0);
		
		CheckBox adminRole = createCheckBox("Admin", 15, 96, Pos.CENTER, 202, 90);
		
		CheckBox instructorRole = createCheckBox("Instructor", 15, 96, Pos.CENTER, 202, 130);
		
		CheckBox studentRole = createCheckBox("Student", 15, 96, Pos.CENTER, 202, 170);
		
		TextField outputCode = createText("", 15, 96, Pos.CENTER, 202, 230);
		
		Button generateButton = createButton(
				(event) -> {
					//Based on which boxes are checked, create the roles string array
					int numRoles = 0;
					if (adminRole.isSelected())
						numRoles++;
					if (instructorRole.isSelected())
						numRoles++;
					if (studentRole.isSelected())
						numRoles++;
					String[] roles = new String[numRoles];
					numRoles = 0;
					if (adminRole.isSelected()) {
						roles[numRoles] = "admin";
						numRoles++;
					}
					if (instructorRole.isSelected()) {
						roles[numRoles] = "instructor";
						numRoles++;
					}
					if (studentRole.isSelected()) {
						roles[numRoles] = "student";
						numRoles++;
					}
					//If the code has roles, create an account, otherwise send an error
					if (roles.length > 0)
						generateInviteCode(roles, outputCode);
					else
						errorMessage.setText("Must have role to generate invite code!");
				},
			"Generate", 15, 96, Pos.CENTER, 202, 200);
		
		//Go back to the admin page
		Button backButton = createButton(
				(event) -> {
					setAdminPage();
				},
			"Back", 15, 96, Pos.CENTER, 202, 350);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, adminRole, instructorRole, studentRole,
				backButton, generateButton, outputCode, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This private method creates an invite code and displays it on a text field
	 * @param roles
	 * @param codeText
	 */
	private static void generateInviteCode(String[] roles, TextField codeText) {
		String code = Password.generateRandomString(12);
		databaseHelper.addInviteUser(code, roles);
		
		codeText.setText(code);
	}
	
	/**
	 * Sets up the page where every user in the system is displayed
	 */
	public static void setListUsersPage(String type) {
		Pane root = new Pane();
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(root);
		
		Pane promptPane = new Pane();
		promptPane.setVisible(false);
		Button noButton = createButton((event) -> {promptPane.setVisible(false);}, "BACK", 30, 256, Pos.CENTER, 128, 162);
		Button deleteButton = createButton((event) -> {}, "DELETE", 30, 256, Pos.CENTER, 128, 232);
		promptPane.getChildren().addAll(noButton, deleteButton);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 0);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Users:", 30, 512, Pos.CENTER, 0, 0);
		
		//Go through each user and create a label for the name and email, and give them buttons
		numGroups = 0;
		databaseHelper.forEachUser((id, email, roles, first, middle, last, preferred) -> {
			boolean instr = false;
			int i = id - numGroups;
			if (type.equals("instructor")) {
				boolean bool = false;
				for (String s : roles) {
					if (s.equals("student"))
						bool = true;
				}
				if (!bool) {
					numGroups++;
					return;
				}
				instr = true;
			}
			Label user = createLabel(preferred + " (" + first + " " + middle + " " + last + ")\n" + email, 13, 226, Pos.TOP_LEFT, 30, i * 30 + 50);
			
			Button accessButton = createButton(//Access button
					(event) -> {
						setEditUserAccessPage(email, preferred, roles, type);
					},
				"Edit Access", 15, 64, Pos.CENTER, 238, i * 30 + 50);
			Button userButton = createButton(//Edit button
					(event) -> {
						setEditUserPage(email, preferred);
					},
				"Edit", 15, 64, Pos.CENTER, 338, i * 30 + 50);
			if (!email.equals(currentEmail) && !instr)
				root.getChildren().addAll(accessButton, userButton);
			root.getChildren().addAll(user);
			if (!email.equals(currentEmail) && !instr) {//If the email is not the logged in user, it can be deleted (instructors cannot delete students)
				Button removeUserButton = createButton(
						(event) -> {},
					"Remove user", 14, 64, Pos.CENTER, 398, i * 30 + 50);
				removeUserButton.setOnAction((event) -> {
						promptDelete(promptPane, deleteButton, user, userButton, removeUserButton, databaseHelper.deleteUser(email));
					});
				root.getChildren().add(removeUserButton);
			}
		});
		
		//Go back to the admin page with a button press
		Button backButton = createButton(
				(event) -> {
					if (type.equals("admin"))
						setAdminPage();
					else if (type.equals("instructor"))
						setInstructorPage();
					else setStudentPage();
				},
			"Back", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, backButton, errorMessage, promptPane);
		
		Scene scene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Sets up the page where every user in the system is displayed
	 */
	public static void setEditUserAccessPage(String email, String preferredName, String[] roles, String type) {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Edit user access: " + preferredName, 30, 512, Pos.CENTER, 0, 0);
		
		ArrayList<String> groups = new ArrayList<String>();
		
		databaseHelper.forEachSpecialArticle((id, titleName, group, author, abstrac, keywords, body, references, i) -> {
			for (String str : groups) {
				if (str.equals(group))
					return;
			}
			groups.add(group);
		}, currentEmail, type);
		
		for (int i = 0; i < groups.size(); i++) {
			Label l = createLabel(groups.get(i), 15, 128, Pos.BASELINE_LEFT, 6, 70 + i * 30);
			root.getChildren().add(l);
			for (int u = 0; u < roles.length; u++) {
				String display = roles[u] + " (edit: ";
				if (databaseHelper.doesHaveAdminAccessSpecial(email, roles[u], groups.get(i))) {
					display += "t)";
				} else
					display += "f)";
				CheckBox b = createCheckBox(display, 13, 96, Pos.CENTER, 106 + 128 * u, 70 + i * 30);
				int iD = i;
				int uD = u;
				b.setOnAction((event) -> {
					databaseHelper.removeUserAccessSpecial(email, roles[uD], groups.get(iD));
					if (!b.isSelected()) {
						String text = b.getText();
						if (text.substring(text.length() - 2, text.length() - 1).equals("t")) {
							//b.setText(text.substring(0, text.length() - 2) + "f)");
						} else {
							databaseHelper.addUserAdminAccessSpecial(email, roles[uD], groups.get(iD));
							//b.setText(text.substring(0, text.length() - 2) + "t)");
							//b.setSelected(true);
						}
					} else {
						databaseHelper.addUserAccessViewSpecial(email, roles[uD], groups.get(iD));
					}
					System.out.println(groups.get(iD));
					String displayNew = roles[uD] + " (edit: ";
					if (databaseHelper.doesHaveAdminAccessSpecial(email, roles[uD], groups.get(iD))) {
						displayNew += "t)";
					} else
						displayNew += "f)";
					b.setText(displayNew);
					b.setSelected(databaseHelper.doesUserHaveAccess(email, groups.get(iD)));
				});
				b.setSelected(databaseHelper.doesUserHaveAccess(email, groups.get(i)));
				
				root.getChildren().add(b);
			}
		}
		
//		Button generateButton = createButton(
//				(event) -> {
//					
//				},
//			"Update", 15, 96, Pos.CENTER, 202, 200);
		
		Button backButton = createButton(
				(event) -> {
					if (type.equals("admin"))
						setAdminPage();
					else if (type.equals("instructor"))
						setInstructorPage();
					else
						setStudentPage();
				},
			"Back", 15, 96, Pos.CENTER, 202, groups.size() * 30 + 70);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title,
				backButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method creates a menu to prompt if the admin really wants to delete a user or not
	 * @param prompt
	 * @param yes
	 * @param user
	 * @param userButton
	 * @param removeButton
	 * @param statement
	 */
	private static void promptDelete(Pane prompt, Button yes, Label user, Button userButton, Button removeButton, PreparedStatement statement) {
		//Make the prompt visible
		prompt.setVisible(true);
		//Set the DELETE button of the prompt delete the selected user
		yes.setOnAction((event) -> {
			try {
				statement.executeUpdate();
				prompt.setVisible(false);
				user.setVisible(false);
				userButton.setVisible(false);
				removeButton.setVisible(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Allows you to edit the roles of a user
	 */
	public static void setEditUserPage(String email, String preferredName) {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Edit user: " + preferredName, 30, 512, Pos.CENTER, 0, 0);
		
		CheckBox adminRole = createCheckBox("Admin", 15, 96, Pos.CENTER, 202, 90);
		
		CheckBox instructorRole = createCheckBox("Instructor", 15, 96, Pos.CENTER, 202, 130);
		
		CheckBox studentRole = createCheckBox("Student", 15, 96, Pos.CENTER, 202, 170);
		
		Button generateButton = createButton(
				(event) -> {
					//Create roles from selected check boxes
					int numRoles = 0;
					if (adminRole.isSelected())
						numRoles++;
					if (instructorRole.isSelected())
						numRoles++;
					if (studentRole.isSelected())
						numRoles++;
					String[] roles = new String[numRoles];
					numRoles = 0;
					if (adminRole.isSelected()) {
						roles[numRoles] = "admin";
						numRoles++;
					}
					if (instructorRole.isSelected()) {
						roles[numRoles] = "instructor";
						numRoles++;
					}
					if (studentRole.isSelected()) {
						roles[numRoles] = "student";
						numRoles++;
					}
					if (roles.length > 0)
						databaseHelper.setUserRoles(email, roles);
					else
						errorMessage.setText("Must have role to generate invite code!");
				},
			"Update", 15, 96, Pos.CENTER, 202, 200);
		
		Button backButton = createButton(
				(event) -> {
					setAdminPage();
				},
			"Back", 15, 96, Pos.CENTER, 202, 350);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, adminRole, instructorRole, studentRole,
				backButton, generateButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method sets up the instructor page after logging in
	 */
	public static void setInstructorPage() {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 320);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("HELLO, " + loginPreferredName, 30, 512, Pos.CENTER, 0, 0);
		
		//Go to the list users page
		Button listUsersButton = createButton(
				(event) -> {
					setListUsersPage("instructor");
				},
			"List Users", 13, 158, Pos.CENTER, 180, 120);
		
		//Go to the article page
		Button articlesButton = createButton(
				(event) -> {
					setArticleModPage("instructor");
				},
			"Articles", 13, 158, Pos.CENTER, 180, 170);
		
		//Go to the restore page
		Button backupButton = createButton(
				(event) -> {
					setBackupPage("instructor");
				},
			"Backup System", 13, 158, Pos.CENTER, 180, 220);
		
		//Go to the restore page
		Button restoreButton = createButton(
				(event) -> {
					setRestorePage("instructor");
				},
			"Restore System", 13, 158, Pos.CENTER, 180, 270);
		
		//Logout button
		Button logoutButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Logout", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, listUsersButton, articlesButton, backupButton, restoreButton, logoutButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method sets up the student page after logging in
	 */
	public static void setStudentPage() {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 170);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("HELLO, " + loginPreferredName, 30, 512, Pos.CENTER, 0, 0);
		
		Button searchButton = createButton(
				(event) -> {
					setSearchPage("student");
				},
			"Search articles", 20, 256, Pos.CENTER, 128, 128);
		
		Button questionButton = createButton(
				(event) -> {
					setQuestionPage();
				},
			"Ask a question", 15, 128, Pos.CENTER, 256 - 64, 320);
		
		Button findButton = createButton(
				(event) -> {
					setFindPage();
				},
			"Can't find something?", 15, 168, Pos.CENTER, 256 - 84, 350);
		
		Button logoutButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Logout", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, searchButton, questionButton, findButton, logoutButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method sets up the special question page
	 */
	public static void setFindPage() {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 170);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Ask a special question:", 30, 512, Pos.CENTER, 0, 0);
		
		TextArea textField = createTextArea("", 15, 500, true, 6, 128);
		
		Button askButton = createButton(
				(event) -> {
					//TODO Ask the question in the database
					setStudentPage();
				},
			"Ask", 15, 168, Pos.CENTER, 256 - 84, 350);
		
		Button backButton = createButton(
				(event) -> {
					setStudentPage();
				},
			"Back", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, textField, askButton, backButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method sets up the page for a student to ask a general question
	 */
	public static void setQuestionPage() {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 170);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Ask a question:", 30, 512, Pos.CENTER, 0, 0);
		
		TextArea textField = createTextArea("", 15, 500, true, 6, 128);
		
		Button askButton = createButton(
				(event) -> {
					//TODO Ask the question in the database
					setStudentPage();
				},
			"Ask", 15, 168, Pos.CENTER, 256 - 84, 350);
		
		Button backButton = createButton(
				(event) -> {
					setStudentPage();
				},
			"Back", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, textField, askButton, backButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}

	/**
	 * This method sets up the student article search page
	 */
	public static void setSearchPage(String type) {
		Pane root = new Pane();
		
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(root);
		
		//The big title of the page
		Label title = createLabel("Search", 30, 512, Pos.CENTER, 0, 0);
		
		CheckBox allBox = createCheckBox("All", 10, 128, Pos.CENTER, 256-64, 120);
		CheckBox beginnerBox = createCheckBox("Beginner", 10, 128, Pos.CENTER, 256-128, 70);
		beginnerBox.setSelected(true);
		beginnerBox.setOnAction((event) -> {
			if (allBox.isSelected() && !beginnerBox.isSelected()) {
				allBox.setSelected(false);
			}
		});
		CheckBox intermediateBox = createCheckBox("Intermediate", 10, 128, Pos.CENTER, 256, 70);
		intermediateBox.setSelected(true);
		intermediateBox.setOnAction((event) -> {
			if (allBox.isSelected() && !intermediateBox.isSelected()) {
				allBox.setSelected(false);
			}
		});
		CheckBox advancedBox = createCheckBox("Advanced", 10, 128, Pos.CENTER, 256-128, 90);
		advancedBox.setSelected(true);
		advancedBox.setOnAction((event) -> {
			if (allBox.isSelected() && !advancedBox.isSelected()) {
				allBox.setSelected(false);
			}
		});
		CheckBox expertBox = createCheckBox("Expert", 10, 128, Pos.CENTER, 256, 90);
		expertBox.setSelected(true);
		expertBox.setOnAction((event) -> {
			if (allBox.isSelected() && !expertBox.isSelected()) {
				allBox.setSelected(false);
			}
		});
		allBox.setSelected(true);
		allBox.setOnAction((event) -> {
			boolean set = allBox.isSelected();
			if (set) {
				beginnerBox.setSelected(set);
				intermediateBox.setSelected(set);
				advancedBox.setSelected(set);
				expertBox.setSelected(set);
			}
		});
		
		root.getChildren().addAll(beginnerBox, intermediateBox, advancedBox, expertBox, allBox);
		
		Label keywordLabel = createLabel("Keywords:", 15, 500, Pos.CENTER, 6, 150);
		TextField keywords = createTextField("", 15, 500, Pos.CENTER, 6, 180);
		
		ArrayList<String> groups = new ArrayList<String>();
		
		databaseHelper.forEachArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups.add(group);
		});
		databaseHelper.forEachSpecialArticle((id, titleArticle, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean test = false;
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups) {
				if (str == group) {
					test = true;
					break;
				}
			}
			if (!test)
				groups.add(group);
		}, currentEmail, type);
		
		ArrayList<CheckBox> groupBoxes = new ArrayList<CheckBox>();
		CheckBox allGroupBox = createCheckBox("All Groups", 15, 128, Pos.CENTER, 192 + 64, 240);
		allGroupBox.setSelected(true);
		root.getChildren().add(allGroupBox);
		
		for (int i = 0; i < groups.size(); i++) {
			CheckBox groupNameLabel = createCheckBox(groups.get(i), 15, 128, Pos.CENTER, 192 + 128 * (i%2), (i/2) * 30 + 270);
			groupNameLabel.setSelected(true);
			groupNameLabel.setOnAction((event) -> {
				if (!groupNameLabel.isSelected())
					allGroupBox.setSelected(false);
			});
			root.getChildren().add(groupNameLabel);
			groupBoxes.add(groupNameLabel);
		}
		allGroupBox.setOnAction((event) -> {
			if (allGroupBox.isSelected()) {
				for (CheckBox b : groupBoxes) {
					b.setSelected(true);
				}
			}
		});
		
		Button searchButton = createButton(
				(event) -> {
					ArrayList<String> groupSearched = new ArrayList<String>();
					for (CheckBox b : groupBoxes) {
						if (b.isSelected())
							groupSearched.add(b.getText());
					}
					
					for (int i = searchHistory.size() - 1; i >= 0; i--) {
						if (searchHistory.get(i).equals(keywords.getText()))
							searchHistory.remove(i);
					}
					searchHistory.add(0, keywords.getText());
					if (searchHistory.size() > 5)
						searchHistory.remove(searchHistory.size() - 1);
					setSearchingPage(keywords.getText(), groupSearched,
							beginnerBox.isSelected(), 
							intermediateBox.isSelected(),
							advancedBox.isSelected(),
							expertBox.isSelected(), type);
				},
				"Search", 20, 256, Pos.CENTER, 128, groups.size() / 2 * 30 + 270);
		
		Button backButton = createButton(
				(event) -> {
					if (type.equals("admin"))
						setAdminPage();
					else if (type.equals("instructor"))
						setInstructorPage();
					else setStudentPage();
				},
			"Back", 15, 64, Pos.CENTER, 438, 10);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, groups.size() / 2 * 30 + 310);
		errorMessage.setTextFill(Color.RED);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, keywordLabel, keywords, searchButton);
		int posX = 256 - 51 * searchHistory.size();
		for (String s : searchHistory) {
			Button newButton = createButton(
					null,
					s, 12, 100, Pos.CENTER, posX, 210);
			newButton.setOnAction((event) -> {
				keywords.setText(newButton.getText());
			});
			posX += 102;
			root.getChildren().add(newButton);
		}
		root.getChildren().addAll(backButton, errorMessage);
		
		Scene scene = new Scene(scroll, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This method sets up the student article search page
	 */
	public static void setSearchingPage(String keywords, ArrayList<String> groupSearched, boolean beginner, boolean intermediate, boolean advanced, boolean expert, String type) {
		Pane root = new Pane();
		
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(root);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 320);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label titleLabel = createLabel("Search Results:", 30, 512, Pos.CENTER, 0, 0);
		
		ArrayList<String> groups = new ArrayList<String>();
		int[] numDifficulty = new int[4];
		
		ArrayList<Article> articlesSearched = new ArrayList<Article>();
		
		numGroups = 0;
		databaseHelper.forEachArticle((id, title, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean found = false;
			for (String str : groupSearched) {
				if (str.equals(group)) {
					found = true;
					break;
				}
			}
			if (!found)
				return;
			
			found = false;
			if (!keywords.equals("")) {
				String[] realKeywords = splitKeywordSearch(keywords);
				for (String str : realKeywords) {
					if (title.contains(str) || author.contains(str) || abstrac.contains(str) || keywordsArticle.contains(str)) {
						found = true;
						break;
					}
				}
				if (!found)
					return;
			}
			
			boolean test = false;
			if (keywordsArticle.toLowerCase().contains("beginner")) {
				numDifficulty[0]++;
				if (!beginner)
					return;
			}
			if (keywordsArticle.toLowerCase().contains("intermediate")) {
				numDifficulty[1]++;
				if (!intermediate)
					return;
			}
			if (keywordsArticle.toLowerCase().contains("advanced")) {
				numDifficulty[2]++;
				if (!advanced)
					return;
			}
			if (keywordsArticle.toLowerCase().contains("expert")) {
				numDifficulty[3]++;
				if (!expert)
					return;
			}
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups) {
				if (str.equals(group)) {
					test = true;
					break;
				}
			}
			if (!test)
				groups.add(group);
			
			numGroups++;
			articlesSearched.add(new Article(id, title, group, author, abstrac, keywordsArticle, body, references));
		});
		databaseHelper.forEachSpecialArticle((id, title, group, author, abstrac, keywordsArticle, body, references, i) -> {
			boolean found = false;
			for (String str : groupSearched) {
				if (str.equals(group)) {
					found = true;
					break;
				}
			}
			if (!found)
				return;
			
			found = false;
			if (!keywords.equals("")) {
				String[] realKeywords = splitKeywordSearch(keywords);
				for (String str : realKeywords) {
					if (title.contains(str) || author.contains(str) || abstrac.contains(str) || keywordsArticle.contains(str)) {
						found = true;
						break;
					}
				}
				if (!found)
					return;
			}
			
			boolean test = false;
			if (keywordsArticle.toLowerCase().contains("beginner")) {
				numDifficulty[0]++;
				if (!beginner)
					return;
			}
			if (keywordsArticle.toLowerCase().contains("intermediate")) {
				numDifficulty[1]++;
				if (!intermediate)
					return;
			}
			if (keywordsArticle.toLowerCase().contains("advanced")) {
				numDifficulty[2]++;
				if (!advanced)
					return;
			}
			if (keywordsArticle.toLowerCase().contains("expert")) {
				numDifficulty[3]++;
				if (!expert)
					return;
			}
			
			//Add displayed groups to the list of groups at the top
			for (String str : groups) {
				if (str.equals(group)) {
					test = true;
					break;
				}
			}
			if (!test)
				groups.add(group);
			
			numGroups++;
			articlesSearched.add(new Article(id, title, group, author, abstrac, keywordsArticle, body, references, true));
		}, currentEmail, type);
		
		Label allGroups = createLabel("Groups:", 20, 500, Pos.CENTER, 6, 70);
		root.getChildren().add(allGroups);
		
		for (int i = 0; i < groups.size(); i++) {
			Label groupName = createLabel(groups.get(i), 15, 256, Pos.CENTER, 256 - 192 + (i % 2) * 128, (i/2) * 30 + 100);
			root.getChildren().add(groupName);
		}
		int currentHeight = (groups.size()/2) * 30 + 100 + 30;
		
		Label beginnerArticles = createLabel("Beginner Articles: " + numDifficulty[0], 15, 128, Pos.CENTER, 120, currentHeight);
		Label intermediateArticles = createLabel("Intermediate Articles: " + numDifficulty[1], 15, 128, Pos.CENTER, 264, currentHeight);
		currentHeight += 30;
		Label advancedArticles = createLabel("Advanced Articles: " + numDifficulty[2], 15, 128, Pos.CENTER, 120, currentHeight);
		Label expertArticles = createLabel("Expert Articles: " + numDifficulty[3], 15, 128, Pos.CENTER, 264, currentHeight);
		
		if (beginner)
			root.getChildren().add(beginnerArticles);
		if (intermediate)
			root.getChildren().add(intermediateArticles);
		if (advanced)
			root.getChildren().add(advancedArticles);
		if (expert)
			root.getChildren().add(expertArticles);
		
		currentHeight += 45;
		int articleNum = 1;
		for (Article a : articlesSearched) {
			Label l = createLabel(articleNum + ",\t" + a.title + ",\t" + a.authors + ":\n" + a.abstrac, 15, 500, Pos.BASELINE_LEFT, 6, currentHeight);
			Button b = createButton((event) -> {
				setArticleViewPage(type, a.id, a.special);
			}, "View", 15, 128, Pos.CENTER, 300, currentHeight);
			articleNum++;
			currentHeight += 60;
			
			root.getChildren().addAll(l, b);
		}
		
		Button backButton = createButton(
				(event) -> {
					setSearchPage(type);
				},
			"Back", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(titleLabel, backButton, errorMessage);
		
		Scene scene = new Scene(scroll, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	private static String[] splitKeywordSearch(String keywords) {
		return keywords.split(" +");
	}
	
	/**
	 * This method sets up the account creation page
	 */
	public static void setCreateNewAccountPage() {
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		boolean testEmpty = true;//This runs differently if the database is empty or not
		try {
			testEmpty = databaseHelper.isDatabaseEmpty();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		final boolean emptyDatabase = testEmpty;
		
		Pane root = new Pane();
		
		Label title = createLabel("Create Account", 50, 512, Pos.CENTER, 0, 0);
		
		Label codeTitle = createLabel("Invite Code:", 15, 512, Pos.CENTER, 0, 120);
		TextField codeInput = createTextField("", 15, 256, Pos.CENTER, 128, 140);
		
		Label emailTitle = createLabel("Email:", 15, 512, Pos.CENTER, 0, 70);
		TextField emailInput = createTextField("", 15, 256, Pos.CENTER, 128, 90);
		
		Label passwordTitle = createLabel("Password:", 15, 512, Pos.CENTER, 0, 120);
		TextField passwordInput = createPasswordField(15, 256, Pos.CENTER, 128, 140);
		
		Label confirmPasswordTitle = createLabel("Confirm Password:", 15, 512, Pos.CENTER, 0, 170);
		TextField confirmPasswordInput = createPasswordField(15, 256, Pos.CENTER, 128, 190);
		
		Label tempPasswordTitle = createLabel("Temporary Password:", 15, 512, Pos.CENTER, 0, 180);
		TextField tempPassword = createText("", 15, 128, Pos.CENTER, 256-64-32, 200);
		
		Button createButton = createButton(
				(event) -> {
					if (passwordInput.getText().equals(confirmPasswordInput.getText())) {
						if (emptyDatabase) {
							//If the database is empty, create an admin account
							createFirstAdmin(emailInput.getText(), passwordInput.getText());
							currentEmail = emailInput.getText();
						} else {
							//If the database is not empty, try to create a normal account
							String inviteCode = codeInput.getText();
							boolean validCode = databaseHelper.validateInviteCode(inviteCode);
							String tempPass = Password.generateRandomString(12);
							//Make sure the invite code is valid, otherwise throw an error
							if (validCode) {
								createAccount(emailInput.getText(), tempPass, databaseHelper.getInviteCodeRoles(inviteCode));
								tempPassword.setText(tempPass);
								currentEmail = emailInput.getText();
							} else
								failCreateAccount(errorMessage, "Invite code is INVALID!");
						}
					} else {
						//DO NOT create an account if the passwords don't match
						failCreateAccount(errorMessage, "Passwords do NOT match!");
					}
				},
			"Continue", 15, 64, Pos.CENTER, 218, 240);
		
		Button backButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Back to Login", 13, 128, Pos.CENTER, 194, 290);
		
		root.getChildren().addAll(title, emailTitle, emailInput);
		//Add elements only if the database is not empty vs empty
		if (!emptyDatabase)
			root.getChildren().addAll(codeTitle, codeInput, tempPasswordTitle, tempPassword, backButton);
		else
			root.getChildren().addAll(passwordTitle,
					passwordInput, confirmPasswordTitle, confirmPasswordInput);
		//Add items regardless of database
		root.getChildren().addAll(createButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This function creates an admin and moves over to setup the rest of the account
	 * @param email
	 * @param password
	 */
	private static void createFirstAdmin(String email, String password) {
		databaseHelper.register(email, password, new String[]{"admin"});
		databaseHelper.setCompleteSetup(email);
		setSetupAccountPage();
	}
	
	/**
	 * This function creates a new account in the database
	 * @param email
	 * @param password
	 * @param roles
	 */
	private static void createAccount(String email, String password, String[] roles) {
		databaseHelper.register(email, password, roles);
	}
	
	/**
	 * Creates an error
	 * @param errorText
	 * @param message
	 */
	private static void failCreateAccount(Label errorText, String message) {
		errorText.setText(message);
	}
	
	/**
	 * Create the account setup page
	 */
	public static void setSetupAccountPage() {
		Pane root = new Pane();
		
		Label title = createLabel("Finish Account Creation", 40, 512, Pos.CENTER, 0, 0);
		
		Label firstTitle = createLabel("First Name:", 15, 128, Pos.CENTER, 128, 70);
		TextField firstInput = createTextField("", 15, 128, Pos.CENTER, 256, 70);
		
		Label middleTitle = createLabel("Middle Name:", 15, 128, Pos.CENTER, 128, 100);
		TextField middleInput = createTextField("", 15, 128, Pos.CENTER, 256, 100);
		
		Label lastTitle = createLabel("Last Name:", 15, 128, Pos.CENTER, 128, 130);
		TextField lastInput = createTextField("", 15, 128, Pos.CENTER, 256, 130);
		
		Label preferredTitle = createLabel("Preferred Name:", 15, 128, Pos.CENTER, 128, 160);
		TextField preferredInput = createTextField("", 15, 128, Pos.CENTER, 256, 160);
		
		Label passwordTitle = createLabel("Password:", 15, 128, Pos.CENTER, 128, 190);
		TextField passwordInput = createPasswordField(15, 128, Pos.CENTER, 256, 190);
		
		Label password2Title = createLabel("Confirm Password:", 15, 128, Pos.CENTER, 128, 220);
		TextField password2Input = createPasswordField(15, 128, Pos.CENTER, 256, 220);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 250);
		errorMessage.setTextFill(Color.RED);
		
		Button continueButton = createButton(
				(event) -> {
					//When setting up an account, check to see if the password needs to be reset
					if (databaseHelper.shouldUserReset(currentEmail)) {
						if (passwordInput.getText().equals(password2Input.getText())) {
							databaseHelper.setPassword(currentEmail, passwordInput.getText());
						} else {
							failCreateAccount(errorMessage, "Passwords do NOT match!");
							return;
						}
					}
					setupInformation(firstInput.getText(), middleInput.getText(), lastInput.getText(), preferredInput.getText());
					databaseHelper.setCompleteSetup(currentEmail);
				},
			"Continue", 15, 64, Pos.CENTER, 218, 270);
		
		root.getChildren().addAll(title, firstTitle, firstInput, middleTitle,
				middleInput, lastTitle, lastInput, preferredTitle, preferredInput,
				continueButton, errorMessage);
		
		//If the password needs to be reset, add the password input fields
		if (databaseHelper.shouldUserReset(currentEmail) || databaseHelper.checkFinish(currentEmail))
			root.getChildren().addAll(passwordTitle, passwordInput, password2Title, password2Input);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * This function adds the extra information of an account to the database
	 * @param first
	 * @param middle
	 * @param last
	 * @param preferred
	 */
	private static void setupInformation(String first, String middle, String last, String preferred) {
		databaseHelper.finishRegistration(currentEmail, first, middle, last, preferred);
		setLoginPage();
	}
	
	/**
	 * Creates a text label for easy graphical design
	 * @param text
	 * @param fontSize
	 * @param width
	 * @param position
	 * @param x
	 * @param y
	 * @return
	 */
	private static Label createLabel(String text, double fontSize, double width, Pos position, double x, double y){
		Label l = new Label(text);
		l.setFont(Font.font(FONT_NAME, fontSize));
		l.setMinWidth(width);
		l.setAlignment(position);
		l.setLayoutX(x);
		l.setLayoutY(y);
		
		return l;
	}
	
	/**
	 * Creates a text field for easy graphical design
	 * @param defaultText
	 * @param fontSize
	 * @param width
	 * @param position
	 * @param x
	 * @param y
	 * @return
	 */
	private static TextField createText(String defaultText, double fontSize, double width, Pos position, double x, double y){
		TextField t = new TextField(defaultText);
		t.setFont(Font.font(FONT_NAME, fontSize));
		t.setMinWidth(width);
		t.setAlignment(position);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(false);
		
		return t;
	}
	
	/**
	 * Creates an editable text field for easy graphical design
	 * @param defaultText
	 * @param fontSize
	 * @param width
	 * @param position
	 * @param x
	 * @param y
	 * @return
	 */
	private static TextField createTextField(String defaultText, double fontSize, double width, Pos position, double x, double y){
		TextField t = new TextField(defaultText);
		t.setFont(Font.font(FONT_NAME, fontSize));
		t.setMinWidth(width);
		t.setAlignment(position);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(true);
		
		return t;
	}
	
	/**
	 * Creates an editable text field for easy graphical design
	 * @param defaultText
	 * @param fontSize
	 * @param width
	 * @param position
	 * @param x
	 * @param y
	 * @return
	 */
	private static TextArea createTextArea(String defaultText, double fontSize, double width, boolean wrap, double x, double y){
		TextArea t = new TextArea(defaultText);
		t.setFont(Font.font(FONT_NAME, fontSize));
		t.setMaxWidth(width);
		t.setWrapText(wrap);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(true);
		
		return t;
	}
	
	/**
	 * Creates an editable password field for easy graphical design
	 * @param defaultText
	 * @param fontSize
	 * @param width
	 * @param position
	 * @param x
	 * @param y
	 * @return
	 */
	private static TextField createPasswordField(double fontSize, double width, Pos position, double x, double y){
		TextField t = new PasswordField();
		t.setFont(Font.font(FONT_NAME, fontSize));
		t.setMinWidth(width);
		t.setAlignment(position);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(true);
		
		return t;
	}
	
	/**
	 * Creates a button
	 * @param defaultText
	 * @param fontSize
	 * @param width
	 * @param position
	 * @param x
	 * @param y
	 * @return
	 */
	private static Button createButton(EventHandler<ActionEvent> event, String text, double fontSize, double width, Pos position, double x, double y){
		Button b = new Button();
		b.setText(text);
		b.setOnAction(event);
		b.setFont(Font.font(FONT_NAME, fontSize));
		b.setMinWidth(width);
		b.setAlignment(position);
		b.setLayoutX(x);
		b.setLayoutY(y);
		
		return b;
	}
	
	/**
	 * Creates a text field for easy graphical design
	 * @param defaultText
	 * @param fontSize
	 * @param width
	 * @param position
	 * @param x
	 * @param y
	 * @return
	 */
	private static CheckBox createCheckBox(String defaultText, double fontSize, double width, Pos position, double x, double y){
		CheckBox t = new CheckBox(defaultText);
		t.setSelected(false);
		t.setFont(Font.font(FONT_NAME, fontSize));
		t.setMinWidth(width);
		t.setAlignment(position);
		t.setLayoutX(x);
		t.setLayoutY(y);
		
		return t;
	}
}
