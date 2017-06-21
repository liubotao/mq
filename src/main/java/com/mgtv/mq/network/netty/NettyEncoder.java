package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

public class NettyEncoder extends MessageToByteEncoder<Command> {

    protected void encode(ChannelHandlerContext channelHandlerContext, Command command, ByteBuf byteBuf) throws Exception {

    }
}
