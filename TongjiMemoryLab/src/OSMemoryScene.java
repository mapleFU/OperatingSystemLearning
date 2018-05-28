import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OSMemoryScene extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("views/OSMemoryScene.fxml"));
            primaryStage.setTitle("Hello World");
            Scene scene = new Scene(root, 300, 275);
            scene.getStylesheets().add(getClass().getResource("css/OSMemoryMain.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
