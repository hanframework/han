package org.hanframework.web.annotation;

import org.hanframework.beans.annotation.HanComponent;

import java.lang.annotation.*;

/**
 * @author liuxin
 * @version Id: WebSocket.java, v 0.1 2019-05-10 17:58
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HanComponent
public @interface WebSocket {
  String value() default "";
}
