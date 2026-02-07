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

public class SerialPortView extends ViewFXML<SerialComm> {
    @FXML private AnchorPane root;
    @FXML private ComboBox<String> serialPortNamePicker, stopBitsPicker, parityPicker, flowControlPicker;
    @FXML private ComboBox<Integer> baudRatePicker, dataBitsPicker;
    @FXML private CheckBox hexReceive, hexSend, receiveSave, sendSave, receiveShow, timedDispatch;
    @FXML private TextArea receive, send;
    @FXML private Label receiveNumber, sendNumber, sendLabel, receiveLabel, frequencyLabel, msLabel;
    @FXML private Label serialPortLabel, baudRateLabel, dataBitsLabel, stopBitsLabel, parityLabel, flowControlLabel, linkStatusLabel, simStatusLabel;
    @FXML private Circle serialPortLight, analogLight;
    @FXML private Button serialPortSwitch, clearReceiveBtn, sendDataBtn, clearSendBtn, newWindowBtn, analogReplyBtn;
    @FXML private TextField time;

    private final Timeline circularSending = new Timeline();
    private volatile long waitTime = 1000;
    private byte[] bytes;
    private int theme = 0;
    private boolean isUpdatingI18n = false; // 锁，防止刷新列表触发重连

    @Override
    protected SerialComm createViewModel() { return new SerialComm(); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        setupBindings();
        setupListeners();
        updateUITexts(); // 初始化所有翻译
        setupInitialData();
    }

    private void setupInitialData() {
        circularSending.setCycleCount(Timeline.INDEFINITE);
        baudRatePicker.getItems().addAll(SerialComm.BAUD_RATE);
        dataBitsPicker.getItems().addAll(SerialComm.DATA_BITS);
        stopBitsPicker.getItems().addAll(SerialComm.STOP_BITS);

        serialPortNamePicker.setValue(serialPortNamePicker.getItems().isEmpty() ? "" : serialPortNamePicker.getItems().getFirst());

        baudRatePicker.setValue(viewModel.getBaudRate());
        dataBitsPicker.setValue(viewModel.getDataBits());
        stopBitsPicker.setValue(viewModel.getStopString());
    }

    private void setupBindings() {
        sendNumber.textProperty().bind(viewModel.getSEND_LONG_PROPERTY().asString());
        receiveNumber.textProperty().bind(viewModel.getRECEIVE_LONG_PROPERTY().asString());
        serialPortSwitch.textProperty().bind(viewModel.getSerialPortState().map(s ->
                I18nUtils.getString(s ? "button.close_serial_port" : "button.open_serial_port")));
        serialPortLight.fillProperty().bind(viewModel.getSerialPortState().map(s -> s ? Color.LIME : Color.RED));
        serialPortNamePicker.itemsProperty().bind(Bindings.createObjectBinding(
                () -> FXCollections.observableArrayList(PortMonitor.serialPorts.get().split("\n")), PortMonitor.serialPorts));
    }

    private void setupListeners() {
        // 串口基本参数
        serialPortNamePicker.valueProperty().addListener((o, old, n) -> viewModel.setSerialPortName(n));
        baudRatePicker.valueProperty().addListener((o, old, n) -> viewModel.setBaudRate(n));
        dataBitsPicker.valueProperty().addListener((o, old, n) -> viewModel.setDataBits(n));
        stopBitsPicker.valueProperty().addListener((o, old, n) -> viewModel.setStopBits(n));

        // 核心：基于索引的多语言参数监听
        parityPicker.getSelectionModel().selectedIndexProperty().addListener((o, old, n) -> {
            if (!isUpdatingI18n && n.intValue() >= 0) viewModel.setParityByIndex(n.intValue());
        });
        flowControlPicker.getSelectionModel().selectedIndexProperty().addListener((o, old, n) -> {
            if (!isUpdatingI18n && n.intValue() >= 0) viewModel.setFlowControlByIndex(n.intValue());
        });

        // 接收显示逻辑
        viewModel.getRECEIVE_LONG_PROPERTY().addListener((o, old, n) -> {
            if (receiveShow.isSelected()) {
                receive.setText(hexReceive.isSelected() ? CodeFormat.hex(viewModel.getData()) : CodeFormat.utf8(viewModel.getData()));
                receive.setScrollTop(Double.MAX_VALUE);
            }
        });

        // 定时发送逻辑
        timedDispatch.selectedProperty().addListener((o, old, n) -> {
            if (n) {
                circularSending.getKeyFrames().setAll(new KeyFrame(Duration.millis(waitTime), e -> {
                    if (viewModel.write(bytes) < 1) { circularSending.stop(); timedDispatch.setSelected(false); }
                }));
                circularSending.play();
            } else { circularSending.stop(); }
        });

        // 输入监听
        send.textProperty().addListener((o, old, n) -> updateSendBytes());
        hexSend.selectedProperty().addListener((o, old, n) -> updateSendBytes());
        time.textProperty().addListener((o, old, n) -> {
            try { waitTime = Math.max(1, Long.parseLong(n)); viewModel.setWaitTime(waitTime); }
            catch (Exception e) { time.setText(old); }
        });

        receiveSave.selectedProperty().addListener((o, old, n) -> viewModel.setReceiveSave(n));
        sendSave.selectedProperty().addListener((o, old, n) -> viewModel.setSendSave(n));
    }

    public void updateUITexts() {
        // 1. 刷新普通控件文本
        receiveShow.setText(I18nUtils.getString("checkbox.receive_display"));
        receiveSave.setText(I18nUtils.getString("checkbox.record_receive_data"));
        sendSave.setText(I18nUtils.getString("checkbox.record_send_data"));
        timedDispatch.setText(I18nUtils.getString("checkbox.loop_send"));
        hexSend.setText(I18nUtils.getString("checkbox.hex_send"));
        hexReceive.setText(I18nUtils.getString("checkbox.hex_display"));
        clearReceiveBtn.setText(I18nUtils.getString("button.clear_receive"));
        sendDataBtn.setText(I18nUtils.getString("button.send_data"));
        clearSendBtn.setText(I18nUtils.getString("button.clear_send"));
        newWindowBtn.setText(I18nUtils.getString("button.new_window"));
        analogReplyBtn.setText(I18nUtils.getString("button.analog_reply"));
        sendLabel.setText(I18nUtils.getString("label.send") + ":");
        receiveLabel.setText(I18nUtils.getString("label.receive") + ":");
        frequencyLabel.setText(I18nUtils.getString("label.frequency"));
        msLabel.setText(I18nUtils.getString("label.ms_per_time"));
        serialPortLabel.setText(I18nUtils.getString("label.serial_port_number"));
        baudRateLabel.setText(I18nUtils.getString("label.baud_rate"));
        dataBitsLabel.setText(I18nUtils.getString("label.data_bits"));
        stopBitsLabel.setText(I18nUtils.getString("label.stop_bits"));
        parityLabel.setText(I18nUtils.getString("label.parity"));
        flowControlLabel.setText(I18nUtils.getString("label.flow_control"));
        linkStatusLabel.setText(I18nUtils.getString("label.link_status"));
        simStatusLabel.setText(I18nUtils.getString("label.sim_status"));

        // 2. 刷新 ComboBox 列表文字
        refreshComboBoxI18nItems();
    }

    private void refreshComboBoxI18nItems() {
        isUpdatingI18n = true;
        int pIdx = Math.max(parityPicker.getSelectionModel().getSelectedIndex(), 0);
        int fIdx = Math.max(flowControlPicker.getSelectionModel().getSelectedIndex(), 0);

        parityPicker.getItems().setAll(
                I18nUtils.getString("parity.none"), I18nUtils.getString("parity.odd"),
                I18nUtils.getString("parity.even"), I18nUtils.getString("parity.mark"), I18nUtils.getString("parity.space")
        );
        parityPicker.getSelectionModel().select(pIdx);

        flowControlPicker.getItems().setAll(
                I18nUtils.getString("flow.none"), I18nUtils.getString("flow.rts_cts"),
                I18nUtils.getString("flow.dsr_dtr"), I18nUtils.getString("flow.xon_xoff")
        );
        flowControlPicker.getSelectionModel().select(fIdx);
        isUpdatingI18n = false;
    }

    private void updateSendBytes() {
        String t = send.getText();
        if (t == null || t.isEmpty()) { bytes = null; }
        else { bytes = hexSend.isSelected() ? CodeFormat.hex(t) : CodeFormat.utf8(t); }
    }

    @FXML void min() { ((Stage) root.getScene().getWindow()).setIconified(true); }
    @FXML void close() { ((Stage) root.getScene().getWindow()).close(); viewModel.close(); }
    @FXML void switchLanguage() {
        Locale[] locales = I18nUtils.getSupportedLocales();
        int next = 0;
        for(int i=0; i<locales.length; i++) {
            if(locales[i].equals(I18nUtils.getCurrentLocale())) { next = (i+1)%locales.length; break; }
        }
        I18nUtils.setLocale(locales[next]);
        updateUITexts();
    }
    @FXML void switchTheme() { theme++; root.getScene().setUserAgentStylesheet(Theme.rotationTheme(theme)); }
    @FXML void analogReply() {
        File file = FILE_CHOOSER.showOpenDialog(root.getScene().getWindow());
        analogLight.setFill(viewModel.createDeviceSimulator(file) ? Color.LIME : Color.RED);
    }
    @FXML void cleanReceive() { viewModel.getBuffer().close(); receive.clear(); }
    @FXML void cleanSend() { send.clear(); }
    @FXML void cleanReceiveNumber() { viewModel.clearReceive(); }
    @FXML void cleanSendNumber() { viewModel.clearSend(); }
    @FXML void sendData() { viewModel.write(bytes); }
    @FXML void serialPortSwitch() { if (viewModel.getSerialPortState().get()) viewModel.close(); else viewModel.openSerialPort(); }
    @FXML void createSerialPortStage() {
        SerialPortView v = createSerialPortView();
        v.initTheme(this.theme).initSerialPort(viewModel.getBaudRate(), viewModel.getDataBits(), viewModel.getStopString(), "", "");
        v.getStage().show();
    }
    public static SerialPortView createSerialPortView() {
        Stage s = new Stage(StageStyle.TRANSPARENT);
        FXMLLoader l = FX.fxmlLoader("serialPortView.fxml");
        l.setBuilderFactory(JAVAFX_BUILDER_FACTORY);
        Scene sc = FX.loadScene(l);
        sc.setFill(Color.TRANSPARENT);
        s.setScene(sc);
        FX.stageDrag(sc, s);
        return l.getController();
    }
    public SerialPortView initTheme(int t) { this.theme = t; switchTheme(); return this; }
    public void initSerialPort(int b, int d, String s, String p, String f) {
        baudRatePicker.setValue(b); dataBitsPicker.setValue(d); stopBitsPicker.setValue(s);
    }
    public Stage getStage() { return (Stage) root.getScene().getWindow(); }
}