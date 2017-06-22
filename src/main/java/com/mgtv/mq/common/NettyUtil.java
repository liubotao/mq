package com.mgtv.mq.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class NettyUtil {

    private static final Logger log = LoggerFactory.getLogger(NettyUtil.class);

    public static String parseChannelRemoteAddress(final Channel channel) {
        if (null == channel) {
            return "";
        }

        SocketAddress socketAddress = channel.remoteAddress();
        final String address = (socketAddress != null) ? socketAddress.toString() : "";
        if (address.length() > 0) {
            int index = address.lastIndexOf("/");
            if (index >= 0) {
                return address.substring(index + 1);
            }
            return address;
        }
        return "";
    }

    public static void closeChannel(Channel channel) {
        final String remoteAddress = parseChannelRemoteAddress(channel);
        channel.close().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.info("closeChannel: close the connection to remote address [{}], result:{}", remoteAddress,
                        channelFuture.isSuccess());
            }
        });
    }
}
