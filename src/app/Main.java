package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Database;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Database.createTables(); // Create DB tables if not exist
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        stage.setTitle("Personal Finance Manager");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Optional: add CSS
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("Personal Finance Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
