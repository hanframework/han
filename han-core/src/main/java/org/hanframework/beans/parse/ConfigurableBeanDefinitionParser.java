package org.hanframework.beans.parse;


import org.hanframework.autoconfigure.Configuration;
import org.hanframework.beans.ConfigurableDefinitionBuilder;
import org.hanframework.beans.annotation.HanBean;
import org.hanframework.beans.annotation.Lazy;
import org.hanframework.beans.annotation.Primary;
import org.hanframework.beans.annotation.Scope;
import org.hanframework.beans.beandefinition.ConfigurableBeanDefinition;
import org.hanframework.beans.beandefinition.GenericBeanDefinition;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.parse.exception.BeanDefinitionParserException;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.reflection.ClassTools;
import org.hanframework.tool.string.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author liuxin
 * @version Id: ConfigurableBeanDefinitionParser.java, v 0.1 2019-01-30 09:53
 */
public class ConfigurableBeanDefinitionParser extends AbstractCustomerBeanDefinitionParser {

    @Override
    public boolean allowParse(Class cls) {
        return AnnotationTools.isContainsAnnotation(cls, Configuration.class);
    }

    /**
     * 方法Bean的实例条件
     * 1. 执行体
     * 2. 方法实参(可能是Bean,可能是@Value值)
     */
    @Override
    public void doCustomerBeanDefinitionParser(Class beanCls, final List<ConfigurableBeanDefinition> configurableBeanDefinitions) throws BeanDefinitionParserException {
        List<Method> declaredMethods = AnnotationTools.findMethods(beanCls, HanBean.class);
        for (Method beanMethod : declaredMethods) {
            //通用的放这里做，开发者能看到的都是他要关心的
            ConfigurableBeanDefinition configurableBeanDefinition = new GenericBeanDefinition();
            //如果设置实例化自定义的实例化
            configurableBeanDefinition.setConfigurationBeanMethod(new ConfigurationBeanMethod(beanMethod, beanMethod.getParameters(), beanCls));
            //从方法上读取注解
            ConfigurableDefinitionBuilder
                    .lifeCycleBuilder(configurableBeanDefinition, beanMethod.getDeclaredAnnotations());
            HanBean hanBean = AnnotationTools.findAnnotation(beanMethod.getDeclaredAnnotations(), HanBean.class);
            String beanInnerName = (null == hanBean | StringTools.isBlank(hanBean.name())) ? StringTools.uncapitalize(ClassTools.getShortName(beanMethod.getReturnType())) : hanBean.name();
            configurableBeanDefinition.setBeanName(beanInnerName);
            configurableBeanDefinition.setBeanClass(beanMethod.getReturnType());
            configurableBeanDefinition.setInitMethodName(hanBean.initMethod());
            configurableBeanDefinition.setDestroyMethodName(hanBean.destroyMethod());
            configurableBeanDefinition.setPrimary(AnnotationTools.isContainsAnnotation(beanCls, Primary.class));
            configurableBeanDefinitions.add(configurableBeanDefinition);
        }
    }
}
