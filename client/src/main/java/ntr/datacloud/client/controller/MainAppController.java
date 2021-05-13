package ntr.datacloud.client.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.client.model.ClientProperties;
import ntr.datacloud.client.stage.AuthStage;
import ntr.datacloud.client.stage.Dialog;
import ntr.datacloud.client.stage.MainStage;
import ntr.datacloud.common.filemanager.FileEntity;
import ntr.datacloud.client.model.NettyNetwork;
import ntr.datacloud.common.filemanager.FileManager;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.data.*;
import ntr.datacloud.common.messages.service.LogoutMessage;
import ntr.datacloud.common.messages.service.ServiceMessageStatus;


@Log4j
public class MainAppController implements Initializable {


    private NettyNetwork network;
    private FileManager fileManager;
    private ClientProperties properties;
    private Image folderIcon;
    private Image fileIcon;


    private final Callback iconValueFactory = new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
        @Override
        public ObservableValue call(TableColumn.CellDataFeatures cellDataFeatures) {

            FileEntity file = (FileEntity) cellDataFeatures.getValue();
            return new SimpleObjectProperty<>(
                    new ImageView(
                            file.getType() == FileEntity.FileType.REGULAR_FILE ? fileIcon : folderIcon
                    ));
        }
    };

    @FXML
    private VBox primaryPane;

    @FXML
    private TableView serverFileList;
    @FXML
    private TableColumn serverFileIconCol;
    @FXML
    private TableColumn serverFileListNameCol;
    @FXML
    private TableColumn serverFileListSizeCol;

    @FXML
    private TableView clientFileList;
    @FXML
    private TableColumn clientFileIconCol;
    @FXML
    private TableColumn clientFileListNameCol;
    @FXML
    private TableColumn clientFileListSizeCol;


    @FXML
    private ImageView btnLogout;


    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        network = NettyNetwork.getInstance();
        properties = ClientProperties.getInstance();
        fileManager = new FileManagerImpl(properties.getRootDir().toString());

        folderIcon = new Image("/images/folder.png");
        fileIcon = new Image("/images/file.png");

        // get all files from client root directory
        Platform.runLater(() -> {

            clientFileList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            ObservableList<FileEntity> clientFiles = null;
            try {
                clientFiles = FXCollections.observableList(fileManager.getFiles());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            clientFileIconCol.setStyle("-fx-alignment: CENTER;");
            clientFileIconCol.setCellValueFactory(iconValueFactory);


            clientFileListNameCol.setCellValueFactory(new PropertyValueFactory<FileEntity, String>("name"));


            clientFileListSizeCol.setCellValueFactory(new PropertyValueFactory<FileEntity, String>("size"));

            clientFileList.setItems(clientFiles);
            clientFileList.setRowFactory(tv -> {
                TableRow<FileEntity> row = new TableRow<>();
                row.setOnMouseClicked(mouseEvent -> {
                    if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        FileEntity clickedFile = row.getItem();
                        if (clickedFile.getType() == FileEntity.FileType.DIRECTORY) {
                            FileEntity file = row.getItem();

                            Platform.runLater(() -> {
                                try {
                                    fileManager.changeDir(file.getName());
                                    clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (NoSuchFileException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                });
                return row;
            });
        });

        //get all files from server root directory
        Platform.runLater(() -> {

            serverFileList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            network.sendMsg(GetFilesMessage.builder()
                    .build());


            GetFilesMessage message = (GetFilesMessage) network.readMessage();


            if (message.getStatus() == DataMessageStatus.OK) {
                ObservableList<FileEntity> serverFiles = FXCollections.observableList(
                        message.getFiles()
                );

                serverFileIconCol.setStyle("-fx-alignment: CENTER;");
                serverFileIconCol.setCellValueFactory(iconValueFactory);

                serverFileListNameCol.setCellValueFactory(
                        new PropertyValueFactory<FileEntity, String>("name")
                );

                serverFileListSizeCol.setCellValueFactory(
                        new PropertyValueFactory<FileEntity, String>("size")
                );

                serverFileList.setItems(serverFiles);

                serverFileList.setRowFactory(tv -> {
                    TableRow<FileEntity> row = new TableRow<>();
                    row.setOnMouseClicked(mouseEvent -> {
                        if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                            FileEntity clickedFile = row.getItem();
                            if (clickedFile.getType() == FileEntity.FileType.DIRECTORY) {
                                FileEntity file = row.getItem();

                                Platform.runLater(() -> {
                                    network.sendMsg(GetFilesMessage.builder()
                                            .relDir(clickedFile.getName())
                                            .build());

                                    GetFilesMessage filesMessage = (GetFilesMessage) network.readMessage();

                                    handleResponseMessage(filesMessage);

                                });
                            }
                        }
                    });
                    return row;
                });


            } else {
                notifyAlert(message.getStatus().getStatusText());
            }
        });
    }

    //menu controls

    public void menuLogout(ActionEvent event) {
        logout();
    }

    public void menuClose(ActionEvent event) {
        exit();
    }

    public void showAbout(ActionEvent event) {
        new Dialog((Stage) primaryPane.getScene().getWindow(),
                Dialog.Type.INFORMATION,
                "DataCloud\nby Nabiyev Timur\n2021");
    }

    // Header controls

    public void onLogOut(MouseEvent event) {
        logout();
    }

    // Client controls

    public void upload(ActionEvent event) throws IOException, IllegalAccessException {

        FileEntity uploadedFile = (FileEntity) clientFileList.getSelectionModel().getSelectedItem();
        if (uploadedFile == null) return;

        Platform.runLater(() -> {
            try {
                List<byte[]> bytes = fileManager.fileToBytes(
                        uploadedFile.getName(),
                        properties.getMaxFileFrame()
                );

                List<UploadMessage> messages = fileManager.fileToBytes(uploadedFile.getName(), properties.getMaxFileFrame())
                        .stream().map(b -> UploadMessage.builder()
                                .fileName(uploadedFile.getName())
                                .content(b)
                                .build())
                        .collect(Collectors.toList());
                messages.get(messages.size() - 1).setLast(true);

                for (UploadMessage wMessage : messages) {
                    network.sendMsg(wMessage);
                    UploadMessage dMessage = (UploadMessage) network.readMessage();

                    if (dMessage.getStatus() != DataMessageStatus.OK) {
                        notifyAlert(dMessage.getStatus().getStatusText());
                        throw new IOException(dMessage.getErrorText());
                    }
                    if (dMessage.isLast()) {
                        handleResponseMessage(dMessage);
                    }
                }
            } catch (IllegalAccessException | IOException e) {
                log.warn(e);
            }
        });
    }

    public void download(ActionEvent event) {


        FileEntity downloadedFile = (FileEntity) serverFileList.getSelectionModel().getSelectedItem();
        if (downloadedFile == null) return;

        Platform.runLater(() -> {

            network.sendMsg(DownloadMessage
                    .builder()
                    .fileName(downloadedFile.getName())
                    .build());

            try {
                while (true) {
                    DownloadMessage message = (DownloadMessage) network.readMessage();

                    if (message.getStatus() != DataMessageStatus.OK) {
                        log.warn(message.getErrorText());
                        notifyAlert(message.getStatus().getStatusText());
                        return;
                    }

                    // Check if file exists
                    if (fileManager.fileExists(message.getFileName()) && !fileManager.isFileTransfer()) {
                        notifyAlert(String.format(
                                "The file %s already exists",
                                message.getFileName()
                        ));
                        return;
                    }

                    fileManager.setFileTransfer(true);
                    fileManager.bytesToFile(message.getContent(), message.getFileName());
                    if (message.isLast()) {
                        fileManager.setFileTransfer(false);
                        break;
                    }
                }
            } catch (IllegalAccessException | IOException e) {
                log.warn(e);

            }

            // update tableview
            try {
                clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));
            } catch (IllegalAccessException | IOException e) {
                log.warn(e);
            }
        });
    }


    public void goToParentDirOnClient(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                if (fileManager.changeDirToParent()) {
                    clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));
                }
            } catch (IllegalAccessException | IOException e) {
                log.warn(e);
                notifyAlert("Cannot change directory");
            }
        });
    }

    public void createDirOnClient(ActionEvent event) {

        Dialog dialog = new Dialog(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                Dialog.Type.TEXT_INPUT,
                "Enter new folder name",
                ""
        );

        String newDir = dialog.getText();

        Platform.runLater(() -> {
            try {
                if (fileManager.fileExists(newDir)) {
                    notifyAlert(DataMessageStatus.DIRECTORY_EXISTS.getStatusText());
                    return;
                }

                fileManager.createDir(newDir);
                clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));

            } catch (IllegalAccessException | IOException e) {
                log.warn(e);
                notifyAlert("Cannot create directory");
            }
        });
    }

    public void deleteFileFromClient(ActionEvent event) {
        Platform.runLater(() -> {

            FileEntity fileToDelete = (FileEntity) clientFileList.getSelectionModel().getSelectedItem();
            if (fileToDelete == null) return;
            try {
                fileManager.delete(fileToDelete.getName());
                clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));
            } catch (IllegalAccessException | IOException e) {
                log.warn(e);
                notifyAlert("Cannot delete file " + fileToDelete.getName());
            }
        });
    }

    public void RenameFileOnClient(ActionEvent event) {

        FileEntity file = (FileEntity) clientFileList.getSelectionModel().getSelectedItem();
        if (file == null) return;


        Dialog dialog = new Dialog(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                Dialog.Type.TEXT_INPUT,
                "Enter new file or folder name",
                file.getName()
        );

        String newName = dialog.getText();

        Platform.runLater(() -> {
            try {
                fileManager.rename(file.getName(), newName);
                clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));

            } catch (IllegalAccessException | IOException e) {
                log.warn(e);
                notifyAlert("Cannot rename file o directory " + file.getName());
            }
        });
    }

    // Server controls

    public void goToParentDirOnServer(ActionEvent event) {
        Platform.runLater(() -> {
            network.sendMsg(ChangeDirMessage.builder()
                    .relPath("..")
                    .build());

            ChangeDirMessage message = (ChangeDirMessage) network.readMessage();

            handleResponseMessage(message);
        });
    }

    public void createDirOnServer(ActionEvent event) {

        Dialog dialog = new Dialog(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                Dialog.Type.TEXT_INPUT,
                "Enter new folder name",
                ""
        );

        String newFolderName = dialog.getText();


        Platform.runLater(() -> {
            network.sendMsg(CreateDirMessage.builder()
                    .newDir(newFolderName)
                    .build());

            CreateDirMessage message = (CreateDirMessage) network.readMessage();

            handleResponseMessage(message);
        });
    }

    public void deleteFileFromServer(ActionEvent event) {

        FileEntity file = (FileEntity) serverFileList.getSelectionModel().getSelectedItem();
        if (file == null) return;

        Platform.runLater(() -> {
            network.sendMsg(DeleteMessage.builder()
                    .fileToDelete(file.getName())
                    .build());

            DeleteMessage message = (DeleteMessage) network.readMessage();

            handleResponseMessage(message);
        });
    }

    public void renameFileOnServer(ActionEvent event) {

        FileEntity file = (FileEntity) serverFileList.getSelectionModel().getSelectedItem();
        if (file == null) return;

        Dialog dialog = new Dialog(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                Dialog.Type.TEXT_INPUT,
                "Enter new file or folder name",
                file.getName()
        );

        String newName = dialog.getText();

        Platform.runLater(() -> {

            network.sendMsg(RenameMessage
                    .builder()
                    .oldName(file.getName())
                    .newName(newName)
                    .build());

            RenameMessage message = (RenameMessage) network.readMessage();

            handleResponseMessage(message);
        });
    }

    // Helpers

    private void handleResponseMessage(DataMessage message) {
        if (message.getStatus() == DataMessageStatus.OK) {
            serverFileList.setItems(FXCollections.observableList(message.getFiles()));
        } else {
            log.warn(message.getErrorText());
            notifyAlert(message.getStatus().getStatusText());
        }
    }

    private void logout() {
        Platform.runLater(() -> {
            network.sendMsg(LogoutMessage.builder()
                    .login(properties.getLogin())
                    .build());
            LogoutMessage message = (LogoutMessage) network.readMessage();
            AuthStage.getStage().show();
            MainStage.getStage().close();
            properties = null;
        });


    }

    private void exit() {
        Platform.runLater(() -> {
            network.sendMsg(LogoutMessage.builder()
                    .login(properties.getLogin())
                    .build());
            LogoutMessage message = (LogoutMessage) network.readMessage();

            if (message.getStatus() != ServiceMessageStatus.OK) {
                log.warn("Abnormal program termination.");
            }
            MainStage.getStage().close();
            MainStage.getStage().close();
            network.terminate();
            properties = null;
            Platform.exit();
            System.exit(0);
        });
    }

    private void notifyAlert(String contentText) {
        new Dialog(
                (Stage) primaryPane.getScene().getWindow(),
                Dialog.Type.ALERT,
                contentText
        );
    }
}

