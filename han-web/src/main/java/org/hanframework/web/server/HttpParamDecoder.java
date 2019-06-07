package org.hanframework.web.server;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuxin
 * @version Id: HttpParamDecoder.java, v 0.1 2019-05-10 10:25
 */
public class HttpParamDecoder {

  /**
   * 解析请求参数
   *
   * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
   */
  public static Map<String, Object> parse(FullHttpRequest request) {
    Map<String, Object> paramMap = new HashMap(10);
    try {
      HttpMethod method = request.method();
      if (HttpMethod.GET == method) {
        // 是GET请求
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        decoder.parameters().entrySet().forEach(entry -> {
          paramMap.put(entry.getKey(), entry.getValue().get(0));
        });
      } else if (HttpMethod.POST == method) {
        // 是POST请求
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        if (request.content().isReadable()) {
          String json = request.content().toString(CharsetUtil.UTF_8);
          System.err.println("不是表单:" + json);
        }
        decoder.offer(request);
        List<InterfaceHttpData> paramList = decoder.getBodyHttpDatas();
        for (InterfaceHttpData param : paramList) {
          InterfaceHttpData.HttpDataType httpDataType = param.getHttpDataType();
          if (httpDataType == InterfaceHttpData.HttpDataType.Attribute) {
            paramMap.put(param.getName(), ((Attribute) param).getValue());
          } else if (httpDataType == InterfaceHttpData.HttpDataType.FileUpload) {

          } else if (httpDataType == InterfaceHttpData.HttpDataType.InternalAttribute) {

          }
        }
      }
    } catch (Exception e) {

    }
    return Collections.unmodifiableMap(paramMap);
  }
}
