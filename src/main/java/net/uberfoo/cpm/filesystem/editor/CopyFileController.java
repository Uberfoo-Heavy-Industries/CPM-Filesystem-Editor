package net.uberfoo.cpm.filesystem.editor;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public class CopyFileController {

    @FXML
    private Button copyButton;

    private static final UnaryOperator<TextFormatter.Change> USER_NUMBER_VALIDATION_FORMATTER = change -> {
        if (!change.getControlNewText().matches("\\d*")) {
            change.setText(""); //else make no change
            change.setRange(    //don't remove any selected text either.
                    change.getRangeStart(),
                    change.getRangeStart()
            );
        }
        var newText = change.getControlNewText();
        if (newText != null && !newText.isBlank() && Integer.parseInt(newText) < 32) {
            change.getControl().setStyle("");
        } else {
            change.getControl().setStyle("-fx-background-color: rgba(255,0,0,0.50)");
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
        if (newText != null && !newText.isBlank()) {
            change.getControl().setStyle("");
        } else {
            change.getControl().setStyle("-fx-background-color: rgba(255,0,0,0.50)");
        }
        return change; //if change is a number
    };

    private IntegerProperty userNumber;
    private StringProperty filename;
    private BooleanProperty canceled;

    @FXML
    private TextField userNumberField;
    @FXML
    private TextField nameField;
    @FXML
    private AnchorPane copyFileView;

    @FXML
    public void initialize() {
        userNumber = new SimpleIntegerProperty(0);
        filename = new SimpleStringProperty();
        canceled = new SimpleBooleanProperty(false);

        userNumber.bind(userNumberField.textProperty().map(x -> Integer.parseInt(x)));
        filename.bindBidirectional(nameField.textProperty());

        userNumberField.setTextFormatter(new TextFormatter<>(USER_NUMBER_VALIDATION_FORMATTER));
        nameField.setTextFormatter(new TextFormatter<>(FILENAME_VALIDATION_FORMATTER));

        copyButton.disableProperty()
                .bind(
                        Bindings.not(Bindings.and(BooleanExpression.booleanExpression(
                                userNumberField.textProperty().map(newText -> newText != null && !newText.isBlank() && Integer.parseInt(newText) < 32).orElse(false)),
                                BooleanExpression.booleanExpression(nameField.textProperty().map(newText -> !newText.isBlank()).orElse(false))
                        ))
                );
    }

    @FXML
    protected void onCancelButton() {
        canceled.set(true);
        ((Stage)copyFileView.getScene().getWindow()).close();
    }

    @FXML
    protected void onCopyButton() {
        canceled.set(false);
        ((Stage)copyFileView.getScene().getWindow()).close();
    }

    public boolean isCanceled() {
        return canceled.get();
    }

    public BooleanProperty canceledProperty() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled.set(canceled);
    }

    public int getUserNumber() {
        return userNumber.get();
    }

    public IntegerProperty userNumberProperty() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber.set(userNumber);
    }

    public String getFilename() {
        return filename.get();
    }

    public StringProperty filenameProperty() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename.set(filename);
    }

    public void setNormalizedFilename(String filename) {
        var splitName = filename.split("\\.", 3);
        this.filename.set(splitName[0].substring(0,Math.min(8, splitName[0].length())).toUpperCase()
                + (splitName.length > 1 ? "." + splitName[1].substring(0,Math.min(3, splitName[1].length())).toUpperCase() : ""));
    }
}
