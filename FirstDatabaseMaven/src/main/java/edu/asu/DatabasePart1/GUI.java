package edu.asu.DatabasePart1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GUI extends Application {

	public static void main(String[] args) {
		launch();
	}
	@Override
	public void start(Stage theStage) throws Exception {
theStage.setTitle("Lynn Robert Carter");			// Label the stage (a window)
		
		Pane theRoot = new Pane();
		
		Scene theScene = new Scene(theRoot, 500, 500);	// Create the scene
		
		theStage.setScene(theScene);						// Set the scene on the stage
		
		theStage.show();
	}
	
	
}
