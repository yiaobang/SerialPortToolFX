module SerialPortToolFX {
    // FXML 反射访问
    opens com.yiaobang.serialporttoolfx.view to javafx.fxml;
    // Application 入口类必须对 JavaFX 开放
    opens com.yiaobang.serialporttoolfx to javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    requires typesafe.config;
    requires com.google.gson;
    requires org.apache.commons.text;
    requires org.apache.commons.codec;
    requires com.fazecast.jSerialComm;
    requires static lombok;
    requires atlantafx.base;
}