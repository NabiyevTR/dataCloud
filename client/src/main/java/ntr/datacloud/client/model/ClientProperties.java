package ntr.datacloud.client.model;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
public class ClientProperties {

    private static ClientProperties INSTANCE;

    public static ClientProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientProperties();
        }
        return INSTANCE;
    }

    private ClientProperties() {
        File dir = new File(rootDir.toString());
        if (!dir.exists()){
            dir.mkdirs();
        }
    }

    private String login;
    private String password;
    private final int timeOut = 3000; //ms
    private final Path rootDir = Paths.get("./storage/client").toAbsolutePath().normalize();
    private int maxFileFrame;


}
