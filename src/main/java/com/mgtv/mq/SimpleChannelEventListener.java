package com.mgtv.mq;

import com.mgtv.mq.network.netty.NettyChannelEventListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleChannelEventListener implements NettyChannelEventListener {

    private static final Logger log = LoggerFactory.getLogger(SimpleChannelEventListener.class);

    public void onChannelConnect(String remoteAddress, Channel channel) {
        log.info("SimpleChannelEventListener onChannelConnect");
    }

    public void onChannelClose(String remoteAddress, Channel channel) {
        log.info("SimpleChannelEventListener onChannelClose");
    }

    public void onChannelException(String remoteAddress, Channel channel) {
        log.info("SimpleChannelEventListener onChannelException");
    }

    public void onChannelIdle(String remoteAddress, Channel channel) {
        log.info("SimpleChannelEventListener onChannelIdle");
    }
}
