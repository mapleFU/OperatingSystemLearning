package views;

import Generator.RCodeGenerator;
import Generator.RandomCodeGenerator;
import Memory.EvictAlgorithm.EvictBase;
import Memory.EvictAlgorithm.FIFOEvict;
import Memory.EvictAlgorithm.LRUEvict;
import Memory.EvictAlgorithm.RandEvict;
import Memory.PhysicsMemory;
import Memory.Worker;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.util.Pair;
import models.PhysicMemoryBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

        public int getBios() {
            return bios.get();
        }

        public IntegerProperty biosProperty() {
            return bios;
        }

        public void setBios(int bios) {
            this.bios.set(bios);
        }

        private final IntegerProperty bios;

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
            bios = new SimpleIntegerProperty(coden % 10);

            codenum.addListener(((observable, oldValue, newValue) -> {
                physicMemory.setValue(newValue.intValue() / 10);
                bios.setValue(newValue.intValue() % 10);
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
    private Toggle toggle3;

    @FXML
    private Button executeNext;

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
     * 需要操作的BUTTONS
     */
    private ArrayList<Button> operationButtons;
    /**
     * 对应的RADIO GROUP
     */
    private ToggleGroup toggleGroup;
    /**
     * 一个初始化为0的判断是否DISABLE的按钮
     */
    private static int fxmlDisabled;
    private static ReentrantLock rlock;
    private static Lock fxmlDisabledLock;

    static {
        fxmlDisabled = -1;
        rlock = new ReentrantLock();
        fxmlDisabledLock = new ReentrantLock();
    }

    private void initButtonSwitchLock()
    {
        operationButtons = new ArrayList<>();
        operationButtons.add(executeAll);
        operationButtons.add(restartButton);
        operationButtons.add(comboExecute);
        operationButtons.add(executeNext);
        toggleGroup = radioGroup;

    }

    /**
     * 开始操作，DISABLE掉别的按钮
     */
    private void startExecution() {

        Platform.runLater(()->{
            fxmlDisabledLock.lock();
            if(fxmlDisabled == -1) {
                for (Button b:
                        operationButtons) {
                    b.setDisable(true);
                }
                toggleGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) toggle ;
                    node.setDisable(true);
                });
                System.out.println("start execution");
            }
            ++fxmlDisabled;
            fxmlDisabledLock.unlock();
        });

    }

    /**
     * 结束操作，
     */
    private void endExecution() {
        Platform.runLater(()-> {
            fxmlDisabledLock.lock();
            if (fxmlDisabled == 0) {
                System.out.println("end execution");
                for (Button b:
                        operationButtons) {
                    b.setDisable(false);
                }
                toggleGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) toggle ;
                    node.setDisable(false);
                });
            }
            fxmlDisabled--;
            fxmlDisabledLock.unlock();
        });

    }

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

    private ObservableList<PieChart.Data> piechartData;

    private final int FRAME_NUM = 4;
//    private RandomCodeGenerator rcg;
    private RCodeGenerator rcg;


    private void initializeInnerGlobal() {
        rcg = new RCodeGenerator(320);
        physicMemoryBeans = new ArrayList<>();
        workers = new ArrayList<>();
        frameStringProperties = new ArrayList<>();
        pageFaultRates = new ArrayList<>();
        datalist = new ArrayList<>();
//        pageErrorDataList = new ArrayList<>();
    }

    /**
     * initialize inner functions after init globals
     */
    private Lock updateRadioLock = new ReentrantLock();

    private void initializeAfterInnerGlobal() {
        EvictBase[] evictBases = new EvictBase[3];
        evictBases[0] = new LRUEvict(FRAME_NUM);
        evictBases[1] = new FIFOEvict(FRAME_NUM);
        evictBases[2] = new RandEvict(FRAME_NUM);

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

        piechartData = datalist.get(0);

        //binding
        // 最初的表现层, 绑定在序号0
        ctxStringProperty = frameStringProperties.get(0);
        fxPageFaultChart.setData(piechartData);
        pageFaultShown.textProperty().bind(Bindings.convert(pageFaultRates.get(0)));

        for (int i = 0; i < FRAME_NUM; i++) {
            shownFrames.get(i).textProperty().bindBidirectional(ctxStringProperty[i]);
        }

        radioGroup.selectToggle(toggle1);

        // table view
        // https://docs.oracle.com/javafx/2/fxml_get_started/fxml_tutorial_intermediate.htm

        radioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(()-> {
                startExecution();

                if (radioGroup.getSelectedToggle() != null) {
                    // Property unbind

                    for (int i = 0; i < FRAME_NUM; i++) {
                        shownFrames.get(i).textProperty().unbindBidirectional(ctxStringProperty[i]);
                    }
                    pageFaultShown.textProperty().unbind();

//                    fxPageFaultChart.setData(datalist.);

                    // 如果不符合要求抛出异常
                    int dataIndex = -1;

                    if (newValue.equals(toggle1)) {
                        dataIndex = 0;
                    } else if (newValue.equals(toggle2)) {
                        dataIndex = 1;
                    } else if (newValue.equals(toggle3)) {
                        dataIndex = 2;
                    }
                    updateRadioUI(dataIndex);


                    for (int i = 0; i < FRAME_NUM; i++) {
                        shownFrames.get(i).textProperty().bindBidirectional(ctxStringProperty[i]);
                    }

                }
                endExecution();
            });
        });
    }

    private void updateRadioUI(int dataIndex) {
        try {
            PieChart.Data fault = new PieChart.Data("Fault", 0);
            fault.pieValueProperty().bindBidirectional(pageFaultRates.get(dataIndex));
            PieChart.Data unfault = new PieChart.Data("Unfault", 0);
            unfault.pieValueProperty().bind(Bindings.subtract(1, pageFaultRates.get(dataIndex)));

            ObservableList<PieChart.Data> observableList = FXCollections.observableArrayList(
                    fault,
                    unfault
            );


            piechartData.setAll(observableList);
            ctxStringProperty = frameStringProperties.get(dataIndex);
            pageFaultShown.textProperty().bind(Bindings.convert(pageFaultRates.get(dataIndex)));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("系统继续运行");
            Platform.exit();
        }
    }


    @FXML
    private void executeCode() {
        // 凡是执行都要修改对应的TBV
        startExecution();
        if (rcg.hasNext()) {

            Iterator<Worker> workerIterator = workers.iterator();
            int code = rcg.next();
            while (workerIterator.hasNext()) {
                workerIterator.next().executeCode(code);
            }



            // 添加对应的shown code.
            tableView.getItems().add(new ShownCode(code));
        }
        endExecution();
    }

    @FXML
    private void executeFive() {
        startExecution();
        for (int i = 0; i < 5 && rcg.hasNext(); i++) {
            executeCode();
        }
        endExecution();
    }

    @FXML
    private void executeAll() {
        startExecution();
        int cnt = 0;
        while (rcg.hasNext()) {
            ++cnt;
            executeCode();
        }
        System.out.println("共执行了 " + cnt + "条指令" + "，命中等如下： "+fxPageFaultChart.dataProperty().get());
        endExecution();
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
         startExecution();
         initializeInnerGlobal();
         initializeAfterInnerGlobal();
         endExecution();
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
        initButtonSwitchLock();

    }
}
