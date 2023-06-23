package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SelectDpbDialog extends Dialog<DiskParameterBlockView> {

    @FXML
    private ChoiceBox<DiskParameterBlockView> selectBox;

    public SelectDpbDialog(Window owner, List<DiskParameterBlockView> items) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("select-dpb-view.fxml"));
            fxmlLoader.setController(this);

            DialogPane dialogPane = fxmlLoader.load();

            selectBox.getItems().addAll(items);
            selectBox.setValue(items.get(0));

            dialogPane.lookupButton(ButtonType.OK).disableProperty()
                    .bind(selectBox.valueProperty().map(Objects::isNull));

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle("Select Disk Parameters");
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> buttonType == ButtonType.OK ? selectBox.getValue() : null);

            setOnShowing(dialogEvent -> Platform.runLater(() -> selectBox.requestFocus()));
        } catch (IOException e) {
            AlertDialogs.unexpectedAlert(owner, e);
        }

    }

    @FXML
    public void initialize() {
    }

}
