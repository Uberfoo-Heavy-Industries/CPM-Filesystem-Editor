package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class CopyFileDialog extends Dialog<Boolean> {

    @FXML
    private ButtonType copyButton;

    private static final UnaryOperator<TextFormatter.Change> USER_NUMBER_VALIDATION_FORMATTER = change -> {
        if (!change.getControlNewText().matches("\\d*")) {
            change.setText(""); //else make no change
            change.setRange(    //don't remove any selected text either.
                    change.getRangeStart(),
                    change.getRangeStart()
            );
        }
        var newText = change.getControlNewText();
        if (!newText.isBlank() && Integer.parseInt(newText) < 32) {
            change.getControl().setStyle("");
        } else {
            change.getControl().setStyle("-fx-border-color: rgba(255,0,0,0.50)");
        }
        return change; //if change is a number
    };

    private static final UnaryOperator<TextFormatter.Change> FILENAME_VALIDATION_FORMATTER = change -> {
        if (!change.getControlNewText().matches("\\w{0,8}(\\.\\w{0,3})?")) {
            change.setText(""); //else make no change
            change.setRange(    //don't remove any selected text either.
                    change.getRangeStart(),
                    change.getRangeStart()
            );
        }
        var newText = change.getControlNewText();
        if (!newText.isBlank()) {
            change.getControl().setStyle("");
        } else {
            change.getControl().setStyle("-fx-border-color: rgba(255,0,0,0.50)");
        }
        return change; //if change is a number
    };

    private IntegerProperty userNumber;
    private StringProperty filename;
    @FXML
    private TextField userNumberField;
    @FXML
    private TextField nameField;
    @FXML
    private DialogPane copyFileView;

    public CopyFileDialog(Window owner, String title, String buttonText) {
        try {
            userNumber = new SimpleIntegerProperty(0);
            filename = new SimpleStringProperty();

            FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("copy-file-view.fxml"));
            fxmlLoader.setController(this);

            DialogPane dialogPane = fxmlLoader.load();

            userNumber.bind(userNumberField.textProperty().map(Integer::parseInt));
            filename.bindBidirectional(nameField.textProperty());

            userNumberField.setTextFormatter(new TextFormatter<>(USER_NUMBER_VALIDATION_FORMATTER));
            nameField.setTextFormatter(new TextFormatter<>(FILENAME_VALIDATION_FORMATTER));

            dialogPane.lookupButton(copyButton).disableProperty()
                    .bind(
                            Bindings.not(Bindings.and(BooleanExpression.booleanExpression(
                                            userNumberField.textProperty().map(newText -> newText != null && !newText.isBlank() && Integer.parseInt(newText) < 32).orElse(false)),
                                    BooleanExpression.booleanExpression(nameField.textProperty().map(newText -> !newText.isBlank()).orElse(false))
                            ))
                    );

            ((Button)dialogPane.lookupButton(copyButton)).textProperty().set(buttonText);

            setTitle(title);
            initOwner(owner);
            setResultConverter(x -> x == copyButton);
            setDialogPane(dialogPane);
        } catch (IOException e) {
            AlertDialogs.unexpectedAlert(owner, e);
        }
    }

    public int getUserNumber() {
        return userNumber.get();
    }

    public String getFilename() {
        return filename.get();
    }

    public void setNormalizedFilename(String filename) {
        var splitName = filename.split("\\.", 3);
        this.filename.set(splitName[0].substring(0,Math.min(8, splitName[0].length())).toUpperCase()
                + (splitName.length > 1 ? "." + splitName[1].substring(0,Math.min(3, splitName[1].length())).toUpperCase() : ""));
    }

}
