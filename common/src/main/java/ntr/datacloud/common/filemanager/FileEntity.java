package ntr.datacloud.common.filemanager;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;


@Data
public class FileEntity implements Serializable {

    public enum FileType {
        REGULAR_FILE,
        DIRECTORY
    }

    private String name;
   // private Path path; //Cannot be serialized. Must be deleted
    private String size;
    private FileType type;

    public FileEntity(Path path) {
        this.name = path.getFileName().toString();
     //   this.path = path;
        this.type = (new File(path.toString())).isDirectory() ? FileType.DIRECTORY : FileType.REGULAR_FILE;
        try {
            this.size = (new File(path.toString())).isDirectory() ? "" : Files.size(path) / 1024 + "KB";
        } catch (IOException e) {
            this.size = "";
        }
    }
}