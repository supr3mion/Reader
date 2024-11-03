module com.nhlstenden.reader2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires junrar;
    requires java.sql;
    requires jdk.jsobject;
    requires javafx.swing;
    requires org.apache.commons.io;

    opens com.nhlstenden.reader2 to javafx.fxml;
    exports com.nhlstenden.reader2;
    exports com.nhlstenden.reader2.controllers;
    opens com.nhlstenden.reader2.controllers to javafx.fxml;
    exports com.nhlstenden.reader2.annotations;
    opens com.nhlstenden.reader2.annotations to javafx.fxml;


}