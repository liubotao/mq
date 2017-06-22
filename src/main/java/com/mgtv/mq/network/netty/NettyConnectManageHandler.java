package com.mgtv.mq.network.netty;

import com.mgtv.mq.common.NettyUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.Net;


public class NettyConnectManageHandler extends ChannelDuplexHandler {

    final static Logger log = LoggerFactory.getLogger(NettyConnectManageHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
        log.info("Netty Server Pipeline : channelRegistered {}", remoteAddress);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
        log.info("Netty Server Pipeline : channelUnregistered {}", remoteAddress);
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
        log.info("Netty Server Pipeline : channelActive {}", remoteAddress);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
        log.info("Netty Server Pipeline : channelInactive {}", remoteAddress);
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
                log.warn("Netty Server pipeline: Idle exception {}", remoteAddress);
                NettyUtil.closeChannel(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
        log.warn("Netty Server Pipeline: exceptionCaught {}", remoteAddress);
        log.warn("Netty Server Pipeline: exceptionCaught exception", cause);
        NettyUtil.closeChannel(ctx.channel());
    }
}
