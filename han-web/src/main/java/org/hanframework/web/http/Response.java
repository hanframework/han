package org.hanframework.web.http;


import lombok.Getter;
import org.hanframework.web.server.HttpResponseStatus;

/**
 * @author liuxin
 * @version Id: Response.java, v 0.1 2019-05-08 19:24
 */
public class Response {

    private boolean success;

    private Object result;

    private HttpResponseStatus httpResponseStatus;

    @Getter
    private HttpHeaders httpHeaders;

    public Response(HttpResponseStatus httpResponseStatus) {
        this(false, null, httpResponseStatus, null);
    }

    public Response(Object result) {
        this(true, result, HttpResponseStatus.OK, null);
    }

    public Response(Object result, HttpHeaders httpHeaders) {
        this(true, result, HttpResponseStatus.OK, httpHeaders);
    }


    public Response(boolean success, Object result, HttpResponseStatus httpResponseStatus, HttpHeaders httpHeaders) {
        this.success = success;
        this.result = result;
        this.httpResponseStatus = httpResponseStatus;
        this.httpHeaders = httpHeaders;
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
