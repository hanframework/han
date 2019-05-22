package org.hanframework.web.server.handler;

import org.hanframework.web.server.channel.Channel;

/**
 * @author liuxin
 * @version Id: ChannelHandler.java, v 0.1 2019-05-11 14:47
 */
public interface HanChannelHandler {

  /**
   * 接受连接
   *
   * @param channel channel.
   * @throws RemotingException 远程调用异常
   */
  void connected(Channel channel) throws RemotingException;

  /**
   * 断开连接
   *
   * @param channel channel.
   * @throws RemotingException 远程调用异常
   */
  void disconnected(Channel channel) throws RemotingException;

  /**
   * 发送消息
   *
   * @param channel channel.
   * @param message message.
   * @throws RemotingException 远程调用异常
   */
  void sent(Channel channel, Object message) throws RemotingException;

  /**
   * 接受消息
   *
   * @param channel channel.
   * @param message message.
   * @throws RemotingException 远程调用异常
   */
  void received(Channel channel, Object message) throws RemotingException;

  /**
   * 捕捉异常
   *
   * @param channel   channel.
   * @param exception exception.
   * @throws RemotingException 远程调用异常
   */
  void caught(Channel channel, Throwable exception) throws RemotingException;

}
