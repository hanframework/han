package org.hanframework.beans.beanfactory.impl;

import org.hanframework.beans.beanfactory.BeanNameGenerator;
import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.factorybean.FactoryBean;
import org.hanframework.tool.reflection.ClassTools;
import org.hanframework.tool.string.StringTools;

/**
 * @author liuxin
 * @version Id: DefaultBeanNameGenerator.java, v 0.1 2018/10/26 6:15 PM
 */
public class DefaultBeanNameGenerator implements BeanNameGenerator {

    /**
     * 默认就是首字母小写
     * 如果是FactoryBean就在前面加$
     * @param definition
     * @return
     */
    @Override
    public String generateBeanName(BeanDefinition definition) {
        String beanName = definition.getBeanName();
        return StringTools.isEmpty(beanName)?generateBeanName(definition.getOriginClass()):beanName;
    }

    @Override
    public String generateBeanName(Class beanClass) {
        boolean factoryBean = FactoryBean.class.isAssignableFrom(beanClass);
        return factoryBean? BeanFactory.FACTORY_BEAN_PREFIX+ StringTools.uncapitalize(ClassTools.getShortName(beanClass)):StringTools.uncapitalize(ClassTools.getShortName(beanClass));
    }
}
