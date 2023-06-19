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
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SelectDiskDialog extends Dialog<PlatformDiskFactory.OSDiskEntry> {

    @FXML
    private ChoiceBox<PlatformDiskFactory.OSDiskEntry> selectBox;

    public SelectDiskDialog(Window owner, List<PlatformDiskFactory.OSDiskEntry> items) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("select-disk-view.fxml"));
            fxmlLoader.setController(this);

            DialogPane dialogPane = fxmlLoader.load();

            selectBox.setConverter(new StringConverter<PlatformDiskFactory.OSDiskEntry>() {
                @Override
                public String toString(PlatformDiskFactory.OSDiskEntry entry) {
                    var size = String.format("%.2f", entry.size() / (1024f * 1024f));
                    return entry.name() + " (" +entry.address() + ") " + size + "Mb";
                }

                @Override
                public PlatformDiskFactory.OSDiskEntry fromString(String string) {
                    return null;
                }
            });

            selectBox.getItems().addAll(items);
            if (items.size() > 0) selectBox.setValue(items.get(0));

            dialogPane.lookupButton(ButtonType.OK).disableProperty()
                    .bind(selectBox.valueProperty().map(Objects::isNull));

            initOwner(owner);
            initModality(Modality.APPLICATION_MODAL);

            setResizable(false);
            setTitle("Select Disk");
            setDialogPane(dialogPane);
            setResultConverter(buttonType -> buttonType == ButtonType.OK ? selectBox.getValue() : null);

            WindowUtil.positionDialog(owner, this, dialogPane.getWidth(), dialogPane.getHeight());

            setOnShowing(dialogEvent -> Platform.runLater(() -> {
                selectBox.requestFocus();
            }));
        } catch (IOException e) {
            AlertDialogs.unexpectedAlert(owner, e);
        }

    }

    @FXML
    public void initialize() {
    }

}
