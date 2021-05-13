package ntr.datacloud.server;

import io.netty.channel.Channel;
import io.netty.util.internal.ConcurrentSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ntr.datacloud.server.services.executors.DataExecutor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectedClients {

    private static ConnectedClients INSTANCE;
    private final ConcurrentMap<Channel, DataExecutor> executors = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Channel> connectedLogins = new ConcurrentHashMap<>();

    public static synchronized ConnectedClients getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectedClients();
        }
        return INSTANCE;
    }

    public boolean put(Channel channel, String login, DataExecutor executor) {
        if (!executors.containsKey(channel)) {
            executors.put(channel, executor);
            connectedLogins.put(login, channel);
            return true;
        }
        return false;
    }

    public boolean contains(Channel channel) {
        return executors.containsKey(channel);
    }

    public boolean contains(String login) {
        return connectedLogins.containsKey(login);
    }

    public DataExecutor getExecutor(Channel channel) {
        return executors.get(channel);
    }

    public void remove(String login) {
        if (connectedLogins.containsKey(login)) {
            Channel channel = connectedLogins.get(login);
            connectedLogins.remove(login);
            executors.remove(channel);
        }
    }
}
