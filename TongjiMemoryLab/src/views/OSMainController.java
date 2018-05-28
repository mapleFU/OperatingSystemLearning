package views;

import Generator.RandomCodeGenerator;
import Memory.EvictAlgorithm.EvictBase;
import Memory.EvictAlgorithm.FIFOEvict;
import Memory.EvictAlgorithm.LRUEvict;
import Memory.PhysicsMemory;
import Memory.Worker;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.util.Pair;
import models.PhysicMemoryBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * 主要的控制器
 * 注意，只有页面上有的才可以...
 */
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

    @FXML
    private Button comboExecute;

    @FXML
    private Button executeAll;

    @FXML
    private Button pageFaultShown;
    /**
     * 唯一的表示指令--PFN映射的表
     */
    @FXML
    private TableView<ShownCode> tableView;

    /**
     * 展示的FRAME序号
     * TODO: 修改0 不会修改FRAME的问题
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
    private ArrayList<FloatProperty> pageFaultRates;
    private ArrayList<ObservableList<PieChart.Data>> datalist;

    private StringProperty[] ctxStringProperty;


    private final int FRAME_NUM = 4;
    private RandomCodeGenerator rcg;


    private void initializeInnerGlobal() {
        rcg = new RandomCodeGenerator(320);
//        ctxStringProperty = new StringProperty[FRAME_NUM];
        physicMemoryBeans = new ArrayList<>();
        workers = new ArrayList<>();
        frameStringProperties = new ArrayList<>();
        pageFaultRates = new ArrayList<>();
        datalist = new ArrayList<>();
    }

    /**
     * initialize inner functions after init globals
     */
    private void initializeAfterInnerGlobal() {
        EvictBase[] evictBases = new EvictBase[2];
        evictBases[0] = new LRUEvict(FRAME_NUM);
        evictBases[1] = new FIFOEvict(FRAME_NUM);

        tableView.getItems().clear();
        // init frame
        for (EvictBase evb:
                evictBases) {

//            datalist.add();

            Pair<Worker, PhysicsMemory> wp_pair = Worker.generateWorker(evb);
            workers.add(wp_pair.getKey());
            PhysicMemoryBean pmb = new PhysicMemoryBean(wp_pair.getValue());
            physicMemoryBeans.add(pmb);
            pageFaultRates.add(pmb.pageFaultRateProperty());

            PieChart.Data fault = new PieChart.Data("Fault", 0);
            fault.pieValueProperty().bindBidirectional(pmb.pageFaultRateProperty());
            PieChart.Data unfault = new PieChart.Data("Unfault", 0);
            unfault.pieValueProperty().bind(Bindings.subtract(1, pmb.pageFaultRateProperty()));

            ObservableList<PieChart.Data> observableList = FXCollections.observableArrayList(
                    fault,
                    unfault
            );
            datalist.add(observableList);

            StringProperty[] strings = new StringProperty[FRAME_NUM];
//            Button[] buttons = new Button[FRAME_NUM];
            for (int i = 0; i < FRAME_NUM; i++) {
                strings[i] = new SimpleStringProperty("None");
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
//        fxPageFaultChart.setData(datalist.get(0));
        fxPageFaultChart.setData(datalist.get(0));
        pageFaultShown.textProperty().bind(Bindings.convert(pageFaultRates.get(0)));

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
                pageFaultShown.textProperty().unbind();
                if (radioGroup.getSelectedToggle().equals(toggle1)) {
                    ctxStringProperty = frameStringProperties.get(0);
                    pageFaultShown.textProperty().bind(Bindings.convert(pageFaultRates.get(0)));

                } else if (radioGroup.getSelectedToggle().equals(toggle2)) {
                    ctxStringProperty = frameStringProperties.get(1);
                    pageFaultShown.textProperty().bind(Bindings.convert(pageFaultRates.get(1)));
                }
                for (int i = 0; i < FRAME_NUM; i++) {
                    shownFrames.get(i).textProperty().bindBidirectional(ctxStringProperty[i]);
                }
            }
        });
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

    @FXML
    private void executeFive() {
        for (int i = 0; i < 5 && rcg.hasNext(); i++) {
            executeCode();
        }
    }

    @FXML
    private void executeAll() {
        while (rcg.hasNext()) {
            executeCode();
        }
    }

    /**
     * 表示缺页的Chart
     */
    @FXML
    private PieChart fxPageFaultChart;

    @FXML
    private Button restartButton;
    /**
     * 重新启动程序以便于多次测试
     */
    @FXML
    private void Restart() {
         initializeInnerGlobal();
         initializeAfterInnerGlobal();

    }


    /**
     * initialize 一般用于bind
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeInnerGlobal();
        initializeAfterInnerGlobal();
    }
}
