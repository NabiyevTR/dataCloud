package ntr.datacloud.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.DataMessage;
import ntr.datacloud.common.messages.data.DataMessageStatus;
import ntr.datacloud.common.messages.service.*;
import ntr.datacloud.server.services.executors.DataExecutor;
import ntr.datacloud.server.services.executors.ServiceExecutor;

import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    private final ConnectedClients clients = ConnectedClients.getInstance();
    private final ServerProperties properties = ServerProperties.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        log.debug(
                String.format("%s: Server received message from %s%nMessage: %s",
                        getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
        );

        if (message instanceof DataMessage) {

            DataMessage dataMessage = (DataMessage) message;

            // che
            if (clients.contains(ctx.channel())) {
                clients.getExecutor(ctx.channel()).execute(dataMessage);

            } else {
                dataMessage.setStatus(DataMessageStatus.ACCESS_DENIED);
            }


        } else if (message instanceof ServiceMessage) {

            ServiceMessage serviceMessage = (ServiceMessage) message;
            ServiceExecutor.execute(serviceMessage);

            // add client to list after successful authorization
            if (serviceMessage instanceof AuthMessage
                    && serviceMessage.getStatus() == ServiceMessageStatus.OK) {
                if (!addClient(ctx.channel(), (AuthMessage)serviceMessage)) {
                    //User is on the client connected list but not authenticated
                    serviceMessage.setStatus(ServiceMessageStatus.USER_IS_NOT_IN_CONNECTED_CLIENT_LIST);

                } else {
                    log.info(
                            String.format("Client %s connected.",
                                    ctx.channel().remoteAddress())
                    );
                }
            }

            if (serviceMessage instanceof LogoutMessage && serviceMessage.getStatus() == ServiceMessageStatus.OK) {
               clients.remove(ctx.channel());
                ctx.channel().closeFuture();
                log.info(
                        String.format("Client %s disconnected.",
                                ctx.channel().remoteAddress())
                );
            }
        }

        ctx.writeAndFlush(message);

        log.debug(
                String.format("%s: Server sent message to %s%nMessage: %s",
                        getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
        );
    }

    private boolean addClient(Channel channel, AuthMessage message) {

        Path rootPath = Paths.get(
                properties.getRootDir().toString(),
                message.getLogin()
        );

        return clients.put(
                channel,
                new DataExecutor(new FileManagerImpl(rootPath.normalize().toString())));
    }

}
