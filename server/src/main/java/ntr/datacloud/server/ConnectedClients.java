package ntr.datacloud.server;

import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ntr.datacloud.server.services.executors.DataExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectedClients {

    private static ConnectedClients INSTANCE;
    private final ConcurrentMap<Channel, DataExecutor> executors = new ConcurrentHashMap<>();

    public static synchronized ConnectedClients getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectedClients();
        }
        return INSTANCE;
    }


    public boolean put(Channel channel, DataExecutor executor) {
        if (!executors.containsKey(channel)) {
            executors.put(channel, executor);
            return true;
        }
        return false;
    }

    public void remove(Channel channel) {
        executors.remove(channel);
    }
}
