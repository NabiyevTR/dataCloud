package ntr.datacloud.client.model;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class ClientProperties {

    private static ClientProperties INSTANCE;

    public static ClientProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientProperties();
        }
        return INSTANCE;
    }

    private Path rootDir = Paths.get("G:\\Мой диск\\Программирование\\dataCloud\\test\\client");
    private String login;
    private String password;


}
