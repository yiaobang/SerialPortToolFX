package com.yiaobang.serialPortToolFX;

import com.yiaobang.javafxTool.theme.Theme;
import com.yiaobang.serialPortToolFX.serialComm.SerialPortMonitor;
import com.yiaobang.serialPortToolFX.view.SerialPortView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AppLauncher extends Application {
    public static final File ROOT_FILE_PATH;
    public static final FileChooser FILE_CHOOSER = new FileChooser();

    static {
        //当前目录
        ROOT_FILE_PATH = new File(System.getProperty("user.dir"));
    }

    public static JavaFXBuilderFactory JAVAFX_BUILDER_FACTORY;

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.allowhidpi", "false");
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        //加载主题
        Application.setUserAgentStylesheet(Theme.CUPERTINO_LIGHT.getCss());
        JAVAFX_BUILDER_FACTORY = new JavaFXBuilderFactory();
        //初始化文件选择器
        FILE_CHOOSER.setTitle("选择json文件");
        FILE_CHOOSER.setInitialDirectory(ROOT_FILE_PATH);
        FILE_CHOOSER.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json文件 (*.json)", "*.json")
        );
        //串口监控
        SerialPortMonitor.init();
    }

    @Override
    public void start(Stage primaryStage) {
        SerialPortView.createSerialPortView().getStage().show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }
}
