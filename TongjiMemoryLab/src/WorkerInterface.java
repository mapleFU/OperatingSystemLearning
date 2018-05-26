import Memory.EvictAlgorithm.EvictAlgoFactory;
import Memory.EvictAlgorithm.EvictBase;
import Memory.PhysicsMemory;
import Memory.Worker;
import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;


/**
 * 对于Worker运行的
 */
@Deprecated
public class WorkerInterface extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static Label generateLabel(EvictBase evictAlgo) {
        return new Label(evictAlgo.getEvictAlgoName());
    }

    private static List<String> nameList;

    static {
        // 静态猝死化NAMELIST
        nameList = new LinkedList<>();
        nameList.add("FIFO");
        nameList.add("LRU");
    }

    static JFXListView<String> generateEvictList() {

        JFXListView<String> list = new JFXListView<>();
        for (String s:
             nameList) {
            list.getItems().add(s);
        }

        list.getStyleClass().add("mylistview");
        return list;
    }

    static JFXComboBox<String> generateEvictCombobox() {
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        for (String s:
                nameList) {
            comboBox.getItems().add(s);
        }
        return comboBox;
    }


    @Override
    public void start(Stage primaryStage) {
        ComboBox<String> comboBox1 = generateEvictCombobox();
        comboBox1.setValue("FIFO");
        ComboBox<String> comboBox2 = generateEvictCombobox();
        comboBox2.setValue("LRU");

        EvictAlgoFactory evictAlgoFactory = new EvictAlgoFactory();
        JFXButton jfoenixButton = new JFXButton("启动算法");

        jfoenixButton.setOnMouseClicked((event -> {
            // validate
            String s1 = comboBox1.getValue();
            String s2 = comboBox2.getValue();
            comboBox1.setValue(null);
            comboBox2.setValue(null);
            if (s1 == null || s2 == null) {
                return;
            }
            Pair<Worker, PhysicsMemory> wp_pair1 = main.generateWorker(evictAlgoFactory.createEvict(s1));
            Pair<Worker, PhysicsMemory> wp_pair2 = main.generateWorker(evictAlgoFactory.createEvict(s2));
            primaryStage.setScene(new Scene(new RunningScene(primaryStage, wp_pair1, wp_pair2)));
            primaryStage.show();
        }));

        GridPane gridPane = new GridPane();
        gridPane.add(comboBox1, 0, 1);
        gridPane.add(comboBox2, 0, 2);
        gridPane.add(jfoenixButton, 0, 3);

        Scene scene = new Scene(gridPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
