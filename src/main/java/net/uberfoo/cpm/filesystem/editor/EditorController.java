package net.uberfoo.cpm.filesystem.editor;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.uberfoo.cpm.filesystem.CpmDisk;
import net.uberfoo.cpm.filesystem.PartitionedDisk;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class EditorController {

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
    private MenuItem openDiskMenuItem;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private MenuItem treeContextMenuDeleteItem;

    @FXML
    private MenuItem treeContextMenuExportItem;

    @FXML
    private MenuItem treeContextMenuImportItem;

    @FXML
    private MenuItem exportPartDiskMenuItem;

    @FXML
    private MenuItem flashDiskMenuItem;

    private TreeItem<? extends CpmItemTreeView> root;

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

        treeContextMenuImportItem.visibleProperty()
                .bind(fileTree.getFocusModel().focusedItemProperty()
                        .<Boolean>map(x -> (((TreeItem) x).getValue() instanceof AcceptsImports))
                        .orElse(true));

        exportPartDiskMenuItem.disableProperty()
                .bind(fileTree.getFocusModel().focusedItemProperty()
                        .<Boolean>map(x -> (((TreeItem) x).getValue() instanceof PartitionTreeView))
                        .orElse(true));

        flashDiskMenuItem.disableProperty()
                .bind(fileTree.getFocusModel().focusedItemProperty()
                        .<Boolean>map(x -> (((TreeItem) x).getValue() instanceof PartitionTreeView))
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
                if (dragBoard.hasFiles() && row.getTreeItem() != null
                        && row.getTreeItem().getValue() instanceof CpmDiskTreeView diskTreeItem) {
                    copyFilesToCpm(diskTreeItem, dragBoard.getFiles(), "Copy");
                    refreshDisk(row.getTreeItem());
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
        var selectDpbDialog = new SelectDpbDialog(rootPane.getScene().getWindow(),
                List.of(new DiskParameterBlockView("Z80 Retro Badge", PartitionTables.Z80RB_DPB),
                        new DiskParameterBlockView("Osborne 1", PartitionTables.OSBORNE_1_DPB)));
        WindowUtil.positionDialog(rootPane.getScene().getWindow(), selectDpbDialog, selectDpbDialog.getWidth(), selectDpbDialog.getHeight());
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

        try (var channel = new RandomAccessFile(file, "rw").getChannel()) {
            var disk = new CpmDisk(result.get().toDiskParameterBlock(), channel);
            var diskRoot = new TreeItem<>(new CpmDiskTreeView(disk, file.getName(), channel));

            refreshDisk(diskRoot);

            root.getChildren().add((TreeItem)diskRoot);
        } catch (IOException e) {
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
        }
    }

    @FXML
    protected void onFileMenuOpenPartDiskClick() {
        Optional<DiskPartitionsView> result = getDiskPartitionsView();
        if (result.isEmpty()) {
            return;
        }

        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Partitioned Disk");
        fileChooser.initialDirectoryProperty()
                .setValue(Path.of(preferences.get("LAST_PART_DISK_PATH", System.getProperty("user.home"))).toFile());
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file != null) preferences.put("LAST_PART_DISK_PATH", file.getParentFile().getPath());
        try (var channel = new RandomAccessFile(file, "rw").getChannel()) {
            var disk = new PartitionedDisk(channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size()), result.get().getPartitionTable());
            openPartitionDisk(disk, file.getName(), channel);
        } catch (IOException | ClassNotFoundException e) {
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
        }
    }

    private Optional<DiskPartitionsView> getDiskPartitionsView() {
        var selectPartitionsDialog = new SelectPartTableDialog(rootPane.getScene().getWindow(),
                List.of(new DiskPartitionsView("Z80 Retro Board", PartitionTables.Z80RB_TABLE)));

        WindowUtil.positionDialog(rootPane.getScene().getWindow(), selectPartitionsDialog, selectPartitionsDialog.getWidth(), selectPartitionsDialog.getHeight());
        var result = selectPartitionsDialog.showAndWait();
        return result;
    }

    private void openPartitionDisk(PartitionedDisk disk, String name, Closeable channel) {
        var diskRoot = new TreeItem<CpmItemTreeView>(new PartitionedDiskView(name, disk, channel));
        refreshPartitionedDisk(diskRoot);
        root.getChildren().add((TreeItem)diskRoot);
    }

    @FXML
    protected void onFileMenuCloseClick() {
        try {
            if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof ClosableItem diskView) {
                diskView.close();
                root.getChildren().remove(fileTree.getFocusModel().getFocusedItem());
            }
        } catch (IOException e) {
            AlertDialogs.fileErrorAlert(rootPane.getScene().getWindow(), e);
        }
    }

    @FXML
    protected void onContextMenuDeleteClick() {
        try {
            if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof DeletableItem item) {
                item.delete();
                var parent = ((TreeItem<? extends CpmItemTreeView>) fileTree.getFocusModel().getFocusedItem())
                        .parentProperty().get()
                        .parentProperty().get();
                refreshFromDelete((CpmFileTreeView)item, parent);
            }
        } catch (IOException e) {
            AlertDialogs.fileErrorAlert(rootPane.getScene().getWindow(), e);
        } catch (NoSuchElementException e) {
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
        }
    }

    @FXML
    protected void onContextMenuExportClick() {
        try {
            if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof ExportableItem item) {
                var fileChooser = new FileChooser();
                fileChooser.setTitle("Export File");
                fileChooser.setInitialFileName(item.getName());
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
        } catch (FileAlreadyExistsException e) {
            AlertDialogs.fileExistsAlert(rootPane.getScene().getWindow(), e);
        } catch (IOException e) {
            AlertDialogs.fileErrorAlert(rootPane.getScene().getWindow(), e);
        }
    }

    @FXML
    protected void onContextMenuImportClick() {
        if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof AcceptsImports item) {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Import File");
            fileChooser.initialDirectoryProperty()
                    .setValue(Path.of(preferences.get("LAST_IMPORT_PATH", System.getProperty("user.home"))).toFile());

            File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
            if (file != null) preferences.put("LAST_IMPORT_PATH", file.getParentFile().getPath());
            else return;

            copyFilesToCpm((DiskItem) item, List.of(file), "Import");
            var treeItem = ((TreeItem<?>) fileTree.getFocusModel().getFocusedItem());
            refreshDisk(treeItem);
        }
    }

    @FXML
    protected void onFileMenuOpenDiskClick() {
        try {
            Optional<DiskPartitionsView> tableResult = getDiskPartitionsView();
            if (tableResult.isEmpty()) {
                return;
            }

            PlatformDiskFactory.OSDiskEntry selection = getOsDiskEntry();
            if (selection == null) return;
            var addr = selection.address();

            ByteBuffer bb = ByteBuffer.allocate((int)selection.size());

            var progressDialog = new ProgressDialog(rootPane.getScene().getWindow(), selection.size(), "Loading disk...");
            WindowUtil.positionDialog(rootPane.getScene().getWindow(), progressDialog, progressDialog.getWidth(), progressDialog.getHeight());
            progressDialog.show();

            var disk = PlatformDiskFactory.getInstance().getDisk(selection);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try (var reader = disk.openReader()) {
                        long sum = 0;
                        var size = tableResult.get().getPartitionTable().diskSize();
                        updateProgress(sum, size);
                        var block = new byte[(int)Math.divideExact(size, 16)];
                        while (sum < size) {
                            reader.read(block);
                            sum += block.length;
                            bb.put(block);
                            updateProgress(sum, size);
                            if (isCancelled()) {
                                return null;
                            }
                        }
                        PartitionedDisk partitionedDisk = new PartitionedDisk(bb.rewind(), tableResult.orElseThrow().getPartitionTable());
                        openPartitionDisk(partitionedDisk, addr, () -> {});
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
                    } finally {
                        Platform.runLater(progressDialog::close);
                    }
                    return null;
                }

            };

            progressDialog.progressProperty().bind(task.progressProperty());
            progressDialog.setOnCloseRequest((x) -> task.cancel());

            new Thread(task).start();

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
        }
    }

    @FXML
    protected void onFileMenuExportPartDiskClick() {
        try {
            if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof PartitionedDiskView item) {
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

                Files.write(Files.createFile(file.toPath()), item.partitionedDisk().createDisk().array(), StandardOpenOption.APPEND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
        }
    }

    @FXML
    protected void onFileMenuFlashDiskClick() {
        try {
            if (((TreeItem) fileTree.getFocusModel().getFocusedItem()).getValue() instanceof PartitionedDiskView item) {
                PlatformDiskFactory.OSDiskEntry selection = getOsDiskEntry();

                if (selection == null) return;

                var progressDialog = new ProgressDialog(rootPane.getScene().getWindow(), selection.size(), "Flashing disk...");
                WindowUtil.positionDialog(rootPane.getScene().getWindow(), progressDialog, progressDialog.getWidth(), progressDialog.getHeight());

                var disk = PlatformDiskFactory.getInstance().getDisk(selection);
                progressDialog.show();
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        long size = item.partitionedDisk().getDiskSize();
                        updateProgress(0, size);
                        try (var writer = disk.openWriter()) {
                            long sum = 0; //
                            var block = new byte[(int)Math.divideExact(size, 16)];
                            var buff = item.partitionedDisk().createDisk();
                            while (sum < size) {
                                buff.get(block);
                                writer.write(block);
                                sum += block.length;
                                updateProgress(sum, size);
                                if (isCancelled()) {
                                    return null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
                        } finally {
                            Platform.runLater(progressDialog::close);
                        }
                        return null;
                    }

                };
                progressDialog.progressProperty().bind(task.progressProperty());
                progressDialog.setOnCloseRequest((x) -> task.cancel());
                new Thread(task).start();
            }


        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
        }
    }

    private PlatformDiskFactory.OSDiskEntry getOsDiskEntry() throws Exception {
        var list = PlatformDiskFactory.getInstance().getDiskList().stream()
                .filter(PlatformDiskFactory.OSDiskEntry::removable)
                .filter(disk -> disk.size() < 4L * 1024 * 1024 * 1024)
                .toList();
        var selectDiskDialog = new SelectDiskDialog(rootPane.getScene().getWindow(), list);
        WindowUtil.positionDialog(rootPane.getScene().getWindow(), selectDiskDialog, selectDiskDialog.getWidth(), selectDiskDialog.getHeight());
        var result = selectDiskDialog.showAndWait();

        return result.orElse(null);
    }

    private void refreshDisk(TreeItem<?> diskRoot) {
        var expanded = diskRoot.isExpanded();
        diskRoot.getChildren().clear();
        try {
            ((DiskItem)diskRoot.getValue()).disk().refresh();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), ioe);
            return;
        }
        ((DiskItem) diskRoot.getValue()).disk().getFilesStream()
                .map(f -> new CpmFileTreeView(f, (DiskItem) diskRoot.getValue()))
                .map(TreeItem::new)
                .forEach(addTreeChild(diskRoot));
        diskRoot.expandedProperty().set(expanded);
    }

    private void refreshPartitionedDisk(TreeItem<CpmItemTreeView> diskRoot) {
        var expanded = diskRoot.isExpanded();
        diskRoot.getChildren().clear();
        ((PartitionedDiskView) diskRoot.getValue()).partitionedDisk().getDisks().stream()
                .map(d -> new PartitionTreeView(d.disk(), d.label()))
                .map(TreeItem<CpmItemTreeView>::new)
                .forEach(t -> {
                    refreshDisk(t);
                    diskRoot.getChildren().add(t);
                });
        diskRoot.expandedProperty().set(expanded);
    }

    private void copyFile(AcceptsImports disk, File file, String filename, int userNum) {
        try (var channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            disk.importFile(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()), filename, userNum);
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
            AlertDialogs.fileExistsAlert(rootPane.getScene().getWindow(), e);
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialogs.fileErrorAlert(rootPane.getScene().getWindow(), e);
        }
    }

    private static Consumer<TreeItem<CpmFileTreeView>> addTreeChild(TreeItem<?> diskRoot) {
        return f -> {
            var fileView = f.getValue();
            var stat = fileView.file().getStat();
            var userGroupRoot = (diskRoot.getChildren()).stream()
                    .filter(x -> ((CpmUserGroupView) x.getValue()).userNumber() == stat)
                    .findFirst()
                    .orElse(new TreeItem(new CpmUserGroupView(stat)));
            if (!diskRoot.getChildren().contains(userGroupRoot)) diskRoot.getChildren().add(userGroupRoot);
            userGroupRoot.getChildren().add(f);
        };
    }

    private void refreshFromDelete(CpmFileTreeView file, TreeItem<? extends CpmItemTreeView> parentTreeItem) throws IOException {
        parentTreeItem.getChildren().clear();
        file.parent().disk().refresh();
        refreshDisk(parentTreeItem);
    }

    private void copyFilesToCpm(DiskItem diskView, List<File> files, String name) {
        CopyFileController controller = getCopyFileController(files, name);

        var filename = controller.getFilename();
        var userNum = controller.getUserNumber();

        // TODO: Support copying one file for now
        copyFile(diskView, files.get(0), filename, userNum);
    }

    private CopyFileController getCopyFileController(List<File> files, String name) {
        FXMLLoader fxmlLoader = new FXMLLoader(EditorApp.class.getResource("copy-file-view.fxml"));
        Stage stage = loadCopyFileView(name, fxmlLoader);

        var controller = (CopyFileController) fxmlLoader.getController();
        // TODO: Support copying one file for now
        controller.setNormalizedFilename(files.get(0).getName());
        controller.setCopyButtonText(name);
        if (stage != null) {
            stage.showAndWait();
        }
        return controller;
    }

    private Stage loadCopyFileView(String title, FXMLLoader fxmlLoader) {
        Stage stage = new Stage();
        stage.setTitle(title + " file");
        stage.initOwner(rootPane.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        try {
            WindowUtil.positionDialog(rootPane.getScene().getWindow(), stage, 310, 143);
            stage.setScene(new Scene(fxmlLoader.load(), 310, 143));
        } catch (IOException e) {
            e.printStackTrace();;
            AlertDialogs.unexpectedAlert(rootPane.getScene().getWindow(), e);
            return null;
        }
        return stage;
    }

}
