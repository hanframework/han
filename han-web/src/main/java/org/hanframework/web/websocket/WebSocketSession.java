package org.hanframework.web.websocket;

import lombok.Getter;

/**
 * @author liuxin
 * @version Id: WebSocketSession.java, v 0.1 2019-06-11 14:58
 */
public class WebSocketSession {

    @Getter
    private final String url;

    @Getter
    private Type type;

    /**
     * 客户端到服务端数据
     */
    private byte[] data;

    /**
     * 服务端到客户端数据
     */
    private byte[] res;

    public WebSocketSession(Type type, byte[] data, String uri) {
        this.type = type;
        this.data = data;
        this.url = uri;
    }

    public byte[] getData() {
        return data;
    }

    public String content() {
        if (type == Type.TEXT) {
            return new String(data);
        }
        throw new UnsupportedOperationException("当前Type格式:" + type);
    }

    public void clearClientData() {
        this.data = null;
    }

    public void write(byte[] data) {
        this.res = data;
    }

    public void write(String text) {
        write(text.getBytes());
    }

    enum Type {
        /**
         * ping
         */
        PING,
        /**
         * ping的响应数据
         */
        PONG,
        /**
         * 文本数据
         */
        TEXT,
        /**
         * 二进制数据
         */
        BYTE,
        /**
         * 关闭
         */
        CLOSE,
        /**
         * 超大文本或者二进制数据
         */
        CONT;
    }
}
