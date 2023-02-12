package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class SelectPartitionsDialog extends Dialog<DiskParameterBlockView> {

    @FXML
    private ChoiceBox<DiskParameterBlockView> selectBox;

    public SelectPartitionsDialog(Window owner, List<DiskParameterBlockView> items) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("select-partitions-view.fxml"));
            fxmlLoader.setController(this);

            DialogPane dialogPane = fxmlLoader.load();

            selectBox.getItems().addAll(items);
            selectBox.setValue(items.get(0));

            dialogPane.lookupButton(ButtonType.OK).disableProperty().bind(selectBox.valueProperty().map(x -> x == null));

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle("Select Disk Parameters");
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> buttonType == ButtonType.OK ? selectBox.getValue() : null);

            setOnShowing(dialogEvent -> Platform.runLater(() -> selectBox.requestFocus()));
        } catch (IOException e) {
            AlertDialogs.unexpectedAlert(e);
        }

    }

    @FXML
    public void initialize() {
    }

}
