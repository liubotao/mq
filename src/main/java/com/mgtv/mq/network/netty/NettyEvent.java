package com.mgtv.mq.network.netty;

import io.netty.channel.Channel;

public class NettyEvent {

    private final NettyEventType nettyEventType;

    private final String remoteAddress;

    private final Channel channel;

    public NettyEvent(NettyEventType nettyEventType, String remoteAddress, Channel channel) {
        this.nettyEventType = nettyEventType;
        this.remoteAddress = remoteAddress;
        this.channel = channel;
    }

    public NettyEventType getNettyEventType() {
        return nettyEventType;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "NettyEvent[" +
                "nettyEventType=" + nettyEventType +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", channel=" + channel +
                ']';
    }
}
