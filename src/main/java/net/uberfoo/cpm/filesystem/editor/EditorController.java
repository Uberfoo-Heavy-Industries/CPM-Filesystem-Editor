package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.uberfoo.cpm.filesystem.CpmDisk;
import net.uberfoo.cpm.filesystem.DiskParameterBlock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.BitSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import static net.uberfoo.cpm.filesystem.DiskParameterBlock.createSkewTab;

public class EditorController {

    public static final DiskParameterBlock Z80RB_DPB = new DiskParameterBlock(
            512,
            128,
            5,
            31,
            1,
            2047,
            511,
            240,
            0,
            0,
            0,
            new int[0]
    );

    public static final DiskParameterBlock OSBORNE_1_DPB = new DiskParameterBlock(
            256,
            20,
            4,
            15,
            1,
            45,
            63,
            0x80,
            0x00,
            0,
            3,
            createSkewTab(2, 10)
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

    @FXML
    private MenuItem treeContextMenuExportItem;

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

        treeContextMenuExportItem.visibleProperty()
                .bind(fileTree.getFocusModel().focusedItemProperty()
                        .<Boolean>map(x -> (((TreeItem) x).getValue() instanceof ExportableItem))
                        .orElse(true));

        fileTree.setRowFactory(view -> {
            var row = new TreeTableRow();
            row.setOnDragOver(e -> {
                var dragBoard = e.getDragboard();
                if (dragBoard.hasFiles()
                        && row.getTreeItem() != null
                        && row.getTreeItem().getValue() instanceof CpmDiskTreeView) {
                    e.acceptTransferModes(TransferMode.COPY);
                }
                e.consume();
            });
            row.setOnDragDropped(e -> {
                var dragBoard = e.getDragboard();
                if (dragBoard.hasFiles()&& row.getTreeItem() != null
                        && row.getTreeItem().getValue() instanceof CpmDiskTreeView diskTreeView) {
                    var disk = diskTreeView.getDisk();
                    copyFilesTo(row.getTreeItem(), dragBoard.getFiles());
                }
            });
            return row;
        });

        fileTree.setOnDragOver(e -> {
            var db = e.getDragboard();
            if (db.hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

    }

    @FXML
    protected void onFileMenuExitClick() {
        Platform.exit();
    }

    @FXML
    protected void onFileMenuOpenClick() {
        Stage stage = new Stage();

        var selectDpbDialog = new SelectDpbDialog(stage,
                List.of(new DiskParameterBlockView("Z80 Retro Badge", Z80RB_DPB),
                        new DiskParameterBlockView("Osborne 1", OSBORNE_1_DPB)));
        var result = selectDpbDialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.initialDirectoryProperty()
                .setValue(Path.of(preferences.get("LAST_PATH", System.getProperty("user.home"))).toFile());
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file != null) preferences.put("LAST_PATH", file.getParentFile().getPath());
        try {
            var channel = new RandomAccessFile(file, "rw").getChannel();
            var disk = new CpmDisk(result.get().toDiskParameterBlock(), channel);
            var diskRoot = new TreeItem<CpmItemTreeView>(new CpmDiskTreeView(disk, file.getName(), channel));

            refreshDisk(diskRoot);

            root.getChildren().add(diskRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void refreshDisk(TreeItem<CpmItemTreeView> diskRoot) {
        ((CpmDiskTreeView)diskRoot.getValue()).getDisk().getFilesStream()
                .map(f -> new CpmFileTreeView(f, (CpmDiskTreeView) diskRoot.getValue()))
                .map(f -> new TreeItem<CpmItemTreeView>(f))
                .forEach(addTreeChild(diskRoot));
    }

    private static void copyFilesTo(TreeItem<CpmItemTreeView> diskRoot, List<File> files) {
        var diskView = ((CpmDiskTreeView)diskRoot.getValue());
        var disk = diskView.getDisk();
        FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("copy-file-view.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Copy file");
        try {
            stage.setScene(new Scene(fxmlLoader.load(),310, 143));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        var controller = (CopyFileController)fxmlLoader.getController();
        // TODO: Support copying one file for now
        controller.setNormalizedFilename(files.get(0).getName());
        stage.showAndWait();

        var filename = controller.getFilename();
        var userNum = controller.getUserNumber();

        // TODO: Support copying one file for now
        try {
            var channel = FileChannel.open(files.get(0).toPath(), StandardOpenOption.READ);
            disk.createFile(filename, userNum, new BitSet(11),
                    channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
            disk.refresh();
            diskRoot.getChildren().clear();
            refreshDisk(diskRoot);
        } catch (FileAlreadyExistsException e) {
            fileExistsAlert(e);
        } catch (IOException e) {
            fileErrorAlert(e);
        }
    }

    private static void fileErrorAlert(IOException e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "A file error occurred!\n" + e.getMessage());
        errDialog.showAndWait();
    }

    private static void fileExistsAlert(FileAlreadyExistsException e) {
        var errDialog = new Alert(Alert.AlertType.ERROR, "File already exists!\n" + e.getMessage());
        errDialog.showAndWait();
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
    protected void onFileMenuCloseClick() {
        try {
            if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof CpmDiskTreeView diskView) {
                diskView.getChannel().close();
                root.getChildren().remove(fileTree.getFocusModel().getFocusedItem());
            }
        } catch (IOException e) {
            fileErrorAlert(e);
        }
    }

    @FXML
    protected void onContextMenuDeleteClick() {
        try {
            if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof DeletableItem item) {
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
        } catch (IOException e) {
            fileErrorAlert(e);
        }
    }

    @FXML
    protected void onContextMenuExportClick() throws IOException {
        if (((TreeItem)fileTree.getFocusModel().getFocusedItem()).getValue() instanceof ExportableItem item) {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Export File");
            fileChooser.initialDirectoryProperty()
                    .setValue(Path.of(preferences.get("LAST_EXPORT_PATH", System.getProperty("user.home"))).toFile());
            File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());

            if (file != null) preferences.put("LAST_EXPORT_PATH", file.getParentFile().getPath());
            else return;

            if (file.exists()) {
                Files.delete(file.toPath());
            }

            Files.write(Files.createFile(file.toPath()), item.retrieveFileContents().array(), StandardOpenOption.APPEND);

        }
    }
}
