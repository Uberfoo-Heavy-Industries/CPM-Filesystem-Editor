package net.uberfoo.cpm.filesystem.editor;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import net.uberfoo.cpm.filesystem.CpmDisk;
import net.uberfoo.cpm.filesystem.DiskParameterBlock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
    private TreeTableColumn nameColumn;
    @FXML
    private TreeTableColumn sizeColumn;
    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));
    }

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
            var disk = new CpmDisk(Z80RB_DPB, channel);
            var root = new TreeItem<Object>(new CpmDiskTreeView(disk, "disk"));

            disk.getFilesStream()
                    .map(f -> new CpmFileTreeView(f))
                    .map(f -> new TreeItem<Object>(f))
                    .forEach(f -> root.getChildren().add(f));

            fileTree.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
