package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class NettyEncoder extends MessageToByteEncoder<Command> {
    final static Logger log = LoggerFactory.getLogger(NettyEncoder.class);

    protected void encode(ChannelHandlerContext channelHandlerContext, Command command, ByteBuf out) throws Exception {
        try {
            ByteBuffer header = command.encodeHeader();
            out.writeBytes(header);
            byte[] body = command.getBody();
            if (body != null) {
                out.writeBytes(body);
            }
        } catch (Exception e) {
            log.error("encode error ", e);
            if (command != null) {
                log.error(command.toString());
            }
            channelHandlerContext.close();
        }
    }
}
