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
    private TreeTableColumn nameColumn;
    @FXML
    private TreeTableColumn sizeColumn;
    @FXML
    private BorderPane rootPane;

    private TreeItem<CpmItemTreeView> root;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));
        root = new TreeItem<>();
        fileTree.setRoot(root);
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
            var diskRoot = new TreeItem<CpmItemTreeView>(new CpmDiskTreeView(disk, file.getName(), channel));

            disk.getFilesStream()
                    .map(f -> new CpmFileTreeView(f))
                    .map(f -> new TreeItem<CpmItemTreeView>(f))
                    .forEach(f -> diskRoot.getChildren().add(f));

            root.getChildren().add(diskRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onFileMenuCloseClick() throws IOException {
         if (((TreeItem)fileTree.getFocusModel().getFocusedItem()).getValue() instanceof CpmDiskTreeView diskView) {
             diskView.getChannel().close();
             root.getChildren().remove(fileTree.getFocusModel().getFocusedItem());
         }
    }
}
