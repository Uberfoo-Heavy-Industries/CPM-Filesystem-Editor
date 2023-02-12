package net.uberfoo.cpm.filesystem.editor;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class AlertDialogs {
    static void fileErrorAlert(IOException e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "A file error occurred!\n" + e.getMessage());
        errDialog.showAndWait();
    }

    static void fileExistsAlert(FileAlreadyExistsException e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "File already exists!\n" + e.getMessage());
        errDialog.showAndWait();
    }

    static void unexpectedAlert(Exception e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "Unexpected error: " + e.getMessage());
        errDialog.showAndWait();
    }
}