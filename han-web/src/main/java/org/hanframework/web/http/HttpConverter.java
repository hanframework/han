package org.hanframework.web.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import org.hanframework.web.annotation.HttpMethod;
import org.hanframework.web.server.HttpParamDecoder;

import java.util.Map;

/**
 * @author liuxin
 * @version Id: HttpConverter.java, v 0.1 2019-05-13 18:50
 */
public final class HttpConverter {

    public static boolean match(Object message) {
        return message instanceof FullHttpRequest;
    }

    public static Request buildHttpRequest(String requestId, Object message) {

        if (message instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) message;
            HttpMethod httpMethod = HttpMethod.valueOf(fullHttpRequest.method().name());
            HttpHeaders httpHeaders = new HttpHeaders();
            for (Map.Entry<String, String> entry : fullHttpRequest.headers()) {
                httpHeaders.set(entry.getKey(), entry.getValue());
            }
            HttpVersion httpVersion = fullHttpRequest.protocolVersion();
            //封装请求
            return Request.builder().url(fullHttpRequest.uri())
                    .httpVersion(httpVersion.text())
                    .httpHeaders(httpHeaders).httpMethod(httpMethod)
                    .paramMap(HttpParamDecoder.parse(fullHttpRequest)).id(requestId)
                    .build();
        }
        //不存在这种情况
        return null;
    }

    public static HttpResponse buildHttpResponse(Response response, Throwable throwable) {
        ByteBuf buf;
        io.netty.handler.codec.http.HttpHeaders headers = headers(response.getHttpHeaders());
        if (null != throwable) {
            HttpResponseStatus internalServerError = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            buf = Unpooled.buffer(internalServerError.reasonPhrase().length());
            buf.writeBytes(internalServerError.reasonPhrase().getBytes());
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, internalServerError, buf, headers, headers);
        }
        if (response.isSuccess()) {
            String result = (String) response.getResult();
            buf = Unpooled.buffer(result.length());
            buf.writeBytes(result.getBytes());
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf, headers, headers);
        } else {
            buf = Unpooled.buffer(response.getHttpResponseStatus().reasonPhrase().length());
            buf.writeBytes(response.getHttpResponseStatus().reasonPhrase().getBytes());
            HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(response.getHttpResponseStatus().code());
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus, buf, headers, headers);
        }
    }


    public static HttpResponse buildFailHttpResponse(Throwable throwable) {
        ByteBuf buf;
        HttpResponseStatus internalServerError = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        buf = Unpooled.buffer(internalServerError.reasonPhrase().length());
        buf.writeBytes(internalServerError.reasonPhrase().getBytes());
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, internalServerError, buf);

    }


    private static io.netty.handler.codec.http.HttpHeaders headers(HttpHeaders hanHttpHeaders) {
        if (null != hanHttpHeaders) {
            DefaultHttpHeaders httpHeaders = new DefaultHttpHeaders();
            for (Map.Entry<String, Object> entry : hanHttpHeaders.getHeads().entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
            return httpHeaders;
        }
        return new DefaultHttpHeaders();

    }


}
