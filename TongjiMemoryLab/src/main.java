import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Parent root;
        try {

            root = FXMLLoader.load(getClass().getResource("OSMemoryScene.fxml"));



        } catch (IOException e) {

            try {
                // 在 jar 中这样加载资源
                // https://stackoverflow.com/questions/19602727/how-to-reference-javafx-fxml-files-in-resource-folder
                // https://stackoverflow.com/questions/26675048/classloader-getresource-doesnt-work-in-jar-file
                InputStream stream = getClass().getClassLoader().getResourceAsStream("OSMemoryScene.fxml");
                FXMLLoader loader = new FXMLLoader();
                loader.load(stream);
                root = loader.getRoot();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
        primaryStage.setTitle("内存模拟测试");
        Scene scene = new Scene(root, 300, 700);
//            scene.getStylesheets().add(getClass().getResource("css/OSMemoryMain.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
