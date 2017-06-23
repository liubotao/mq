package com.mgtv.mq;

import com.mgtv.mq.network.netty.NettyServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        ExecutorService remotingExecutor = Executors.newFixedThreadPool(2,
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    public Thread newThread(Runnable r) {
                        return new Thread(r, "RemotingExecutorThread_" + this.threadIndex.incrementAndGet());
                    }
                });

        final SimpleChannelEventListener simpleChannelEventListener = new SimpleChannelEventListener();
        NettyServer nettyServer = new NettyServer(simpleChannelEventListener);

        nettyServer.registerDefaultProcessor(new SimpleRequestProcessor(), remotingExecutor);

        nettyServer.start();
    }
}
