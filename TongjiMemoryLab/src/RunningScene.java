/**
 * https://stackoverflow.com/questions/42566161/javafx-and-the-observer-pattern-updating-a-ui
 */

import Generator.RandomCodeGenerator;
import Memory.PhysicsMemory;
import Memory.Worker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class RunningScene extends GridPane {
    private final Stage primaryStage;
    // 决定ctx的两个List
    private List<Worker> workerList;
    private ArrayList<PhysicsMemory> pmList;
    private Label[] frameLabels;

    /**
     * 各个算法的frameList
     * is a ctx
     */
    private ArrayList<Button[]> frameButtonsLists;
    private Button[] frameButtons;
    private final int FRAME_NUM = 4;
    private RandomCodeGenerator rcg;
    private ListView<String> codeExecutedView;


    /**
     * 根据rcg执行一条指令
     */
    private boolean executeOneCode() {
        if (rcg.hasNext()) {
            Iterator<Worker> workerIterator = workerList.iterator();
            Iterator<PhysicsMemory> physicsMemoryIterator = pmList.iterator();
            Iterator<Button[]> b_iter = frameButtonsLists.iterator();
            int code = rcg.next();
            int cnt = 0;
            // TODO: 改成针对数组的index.
            while (workerIterator.hasNext()) {
                workerIterator.next().executeCode(code);

                // TODO: add something here but for the only P.F.
                PhysicsMemory pm = physicsMemoryIterator.next();
                int[] changedArr = pm.getChangedPageNumbersInPhysicsMemory();
                if (changedArr != null) {
                    for (int i = 0; i < changedArr.length; i++) {
                        if (changedArr[i] != -1) {
                            // 在对应的地方添加
                            Button[] buttons = frameButtonsLists.get(cnt);
                            if (buttons[i] == null) {
                                System.err.println("Fuck you!");
                            }
                            buttons[i].setText("Frame" + changedArr[i]);
                        }
                    }
                }
                ++cnt;
            }
            codeExecutedView.getItems().add(Integer.toString(code));
            return true;
        }
        return false;
    }

    public RunningScene(Stage primaryStage, Pair<Worker, PhysicsMemory>... workers) {
        frameButtonsLists = new ArrayList<>();
        this.primaryStage = primaryStage;
        // init first
        rcg = new RandomCodeGenerator(320);

        // init worker list
        workerList = new ArrayList<>();
        pmList = new ArrayList<>();
        for (Pair<Worker, PhysicsMemory> w:
             workers) {
            workerList.add(w.getKey());
            // 生成对应的链接
            pmList.add(w.getValue());
        }

        this.setVgap(4);

        // 加入四个BUTTON

        // frame 字段
        frameLabels = new Label[FRAME_NUM];
        for (int i = 0; i < workers.length; i++) {
            Button[] buttons = new Button[FRAME_NUM];
            for (int j = 0; j < FRAME_NUM; j++) {
                buttons[j] = new JFXButton("None");
            }
            frameButtonsLists.add(buttons);
        }
        // 上下文frameButtons
        frameButtons = frameButtonsLists.get(0);

        for (int i = 0; i < FRAME_NUM; i++) {
            frameLabels[i] = new Label("Frame(" + (int)(i + 1) + ")");
            this.add(frameLabels[i], i, 0);
//            frameButtons[i] = new JFXButton("None");
            this.add(frameButtons[i], i, 1);
        }

        // set select
        Button excuteButton = new JFXButton("Execute");
        Button excuteAllButton = new JFXButton("ExecuteAll");
        TextField textField = new JFXTextField();
        final String TEXT_DEFAULT = "1";
        textField.setText(TEXT_DEFAULT);

        this.add(excuteAllButton, 0, 2);
        this.add(excuteButton, 1, 2);
        this.add(textField, 2, 2);

        // set two list views.
        codeExecutedView = new JFXListView<>();
        this.add(codeExecutedView, 0, 3);

        // add events
        excuteAllButton.setOnMouseClicked((event -> {
            while (executeOneCode()) ;
        }));

        excuteButton.setOnMouseClicked((event -> {
            if (!rcg.hasNext()) {
                return;
            }
            String text = textField.getText();
            textField.setText(TEXT_DEFAULT);
            int i;
            if (text.equals(TEXT_DEFAULT)) {
                i = 1;
            } else {
                i = Integer.parseInt(text);
            }
            // DEBUG TEXT
            System.out.println(text);

            for (int j = 0; j < i; j++) {

                if (!executeOneCode()) {
                    break;
                }
            }

        }));

    }
}
