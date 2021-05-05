package ntr.datacloud.client.controller;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import ntr.datacloud.client.DataCloudClient;
import ntr.datacloud.client.model.ClientProperties;
import ntr.datacloud.client.stage.AuthStage;
import ntr.datacloud.client.stage.MainStage;
import ntr.datacloud.common.filemanager.FileEntity;
import ntr.datacloud.client.model.NettyNetwork;
import ntr.datacloud.common.filemanager.FileManager;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.*;
import ntr.datacloud.common.messages.service.LogoutMessage;

import javax.swing.plaf.metal.MetalIconFactory;


@Log4j
public class MainAppController implements Initializable {


    private NettyNetwork network;
    private FileManager fileManager;
    private ClientProperties properties;
    private Image folderIcon;
    private Image fileIcon;

    private Callback iconValueFactory = new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
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


            } else {
                //todo notify user
            }


        });

    }


    public void upload(ActionEvent event) throws IOException, IllegalAccessException {


        FileEntity uploadedFile = (FileEntity) clientFileList.getSelectionModel().getSelectedItem();
        if (uploadedFile == null) return;

        Platform.runLater(() -> {

            try {
                network.sendMsg(UploadMessage.builder()
                        .fileName(uploadedFile.getName())
                        .content(fileManager.fileToBytes(uploadedFile.getName()))
                        .build()
                );
            } catch (IllegalAccessException | IOException e) {
                e.printStackTrace();
                //todo error message
            }

            UploadMessage message = (UploadMessage) network.readMessage();

            handleResponseMessage(message);
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

            DownloadMessage message = (DownloadMessage) network.readMessage();

            try {

                if (message.getStatus() == DataMessageStatus.OK) {
                    fileManager.bytesToFile(
                            message.getContent(),
                            message.getFileName()
                    );
                    clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));
                }
            } catch (Exception e) {
                //todo handle exception
                e.printStackTrace();
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
            }
        });
    }

    public void createDirOnClient(ActionEvent event) {

        Dialog dialog = new Dialog(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                Dialog.Type.TEXT_INPUT,
                "Enter new folder name",
                "Folder: "
        );

        String newDir = dialog.getText();

        Platform.runLater(() -> {
            try {
                fileManager.createDir(newDir);
                clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
            } catch (Exception e) {
                //todo handle exception;
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
                "New name: "
        );

        String newName = dialog.getText();

        Platform.runLater(() -> {
            try {
                fileManager.rename(file.getName(), newName);
                clientFileList.setItems(FXCollections.observableList(fileManager.getFiles()));

            } catch (Exception e) {
                //todo handle exception
            }
        });
    }

    public void goToParentDirOnServer(ActionEvent event) {
        Platform.runLater(() -> {

        });
    }

    public void createDirOnServer(ActionEvent event) {

        Dialog dialog = new Dialog(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                Dialog.Type.TEXT_INPUT,
                "Enter new folder name",
                "New folder name: "
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
                "New name: "
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

    private void handleResponseMessage(DataMessage message) {
        if (message.getStatus() == DataMessageStatus.OK) {
            serverFileList.setItems(FXCollections.observableList(message.getFiles()));
        } else {

        }
    }


    public void onKeyPressed(KeyEvent keyEvent) {
        //todo delete error
    }

    public void onLogOut(MouseEvent event) {
        Platform.runLater(() -> {

            network.sendMsg(LogoutMessage.builder()
                    .build());

            LogoutMessage message = (LogoutMessage) network.readMessage();


        });


        try {

            AuthStage.getStage().show();
            MainStage.getStage().hide();
        } catch (Exception e) {
            log.error("Error during changing window: ", e);
        }

    }


}



