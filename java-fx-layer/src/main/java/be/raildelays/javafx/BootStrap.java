package be.raildelays.javafx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Jasper Potts
 * This file is taken from an example project on the fxexperience.com website.
 * http://fxexperience.com/2011/05/maps-in-javafx-2-0/
 *
 * I slightly modified it to compile it in JavaFX 2.0.2 for a French article on JavaFx and Maven
 * on my blog http://notaboekje.blogspot.com
 */
public class BootStrap extends Application {
 
	public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}
