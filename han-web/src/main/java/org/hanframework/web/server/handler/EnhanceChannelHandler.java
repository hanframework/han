package org.hanframework.web.server.handler;

import org.hanframework.web.server.channel.EnhanceChannel;

/**
 * @author liuxin
 * @version Id: ChannelHandler.java, v 0.1 2019-05-11 14:47
 */
public interface EnhanceChannelHandler {

  /**
   * 接受连接
   *
   * @param channel channel.
   * @throws RemotingException 远程调用异常
   */
  void connected(EnhanceChannel channel) throws RemotingException;

  /**
   * 断开连接
   *
   * @param channel channel.
   * @throws RemotingException 远程调用异常
   */
  void disconnected(EnhanceChannel channel) throws RemotingException;

  /**
   * 发送消息
   *
   * @param channel channel.
   * @param message message.
   * @throws RemotingException 远程调用异常
   */
  void sent(EnhanceChannel channel, Object message) throws RemotingException;

  /**
   * 接受消息
   *
   * @param channel channel.
   * @param message message.
   * @throws RemotingException 远程调用异常
   */
  void received(EnhanceChannel channel, Object message) throws RemotingException;

  /**
   * 捕捉异常
   *
   * @param channel   channel.
   * @param exception exception.
   * @throws RemotingException 远程调用异常
   */
  void caught(EnhanceChannel channel, Throwable exception) throws RemotingException;

}
