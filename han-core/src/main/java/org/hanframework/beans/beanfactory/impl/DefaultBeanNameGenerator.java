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
     *
     * @param definition bean描述
     * @return bean名字
     */
    @Override
    public String generateBeanName(BeanDefinition definition) {
        String beanName = definition.getBeanName();
        return StringTools.isEmpty(beanName) ? generateBeanName(definition.getOriginClass()) : beanName;
    }

    /**
     * 生成Bean名字
     *
     * @param beanType bean类型
     * @return bean名字
     */
    @Override
    public String generateBeanName(Class beanType) {
        boolean factoryBean = FactoryBean.class.isAssignableFrom(beanType);
        return factoryBean ? BeanFactory.FACTORY_BEAN_PREFIX + StringTools.uncapitalize(ClassTools.getShortName(beanType)) : StringTools.uncapitalize(ClassTools.getShortName(beanType));
    }
}
