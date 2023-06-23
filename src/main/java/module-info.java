module net.uberfoo.cpm.filesystem.editor {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    requires org.kordamp.ikonli.javafx;
    requires dirtyfx;

    requires cpmutils;
    requires org.apache.logging.log4j;

    requires org.slf4j;
    requires com.sun.jna;
    requires com.sun.jna.platform;

    requires aerofx;
    requires org.jfxtras.styles.jmetro;

    requires org.apache.commons.io;
    requires org.apache.commons.text;

    exports net.uberfoo.cpm.filesystem.editor;
    opens net.uberfoo.cpm.filesystem.editor to javafx.fxml,javafx.base;
    exports net.uberfoo.cpm.filesystem.editor.windows;
    opens net.uberfoo.cpm.filesystem.editor.windows to javafx.base, javafx.fxml;
}