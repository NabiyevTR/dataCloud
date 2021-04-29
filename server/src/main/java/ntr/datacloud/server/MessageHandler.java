package ntr.datacloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import ntr.datacloud.common.messages.Message;

@Log4j
public class MessageHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        ctx.writeAndFlush(message);
        log.debug("Message received: " + message);

    }
}
