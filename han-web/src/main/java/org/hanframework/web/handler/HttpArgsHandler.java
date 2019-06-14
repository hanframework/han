package org.hanframework.web.handler;

import org.hanframework.beans.beandefinition.ValueHolder;
import org.hanframework.web.http.HttpHeaders;
import org.hanframework.web.http.Request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 由容器管理
 *
 * @author liuxin
 * @version Id: HttpArgsHandler.java, v 0.1 2019-06-10 22:20
 */
public class HttpArgsHandler extends AbstractArgsHandler {

    /**
     * TODO 根据请求方法管理
     * <p>
     * 根据不同的请求类型,从请求参数中获取值
     *
     * @param request      请求对象
     * @param valueHolders 参数值
     * @return
     */
    @Override
    public Object[] args(Request request, ValueHolder... valueHolders) {
        return args(request, valueHolders);
    }

    @Override
    public Object[] args(Request request, List<ValueHolder> valueHolders) {
        Object[] args = new Object[valueHolders.size()];
        Map<String, Object> paramMap = request.getParamMap();
        for (ValueHolder valueHolder : valueHolders) {
            int index = valueHolder.getSort();
            String parameterName = valueHolder.getVarName();
            Class<?> parameterType = valueHolder.getParameterType();
            if (parameterType == HttpHeaders.class) {
                args[index] = request.getHttpHeaders();
            } else {
                args[index] = paramMap.getOrDefault(parameterName, null);
            }
        }
        return args;
    }
}
