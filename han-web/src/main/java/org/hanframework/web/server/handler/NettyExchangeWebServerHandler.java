package org.hanframework.web.server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.hanframework.web.server.channel.EnhanceChannel;
import org.hanframework.web.server.channel.DefaultEnhanceChannel;
import org.hanframework.web.tool.NetTools;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 原生线程模型非常干净,我们通过重新定义Channel的方式,使其与容器处理逻辑所隔离。通过将Netty原生Channel 转换成 HanChannel
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
public class NettyExchangeWebServerHandler extends ChannelDuplexHandler {

    private final Map<String, EnhanceChannel> channels = new ConcurrentHashMap();

    private EnhanceChannelHandler channelHandler;

    public NettyExchangeWebServerHandler(EnhanceChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        DefaultEnhanceChannel channel = DefaultEnhanceChannel.getOrAddChannel(ctx.channel());
        try {
            if (channel != null) {
                channels.put(NetTools.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), channel);
            }
            channelHandler.connected(channel);
        } finally {
            //连接断开就移除
            DefaultEnhanceChannel.removeChannelIfDisconnected(ctx.channel());
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        DefaultEnhanceChannel channel = DefaultEnhanceChannel.getOrAddChannel(ctx.channel());
        try {
            channels.remove(NetTools.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()));
            channelHandler.disconnected(channel);
        } finally {
            DefaultEnhanceChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DefaultEnhanceChannel channel = DefaultEnhanceChannel.getOrAddChannel(ctx.channel());
        try {
            channelHandler.received(channel, msg);
        } finally {
            DefaultEnhanceChannel.removeChannelIfDisconnected(ctx.channel());
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
        DefaultEnhanceChannel channel = DefaultEnhanceChannel.getOrAddChannel(ctx.channel());
        try {
            channelHandler.sent(channel, msg);
        } finally {
            DefaultEnhanceChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        DefaultEnhanceChannel channel = DefaultEnhanceChannel.getOrAddChannel(ctx.channel());
        try {
            channelHandler.caught(channel, cause);
        } finally {
            DefaultEnhanceChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }


}
