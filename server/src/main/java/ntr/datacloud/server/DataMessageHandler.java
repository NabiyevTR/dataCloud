package ntr.datacloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.data.DataMessage;
import ntr.datacloud.common.messages.data.GetFilesMessage;
import ntr.datacloud.server.services.executors.DataExecutor;

@Log4j
public class DataMessageHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        if (message instanceof DataMessage) {
            log.debug(
                    String.format("%s: Server received message from %s\nMessage: %s",
                            getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
            );

            DataExecutor.execute((DataMessage) message);
            ctx.writeAndFlush(message);
            log.debug(
                    String.format("%s: Server sent message to %s\nMessage: %s",
                            getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
            );
        } else {
            ctx.fireChannelRead(message);
        }
    }
}
