package org.hanframework.context.listener.event;

/**
 * @author liuxin
 * @version Id: ApplicationEvent.java, v 0.1 2018-12-10 19:51
 */
public class ApplicationEvent<T> extends EventObject<T> {

  private final long timestamp;

  public ApplicationEvent(T source) {
    super(source);
    this.timestamp = System.currentTimeMillis();
  }


  public final long getTimestamp() {
    return this.timestamp;
  }
}
