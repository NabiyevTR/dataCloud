package ntr.datacloud.server;

import lombok.Getter;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ServerProperties {

    private static ServerProperties INSTANCE;
    @Getter
    private final Path rootDir = Paths.get("./storage/server").toAbsolutePath().normalize();
    @Getter
    private final int port = 8189;
    @Getter
    private final int maxMessageSize = 5 * 1024 * 1024;
    @Getter
    private final int maxFileFrame = (int)(4.5 * 1024 * 1024);

    private ServerProperties() {
        File dir = new File(rootDir.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static ServerProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerProperties();
        }
        return INSTANCE;
    }
}
