package edu.asu.DatabasePart1;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
		
		Button loginButton = createButton(
				(event) -> {
//					boolean loggedIn = databaseHelper.login(emailInput.getText(), passwordInput.getText());
//					if (loggedIn) {
//						setLoggingInPage();
//					} else {
//						failCreateAccount(errorMessage, "Invalid email or password");
//					}
				},
			"Continue", 15, 64, Pos.CENTER, 218, 190);
		
		//Add all of the elements to the page
		root.getChildren().addAll(title, emailTitle, emailInput, passwordTitle, passwordInput,
				loginButton, errorMessage);
		
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
		
		boolean testEmpty = true;
		try {
			testEmpty = databaseHelper.isDatabaseEmpty();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		final boolean emptyDatabase = testEmpty;
		
		Pane root = new Pane();
		
		Label title = createLabel("Create Account", 50, 512, Pos.CENTER, 0, 0);
		
		Label codeTitle = createLabel("Invite Code:", 15, 512, Pos.CENTER, 0, 70);
		TextField codeInput = createTextField("", 15, 256, Pos.CENTER, 128, 90);
		
		Label emailTitle = createLabel("Email:", 15, 512, Pos.CENTER, 0, 120);
		TextField emailInput = createTextField("", 15, 256, Pos.CENTER, 128, 140);
		
		Label passwordTitle = createLabel("Password:", 15, 512, Pos.CENTER, 0, 170);
		TextField passwordInput = createPasswordField(15, 256, Pos.CENTER, 128, 190);
		
		Label confirmPasswordTitle = createLabel("Confirm Password:", 15, 512, Pos.CENTER, 0, 220);
		TextField confirmPasswordInput = createPasswordField(15, 256, Pos.CENTER, 128, 240);
		
		Button createButton = createButton(
				(event) -> {
					if (passwordInput.getText().equals(confirmPasswordInput.getText())) {
						if (emptyDatabase) {
							createFirstAdmin(emailInput.getText(), passwordInput.getText());
						} else {
							String inviteCode = codeInput.getText();
							boolean validCode = databaseHelper.validateInviteCode(inviteCode);
							if (validCode)
								createAccount(emailInput.getText(), passwordInput.getText(), databaseHelper.getInviteCodeRole(inviteCode));
							else
								failCreateAccount(errorMessage, "Invite code is INVALID!");
						}
					} else {
						failCreateAccount(errorMessage, "Passwords do NOT match!");
					}
				},
			"Continue", 15, 64, Pos.CENTER, 218, 290);
		
		if (!emptyDatabase)
			root.getChildren().addAll(codeTitle, codeInput);
		root.getChildren().addAll(title, emailTitle, emailInput, passwordTitle,
				passwordInput, confirmPasswordTitle, confirmPasswordInput,
				createButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	private static void createFirstAdmin(String email, String password) {
		databaseHelper.register(email, password, "admin");
		setSetupAccountPage();
	}
	
	private static void createAccount(String email, String password, String role) {
		databaseHelper.register(email, password, role);
		setSetupAccountPage();
	}
	
	private static void failCreateAccount(Label errorText, String message) {
		errorText.setText(message);
	}
	
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
		
		Label errorMessage = createLabel("", 15, 512, Pos.CENTER, 0, 220);
		errorMessage.setTextFill(Color.RED);
		
		Button continueButton = createButton(
				(event) -> {
					setupInformation(firstInput.getText(), middleInput.getText(), lastInput.getText(), preferredInput.getText());
				},
			"Continue", 15, 64, Pos.CENTER, 218, 240);
		
		root.getChildren().addAll(title, firstTitle, firstInput, middleTitle,
				middleInput, lastTitle, lastInput, preferredTitle, preferredInput,
				continueButton, errorMessage);
		
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		appStage.setScene(scene);
		
		appStage.show();
	}
	
	private static void setupInformation(String first, String middle, String last, String preferred) {
		//databaseHelper.AAAA();
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
}
