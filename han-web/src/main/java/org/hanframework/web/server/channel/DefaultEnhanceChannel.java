package org.hanframework.web.server.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import org.hanframework.web.http.HttpConverter;
import org.hanframework.web.http.Response;
import org.hanframework.web.server.handler.RemotingException;
import org.hanframework.web.websocket.WebSocketConverter;
import org.hanframework.web.websocket.WebSocketSession;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liuxin
 * @version Id: HanChannel.java, v 0.1 2019-05-11 10:31
 */
public class DefaultEnhanceChannel extends AbstractEnhanceChannel {

    /**
     * netty 通道
     */
    private final Channel channel;

    private final String channelId;

    private static final ConcurrentMap<Channel, DefaultEnhanceChannel> channelMap = new ConcurrentHashMap<Channel, DefaultEnhanceChannel>();

    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    private DefaultEnhanceChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == null;");
        }
        this.channel = channel;
        this.channelId = buildId(channel);
    }

    public String buildId(Channel channel) {
        return channel.id().asLongText();
    }

    public String getChannelId() {
        return channelId;
    }

    public static DefaultEnhanceChannel getOrAddChannel(Channel ch) {
        if (ch == null) {
            return null;
        }
        DefaultEnhanceChannel ret = channelMap.get(ch);
        if (ret == null) {
            DefaultEnhanceChannel nettyChannel = new DefaultEnhanceChannel(ch);
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
        send(message, 0, false);
    }

    @Override
    public void send(Object message, long timeout, boolean close) {
        Object httpResponse;
        if (Response.class == message.getClass()) {
            Response response = (Response) message;
            httpResponse = HttpConverter.buildHttpResponse(response, null);
        } else if (WebSocketSession.class == message.getClass()) {
            WebSocketSession webSocketSession = (WebSocketSession) message;
            httpResponse = WebSocketConverter.buildWebSocketFrame(webSocketSession);
        } else {
            throw new UnsupportedOperationException("Only support Response.class and  WebSocketSession.class,don't support:" + message.getClass());
        }
        //默认 false
        boolean state;
        try {
            ChannelFuture future = channel.writeAndFlush(httpResponse);
            if (close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            //判断
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
        send(message,0,true);
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
