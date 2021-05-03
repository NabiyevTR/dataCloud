package ntr.datacloud.client.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.client.model.ClientProperties;
import ntr.datacloud.common.filemanager.FileEntity;
import ntr.datacloud.client.model.NettyNetwork;
import ntr.datacloud.common.filemanager.FileManager;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.*;
import ntr.datacloud.common.messages.service.LogonMessage;

@Log4j
public class MainAppController implements Initializable {

    private NettyNetwork network;
    private FileManager fileManager;
    private ClientProperties properties;


    @FXML
    public TableView serverFileList;
    @FXML
    public TableColumn serverFileListNameCol;
    @FXML
    public TableColumn serverFileListSizeCol;


    @FXML
    private TableColumn clientFileListNameCol;
    @FXML
    private TableColumn clientFileListSizeCol;
    @FXML
    public TableView clientFileList;

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        network = NettyNetwork.getInstance();
        properties = ClientProperties.getInstance();
        fileManager = new FileManagerImpl(properties.getRootDir().toString());


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
            clientFileListNameCol.setCellValueFactory(
                    new PropertyValueFactory<FileEntity, String>("name")
            );
            clientFileListSizeCol.setCellValueFactory(
                    new PropertyValueFactory<FileEntity, String>("size")
            );
            clientFileList.setItems(clientFiles);

            clientFileList.setRowFactory(tv -> {
                TableRow<FileEntity> row = new TableRow<>();
                row.setOnMouseClicked(mouseEvent -> {
                    if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        FileEntity clickedFile = row.getItem();
                        if (clickedFile.getType() == FileEntity.FileType.DIRECTORY) {
                            //todo change dir
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
                    .login(properties.getLogin())
                    .password(properties.getPassword())
                    .build());


            Message message = network.readMessage();

            if (message instanceof GetFilesMessage) {

                GetFilesMessage getFilesMessage = (GetFilesMessage) message;
                if (getFilesMessage.getStatus() == DataMessageStatus.OK) {
                    ObservableList<FileEntity> serverFiles = FXCollections.observableList(
                            ((GetFilesMessage) message).getFiles()
                    );
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


            } else {
                //todo notify user
            }


        });


    }


    public void upload(ActionEvent event) throws IOException, IllegalAccessException {


        FileEntity uploadedFile = (FileEntity) clientFileList.getSelectionModel().getSelectedItem();
        if (uploadedFile == null) return;

        network.sendMsg(UploadMessage
                .builder()
                .login(properties.getLogin())
                .password(properties.getPassword())
                .currentDir(fileManager.getCurrentDir())
                .fileName(uploadedFile.getName())
                .content(fileManager.fileToBytes(uploadedFile.getName()))
                .build()
        );

        Platform.runLater(() -> {
            UploadMessage message = (UploadMessage) network.readMessage();
            if (message.getStatus() == DataMessageStatus.OK) {
                serverFileList.setItems(FXCollections.observableList(message.getFiles()));
            }
        });


    }

    public void download(ActionEvent event) {

        Platform.runLater(() -> {

            FileEntity downloadedFile = (FileEntity) serverFileList.getSelectionModel().getSelectedItem();
            if (downloadedFile == null) return;

            network.sendMsg(DownloadMessage
                    .builder()
                    .login(properties.getLogin())
                    .password(properties.getPassword())
                    .currentDir(fileManager.getCurrentDir())
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
        Platform.runLater(() -> {

        });
    }

    public void deleteFileFromServer(ActionEvent event) {
        Platform.runLater(() -> {

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
                    .login(properties.getLogin())
                    .password(properties.getPassword())
                    .currentDir(fileManager.getCurrentDir())
                    .oldName(file.getName())
                    .newName(newName)
                    .build());

            RenameMessage message = (RenameMessage)network.readMessage();

            if (message.getStatus() == DataMessageStatus.OK) {
                serverFileList.setItems(FXCollections.observableList(message.getFiles()));
            }



        });
    }
}

