package ntr.datacloud.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.Message;
import ntr.datacloud.common.messages.RegMessage;
import ntr.datacloud.common.messages.ServiceMessage;
import ntr.datacloud.server.services.executors.ServiceServiceExecutor;

@Log4j
public class ServiceMessageHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        if (message instanceof ServiceMessage) {
            log.debug(
                    String.format("%s: Server received service message from %s\nMessage: %s",
                            getClass().getSimpleName(), ctx.channel().remoteAddress(), message)
            );
            ctx.writeAndFlush(message);
            ServiceServiceExecutor.execute(message);

        } else {
            ctx.fireChannelRead(message);
        }
    }
}