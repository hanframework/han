package org.hanframework.beans;

import org.hanframework.beans.beandefinition.ConstructorArgumentValues;
import org.hanframework.beans.beandefinition.ConstructorMetadata;
import org.hanframework.beans.beandefinition.ValueHolder;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.reflection.ClassTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author liuxin
 * @version Id: ConstructorTools.java, v 0.1 2018-12-14 15:50
 */
public class ConstructorBuilder {

    private static ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public static Optional<List<ConstructorMetadata>> buildConstructors(Class beanCls) {
        List<ConstructorMetadata> constructorInfoList = new ArrayList<>();
        List<Constructor> constructors = Arrays.asList(beanCls.getDeclaredConstructors());
        for (Constructor constructor : constructors) {
            ConstructorArgumentValues cas = new ConstructorArgumentValues();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(constructor);
            Class[] parameterTypes = constructor.getParameterTypes();
            Parameter[] parameters = constructor.getParameters();
            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            for (int i = 0; i < parameterNames.length; i++) {
                String shortName = ClassTools.getShortName(parameterTypes[i]);
                ValueHolder constructorArgumentValue = new ValueHolder(parameterTypes[i],
                        i, parameterNames[i], shortName, Arrays.asList(parameterAnnotations[i]), parameters[i]);

                cas.addConstructorArgumentValue(constructorArgumentValue);
            }
            ConstructorMetadata constructorInfo = new ConstructorMetadata();
            constructorInfo.setConstructorAnnotations(Arrays.asList(constructor.getDeclaredAnnotations()));
            constructorInfo.setConstructorArgumentValues(cas);
            constructorInfo.setOriginalConstructor(constructor);
            constructorInfoList.add(constructorInfo);
        }
        return !constructorInfoList.isEmpty() ? Optional.of(constructorInfoList) : Optional.empty();
    }

}
