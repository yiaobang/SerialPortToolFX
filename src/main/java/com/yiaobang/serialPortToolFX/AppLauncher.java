package com.yiaobang.serialporttoolfx;

import com.yiaobang.serialporttoolfx.framework.theme.Theme;
import com.yiaobang.serialporttoolfx.serial.PortMonitor;
import com.yiaobang.serialporttoolfx.utils.I18nUtils;
import com.yiaobang.serialporttoolfx.view.SerialPortView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * 应用程序启动器
 *
 * @author Y
 * @date 2024/05/14
 */
public class AppLauncher extends Application {
    public static final File ROOT_FILE_PATH;
    public static final FileChooser FILE_CHOOSER = new FileChooser();

    static {
        //当前目录
        ROOT_FILE_PATH = new File(System.getProperty("user.dir"));
    }

    public static JavaFXBuilderFactory JAVAFX_BUILDER_FACTORY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.allowhidpi", "false");
        //加载主题
        Application.setUserAgentStylesheet(Theme.CUPERTINO_LIGHT.getCss());
        JAVAFX_BUILDER_FACTORY = new JavaFXBuilderFactory();
        //初始化文件选择器
        FILE_CHOOSER.setTitle(I18nUtils.getString("file.chooser.title"));
        if (ROOT_FILE_PATH.exists() && ROOT_FILE_PATH.isDirectory()) {
            FILE_CHOOSER.setInitialDirectory(ROOT_FILE_PATH);
        }
        FILE_CHOOSER.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(I18nUtils.getString("file.chooser.json"), "*.json")
        );
        //串口监控
        PortMonitor.init();
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