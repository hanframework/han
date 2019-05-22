package org.hanframework.web.server.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import org.hanframework.web.http.HttpConverter;
import org.hanframework.web.http.Response;
import org.hanframework.web.server.handler.RemotingException;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liuxin
 * @version Id: HanChannel.java, v 0.1 2019-05-11 10:31
 */
public class HanChannel extends AbstractChannel {

    /**
     * netty 通道
     */
    private final Channel channel;

    private static final ConcurrentMap<Channel, HanChannel> channelMap = new ConcurrentHashMap<Channel, HanChannel>();

    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    private HanChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == null;");
        }
        this.channel = channel;
    }

    public static HanChannel getOrAddChannel(Channel ch) {
        if (ch == null) {
            return null;
        }
        HanChannel ret = channelMap.get(ch);
        if (ret == null) {
            HanChannel nettyChannel = new HanChannel(ch);
            if (ch.isActive()) {
                ret = channelMap.putIfAbsent(ch, nettyChannel);
            }
            if (ret == null) {
                ret = nettyChannel;
            }
        }
        return ret;
    }

    public static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            channelMap.remove(ch);
        }
    }


    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }


    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }


    @Override
    public void send(Object message) {
        send(message, 0);
    }

    @Override
    public void send(Object message, long timeout) {
        HttpResponse httpResponse;
        if (Response.class == message.getClass()) {
            Response response = (Response) message;
            httpResponse = HttpConverter.buildHttpResponse(response, null);
        } else {
            httpResponse = null;
        }
        //默认 false
        boolean state;
        try {
            ChannelFuture future = channel.writeAndFlush(httpResponse);
            if (timeout > 0L) {
                state = future.await(timeout);
            } else {
                return;
            }
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new RemotingException("Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage(), e);
        }
        if (state) {
            throw new RemotingException("Failed to send message " + message + " to " + getRemoteAddress()
                    + "in timeout(" + timeout + "ms) limit");
        }

    }

    @Override
    public void sendAndClose(Object message) {
        channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void disconnected() {
        channel.close();
        removeChannelIfDisconnected(channel);
        attributes.clear();
    }

    @Override
    public void exceptionCaught(Throwable throwable) {
        HttpResponse httpResponse = HttpConverter.buildFailHttpResponse(throwable);
        channel.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
    }


}
