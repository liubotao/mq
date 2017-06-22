package com.mgtv.mq.network.netty;

import com.mgtv.mq.common.Pair;
import com.mgtv.mq.protocol.Command;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class NettyServer extends NettyAbstract {

    private final static Logger log = LoggerFactory.getLogger(NettyServer.class);

    private final ServerBootstrap serverBootstrap;

    private EventLoopGroup bossLoopGroup = new NioEventLoopGroup();

    private EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

    private final HashMap<Integer, Pair<NettyRequestProcessor, ExecutorService>> processMaps =
            new HashMap<Integer, Pair<NettyRequestProcessor, ExecutorService>>();

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private int port = 0;

    private final ExecutorService publicExecutor;


    public NettyServer() {
        this.serverBootstrap = new ServerBootstrap();

        this.publicExecutor = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });
    }

    public void registerProcess(int requestCode, NettyRequestProcessor processor, ExecutorService executorService) {
        Pair<NettyRequestProcessor, ExecutorService> pair = new Pair<NettyRequestProcessor, ExecutorService>(processor, executorService);
        this.processMaps.put(requestCode, pair);
    }

    public int localListenPort() {
        return this.port;
    }

    public void start() {
        int port = 9999;

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                2,
                new ThreadFactory() {
                    private AtomicInteger threadIndex = new AtomicInteger(0);

                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
                    }
                }
        );

        try {
            this.serverBootstrap.group(bossLoopGroup, workerLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new NettyDecoder(),
                                    new NettyEncoder(),
                                    new IdleStateHandler(0, 0, 20),
                                    new NettyConnectManageHandler(),
                                    new NettyServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, false);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
            InetSocketAddress address = (InetSocketAddress) future.channel().localAddress();
            this.port = address.getPort();
        } catch (InterruptedException e) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e);
        }
    }

    public void shutDown() {
        try {
            this.bossLoopGroup.shutdownGracefully();
            this.workerLoopGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("NettyServer shutdown Exception, ", e);
        }
    }

    class NettyServerHandler extends SimpleChannelInboundHandler<Command> {
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
            processMessageReceive(channelHandlerContext, command);
        }
    }
}