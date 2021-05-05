package ntr.datacloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.DataMessage;
import ntr.datacloud.common.messages.service.LogonMessage;
import ntr.datacloud.common.messages.service.ServiceMessage;
import ntr.datacloud.common.messages.service.ServiceMessageStatus;
import ntr.datacloud.server.services.executors.DataExecutor;
import ntr.datacloud.server.services.executors.ServiceExecutor;

import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        log.debug(
                String.format("%s: Server received message from %s%nMessage: %s",
                        getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
        );

        if (message instanceof DataMessage) {

            DataMessage dataMessage = (DataMessage) message;
            DataExecutor.execute(dataMessage);

        } else if (message instanceof ServiceMessage) {

            ServiceMessage serviceMessage = (ServiceMessage) message;
            ServiceExecutor.execute(serviceMessage);

            if (serviceMessage instanceof LogonMessage && serviceMessage.getStatus() == ServiceMessageStatus.OK) {
                if (!regActiveClient(ctx, serviceMessage)) {
                    //User is on the client connected list but not authenticated
                    serviceMessage.setStatus(ServiceMessageStatus.USER_IS_NOT_IN_CONNECTED_CLIENT_LIST);
                }
            }
        }

        ctx.writeAndFlush(message);

        log.debug(
                String.format("%s: Server sent message to %s%nMessage: %s",
                        getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
        );
    }

    private boolean regActiveClient(ChannelHandlerContext ctx, Message message) {

        ConnectedClients clients = ConnectedClients.getInstance();
        ServerProperties properties = ServerProperties.getInstance();

        Path rootPath = Paths.get(
                properties.getRootDir().toString(),
                message.getLogin()
        );

        return clients.put(
                ctx.channel(),
                new DataExecutor(new FileManagerImpl(rootPath.normalize().toString())));
    }
}
