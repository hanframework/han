package org.hanframework.beans;

import org.hanframework.beans.beandefinition.ConfigurableBeanDefinition;
import org.hanframework.beans.annotation.Lazy;
import org.hanframework.beans.annotation.Primary;
import org.hanframework.beans.annotation.Scope;
import org.hanframework.beans.beandefinition.ConfigurableBeanDefinition;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.tool.annotation.AnnotationTools;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * 凡是
 *
 * @author liuxin
 * @version Id: BeanDefinitionBuider.java, v 0.1 2019-01-31 14:17
 */
public class ConfigurableDefinitionBuilder {

    /**
     * @param configurableBeanDefinition Bean的描述信息
     * @param declaredAnnotations 关于Bean的声明注解
     */
    public static void lifeCycleBuilder(ConfigurableBeanDefinition configurableBeanDefinition, Annotation[] declaredAnnotations) {
        Lazy annotation = AnnotationTools.findAnnotation(declaredAnnotations, Lazy.class);
        Primary primary = AnnotationTools.findAnnotation(declaredAnnotations, Primary.class);
        Scope scope = AnnotationTools.findAnnotation(Arrays.asList(declaredAnnotations), Scope.class);
        configurableBeanDefinition.setLazyInit(annotation == null ? true : annotation.value());
        configurableBeanDefinition.setScope(scope==null?ConfigurableBeanFactory.SCOPE_SINGLETON:scope.value());
        configurableBeanDefinition.setPrimary(null==primary?false:true);
    }

}
