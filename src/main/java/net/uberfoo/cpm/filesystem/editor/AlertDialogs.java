package net.uberfoo.cpm.filesystem.editor;

import javafx.scene.control.Alert;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class AlertDialogs {
    private static JMetro jMetro = new JMetro(Style.DARK);

    static void fileErrorAlert(Window owner, IOException e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "A file error occurred!\n" + e.getMessage());
        WindowUtil.positionDialog(owner, errDialog, errDialog.getDialogPane().getWidth(), errDialog.getDialogPane().getHeight());
        jMetro.setScene(errDialog.getDialogPane().getScene());
        errDialog.showAndWait();
    }

    static void fileExistsAlert(Window owner, FileAlreadyExistsException e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "File already exists!\n" + e.getMessage());
        WindowUtil.positionDialog(owner, errDialog, errDialog.getDialogPane().getWidth(), errDialog.getDialogPane().getHeight());
        jMetro.setScene(errDialog.getDialogPane().getScene());
        errDialog.showAndWait();
    }

    static void unexpectedAlert(Window owner, Exception e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "Unexpected error: " + e.getMessage());
        WindowUtil.positionDialog(owner, errDialog, errDialog.getDialogPane().getWidth(), errDialog.getDialogPane().getHeight());
        jMetro.setScene(errDialog.getDialogPane().getScene());
        errDialog.showAndWait();
    }
}