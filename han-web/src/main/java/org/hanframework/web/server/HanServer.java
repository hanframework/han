package org.hanframework.web.server;

import org.hanframework.beans.sort.Order;

/**
 * @author liuxin
 * @version Id: HanServer.java, v 0.1 2019-03-27 10:31
 */
@Order(-1)
public interface HanServer {

    int port();

    void open();

    void close();
}
