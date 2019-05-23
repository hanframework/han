package org.hanframework.beans.beandefinition;

import org.hanframework.beans.DefaultParameterNameDiscoverer;
import org.hanframework.beans.ParameterNameDiscoverer;
import org.hanframework.tool.reflection.ClassTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author liuxin
 * @version Id: ValueHolderFactory.java, v 0.1 2019-05-23 13:55
 */
public final class ValueHolderFactory {
    private static ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();


    public static List<ValueHolder> valueHolder(Method method) {
        List<ValueHolder> valueHolders = new ArrayList<>();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String shortName = ClassTools.getShortName(parameter.getType());
            ValueHolder valueHolder = new ValueHolder(parameter.getType(),
                    i, parameterNames[i], shortName, Arrays.asList(parameter.getDeclaredAnnotations()), parameter);
            valueHolders.add(valueHolder);
        }
        return Collections.unmodifiableList(valueHolders);
    }

    public static List<ValueHolder> valueHolder(Constructor constructor) {
        List<ValueHolder> valueHolders = new ArrayList<>();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(constructor);
        Parameter[] parameters = constructor.getParameters();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        for (int i = 0; i < parameterNames.length; i++) {
            String shortName = ClassTools.getShortName(parameters[i].getType());
            ValueHolder valueHolder = new ValueHolder(parameters[i].getType(),
                    i, parameterNames[i], shortName, Arrays.asList(parameterAnnotations[i]), parameters[i]);
            valueHolders.add(valueHolder);
        }
        return Collections.unmodifiableList(valueHolders);
    }
}
