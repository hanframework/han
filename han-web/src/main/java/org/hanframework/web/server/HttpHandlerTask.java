package org.hanframework.web.server;

import org.hanframework.beans.DefaultParameterNameDiscoverer;
import org.hanframework.beans.postprocessor.impl.MethodParameter;
import org.hanframework.web.core.MappingRegistry;
import org.hanframework.web.handler.HandlerMethod;
import org.hanframework.web.handler.URL;
import org.hanframework.web.http.HttpHeaders;
import org.hanframework.web.http.Request;
import org.hanframework.web.http.Response;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author liuxin
 * @version Id: HttpHandlerTask.java, v 0.1 2019-05-09 16:03
 */
public class HttpHandlerTask implements Callable<Response> {
  public final Request request;

  public final MappingRegistry mappingRegistry;

  public HttpHandlerTask(Request request, MappingRegistry mappingRegistry) {
    this.request = request;
    this.mappingRegistry = mappingRegistry;
  }

  @Override
  public Response call() throws Exception {
    Response response = new Response("");
    URL url = new URL(request.getUrl());
    HandlerMethod selector = mappingRegistry.selector(url);
    if (selector == null) {
      response = new Response(HttpResponseStatus.NOT_FOUND);
      return response;
    }
    Method method = selector.getMethod();
    Object result = method.invoke(selector.getBean(), args(request, selector.getParameters()));
    Class<?> returnType = method.getReturnType();
    //判断返回结果
    if (returnType == String.class) {
      response = new Response(result);
    } else if (returnType == Void.class) {
      response = new Response("");
    }
    return response;
  }

  public Object[] args(Request request, MethodParameter[] parameters) {
    Object[] args = new Object[parameters.length];
    Map<String, Object> paramMap = request.getParamMap();
    for (int i = 0; i < parameters.length; i++) {
      MethodParameter parameter = parameters[i];
      parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
      String parameterName = parameter.getParameterName();
      Class<?> parameterType = parameter.getParameterType();
      if (parameterType == HttpHeaders.class) {
        args[i] = request.getHttpHeaders();
      } else {
        args[i] = paramMap.getOrDefault(parameterName, null);
      }
    }
    return args;
  }

}
