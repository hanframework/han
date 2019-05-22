package org.hanframework.beans.exception;

import org.hanframework.tool.reflection.ClassTools;

/**
 * @author liuxin
 * @version Id: BeanInstantiationException.java, v 0.1 2019-03-15 10:31
 */
public class BeanInstantiationException extends RuntimeException {

  public BeanInstantiationException(Class cls, String msg) {
    super(ClassTools.getQualifiedName(cls) + ":" + msg);
  }

  public BeanInstantiationException(String beanName, String msg) {
    super(beanName + ":" + msg);
  }

  public BeanInstantiationException(String msg, Throwable throwable) {
    super(msg, throwable);
  }



  public BeanInstantiationException(Class cls, String msg, Throwable throwable) {
    super(ClassTools.getQualifiedName(cls) + ":" + msg, throwable);
  }
}
