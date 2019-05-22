package org.hanframework.core.classpathscan;

/**
 * @author liuxin
 * @version Id: ScanClassException.java, v 0.1 2019-05-20 13:50
 */
public class ScanClassException extends RuntimeException {
    public ScanClassException() {
    }

    public ScanClassException(String message) {
        super(message);
    }

    public ScanClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScanClassException(Throwable cause) {
        super(cause);
    }

    public ScanClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
