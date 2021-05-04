package ntr.datacloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.DataMessage;
import ntr.datacloud.common.messages.data.GetFilesMessage;
import ntr.datacloud.common.messages.service.ServiceMessage;
import ntr.datacloud.server.services.executors.DataExecutor;
import ntr.datacloud.server.services.executors.ServiceExecutor;

@Log4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        log.debug(
                String.format("%s: Server received message from %s%nMessage: %s",
                        getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
        );

        if (message instanceof DataMessage) {
            DataExecutor.execute((DataMessage) message);
        } else    if (message instanceof ServiceMessage) {
            ServiceExecutor.execute(message);
        }

        ctx.writeAndFlush(message);

        log.debug(
                String.format("%s: Server sent message to %s\nMessage: %s",
                        getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
        );
    }
}
