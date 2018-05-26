import Generator.RandomCodeGenerator;
import Memory.EvictAlgorithm.EvictBase;
import Memory.EvictAlgorithm.FIFOEvict;
import Memory.EvictAlgorithm.LRUEvict;
import Memory.PhysicsMemory;
import Memory.Worker;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Pair;
import models.PhysicMemoryBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class OSMainController implements Initializable{
    // it should be public
    public static class ShownCode {
        public int getCodenum() {
            return codenum.get();
        }

        public IntegerProperty codenumProperty() {
            return codenum;
        }

        public void setCodenum(int codenum) {
            this.codenum.set(codenum);
        }

        private final IntegerProperty codenum;

        public int getPhysicMemory() {
            return physicMemory.get();
        }

        public IntegerProperty physicMemoryProperty() {
            return physicMemory;
        }

        public void setPhysicMemory(int physicMemory) {
            this.physicMemory.set(physicMemory);
        }

        private final IntegerProperty physicMemory;

        private ShownCode(int coden) {
            codenum = new SimpleIntegerProperty(coden);
            physicMemory = new SimpleIntegerProperty(coden / 10);
            codenum.addListener(((observable, oldValue, newValue) -> {
                physicMemory.setValue(newValue.intValue() / 10);
            }));
        }
    }

    @FXML
    private ToggleGroup radioGroup;

    @FXML
    private Toggle toggle1;

    @FXML
    private Toggle toggle2;

    @FXML
    private Button executeNext;

    @FXML
    private Button excuteAll;

    /**
     * 唯一的表示指令--PFN映射的表
     */
    @FXML
    private TableView<ShownCode> tableView;

    /**
     * 展示的FRAME序号
     */
    @FXML
    private ArrayList<Button> shownFrames;


    /**
     * 各个算法的frameList
     * 以及对应的上下文
     */
    private ArrayList<PhysicMemoryBean> physicMemoryBeans;
    private ArrayList<Worker> workers;
    private ArrayList<StringProperty[]> frameStringProperties;

    private StringProperty[] ctxStringProperty;


    private final int FRAME_NUM = 4;
    private RandomCodeGenerator rcg;


    private void initializeInnerGlobal() {
        rcg = new RandomCodeGenerator(320);
//        ctxStringProperty = new StringProperty[FRAME_NUM];
        physicMemoryBeans = new ArrayList<>();
        workers = new ArrayList<>();
        frameStringProperties = new ArrayList<>();
    }

    @FXML
    private void executeCode() {
        // 凡是执行都要修改对应的TBV
        if (rcg.hasNext()) {
            Iterator<Worker> workerIterator = workers.iterator();
            int code = rcg.next();
            while (workerIterator.hasNext()) {
                workerIterator.next().executeCode(code);
            }
            // 添加对应的shown code.
            tableView.getItems().add(new ShownCode(code));
        }
    }

    /**
     * initialize 一般用于bind
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeInnerGlobal();
        EvictBase[] evictBases = new EvictBase[2];
        evictBases[0] = new LRUEvict(FRAME_NUM);
        evictBases[1] = new FIFOEvict(FRAME_NUM);

        for (EvictBase evb:
             evictBases) {
            Pair<Worker, PhysicsMemory> wp_pair = main.generateWorker(evb);
            workers.add(wp_pair.getKey());
            PhysicMemoryBean pmb = new PhysicMemoryBean(wp_pair.getValue());
            physicMemoryBeans.add(pmb);
            StringProperty[] strings = new StringProperty[FRAME_NUM];
//            Button[] buttons = new Button[FRAME_NUM];
            for (int i = 0; i < FRAME_NUM; i++) {
                strings[i] = new SimpleStringProperty("None");
//                buttons[i] = new Button();
//                buttons[i].setText("None");
            }
            // set interact with pmb
            for (int i = 0; i < FRAME_NUM; i++) {
                // 循环处理
                final int index = i;
                pmb.getFrameProperty(i).addListener(((observable, oldValue, newValue) -> {
                    // i is index
                    if (oldValue.equals(newValue)) {
                        return;
                    } else {
                        // 不安全
//                        buttons[i].setText(newValue.toString());
                        strings[index].setValue(newValue.toString());
                    }
                }));
            }
            frameStringProperties.add(strings);

        }


        //binding
        // 最初的表现层, 绑定在序号0
        ctxStringProperty = frameStringProperties.get(0);

        for (int i = 0; i < FRAME_NUM; i++) {
            shownFrames.get(i).textProperty().bindBidirectional(ctxStringProperty[i]);
        }

        radioGroup.selectToggle(toggle1);
        // table view
        // https://docs.oracle.com/javafx/2/fxml_get_started/fxml_tutorial_intermediate.htm
        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (radioGroup.getSelectedToggle() != null) {
                for (int i = 0; i < FRAME_NUM; i++) {
                    shownFrames.get(i).textProperty().unbindBidirectional(ctxStringProperty[i]);
                }
                if (radioGroup.getSelectedToggle().equals(toggle1)) {
                    ctxStringProperty = frameStringProperties.get(0);

                } else if (radioGroup.getSelectedToggle().equals(toggle2)) {
                    ctxStringProperty = frameStringProperties.get(1);
                }
                for (int i = 0; i < FRAME_NUM; i++) {
                    shownFrames.get(i).textProperty().bindBidirectional(ctxStringProperty[i]);
                }
            }
        });
    }
}
