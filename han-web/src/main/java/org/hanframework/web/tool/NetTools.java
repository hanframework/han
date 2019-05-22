package org.hanframework.web.tool;

import java.net.InetSocketAddress;

/**
 * @author liuxin
 * @version Id: NetTools.java, v 0.1 2019-05-11 16:33
 */
public class NetTools {
  public static String toAddressString(InetSocketAddress address) {
    return address.getAddress().getHostAddress() + ":" + address.getPort();
  }

}
