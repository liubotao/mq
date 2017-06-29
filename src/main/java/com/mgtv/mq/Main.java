package com.mgtv.mq;

import com.mgtv.mq.network.netty.NettyServer;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws Exception {
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

    public static String file2String(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            byte[] data = new byte[(int) file.length()];
            boolean result;

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                int length = fileInputStream.read(data);
                System.out.println("file length : " + length);
                System.out.println("fileInputStream length :" + length);
                result = (length == data.length);
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            if (result) {
                return new String(data);
            }
        }
        return null;
    }
}
