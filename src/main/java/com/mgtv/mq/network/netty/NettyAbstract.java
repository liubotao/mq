package com.mgtv.mq.network.netty;

import com.mgtv.mq.protocol.Command;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class NettyAbstract {

    private static final Logger log = LoggerFactory.getLogger(NettyAbstract.class);

    protected final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();

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

    public void putNettyEvent(final NettyEvent nettyEvent) {
        this.nettyEventExecutor.putNettyEvent(nettyEvent);
    }

    public abstract NettyChannelEventListener getNettyChannelEventListener();

    class NettyEventExecutor extends NettyServiceThread {

        private final LinkedBlockingQueue<NettyEvent> eventLinkedBlockingQueue = new LinkedBlockingQueue<NettyEvent>();

        private final int maxSize = 10000;

        public void putNettyEvent(final NettyEvent event) {
            if (this.eventLinkedBlockingQueue.size() <= maxSize) {
                this.eventLinkedBlockingQueue.add(event);
            } else {
                log.warn("event queue size[{}] enough, so drop this event {}", this.eventLinkedBlockingQueue.size(), event.toString());
            }
        }

        public void run() {
            log.info(this.getServiceName() + " service started");

            final NettyChannelEventListener listener = NettyAbstract.this.getNettyChannelEventListener();

            while (!this.isStopped()) {
                try {
                    NettyEvent event = this.eventLinkedBlockingQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getNettyEventType()) {
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddress(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddress(), event.getChannel());
                                break;
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddress(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddress(), event.getChannel());
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    log.warn(this.getServiceName() + " service has exception", e);
                }
            }

            log.info(this.getServiceName() + " service end");
        }

        public String getServiceName() {
            return NettyEventExecutor.class.getSimpleName();
        }
    }

}
