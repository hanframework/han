package org.hanframework.web.server.channel;

/**
 * @author liuxin
 * @version Id: AbstractChannel.java, v 0.1 2019-05-11 10:33
 */
public abstract class AbstractChannel implements Endpoint, Channel {


  @Override
  public String toString() {
    return getLocalAddress() + " -> " + getRemoteAddress();
  }
}
