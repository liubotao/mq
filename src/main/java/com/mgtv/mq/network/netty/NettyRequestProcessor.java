package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.channel.ChannelHandlerContext;

public interface NettyRequestProcessor {

    Command processRequest(ChannelHandlerContext ctx, Command request) throws Exception;

    boolean rejectRequest();
}
