package ntr.datacloud.server.services.filemanager;

import java.nio.file.Path;

public interface FileManager {
    //TODO
    boolean createDir();
    boolean removeDir();
    boolean renameDir();

    boolean removeFile(Path path);
    boolean addFile();

    //TODO what type?
    boolean getFilesStructure();
}
