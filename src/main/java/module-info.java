module net.uberfoo.cpm.filesystem.editor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    requires org.kordamp.ikonli.javafx;

    requires net.uberfoo.cpm.cpmutils;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;

    exports net.uberfoo.cpm.filesystem.editor;
    opens net.uberfoo.cpm.filesystem.editor to javafx.fxml;
}