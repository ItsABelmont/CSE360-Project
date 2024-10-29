package edu.asu.DatabasePart1;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * This class contains the definition for the graphical interface
 * 
 * @author Just Wise
 *
 */
public class GUI extends Application {
	
	private static Stage appStage;
	private static DatabaseHelper databaseHelper;
	
	public static String loginPreferredName;
	public static String currentEmail;
	
	public static final double WINDOW_WIDTH = 512;
	public static final double WINDOW_HEIGHT = 384;
	public static final String FONT_NAME = "Arial";
	
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
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
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
					setListUsersPage();
				},
			"List Users", 13, 158, Pos.CENTER, 180, 120);
		
		//Go to the article page
		Button articlesButton = createButton(
				(event) -> {
					setArticleAdminPage();
				},
			"Articles", 13, 158, Pos.CENTER, 180, 170);
		
		//Logout button
		Button logoutButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Logout", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, generateCodeButton, listUsersButton, articlesButton, logoutButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Creates the page for admins to interact with the article database
	 */
	public static void setArticleAdminPage() {
		Pane root = new Pane();
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 270);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("Article System", 30, 512, Pos.CENTER, 0, 0);
		
		//Create a new article
		Button createButton = createButton(
				(event) -> {
					//setGenerateInviteCodePage();
				},
			"Create Article", 13, 158, Pos.CENTER, 180, 70);
		
		//Update an article's info
		Button updateButton = createButton(
				(event) -> {
					
				},
			"Update Article", 13, 158, Pos.CENTER, 180, 110);
		
		//View an article
		Button viewButton = createButton(
				(event) -> {
					//setArticleAdminPage();
				},
			"View Article", 13, 158, Pos.CENTER, 180, 150);
		
		//Delete an article
		Button deleteButton = createButton(
				(event) -> {
					//setArticleAdminPage();
				},
			"Delete Article", 13, 158, Pos.CENTER, 180, 190);
		
		//Logout button
		Button backButton = createButton(
				(event) -> {
					setAdminPage();
				},
			"Back", 15, 64, Pos.CENTER, 428, 40);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, createButton, updateButton, viewButton, deleteButton, backButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	/**
	 * Steup the page where you create invite codes as an admin
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
	public static void setListUsersPage() {
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
		databaseHelper.forEachUser((id, email, roles, first, middle, last, preferred) -> {
			Label user = createLabel(preferred + " (" + first + " " + middle + " " + last + ")\n" + email, 13, 256, Pos.TOP_LEFT, 30, id * 30 + 50);
			Button userButton = createButton(//Edit button
					(event) -> {
						setEditUserPage(email, preferred);
					},
				"Edit", 15, 64, Pos.CENTER, 338, id * 30 + 50);
			if (!email.equals(currentEmail)) {//If the email is not the logged in user, it can be deleted
				Button removeUserButton = createButton(
						(event) -> {},
					"Remove user", 15, 64, Pos.CENTER, 438, id * 30 + 50);
				removeUserButton.setOnAction((event) -> {
						promptDelete(promptPane, deleteButton, user, userButton, removeUserButton, databaseHelper.deleteUser(email));
					});
				root.getChildren().add(removeUserButton);
			}
			root.getChildren().addAll(user, userButton);
		});
		
		//Go back to the admin page with a button press
		Button backButton = createButton(
				(event) -> {
					setAdminPage();
				},
			"Back", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, backButton, errorMessage, promptPane);
		
		Scene scene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
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
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 0);
		errorMessage.setTextFill(Color.RED);
		
		//The big title of the page
		Label title = createLabel("HELLO, " + loginPreferredName, 30, 512, Pos.CENTER, 0, 0);
		
		//Go to the article page
		Button articlesButton = createButton(
				(event) -> {
					//setArticleInstructorPage();
				},
			"Articles", 13, 158, Pos.CENTER, 180, 70);
		
		Button logoutButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Logout", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, logoutButton, articlesButton, errorMessage);
		
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
		
		Button logoutButton = createButton(
				(event) -> {
					setLoginPage();
				},
			"Logout", 15, 64, Pos.CENTER, 438, 10);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, logoutButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
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
					if (passwordInput.getText().equals(confirmPasswordInput.getText()) && emailInput.getText().length() > 0) {
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
		
		//Add elements only if the database is not empty vs empty
		if (!emptyDatabase)
			root.getChildren().addAll(codeTitle, codeInput, tempPasswordTitle, tempPassword, backButton);
		else
			root.getChildren().addAll(passwordTitle,
					passwordInput, confirmPasswordTitle, confirmPasswordInput);
		//Add items regardless of database
		root.getChildren().addAll(title, emailTitle, emailInput,
				createButton, errorMessage);
		
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
		TextField passwordInput = createTextField("", 15, 128, Pos.CENTER, 256, 190);
		
		Label password2Title = createLabel("Confirm Password:", 15, 128, Pos.CENTER, 128, 220);
		TextField password2Input = createTextField("", 15, 128, Pos.CENTER, 256, 220);
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 250);
		errorMessage.setTextFill(Color.RED);
		
		Button continueButton = createButton(
				(event) -> {
					//When setting up an account, check to see if the password needs to be reset
					if (databaseHelper.shouldUserReset(currentEmail)) {
						if (passwordInput.getText().equals(password2Input.getText()))
							databaseHelper.setPassword(currentEmail, passwordInput.getText());
						else
							failCreateAccount(errorMessage, "Passwords do NOT match!");
							return;
					}
					setupInformation(firstInput.getText(), middleInput.getText(), lastInput.getText(), preferredInput.getText());
				},
			"Continue", 15, 64, Pos.CENTER, 218, 270);
		
		root.getChildren().addAll(title, firstTitle, firstInput, middleTitle,
				middleInput, lastTitle, lastInput, preferredTitle, preferredInput,
				continueButton, errorMessage);
		
		//If the password needs to be reset, add the password input fields
		if (databaseHelper.shouldUserReset(currentEmail))
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
