package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.channel.ChannelHandlerContext;

public abstract class NettyAbstract {

    public void processMessageReceive(ChannelHandlerContext ctx, Command msg) throws Exception {
        final Command command = msg;
        if (command != null) {
            switch (command.getType()) {
                case REQUEST_COMMAND:
                    //processRequestCommand(ctx, command);
                    break;
                case RESPONSE_COMMAD:
                    //processResponseCommand(ctx, command);
                    break;
                default:
                    break;
            }
        }
    }
}
