package ntr.datacloud.server.services.executors;


import lombok.Getter;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.filemanager.FileManager;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.*;
import ntr.datacloud.server.ServerProperties;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Log4j
public class DataExecutor {

    private static final ServerProperties properties = ServerProperties.getInstance();
    @Getter
    private final FileManager fileManager;
    private final Map<String, Consumer<DataMessage>> executors =
            new HashMap<>();


    public DataExecutor(FileManager fileManager) {

        this.fileManager = fileManager;

        executors.put(CreateDirMessage.class.getName(), createDirExecutor());
        executors.put(DeleteMessage.class.getName(), deleteExecutor());
        executors.put(GetFilesMessage.class.getName(), getFilesExecutor());
        executors.put(RenameMessage.class.getName(), renameExecutor());
        executors.put(UploadMessage.class.getName(), uploadExecutor());
        executors.put(ChangeDirMessage.class.getName(), changeDirExecutor());

    }

    public boolean execute(DataMessage message) {
        String key = message.getClass().getName();
        if (executors.containsKey(key)) {
            try {
                executors.get(key).accept(message);
                return true;
            } catch (Exception e) {
                message.setStatus(DataMessageStatus.UNKNOWN_ERROR);
                log.error(e);
                return false;
            }
        } else {
            message.setStatus(DataMessageStatus.UNKNOWN_COMMAND);
            log.warn("Server received unknown command: " + message);
            return false;
        }
    }

    public List<DownloadMessage> executeDownload(DownloadMessage message) {

        try {
            List<DownloadMessage> messages =

                    fileManager.fileToBytes(message.getFileName(), properties.getMaxFileFrame())
                            .stream()
                            .map(p -> DownloadMessage.builder()
                                    .fileName(message.getFileName())
                                    .content(p)
                                    .status(DataMessageStatus.OK)
                                    .build())
                            .collect(Collectors.toList());

            messages.get(messages.size() - 1).setLast(true);

            return messages;

        } catch (IOException | IllegalAccessException e) {
            return Arrays.asList(
                    DownloadMessage.builder()
                            .status(DataMessageStatus.UNKNOWN_ERROR)
                            .errorText(e.getMessage())
                            .build()
            );


        }
    }


    private Consumer<DataMessage> changeDirExecutor() {
        return message -> {
            ChangeDirMessage changeDirMessage = (ChangeDirMessage) message;
            try {
                fileManager.changeDir(changeDirMessage.getRelPath());
                changeDirMessage.setFiles(
                        fileManager.getFiles()
                );
                changeDirMessage.setStatus(DataMessageStatus.OK);
            } catch (NoSuchFileException e) {
                log.error(e);
                changeDirMessage.setErrorText(e.toString());
                changeDirMessage.setStatus(DataMessageStatus.NO_SUCH_DIRECTORY);
            } catch (IllegalAccessException e) {
                log.error(e);
                changeDirMessage.setErrorText(e.toString());
                changeDirMessage.setStatus(DataMessageStatus.ACCESS_DENIED);
            } catch (IOException e) {
                //todo handle exception
            }
        };
    }


    private Consumer<DataMessage> uploadExecutor() {
        return message -> {
            UploadMessage uploadMessage = (UploadMessage) message;
            try {
                // todo check if file exsists

                fileManager.setFileTransfer(true);

                fileManager.bytesToFile(
                        uploadMessage.getContent(),
                        uploadMessage.getFileName());
                if (uploadMessage.isLast()) {
                    fileManager.setFileTransfer(false);
                }

                uploadMessage.setContent(null);
                uploadMessage.setFiles(fileManager.getFiles());
                uploadMessage.setStatus(DataMessageStatus.OK);
            } catch (IOException | IllegalAccessException e) {
                //todo
            }
        };
    }

/*    private Consumer<DataMessage> downloadExecutor() {
        return message -> {
            DownloadMessage downloadMessage = (DownloadMessage) message;
            try {
                downloadMessage.setContent(
                        fileManager.fileToBytes(downloadMessage.getFileName())
                );

                downloadMessage.setStatus(DataMessageStatus.OK);
            } catch (IOException | IllegalAccessException e) {
                //todo handle exception
            }
        };
    }*/


    private Consumer<DataMessage> renameExecutor() {
        return message -> {
            try {
                //     FileManager fileManager = getFileManager(message);
                RenameMessage renameMessage = (RenameMessage) message;
                fileManager.rename(
                        renameMessage.getOldName(),
                        renameMessage.getNewName()
                );
                renameMessage.setFiles(fileManager.getFiles());
                renameMessage.setStatus(DataMessageStatus.OK);
            } catch (IOException e) {
                // todo handle exception
                message.setStatus(DataMessageStatus.UNKNOWN_ERROR);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                message.setStatus(DataMessageStatus.ACCESS_DENIED);
                e.printStackTrace();
                // todo handle exception
            }

        };
    }


    private Consumer<DataMessage> deleteExecutor() {
        return message -> {
            try {
                //    FileManager fileManager = getFileManager(message);
                DeleteMessage deleteMessage = (DeleteMessage) message;
                if (fileManager.delete(
                        deleteMessage.getFileToDelete()
                )) {
                    deleteMessage.setStatus(DataMessageStatus.OK);
                    deleteMessage.setFiles(
                            fileManager.getFiles());
                } else {
                    deleteMessage.setStatus(DataMessageStatus.CANNOT_DELETE);
                }


            } catch (IOException e) {
                //todo handle exception
                e.printStackTrace();
            } catch (IllegalAccessException e) {

                e.printStackTrace();
            }

        };
    }


    private Consumer<DataMessage> createDirExecutor() {
        return message -> {
            try {
                //  FileManager fileManager = getFileManager(message);
                CreateDirMessage createDirMessage = (CreateDirMessage) message;
                fileManager.createDir(createDirMessage.getNewDir());
                createDirMessage.setFiles(
                        fileManager.getFiles()
                );
                createDirMessage.setStatus(DataMessageStatus.OK);
            } catch (IOException | IllegalAccessException e) {
                //todo handle exception
            }
        };
    }


    private Consumer<DataMessage> getFilesExecutor() {
        return message -> {

            // FileManager fileManager = getFileManager(message);
            GetFilesMessage getFilesMessage = (GetFilesMessage) message;


            try {
                fileManager.changeDir(getFilesMessage.getRelDir());
                getFilesMessage.setFiles(fileManager.getFiles());
                getFilesMessage.setStatus(DataMessageStatus.OK);
            } catch (Exception e) {
                log.error(
                        String.format("Cannot get files from directory %s:", fileManager.getCurrentDir()), e);
                getFilesMessage.setStatus(DataMessageStatus.CANNOT_GET_FILES_FROM_SERVER);
                getFilesMessage.setErrorText(e.getMessage());
            }
        };
    }


}
