package org.hanframework.tool.source;

/**
 * @author liuxin
 * @version Id: SourceObject.java, v 0.1 2019-05-10 17:01
 */
public class SourceObject implements java.io.Serializable {

  protected transient Object source;

  public SourceObject(Object source) {
    if (source == null) {
      throw new IllegalArgumentException("null source");
    }
    this.source = source;
  }

  public Object getSource() {
    return source;
  }

  /**
   * Returns a String representation of this EventObject.
   *
   * @return A a String representation of this EventObject.
   */
  @Override
  public String toString() {
    return getClass().getName() + "[source=" + source + "]";
  }
}
