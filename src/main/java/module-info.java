module net.uberfoo.cpm.filesystem.editor {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    requires org.kordamp.ikonli.javafx;
    requires dirtyfx;

    requires cpmutils;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;

    requires org.slf4j;
    requires com.sun.jna;
    requires com.sun.jna.platform;

    requires aerofx;
    requires org.jfxtras.styles.jmetro;

    exports net.uberfoo.cpm.filesystem.editor;
    opens net.uberfoo.cpm.filesystem.editor to javafx.fxml,javafx.base;
}