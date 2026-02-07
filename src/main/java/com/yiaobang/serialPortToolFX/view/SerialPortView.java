package com.yiaobang.serialporttoolfx.view;

import com.yiaobang.serialporttoolfx.framework.core.FX;
import com.yiaobang.serialporttoolfx.framework.mvvm.ViewFXML;
import com.yiaobang.serialporttoolfx.framework.theme.Theme;
import com.yiaobang.serialporttoolfx.serial.SerialComm;
import com.yiaobang.serialporttoolfx.serial.PortMonitor;
import com.yiaobang.serialporttoolfx.utils.CodeFormat;
import com.yiaobang.serialporttoolfx.utils.I18nUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.yiaobang.serialporttoolfx.AppLauncher.FILE_CHOOSER;
import static com.yiaobang.serialporttoolfx.AppLauncher.JAVAFX_BUILDER_FACTORY;

/**
 * 串口视图
 *
 * @author Y
 * @date 2024/05/14
 */
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
    //设备模拟的指示灯
    @FXML
    private Circle analogLight;
    
    // 按钮引用
    @FXML
    private Button clearReceiveBtn;
    @FXML
    private Button sendDataBtn;
    @FXML
    private Button clearSendBtn;
    @FXML
    private Button newWindowBtn;
    @FXML
    private Button analogReplyBtn;
    
    // 标签引用
    @FXML
    private Label sendLabel;
    @FXML
    private Label receiveLabel;
    @FXML
    private Label frequencyLabel;
    @FXML
    private Label msLabel;
    @FXML
    private Label serialPortLabel;
    @FXML
    private Label baudRateLabel;
    @FXML
    private Label dataBitsLabel;
    @FXML
    private Label stopBitsLabel;
    @FXML
    private Label parityLabel;
    @FXML
    private Label flowControlLabel;

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
     * 加载json文件用于在收到串口消息后进行设备模拟
     * 当json文件加载成功时 指示灯变为绿色
     * 当json文件记载失败时 指示灯变为红色
     */
    @FXML
    void analogReply() {
        File file = FILE_CHOOSER.showOpenDialog(root.getScene().getWindow());
        boolean deviceSimulator = viewModel.createDeviceSimulator(file);
        analogLight.setFill(deviceSimulator ? Color.LIME : Color.RED);
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
        SerialPortView serialPortView = SerialPortView.createSerialPortView();
        serialPortView
                .initTheme(this.theme)
                .initSerialPort(viewModel.getBaudRate(),
                        viewModel.getDataBits(),
                        viewModel.getStopString(),
                        viewModel.getParityString(),
                        viewModel.getFlowControlString());
        // 更新新窗口的语言
        serialPortView.updateUITexts();
        Stage stage = serialPortView.getStage();
        stage.setX(window.getX() + 100);
        stage.setY(window.getY() + 100);
        stage.show();
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
     * 切换语言
     */
    @FXML
    void switchLanguage() {
        Locale[] locales = I18nUtils.getSupportedLocales();
        Locale current = I18nUtils.getCurrentLocale();
        int currentIndex = 0;
        for (int i = 0; i < locales.length; i++) {
            if (locales[i].equals(current)) {
                currentIndex = i;
                break;
            }
        }
        int nextIndex = (currentIndex + 1) % locales.length;
        I18nUtils.setLocale(locales[nextIndex]);
        updateUITexts();
    }

    /**
     * 更新所有UI文本
     */
    public void updateUITexts() {
        // 更新串口开关按钮
        serialPortSwitch.textProperty().unbind();
        serialPortSwitch.textProperty().bind(viewModel.getSerialPortState().map(state ->
            state ? I18nUtils.getString("button.close_serial_port") : I18nUtils.getString("button.open_serial_port")));

        // 更新复选框
        receiveShow.setText(I18nUtils.getString("checkbox.receive_display"));
        receiveSave.setText(I18nUtils.getString("checkbox.record_receive_data"));
        sendSave.setText(I18nUtils.getString("checkbox.record_send_data"));
        timedDispatch.setText(I18nUtils.getString("checkbox.loop_send"));
        hexSend.setText(I18nUtils.getString("checkbox.hex_send"));
        hexReceive.setText(I18nUtils.getString("checkbox.hex_display"));
        
        // 更新按钮
        clearReceiveBtn.setText(I18nUtils.getString("button.clear_receive"));
        sendDataBtn.setText(I18nUtils.getString("button.send_data"));
        clearSendBtn.setText(I18nUtils.getString("button.clear_send"));
        newWindowBtn.setText(I18nUtils.getString("button.new_window"));
        analogReplyBtn.setText(I18nUtils.getString("button.analog_reply"));
        
        // 更新标签
        sendLabel.setText(I18nUtils.getString("label.send") + ":");
        receiveLabel.setText(I18nUtils.getString("label.receive") + ":");
        frequencyLabel.setText(I18nUtils.getString("label.frequency"));
        msLabel.setText(I18nUtils.getString("label.ms_per_time"));
        
        // 更新参数标签
        serialPortLabel.setText(I18nUtils.getString("label.serial_port_number"));
        baudRateLabel.setText(I18nUtils.getString("label.baud_rate"));
        dataBitsLabel.setText(I18nUtils.getString("label.data_bits"));
        stopBitsLabel.setText(I18nUtils.getString("label.stop_bits"));
        parityLabel.setText(I18nUtils.getString("label.parity"));
        flowControlLabel.setText(I18nUtils.getString("label.flow_control"));
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
        receiveSave.selectedProperty().addListener((observable, oldValue, newValue) -> viewModel.setReceiveSave(newValue));
        //发送保存的开关
        sendSave.selectedProperty().addListener((observable, oldValue, newValue) -> viewModel.setSendSave(newValue));
        //计数绑定
        sendNumber.textProperty().bind(viewModel.getSEND_LONG_PROPERTY().asString());
        receiveNumber.textProperty().bind(viewModel.getRECEIVE_LONG_PROPERTY().asString());

        //串口开关绑定
        serialPortSwitch.textProperty().bind(viewModel.getSerialPortState().map(state -> 
            state ? I18nUtils.getString("button.close_serial_port") : I18nUtils.getString("button.open_serial_port")));
        //串口指示灯绑定
        serialPortLight.fillProperty().bind(viewModel.getSerialPortState().map(state -> state ? Color.LIME : Color.RED));

        //初始化列表
        baudRatePicker.setValue(viewModel.getBaudRate());
        dataBitsPicker.setValue(viewModel.getDataBits());
        stopBitsPicker.setValue(viewModel.getStopString());
        parityPicker.setValue(viewModel.getParityString());
        flowControlPicker.setValue(viewModel.getFlowControlString());

        //串口参数更新
        serialPortNamePicker.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.setSerialPortName(newValue));
        baudRatePicker.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.setBaudRate(newValue));
        dataBitsPicker.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.setDataBits(newValue));
        stopBitsPicker.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.setStopBits(newValue));
        parityPicker.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.setParity(newValue));
        flowControlPicker.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.setFlowControl(newValue));

        //串口号列表周期刷新
        serialPortNamePicker.itemsProperty().bind(Bindings.createObjectBinding(() -> FXCollections.observableArrayList(PortMonitor.serialPorts.get().split("\n")), PortMonitor.serialPorts));

        //添加参数列表数据
        baudRatePicker.getItems().addAll(SerialComm.BAUD_RATE);
        dataBitsPicker.getItems().addAll(SerialComm.DATA_BITS);
        stopBitsPicker.getItems().addAll(SerialComm.STOP_BITS);
        parityPicker.getItems().addAll(SerialComm.PARITY);
        flowControlPicker.getItems().addAll(SerialComm.FLOW_CONTROL);
        serialPortNamePicker.setValue(serialPortNamePicker.getItems().isEmpty() ? "" : serialPortNamePicker.getItems().getFirst());

        //循环发送的等待时间(ms)
        time.textProperty().addListener((observable, oldValue, newValue) -> {
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
        timedDispatch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                circularSending.getKeyFrames().clear();
                circularSending.getKeyFrames().add(new KeyFrame(Duration.millis(waitTime), event -> {
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
        send.textProperty().addListener((observable, oldValue, newValue) -> updateSendBytes());

        //16进制发送
        hexSend.selectedProperty().addListener((observable, oldValue, newValue) -> updateSendBytes());

        //是否显示接收到的内容
        receiveShow.selectedProperty().addListener((observable, oldValue, newValue) -> viewModel.setReceiveShow(newValue));

        //实时刷新接收到的内容
        viewModel.getRECEIVE_LONG_PROPERTY().addListener((observable, oldValue, newValue) -> {
            if (receiveShow.isSelected()) {
                receive.setText(hexReceive.isSelected() ? CodeFormat.hex(viewModel.getData()) : CodeFormat.utf8(viewModel.getData()));
                receive.setScrollTop(Double.MAX_VALUE);
            }
        });

        //将接收到的内容以16进制的形式进行发送
        hexReceive.selectedProperty().addListener((observable, oldValue, newValue) -> {
            byte[] data = viewModel.getData();
            receive.setText(newValue ? CodeFormat.hex(data) : CodeFormat.utf8(data));
            //滚动条自动滚动
            receive.setScrollTop(Double.MAX_VALUE);
        });

        //如何串口被拔掉则将其关闭并且将串口的名称改为 ""
        PortMonitor.serialPorts.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.contains(viewModel.getSerialPortName())) {
                viewModel.close();
                serialPortNamePicker.setValue("");
            }
        });
        
        // 初始化时更新UI文本
        updateUITexts();
    }

    /**
     * 初始化主题
     *
     * @param theme 主题
     * @return {@link SerialPortView }
     */
    public SerialPortView initTheme(int theme) {
        this.theme = theme;
        switchTheme();
        return this;
    }

    /**
     * 在创建窗口后设置串口的参数(便于拷贝当前串口参数)
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

    /**
     * 创建串口调试窗口
     *
     * @return {@link SerialPortView }
     */
    public static SerialPortView createSerialPortView() {
        try {
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            FXMLLoader loader = FX.fxmlLoader("serialPortView.fxml");
            loader.setBuilderFactory(JAVAFX_BUILDER_FACTORY);
            Scene scene = FX.loadScene(loader); // 这里最容易报错
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.getIcons().add(FX.image("ico.png"));
            FX.stageDrag(scene, stage);
            return loader.getController();
        } catch (Exception e) {
            System.err.println("--- FXML 加载失败详细日志开始 ---");
            e.printStackTrace(); // 关键：打印完整堆栈
            System.err.println("--- FXML 加载失败详细日志结束 ---");
            throw e;
        }
    }

    /**
     * 更新发送字节数组
     * Extract method to eliminate code duplication
     */
    private void updateSendBytes() {
        String text = send.getText();
        if (!text.isEmpty()) {
            bytes = hexSend.isSelected() ? CodeFormat.hex(text) : CodeFormat.utf8(text);
        } else {
            bytes = null;
        }
    }

    public Stage getStage() {
        return FX.getStage(this.root);
    }
}