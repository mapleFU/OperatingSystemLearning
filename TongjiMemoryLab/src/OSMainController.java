import Generator.RandomCodeGenerator;
import Memory.EvictAlgorithm.EvictBase;
import Memory.EvictAlgorithm.FIFOEvict;
import Memory.EvictAlgorithm.LRUEvict;
import Memory.PhysicsMemory;
import Memory.Worker;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import models.PhysicMemoryBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class OSMainController implements Initializable{
    private static class ShownCode {
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
    private Button[] shownFrames;

    /**
     * 各个算法的frameList
     * is a ctx
     */
    private ArrayList<Button[]> frameButtonsLists;
    private ArrayList<PhysicMemoryBean> physicMemoryBeans;
    private ArrayList<Worker> workers;

    private final int FRAME_NUM = 4;
    private RandomCodeGenerator rcg;


    private void initializeInnerGlobal() {
        rcg = new RandomCodeGenerator(320);
        frameButtonsLists = new ArrayList<>();
        physicMemoryBeans = new ArrayList<>();
        workers = new ArrayList<>();

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
            Button[] buttons = new Button[FRAME_NUM];
            for (int i = 0; i < FRAME_NUM; i++) {
                buttons[i] = new Button();
                buttons[i].setText("None");
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
                        buttons[index].setText(newValue.toString());
                    }
                }));
            }

            frameButtonsLists.add(buttons);
        }


        //binding

    }
}
