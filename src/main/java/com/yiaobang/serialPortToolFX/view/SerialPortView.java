package com.yiaobang.serialPortToolFX.view;


import com.yiaobang.javafxTool.mvvm.ViewFXML;
import com.yiaobang.javafxTool.theme.Theme;
import com.yiaobang.serialPortToolFX.serialComm.SerialComm;
import com.yiaobang.serialPortToolFX.serialComm.SerialPortMonitor;
import com.yiaobang.serialPortToolFX.utils.CodeFormat;
import com.yiaobang.serialPortToolFX.utils.SerialPortStage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.yiaobang.serialPortToolFX.AppLauncher.FILE_CHOOSER;


public class SerialPortView extends ViewFXML<SerialComm> {
    /**
     * 循环发送
     */
    private final Timeline circularSending = new Timeline();
    private volatile long waitTime = 1000;
    private volatile byte[] bytes;
    private volatile int theme = 0;
    @FXML
    private AnchorPane root;
    //串口名称选择器
    @FXML
    private ComboBox<String> serialPortNamePicker;
    //波特率选择器
    @FXML
    private ComboBox<Integer> baudRatePicker;
    //数据位选择器
    @FXML
    private ComboBox<Integer> dataBitsPicker;
    //停止位选择器
    @FXML
    private ComboBox<String> stopBitsPicker;
    //校验方式选择器
    @FXML
    private ComboBox<String> parityPicker;
    //流控选择器
    @FXML
    private ComboBox<String> flowControlPicker;
    //16进制接收显示开关
    @FXML
    private CheckBox hexReceive;
    //16进制发送开关
    @FXML
    private CheckBox hexSend;
    //保存接收的字节
    @FXML
    private CheckBox receiveSave;
    //保存发送的字节
    @FXML
    private CheckBox sendSave;
    //接收框
    @FXML
    private TextArea receive;
    //接收的字节数
    @FXML
    private Label receiveNumber;
    //接收显示开关
    @FXML
    private CheckBox receiveShow;
    //发送框
    @FXML
    private TextArea send;
    //发送的字节数
    @FXML
    private Label sendNumber;
    //串口指示灯
    @FXML
    private Circle serialPortLight;
    //发送的时间或者回复的延时
    @FXML
    private TextField time;
    //定时发送开关
    @FXML
    private CheckBox timedDispatch;
    //串口开关
    @FXML
    private Button serialPortSwitch;
    //模拟回复的指示灯
    @FXML
    private Circle analogLight;

    /**
     * 创建视图模型
     *
     * @return {@code SerialComm }
     */
    @Override
    protected SerialComm createViewModel() {
        return new SerialComm();
    }

    /**
     * 最小化窗口
     */
    @FXML
    void min() {
        ((Stage) root.getScene().getWindow()).setIconified(true);
    }

    /**
     * 关闭窗口后清理串口连接
     */
    @FXML
    void close() {
        ((Stage) root.getScene().getWindow()).close();
        viewModel.close();
    }

    /**
     * 加载json文件用于在收到串口消息后进行模拟回复
     * 当json文件加载成功时 指示灯变为绿色
     * 当json文件记载失败时 指示灯变为红色
     */
    @FXML
    void analogReply() {
        File file = FILE_CHOOSER.showOpenDialog(root.getScene().getWindow());
        boolean mockResponses = viewModel.createMockResponses(file);
        analogLight.setFill(mockResponses ? Color.LIME : Color.RED);
    }

    /**
     * 清空接收区的数据
     */
    @FXML
    void cleanReceive() {
        viewModel.getBuffer().close();
        receive.clear();
    }

    /**
     * 清空发送区的数据
     */
    @FXML
    void cleanSend() {
        send.clear();
    }

    /**
     * 创建新窗口
     */
    @FXML
    void createSerialPortStage() {
        Stage window = (Stage) root.getScene().getWindow();
        SerialPortStage serialPortStage = SerialPortStage.create();
        serialPortStage.getSerialPortView()
                .initTheme(this.theme)
                .initSerialPort(viewModel.getBaudRate(), viewModel.getDataBits(), viewModel.getStopSting(),
                        viewModel.getParitySting(), viewModel.getFlowControlSting());

        serialPortStage.getStage().setX(window.getX() + 100);
        serialPortStage.getStage().setY(window.getY() + 100);
        serialPortStage.getStage().show();
    }

    /**
     * 发送数据
     */
    @FXML
    void sendData() {
        viewModel.write(bytes);
    }

    /**
     * 串口开关
     * 串口打开指示灯变为绿色
     * 串口关闭指示灯变为红色
     */
    @FXML
    void serialPortSwitch() {
        if (viewModel.getSerialPortState().get()) {
            viewModel.close();
        } else {
            viewModel.openSerialPort();
        }
    }

    /**
     * 清理接收计数
     */
    @FXML
    void cleanReceiveNumber() {
        viewModel.clearReceive();
    }

    /**
     * 清理发送计数
     */
    @FXML
    void cleanSendNumber() {
        viewModel.clearSend();
    }

    /**
     * 切换主题
     */
    @FXML
    void switchTheme() {
        final int i = theme + 1;
        theme = i;
        root.getScene().setUserAgentStylesheet(Theme.rotationTheme(theme));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        //无限循环发送
        circularSending.setCycleCount(Timeline.INDEFINITE);
        //接收保存的开关
        receiveSave.selectedProperty().addListener((_, _, newValue) -> viewModel.setReceiveSave(newValue));
        //发送保存的开关
        sendSave.selectedProperty().addListener((_, _, newValue) -> viewModel.setSendSave(newValue));
        //计数绑定
        sendNumber.textProperty().bind(viewModel.getSEND_LONG_PROPERTY().asString());
        receiveNumber.textProperty().bind(viewModel.getRECEIVE_LONG_PROPERTY().asString());


        //串口开关绑定
        serialPortSwitch.textProperty().bind(viewModel.getSerialPortState().map(state -> state ? "关闭串口" : "打开串口"));
        //串口指示灯绑定
        serialPortLight.fillProperty().bind(viewModel.getSerialPortState().map(state -> state ? Color.LIME : Color.RED));

        //初始化列表
        baudRatePicker.setValue(viewModel.getBaudRate());
        dataBitsPicker.setValue(viewModel.getDataBits());
        stopBitsPicker.setValue(viewModel.getStopSting());
        parityPicker.setValue(viewModel.getParitySting());
        flowControlPicker.setValue(viewModel.getFlowControlSting());


        //串口参数更新
        serialPortNamePicker.valueProperty().addListener((_, _, newValue) -> viewModel.setSerialPortName(newValue));
        baudRatePicker.valueProperty().addListener((_, _, newValue) -> viewModel.setBaudRate(newValue));
        dataBitsPicker.valueProperty().addListener((_, _, newValue) -> viewModel.setDataBits(newValue));
        stopBitsPicker.valueProperty().addListener((_, _, newValue) -> viewModel.setStopBits(newValue));
        parityPicker.valueProperty().addListener((_, _, newValue) -> viewModel.setParity(newValue));
        flowControlPicker.valueProperty().addListener((_, _, newValue) -> viewModel.setFlowControl(newValue));


        //串口号列表周期刷新
        serialPortNamePicker.itemsProperty().bind(Bindings.createObjectBinding(() -> FXCollections.observableArrayList(SerialPortMonitor.serialPorts.get().split("\n")), SerialPortMonitor.serialPorts));

        //添加参数列表数据
        baudRatePicker.getItems().addAll(SerialComm.BAUD_RATE);
        dataBitsPicker.getItems().addAll(SerialComm.DATA_BITS);
        stopBitsPicker.getItems().addAll(SerialComm.STOP_BITS);
        parityPicker.getItems().addAll(SerialComm.PARITY);
        flowControlPicker.getItems().addAll(SerialComm.FLOW_CONTROL);
        serialPortNamePicker.setValue(serialPortNamePicker.getItems().isEmpty() ? "" : serialPortNamePicker.getItems().getFirst());

        //循环发送的等待时间(ms)
        time.textProperty().addListener((_, oldValue, newValue) -> {
            try {
                waitTime = Integer.parseInt(newValue);
                if (waitTime < 1) {
                    waitTime = 1;
                }
                viewModel.setWaitTime(waitTime);
            } catch (NumberFormatException e) {
                time.setText(oldValue);
            }
        });

        //定时发送开关
        timedDispatch.selectedProperty().addListener((_, _, newValue) -> {
            if (newValue) {
                circularSending.getKeyFrames().clear();
                circularSending.getKeyFrames().add(new KeyFrame(Duration.millis(waitTime), _ -> {
                    if (viewModel.write(bytes) < 1) {
                        circularSending.stop();
                        timedDispatch.setSelected(false);
                    }
                }));
                circularSending.play();
            } else {
                circularSending.stop();
            }
        });


        //更新要发送的数据
        send.textProperty().addListener((_, _, newValue) -> {
            if (!newValue.isEmpty()) {
                bytes = hexSend.isSelected() ? CodeFormat.hex(newValue) : CodeFormat.utf8(newValue);
            } else {
                bytes = null;
            }
        });

        //16进制发送
        hexSend.selectedProperty().addListener((_, _, newValue) -> {
            String text = send.getText();
            if (!text.isEmpty()) {
                bytes = newValue ? CodeFormat.hex(text) : CodeFormat.utf8(text);
            } else {
                bytes = null;
            }
        });

        //是否显示接收到的内容
        receiveShow.selectedProperty().addListener((_, _, newValue) -> viewModel.setReceiveShow(newValue));


        //实时刷新接收到的内容
        viewModel.getRECEIVE_LONG_PROPERTY().addListener((_, _, _) -> {
            if (receiveShow.isSelected()) {
                receive.setText(hexReceive.isSelected() ? CodeFormat.hex(viewModel.getData()) : CodeFormat.utf8(viewModel.getData()));
                receive.setScrollTop(Double.MAX_VALUE);
            }
        });

        //将接收到的内容以16进制的形式进行发送
        hexReceive.selectedProperty().addListener((_, _, newValue) -> {
            byte[] data = viewModel.getData();
            receive.setText(newValue ? CodeFormat.hex(data) : CodeFormat.utf8(data));
            //滚动条自动滚动
            receive.setScrollTop(Double.MAX_VALUE);
        });

        //如何串口被拔掉则将其关闭并且将串口的名称改为 ""
        SerialPortMonitor.serialPorts.addListener((_, _, newValue) -> {
            if (newValue != null && !newValue.contains(viewModel.getSerialPortName())) {
                viewModel.close();
                serialPortNamePicker.setValue("");
            }
        });
    }

    public SerialPortView initTheme(int theme) {
        this.theme = theme;
        switchTheme();
        return this;
    }

    /**
     * 在创建窗口后设置串口的参数
     *
     * @param baudRate 波特率
     * @param dateBits 数据位
     * @param stop     停止位
     * @param parity   校验
     * @param flow     流控
     */
    public void initSerialPort(int baudRate, int dateBits, String stop, String parity, String flow) {
        baudRatePicker.setValue(baudRate);
        dataBitsPicker.setValue(dateBits);
        stopBitsPicker.setValue(stop);
        parityPicker.setValue(parity);
        flowControlPicker.setValue(flow);
    }
}
