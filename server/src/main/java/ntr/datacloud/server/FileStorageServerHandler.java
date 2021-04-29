package ntr.datacloud.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public class FileStorageServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active: " + ctx.channel().localAddress() + " " + ctx.channel().metadata());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //((ByteBuf) msg).release();.

        try {
            //TODO Write logic
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Channel was closed. Error has occurred: ", cause);
        ctx.close();
    }
}
