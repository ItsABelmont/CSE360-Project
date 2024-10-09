package edu.asu.DatabasePart1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GUI extends Application {

	public static void start(String[] args) {
		launch();
	}
	@Override
	public void start(Stage theStage) throws Exception {
		theStage.setTitle("CSE360 Application");
		
		Pane root = new Pane();
		
		Label text = new Label("sample text");
		text.setAlignment(Pos.CENTER);
		root.getChildren().add(text);
		
		Scene scene = new Scene(root, 500, 500);
		
		theStage.setScene(scene);
		
		theStage.show();
	}
	
	
}
