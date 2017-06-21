package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettyClient {


    public static void main(String[] args) throws Exception {

        String host = "127.0.0.1";
        int port = 9999;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new NettyDecoder(),
                                    new ProtocolClient());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}

class ProtocolClient extends SimpleChannelInboundHandler<Command> {

    private final static Logger log = LoggerFactory.getLogger(NettyClient.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
        log.info("command version :" + command.getVersion());
        log.info("command code : " + command.getCode());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}