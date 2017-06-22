package com.mgtv.mq.network.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;


public class NettyConnectManageHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.close();
        ctx.fireUserEventTriggered(evt);
    }

}
