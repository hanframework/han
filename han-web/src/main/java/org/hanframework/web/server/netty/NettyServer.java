package org.hanframework.web.server.netty;

import cn.hutool.core.thread.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuxin
 * @version Id: NettyServer.java, v 0.1 2019-03-27 10:50
 */
public class NettyServer extends AbstractServer {
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

    private static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    private ChannelInitializer channelInitializer;

    public NettyServer(ChannelInitializer channelInitializer, int port) {
        super(port);
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void doOpen() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(DEFAULT_IO_THREADS, new NamedThreadFactory("han-boss", true));
        workerGroup = new NioEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS, new NamedThreadFactory("han-work", true));
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .localAddress(port()).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128);
        //bind
        ChannelFuture channelFuture = bootstrap.bind();
        showBanner();
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
    }


    @Override
    public void doClose() {
        log.info("destroy server resources");
        if (null == channel) {
            log.error("server channel is null");
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workerGroup = null;
        channel = null;
        log.info("destroy success...");
    }

}
