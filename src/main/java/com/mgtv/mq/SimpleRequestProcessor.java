package com.mgtv.mq;

import com.mgtv.mq.network.netty.NettyRequestProcessor;
import com.mgtv.mq.protocol.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRequestProcessor implements NettyRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(SimpleRequestProcessor.class);

    public Command processRequest(ChannelHandlerContext ctx, Command request) throws Exception {
        log.info("request code: {} ,version: {}", request.getCode(), request.getVersion());
        Command responseCommand = Command.createResponseCommand(200, "not set any response code");
        responseCommand.setCode(200);
        responseCommand.setRemark(null);
        return responseCommand;
    }

    public boolean rejectRequest() {
        return false;
    }
}
