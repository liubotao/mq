package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private final static Logger log = LoggerFactory.getLogger(NettyDecoder.class);

    public NettyDecoder() {
        super(16777216, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;

        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (frame == null) {
                return null;
            }

            ByteBuffer byteBuffer = frame.nioBuffer();

            return Command.decode(byteBuffer);
        } catch (Exception e) {
            log.error("decode error ", e);
            ctx.close();
        } finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }
}
