package ntr.datacloud.server;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerProperties {

    private static ServerProperties INSTANCE;
    @Getter
    private final Path rootDir = Paths.get("G:\\Мой диск\\Программирование\\dataCloud\\test\\server");
    @Getter
    private final int port = 8189;

    public static ServerProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerProperties();
        }
        return INSTANCE;
    }


}
