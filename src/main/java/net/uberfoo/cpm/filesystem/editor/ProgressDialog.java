package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;

import static net.uberfoo.cpm.filesystem.editor.Util.toMegabytes;

public class ProgressDialog extends Dialog<Void> {

    @FXML
    private ProgressBar progress;

    @FXML
    private Label label;

    public ProgressDialog(Window owner, long size, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("progress-view.fxml"));
            fxmlLoader.setController(this);

            DialogPane dialogPane = fxmlLoader.load();

            progress.setProgress(0);

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle(title);
            setDialogPane(dialogPane);

            label.textProperty()
                    .bind(progress.progressProperty().map(
                            (x) -> (int)Math.floor(x.doubleValue() * 100)
                                    + "% (" + toMegabytes((int)Math.floor(x.doubleValue() * size)) + ")"));

        } catch (IOException e) {
            AlertDialogs.unexpectedAlert(owner, e);
        }

    }

    public Property progressProperty() {
        return progress.progressProperty();
    }

}
