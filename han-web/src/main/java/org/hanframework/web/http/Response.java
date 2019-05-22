package org.hanframework.web.http;


import org.hanframework.web.server.HttpResponseStatus;

/**
 * @author liuxin
 * @version Id: Response.java, v 0.1 2019-05-08 19:24
 */
public class Response {

  private boolean success;

  private Object result;

  private HttpResponseStatus httpResponseStatus;

  public Response(HttpResponseStatus httpResponseStatus) {
    this.httpResponseStatus = httpResponseStatus;
  }

  public Response(Object result) {
    this.success = true;
    this.result = result;
    this.httpResponseStatus = HttpResponseStatus.OK;
  }

  public boolean isSuccess() {
    return success;
  }

  public Object getResult() {
    return result;
  }

  public HttpResponseStatus getHttpResponseStatus() {
    return httpResponseStatus;
  }
}
