package net.uberfoo.cpm.filesystem.editor;

import javafx.fxml.FXML;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import net.uberfoo.cpm.filesystem.CpmDisk;
import net.uberfoo.cpm.filesystem.DiskParameterBlock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class EditorController {

    public static final DiskParameterBlock Z80RB_DPB = new DiskParameterBlock(
            512,
            5,
            31,
            1,
            2047,
            511,
            240,
            0,
            0,
            0
    );

    @FXML
    private TreeTableView fileTree;
    @FXML
    private BorderPane rootPane;

    @FXML
    protected void onFileMenuExitClick() {
        System.exit(0);
    }

    @FXML
    protected void onFileMenuOpenClick() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        try {
            var channel = new RandomAccessFile(file, "rw").getChannel();
            new CpmDisk(Z80RB_DPB, channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
