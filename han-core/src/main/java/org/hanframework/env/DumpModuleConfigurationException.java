package org.hanframework.env;

/**
 * @author liuxin
 * @version Id: DumpException.java, v 0.1 2019-05-13 21:48
 */
public class DumpModuleConfigurationException extends RuntimeException {
  public DumpModuleConfigurationException() {
  }

  public DumpModuleConfigurationException(String message) {
    super(message);
  }

  public DumpModuleConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DumpModuleConfigurationException(Throwable cause) {
    super(cause);
  }

  public DumpModuleConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
