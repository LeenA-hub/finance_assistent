package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class hello extends Application {

    @Override
    public void start(Stage stage) {
        Button btn = new Button("Say Hello");
        btn.setOnAction(e -> System.out.println("Hello, JavaFX!"));

        StackPane root = new StackPane(btn);
        Scene scene = new Scene(root, 300, 200);

        stage.setTitle("HelloFX Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
