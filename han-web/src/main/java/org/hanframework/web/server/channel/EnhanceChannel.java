package org.hanframework.web.server.channel;

import java.net.InetSocketAddress;

/**
 * @author liuxin
 * @version Id: Channel.java, v 0.1 2019-05-11 10:32
 */
public interface EnhanceChannel {

    String getChannelId();

    InetSocketAddress getRemoteAddress();

    boolean isConnected();

    boolean hasAttribute(String key);

    Object getAttribute(String key);

    void setAttribute(String key, Object value);

    void removeAttribute(String key);

    void send(Object message);

    void send(Object message, long timeout,boolean close);

    void sendAndClose(Object message);

    void disconnected();

    void exceptionCaught(Throwable throwable);


}
