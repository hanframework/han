package org.hanframework.web.http;

import org.hanframework.web.annotation.HttpMethod;
import org.hanframework.web.handler.URL;


/**
 * @author liuxin
 * @version Id: HttpRequest.java, v 0.1 2019-03-27 10:40
 */
public interface HttpRequest extends HttpMessage {
  HttpMethod getMethod();

  String getMethodValue();

  URL getURI();
}
