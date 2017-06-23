package com.mgtv.mq.network.netty;

import com.mgtv.mq.common.NettyUtil;
import com.mgtv.mq.common.Pair;
import com.mgtv.mq.protocol.Command;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class NettyServer extends NettyAbstract {

    private final static Logger log = LoggerFactory.getLogger(NettyServer.class);

    private final ServerBootstrap serverBootstrap;

    private EventLoopGroup bossLoopGroup = new NioEventLoopGroup();

    private EventLoopGroup workerLoopGroup = new NioEventLoopGroup();


    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private int port = 0;

    private final ExecutorService publicExecutor;

    private final NettyChannelEventListener nettyChannelEventListener;


    public NettyServer(final NettyChannelEventListener nettyChannelEventListener) {

        this.nettyChannelEventListener = nettyChannelEventListener;

        this.serverBootstrap = new ServerBootstrap();

        this.publicExecutor = Executors.newFixedThreadPool(4, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });
    }

    public void registerDefaultProcessor(NettyRequestProcessor nettyRequestProcessor, ExecutorService executor) {
        this.defaultRequestProcessor = new Pair<NettyRequestProcessor, ExecutorService>(nettyRequestProcessor, executor);
    }

    public void registerProcessor(int requestCode, NettyRequestProcessor processor, ExecutorService executor) {
        ExecutorService executorService = executor;
        if (null == executorService) {
            executorService = this.publicExecutor;
        }

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
                                    defaultEventExecutorGroup,
                                    new NettyDecoder(),
                                    new NettyEncoder(),
                                    new IdleStateHandler(0, 0, 20),
                                    new NettyConnectManageHandler(),
                                    new NettyServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            InetSocketAddress address = (InetSocketAddress) future.channel().localAddress();
            this.port = address.getPort();
        } catch (InterruptedException e) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e);
        }

        log.info("nettyChannelEventListener nettyEventExecutor start");

        if (this.nettyChannelEventListener != null) {
            this.nettyEventExecutor.start();
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

    public NettyChannelEventListener getNettyChannelEventListener() {
        return nettyChannelEventListener;
    }

    class NettyServerHandler extends SimpleChannelInboundHandler<Command> {
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
            processMessageReceive(channelHandlerContext, command);
        }
    }

    class NettyConnectManageHandler extends ChannelDuplexHandler {

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

            if (NettyServer.this.nettyChannelEventListener != null) {
                NettyServer.this.putNettyEvent(new NettyEvent(NettyEventType.CONNECT, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
            log.info("Netty Server Pipeline : channelInactive {}", remoteAddress);
            super.channelInactive(ctx);

            if (NettyServer.this.nettyChannelEventListener != null) {
                NettyServer.this.putNettyEvent(new NettyEvent(NettyEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
                    log.warn("Netty Server pipeline: Idle exception {}", remoteAddress);
                    NettyUtil.closeChannel(ctx.channel());

                    if (NettyServer.this.nettyChannelEventListener != null) {
                        NettyServer.this.putNettyEvent(new NettyEvent(NettyEventType.IDLE, remoteAddress, ctx.channel()));
                    }
                }
            }
            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = NettyUtil.parseChannelRemoteAddress(ctx.channel());
            log.warn("Netty Server Pipeline: exceptionCaught {}", remoteAddress);
            log.warn("Netty Server Pipeline: exceptionCaught exception", cause);

            if (NettyServer.this.nettyChannelEventListener != null) {
                NettyServer.this.putNettyEvent(new NettyEvent(NettyEventType.EXCEPTION, remoteAddress, ctx.channel()));
            }

            NettyUtil.closeChannel(ctx.channel());
        }
    }
}