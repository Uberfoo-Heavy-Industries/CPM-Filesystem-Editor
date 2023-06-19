package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.Objects;
import java.util.prefs.Preferences;

public class EditorApp extends Application {

    private Scene scene;

    private final Preferences preferences = Preferences.userNodeForPackage(EditorApp.class);

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("editor-view.fxml"));

        scene = new Scene(fxmlLoader.load(),
                preferences.getDouble("WIDTH", 640),
                preferences.getDouble("HEIGHT", 480));

        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);

        stage.setX(preferences.getDouble("WINDOW_X", 600));
        stage.setY(preferences.getDouble("WINDOW_Y", 600));

        stage.getIcons().add(new Image(Objects.requireNonNull(
                ClassLoader.getSystemClassLoader().getResourceAsStream("disc-drive-orange.png"))));

        stage.setTitle("CP/M Filesystem Editor");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        preferences.putDouble("WIDTH", scene.getWidth());
        preferences.putDouble("HEIGHT", scene.getHeight());
        preferences.putDouble("WINDOW_X", scene.getWindow().getX());
        preferences.putDouble("WINDOW_Y", scene.getWindow().getY());
    }

    public static void main(String[] args) {
        launch();
    }
}
