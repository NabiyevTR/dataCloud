package ntr.datacloud.common.filemanager;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j
@AllArgsConstructor
public class FileManagerImpl implements FileManager {

    private final String rootDir;

    private String currentDir;

    public FileManagerImpl(String serverDir) {
        this(serverDir, "");
    }

    @Override
    public String getCurrentDir() {
        return currentDir;
    }

    @Override
    public ArrayList<FileEntity> getFiles(String relDir) throws IOException, IllegalAccessException {

        Path path = Paths.get(rootDir, currentDir, relDir).normalize();

        checkAccess(path);

        ArrayList<FileEntity> files = new ArrayList<>();

        // Get directories and sort by type and by name
        Stream<Path> filesStream = Files.walk(path, 1);
        files.addAll(
                filesStream
                        .filter(e -> !e.equals(path))
                        .map(FileEntity::new)
                        .sorted(Comparator
                                .comparing(FileEntity::getType, Comparator.reverseOrder())
                                .thenComparing(FileEntity::getName))
                        .collect(Collectors.toList())
        );
        return files;
    }

    @Override
    public ArrayList<FileEntity> getFiles() throws IOException, IllegalAccessException {
        return getFiles("");
    }

    @Override
    public boolean changeDir(String relPath) throws IllegalAccessException, NoSuchFileException {
        Path path = Paths.get(rootDir, currentDir, relPath).normalize();

        checkAccess(path);

        if (Files.exists(path)) {
            currentDir = Paths.get(rootDir).relativize(path).toString();
            log.debug("Current dir: " + currentDir);
            return true;
        } else {
            log.debug(String.format("Directory %s does not exist", path.toString()));
            throw new NoSuchFileException(String.format("Directory %s does not exist", path.toString()));
        }
    }

    @Override
    public boolean changeDirToParent() throws IllegalAccessException, NoSuchFileException {
        return changeDir("..");
    }

    @Override
    public boolean rename(String oldName, String newName) throws IOException, IllegalAccessException {

        Path path = Paths.get(rootDir, currentDir, oldName).normalize();

        checkAccess(path);

        Path targetPath = Paths.get(rootDir, currentDir, newName).normalize();
        Files.move(path, targetPath);
        return true;
    }

    @Override
    public boolean delete(String relPath) throws IllegalAccessException, IOException {

        Path path = Paths.get(rootDir, currentDir, relPath).normalize();

        checkAccess(path);

        File fileToDelete = new File(path.toString());
        if (fileToDelete.isDirectory()) {
            return deleteDir(fileToDelete);
        } else {
            return fileToDelete.delete();
        }
    }


    //todo can be shorter?


    @Override
    public boolean createDir(String relPath) throws IOException, IllegalAccessException {

        Path path = Paths.get(rootDir, currentDir, relPath).normalize();

        checkAccess(path);

        Files.createDirectories(path);
        log.info("Directory was created successfully!");
        return true;

    }

    @Override
    public byte[] fileToBytes(String relPath) throws IllegalAccessException, IOException {

        Path path = Paths.get(rootDir, currentDir, relPath).normalize();
        checkAccess(path);
        return Files.readAllBytes(path);
    }

    @Override
    public boolean bytesToFile(byte[] bytes, String fileName) throws IOException, IllegalAccessException {

        Path path = Paths.get(rootDir, currentDir, fileName).normalize();
        checkAccess(path);

        try (FileOutputStream fos = new FileOutputStream(path.toString())) {
            fos.write(bytes);
            return true;
        }
        // todo catch
    }



    private void checkAccess(Path path) throws IllegalAccessException {

        if (rootDir.equals(path.toString())) return;
        if (rootDir.startsWith(path.toString())) {
            log.warn("Illegal access attempt!");
            throw new IllegalAccessException();
        }
    }

    private boolean deleteDir(File dirToDelete) {
        File[] allContents = dirToDelete.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDir(file);
            }
        }
        return dirToDelete.delete();
    }




}
