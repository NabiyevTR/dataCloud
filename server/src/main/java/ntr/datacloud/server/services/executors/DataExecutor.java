package ntr.datacloud.server.services.executors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.filemanager.FileManager;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.data.*;
import ntr.datacloud.server.ServerProperties;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j
public class DataExecutor {

    private static final ServerProperties properties = ServerProperties.getInstance();
    // private final String serverRootDir = properties.getRootDir().toString();


    private static final Map<String, Consumer<DataMessage>> executors =
            new HashMap<>();

    static {
        executors.put(CreateDirMessage.class.getName(), createDirExecutor());
        executors.put(DeleteMessage.class.getName(), deleteExecutor());
        executors.put(DownloadMessage.class.getName(), downloadExecutor());
        executors.put(GetFilesMessage.class.getName(), getFilesExecutor());
        executors.put(RenameMessage.class.getName(), renameExecutor());
        executors.put(UploadMessage.class.getName(), uploadExecutor());
        executors.put(ChangeDirMessage.class.getName(), changeDirExecutor());

    }

    public static boolean execute(DataMessage message) {
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


    private static Consumer<DataMessage> changeDirExecutor() {
        return message -> {
            FileManager fileManager = getFileManager(message);
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


    private static Consumer<DataMessage> uploadExecutor() {
        return message -> {
            FileManager fileManager = getFileManager(message);
            UploadMessage uploadMessage = (UploadMessage) message;
            try {
                fileManager.bytesToFile(
                        uploadMessage.getContent(),
                        uploadMessage.getFileName());
                uploadMessage.setContent(null);
                uploadMessage.setFiles(fileManager.getFiles());
                uploadMessage.setStatus(DataMessageStatus.OK);
            } catch (IOException | IllegalAccessException e) {
                //todo
            }
        };
    }

    private static Consumer<DataMessage> downloadExecutor() {
        return message -> {
            FileManager fileManager = getFileManager(message);
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
    }


    private static Consumer<DataMessage> renameExecutor() {
        return message -> {
            try {
                FileManager fileManager = getFileManager(message);
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


    private static Consumer<DataMessage> deleteExecutor() {
        return message -> {
            try {
                FileManager fileManager = getFileManager(message);
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


    private static Consumer<DataMessage> createDirExecutor() {
        return message -> {
            try {
                FileManager fileManager = getFileManager(message);
                CreateDirMessage createDirMessage = (CreateDirMessage) message;
                fileManager.createDir(createDirMessage.getNewDir());
                fileManager.changeDir(createDirMessage.getNewDir());
                createDirMessage.setFiles(
                        fileManager.getFiles()
                );
                createDirMessage.setStatus(DataMessageStatus.OK);
            } catch (IOException | IllegalAccessException e) {
                //todo handle exception
            }
        };
    }


    private static Consumer<DataMessage> getFilesExecutor() {
        return message -> {

            FileManager fileManager = getFileManager(message);
            GetFilesMessage getFilesMessage = (GetFilesMessage) message;


            try {
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

    private static FileManager getFileManager(DataMessage message) {

        String dir = Paths.get(
                properties.getRootDir().toString(),
                message.getLogin(),
                message.getCurrentDir()
        ).normalize().toString();

        return new FileManagerImpl(dir);
    }


}
