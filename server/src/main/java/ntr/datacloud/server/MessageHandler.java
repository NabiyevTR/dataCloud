package ntr.datacloud.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.filemanager.FileManagerImpl;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.DataMessage;
import ntr.datacloud.common.messages.data.DataMessageStatus;
import ntr.datacloud.common.messages.data.DownloadMessage;
import ntr.datacloud.common.messages.service.*;
import ntr.datacloud.server.services.executors.DataExecutor;
import ntr.datacloud.server.services.executors.ServiceExecutor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static ntr.datacloud.server.services.helpers.Helper.sizeof;

@Log4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    private final ConnectedClients clients = ConnectedClients.getInstance();
    private final ServerProperties properties = ServerProperties.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        log.debug(
                String.format("%s: Server received message from %s%nMessage [Size %s kb]: %s",
                        getClass().getSimpleName(),
                        ctx.channel().remoteAddress(),
                        sizeof(message),
                        message)
        );

        if (message instanceof DownloadMessage) {
            handleDownloadMessage(ctx, (DownloadMessage) message);
            return;
        }

        if (message instanceof DataMessage) {
            handleDataMessage(ctx, (DataMessage) message);
            return;
        }

        if (message instanceof ServiceMessage) {
            handleServiceMessage(ctx, message);
            return;
        }
    }

    private void handleDownloadMessage(ChannelHandlerContext ctx, DownloadMessage message) throws IOException {
        if (clients.contains(ctx.channel())) {
            DataExecutor dataExecutor = clients.getExecutor(ctx.channel());
            List<DownloadMessage> messages = dataExecutor.executeDownload(message);

            for (DownloadMessage msg :messages) {
                sendMessage(ctx, msg);
            }

        } else {
            message.setStatus(DataMessageStatus.ACCESS_DENIED);
            sendMessage(ctx,message);
        }
    }


    private void handleDataMessage(ChannelHandlerContext ctx, DataMessage message) throws IOException {
        if (clients.contains(ctx.channel())) {
            clients.getExecutor(ctx.channel()).execute(message);
        } else {
            message.setStatus(DataMessageStatus.ACCESS_DENIED);
        }
        sendMessage(ctx, message);
    }


    private void handleServiceMessage(ChannelHandlerContext ctx, Message message) throws IOException {
        ServiceMessage serviceMessage = (ServiceMessage) message;
        ServiceExecutor.execute(serviceMessage);

        // add  after successful authorization
        if (serviceMessage instanceof AuthMessage
                && serviceMessage.getStatus() == ServiceMessageStatus.OK) {
            if (!addClient(ctx.channel(), (AuthMessage) serviceMessage)) {
                //User is on the client connected list but not authenticated
                serviceMessage.setStatus(ServiceMessageStatus.USER_IS_NOT_IN_CONNECTED_CLIENT_LIST);
            } else {
                log.info(
                        String.format("Client %s connected.",
                                ctx.channel().remoteAddress())
                );
            }
        }

        // Remove clients after logout
        if (serviceMessage instanceof LogoutMessage && serviceMessage.getStatus() == ServiceMessageStatus.OK) {
            clients.remove(ctx.channel());
            ctx.channel().closeFuture();
            log.info(
                    String.format("Client %s disconnected.",
                            ctx.channel().remoteAddress())
            );
        }

        sendMessage(ctx, message);
    }


    private void sendMessage(ChannelHandlerContext ctx, Message message) throws IOException {
        ctx.writeAndFlush(message);

        log.debug(
                String.format("%s: Server sent message to %s%nMessage [Size %s kb]: %s",
                        getClass().getSimpleName(),
                        ctx.channel().remoteAddress(),
                        sizeof(message),
                        message)
        );
    }


    private boolean addClient(Channel channel, AuthMessage message) {

        Path rootPath = Paths.get(
                properties.getRootDir().toString(),
                message.getLogin()
        );

        return clients.put(
                channel,
                message.getLogin(),
                new DataExecutor(new FileManagerImpl(rootPath.normalize().toString())));
    }

}
