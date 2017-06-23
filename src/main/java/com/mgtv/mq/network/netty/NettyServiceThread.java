package com.mgtv.mq.network.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NettyServiceThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NettyServiceThread.class);

    private static final long joinTime = 90 * 1000;

    protected final Thread thread;

    protected volatile boolean hasNotified = false;

    protected volatile boolean stopped = false;

    public NettyServiceThread() {
        this.thread = new Thread(this, this.getServiceName());
    }

    public abstract String getServiceName();

    public void start() {
        this.thread.start();
    }

    public void shutdown() {
        this.shutdown(false);
    }

    public void shutdown(final boolean interrupt) {
        this.stopped = true;
        log.info("shutdown thread " + this.getServiceName(


        ) + " interrupt " + interrupt);
        synchronized (this) {
            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }

        try {
            if (interrupt) {
                this.thread.interrupt();
            }

            long beginTime = System.currentTimeMillis();
            this.thread.join(this.getJoinTime());
            long eclipseTime = System.currentTimeMillis() - beginTime;
            log.info("join thread  {} eclipse time(ms) ", eclipseTime, this.getJoinTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.stop(false);
    }

    public void stop(final boolean interrupt) {
        this.stopped = true;
        log.info("stop thread {} interrupt {}", this.getServiceName(), interrupt);
        synchronized (this) {
            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }

        if (interrupt) {
            this.thread.interrupt();
        }
    }

    public long getJoinTime() {
        return joinTime;
    }

    public boolean isStopped() {
        return stopped;
    }
}
