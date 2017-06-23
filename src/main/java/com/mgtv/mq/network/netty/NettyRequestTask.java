package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.channel.Channel;

public class NettyRequestTask implements Runnable {

    private final Runnable runnable;

    private final long createTimestamp = System.currentTimeMillis();

    private final Channel channel;

    private final Command request;

    private boolean stopRun = false;

    public NettyRequestTask(Runnable runnable, Channel channel, Command request) {
        this.runnable = runnable;
        this.channel = channel;
        this.request = request;
    }

    public void run() {
        if (!this.stopRun) {
            this.runnable.run();
        }
    }
}
