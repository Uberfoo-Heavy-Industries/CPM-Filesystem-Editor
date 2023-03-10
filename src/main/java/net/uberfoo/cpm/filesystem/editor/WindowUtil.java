package net.uberfoo.cpm.filesystem.editor;

import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WindowUtil
{
    static void positionDialog(Window scene, Stage dialogStage, double width, double height) {
        var x = scene.getX() + scene.getWidth() / 2 - width / 2;
        var y = scene.getY() + scene.getHeight() / 2 - height / 2;
        dialogStage.setY(y);
        dialogStage.setX(x);
    }

    static void positionDialog(Window scene, Dialog dialog, double width, double height) {
        var x = scene.getX() + scene.getWidth() / 2 - width / 2;
        var y = scene.getY() + scene.getHeight() / 2 - height / 2;
        dialog.setY(y);
        dialog.setX(x);
    }
}
