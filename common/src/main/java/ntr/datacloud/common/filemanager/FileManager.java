package ntr.datacloud.common.filemanager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public interface FileManager {

    // cd - change dir
    boolean changeDir(String relPath) throws  IllegalAccessException, NoSuchFileException;

    boolean changeDirToParent() throws IllegalAccessException, NoSuchFileException;

    // todo delete
    // cat - print file in console
    String readFile(String relPath);

    // touch -  create file
    // todo delete
    boolean createFile(String relPath);

    // mkdir - create dir
    boolean createDir(String relPath) throws IllegalAccessException,  IOException;

    ArrayList<FileEntity> getFiles(String relDir) throws IllegalAccessException,  IOException;

    ArrayList<FileEntity> getFiles() throws IllegalAccessException,  IOException;

    String getCurrentDir();

    boolean rename(String oldName, String newName) throws IllegalAccessException,  IOException;

    boolean delete(String relPath) throws IllegalAccessException,  IOException;

    byte[] fileToBytes(String relPath) throws IllegalAccessException, IOException;

    boolean bytesToFile(byte[] bytes, String fileName) throws IOException, IllegalAccessException;

}
