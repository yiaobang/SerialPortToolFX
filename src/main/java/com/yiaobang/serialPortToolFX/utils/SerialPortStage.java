package com.yiaobang.serialPortToolFX.utils;

import com.yiaobang.javafxTool.core.FX;
import com.yiaobang.serialPortToolFX.view.SerialPortView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

import static com.yiaobang.serialPortToolFX.AppLauncher.JAVAFX_BUILDER_FACTORY;

@Getter
public class SerialPortStage {
    private final Stage Stage;
    private final SerialPortView serialPortView;

    public SerialPortStage(Stage stage, SerialPortView serialPortView) {
        Stage = stage;
        this.serialPortView = serialPortView;
    }

    public static SerialPortStage create() {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        FXMLLoader loader = FX.fxmlLoader("serialPortView.fxml");
        loader.setBuilderFactory(JAVAFX_BUILDER_FACTORY);
        Scene scene = FX.loadScene(loader);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getIcons().add(FX.image("ico.png"));
        SerialPortView serialPortView1 = loader.getController();

        FX.stageDrag(scene,stage);

        return new SerialPortStage(stage, serialPortView1);
    }
}
