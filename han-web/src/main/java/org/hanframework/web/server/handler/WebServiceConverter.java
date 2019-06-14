package org.hanframework.web.server.handler;

import org.hanframework.web.http.Request;
import org.hanframework.web.server.channel.EnhanceChannel;
import org.hanframework.web.websocket.WebSocketSession;

/**
 * @author liuxin
 * @version Id: WebServiceConverter.java, v 0.1 2019-06-12 23:34
 */
public interface WebServiceConverter {

    WebSocketSession buildWebSocketSession(EnhanceChannel channel,Object message);

    Request buildHttpRequest(EnhanceChannel channel,Object message);
}
