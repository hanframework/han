package org.hanframework.web.server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.hanframework.web.server.channel.Channel;
import org.hanframework.web.server.channel.HanChannel;
import org.hanframework.web.tool.NetTools;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 所有的请求,都会在这里被处理。
 * 将Netty通道API转换为自己的API
 * 1. 连接的保存
 * 2. 读写的执行
 * 3. 异常的处理
 *
 * @author liuxin
 * @version Id: ServerHandler.java, v 0.1 2019-03-27 10:58
 */
@Slf4j
@io.netty.channel.ChannelHandler.Sharable
public class NettyWebServerHandler extends ChannelDuplexHandler {

    private final Map<String, Channel> channels = new ConcurrentHashMap();

    private HanChannelHandler channelHandler;

    public NettyWebServerHandler(HanChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        HanChannel channel = HanChannel.getOrAddChannel(ctx.channel());
        try {
            if (channel != null) {
                channels.put(NetTools.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), channel);
            }
            channelHandler.connected(channel);
        } finally {
            //连接断开就移除
            HanChannel.removeChannelIfDisconnected(ctx.channel());
        }

    }

    /**
     * 端口连接
     *
     * @param ctx 通道上下文
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        HanChannel channel = HanChannel.getOrAddChannel(ctx.channel());
        try {
            channels.remove(NetTools.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()));
            channelHandler.disconnected(channel);
        } finally {
            HanChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HanChannel channel = HanChannel.getOrAddChannel(ctx.channel());
        try {
            channelHandler.received(channel, msg);
        } finally {
            HanChannel.removeChannelIfDisconnected(ctx.channel());
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //运行其他的
        super.write(ctx, msg, promise);
        HanChannel channel = HanChannel.getOrAddChannel(ctx.channel());
        try {
            channelHandler.sent(channel, msg);
        } finally {
            HanChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        HanChannel channel = HanChannel.getOrAddChannel(ctx.channel());
        try {
            channelHandler.caught(channel, cause);
        } finally {
            HanChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }


}
