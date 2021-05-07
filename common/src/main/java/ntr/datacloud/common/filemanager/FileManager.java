package ntr.datacloud.common.filemanager;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

public interface FileManager {


    boolean changeDir(String relPath) throws  IllegalAccessException, NoSuchFileException;

    boolean changeDirToParent() throws IllegalAccessException, NoSuchFileException;

    boolean createDir(String relPath) throws IllegalAccessException,  IOException;

    ArrayList<FileEntity> getFiles(String relDir) throws IllegalAccessException,  IOException;

    ArrayList<FileEntity> getFiles() throws IllegalAccessException,  IOException;

    String getCurrentDir();

    boolean rename(String oldName, String newName) throws IllegalAccessException,  IOException;

    boolean delete(String relPath) throws IllegalAccessException,  IOException;

    byte[] fileToBytes(String relPath) throws IllegalAccessException, IOException;

    List<byte[]> fileToBytes(String relPath, int frame) throws IllegalAccessException, IOException;

    boolean bytesToFile(byte[] bytes, String fileName) throws IOException, IllegalAccessException;

    boolean isFileTransfer();

     void setFileTransfer(boolean fileTransfer);



}
