package org.hanframework.context.listener;

import org.hanframework.context.listener.event.ApplicationEvent;

/**
 * @author liuxin
 * @version Id: ApplicationListener.java, v 0.1 2018-12-10 19:49
 */
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
  /**
   * 系统时间
   * @param event
   */
  void onApplicationEvent(E event);
}
