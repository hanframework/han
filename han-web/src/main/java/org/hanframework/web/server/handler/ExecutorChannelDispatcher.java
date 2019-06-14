package org.hanframework.web.server.handler;

import org.hanframework.web.server.channel.EnhanceChannel;

/**
 * @author liuxin
 * @version Id: ChannelDispatcher.java, v 0.1 2019-06-12 22:51
 */
public interface ExecutorChannelDispatcher {

    void dispatch(EnhanceChannel channel, Object message);

}
