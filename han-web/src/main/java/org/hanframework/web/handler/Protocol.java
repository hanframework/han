package org.hanframework.web.handler;

/**
 * @author liuxin
 * @version Id: Protocol.java, v 0.1 2019-05-21 10:52
 */
public enum Protocol {
    HTTP("http"),
    HTTPS("https"),
    WEB_SOCKET("ws");

    Protocol(java.lang.String protocol) {
        this.protocol = protocol;
    }

    String protocol;

    public String getProtocol() {
        return protocol;
    }}
