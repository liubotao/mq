package com.mgtv.mq;

import com.mgtv.mq.network.netty.NettyServer;

public class Main {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }
}
