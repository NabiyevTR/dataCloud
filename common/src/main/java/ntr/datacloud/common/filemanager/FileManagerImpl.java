package ntr.datacloud.common.filemanager;

import lombok.extern.log4j.Log4j;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j
public class FileManagerImpl implements FileManager {

    private final String rootDir;

    private String currentDir;

    public boolean fileTransfer = false;

    public FileManagerImpl(String rootDir, String currentDir) {
        this.rootDir = rootDir;
        this.currentDir = currentDir;
    }

    public FileManagerImpl(String serverDir) {
        this(serverDir, "");
    }

    @Override
    public boolean isFileTransfer() {
        return fileTransfer;
    }

    @Override
    public void setFileTransfer(boolean fileTransfer) {
        this.fileTransfer = fileTransfer;
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
    public List<byte[]> fileToBytes(String relPath, int frame) throws IllegalAccessException, IOException {

        Path path = Paths.get(rootDir, currentDir, relPath).normalize();
        checkAccess(path);

        long fileSize = Files.size(path);
        long parts = fileSize / frame + 1;
        byte[] buffer = new byte[frame];
        List<byte[]> bytes = new ArrayList<>((int) (parts * 1.3));

        try (FileInputStream file = new FileInputStream(path.toFile())) {

            for (int i = 0; i < parts; i++) {
                int length = file.read(buffer);
                bytes.add(
                        Arrays.copyOf(buffer, length == frame ? frame : length)
                );
            }
        }
        return bytes;
    }

    @Override
    public boolean bytesToFile(byte[] bytes, String fileName) throws IOException, IllegalAccessException {

        Path path = Paths.get(rootDir, currentDir, fileName).normalize();
        checkAccess(path);

        try (FileOutputStream fos = new FileOutputStream(path.toString(), true)) {
            fos.write(bytes);
            return true;
        }
    }

    @Override
    public boolean fileExists(String fileName) {

        Path path = Paths.get(rootDir, currentDir, fileName).normalize();
        File f = path.toFile();

        return f.exists();
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
