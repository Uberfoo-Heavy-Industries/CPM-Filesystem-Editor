package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
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
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

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

    private final Preferences preferences = Preferences.userNodeForPackage(EditorController.class);

    @FXML
    private TreeTableView fileTree;
    @FXML
    private TreeTableColumn<Object, Object> nameColumn;
    @FXML
    private TreeTableColumn sizeColumn;
    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private MenuItem treeContextMenuDeleteItem;

    private TreeItem<CpmItemTreeView> root;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));

        root = new TreeItem<>();
        fileTree.setRoot(root);

        closeMenuItem.disableProperty()
                .bind(fileTree.getFocusModel().focusedItemProperty()
                        .<Boolean>map(x -> !(((TreeItem) x).getValue() instanceof ClosableItem))
                        .orElse(true));

        treeContextMenuDeleteItem.visibleProperty()
                .bind(fileTree.getFocusModel().focusedItemProperty()
                        .<Boolean>map(x -> (((TreeItem) x).getValue() instanceof DeletableItem))
                        .orElse(true));

    }

    @FXML
    protected void onFileMenuExitClick() {
        Platform.exit();
    }

    @FXML
    protected void onFileMenuOpenClick() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.initialDirectoryProperty()
                .setValue(Path.of(preferences.get("LAST_PATH", System.getProperty("user.home"))).toFile());
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file != null) preferences.put("LAST_PATH", file.getParentFile().getPath());
        try {
            var channel = new RandomAccessFile(file, "rw").getChannel();
            var disk = new CpmDisk(Z80RB_DPB, channel);
            var diskRoot = new TreeItem<CpmItemTreeView>(new CpmDiskTreeView(disk, file.getName(), channel));

            refreshDisk(diskRoot);

            root.getChildren().add(diskRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void refreshDisk(TreeItem<CpmItemTreeView> diskRoot) {
        ((CpmDiskTreeView)diskRoot.getValue()).getDisk().getFilesStream()
                .map(f -> new CpmFileTreeView(f, (CpmDiskTreeView) diskRoot.getValue()))
                .map(f -> new TreeItem<CpmItemTreeView>(f))
                .forEach(addTreeChild(diskRoot));
    }

    private static Consumer<TreeItem<CpmItemTreeView>> addTreeChild(TreeItem<CpmItemTreeView> diskRoot) {
        return f -> {
            var fileView = (CpmFileTreeView)f.getValue();
            var stat = fileView.file().getStat();
            var userGroupRoot = (diskRoot.getChildren()).stream()
                    .filter(x -> ((CpmUserGroupView)x.getValue()).getUserNumber() == stat)
                    .findFirst()
                    .orElse(new TreeItem<>((CpmItemTreeView) new CpmUserGroupView(stat)));
            if (!diskRoot.getChildren().contains(userGroupRoot)) diskRoot.getChildren().add(userGroupRoot);
            userGroupRoot.getChildren().add(f);
        };
    }

    @FXML
    protected void onFileMenuCloseClick() throws IOException {
         if (((TreeItem)fileTree.getFocusModel().getFocusedItem()).getValue() instanceof CpmDiskTreeView diskView) {
             diskView.getChannel().close();
             root.getChildren().remove(fileTree.getFocusModel().getFocusedItem());
         }
    }

    @FXML
    protected void onContextMenuDeleteClick() throws IOException {
        if (((TreeItem)fileTree.getFocusModel().getFocusedItem()).getValue() instanceof DeletableItem item) {
            item.delete();
            if (item instanceof CpmFileTreeView file) {
                var parentTreeItem = root.getChildren()
                        .stream()
                        .filter(x -> x.getValue() == file.parent())
                        .findFirst().orElseThrow();
                parentTreeItem.getChildren().clear();
                file.parent().getDisk().refresh();
                refreshDisk(parentTreeItem);
            }
        }
    }
}
