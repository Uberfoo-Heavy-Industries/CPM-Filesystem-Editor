package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class SelectDpbDialog extends Dialog<DiskParameterBlockView> {

    @FXML
    private ChoiceBox<DiskParameterBlockView> selectBox;

    public SelectDpbDialog(Window owner, List<DiskParameterBlockView> items) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("select-dpb-view.fxml"));
            fxmlLoader.setController(this);

            DialogPane dialogPane = fxmlLoader.load();

            selectBox.getItems().addAll(items);

//            dialogPane.lookupButton(ButtonType.CANCEL).addEventFilter(ActionEvent.ANY, this::onCancel);
//            dialogPane.lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ANY, this::onOk);

            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle("Select Disk Parameters");
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> buttonType == ButtonType.OK ? selectBox.getValue() : null);

            setOnShowing(dialogEvent -> Platform.runLater(() -> selectBox.requestFocus()));
        } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @FXML
    public void initialize() {
    }

}
