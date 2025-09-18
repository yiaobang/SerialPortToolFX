module SerialPortToolFX {
    exports com.yiaobang.serialporttoolfx;
    opens com.yiaobang.serialporttoolfx.view to javafx.fxml;
    opens com.yiaobang.serialporttoolfx to javafx.base;
    
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    requires com.google.gson;
    requires org.apache.commons.text;
    requires org.apache.commons.codec;
    requires com.fazecast.jSerialComm;
    requires static lombok;
    requires atlantafx.base;
}