package org.hanframework.autoconfigure;

import org.hanframework.beans.annotation.HanComponent;
import org.hanframework.beans.beanfactory.impl.AbstractAutowireCapableBeanFactory;
import org.hanframework.beans.parse.ConfigurableBeanDefinitionParser;

import java.lang.annotation.*;

/**
 * 配置的标记,当一个Bean被标记成为一个配置类
 * 此时就会从被标记的类上去查询被@HanBean标记的方法,如果查询到这样的方法存在
 * 此时也会为每个方法生成一个BeanDefinition,并设置ConfigurationBeanMethod属性,
 * 当被设置了该属性,那么该实例的创建就不是通过构造实例或者直接实例而变成在执行实例化操作时候,
 * 去执行该配置方法(ConfigurationBeanMethod)生成实例。
 *
 * @author liuxin
 * @version Id: Configuration.java, v 0.1 2018-12-14 15:37
 * @see AbstractAutowireCapableBeanFactory
 * @see ConfigurableBeanDefinitionParser 解析器
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HanComponent
@Documented
public @interface Configuration {
}
