package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

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
                                    + "% (" + (int)Math.floor(x.doubleValue() * size)
                                    + " bytes)"));

        } catch (IOException e) {
            AlertDialogs.unexpectedAlert(owner, e);
        }

    }

    public Property progressProperty() {
        return progress.progressProperty();
    }

}
