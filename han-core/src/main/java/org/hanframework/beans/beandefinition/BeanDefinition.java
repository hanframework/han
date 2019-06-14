package org.hanframework.beans.beandefinition;

import org.hanframework.tool.annotation.type.AnnotationMetadata;

import java.util.List;

/**
 * @author liuxin
 * @date 2017/11/17 下午11:53
 */
public interface BeanDefinition extends LifeCycleOnReadyDefinition {

    /**
     * 字节码名字
     *
     * @return 类名
     */
    String getOriginClassShortName();

    /**
     * Bean的名字,改名字是最后存在IOC容器的名字。
     * FactoryBean本体的名字规则在前加&符号
     *
     * @return bean名称
     */
    String getBeanName();

    /**
     * 是否是泛型Bean
     *
     * @return true: 工厂bean
     */
    boolean isFactoryBean();

    /**
     * 构造信息
     *
     * @return 构造信息
     */
    List<ConstructorMetadata> getConstructorInfo();


    /**
     * 当前Bean的注解信息和类的基本信息
     *
     * @return 类信息类
     */
    AnnotationMetadata getAnnotationMetadata();

    /**
     * 是否单例
     *
     * @return true单例
     */
    boolean isSingleton();

    /**
     * 当前bean声明的域对象即单例还是原型
     * SCOPE_SINGLETON: 单例
     * SCOPE_PROTOTYPE: 原型
     *
     * @return
     */
    String getScope();

    /**
     * 是否懒加载
     *
     * @return true:懒加载
     */
    boolean isLazy();

    /**
     * 是否原型
     *
     * @return true:原型
     */
    boolean isPrototype();

    /**
     * 是否默认注入
     *
     * @return true:唯一 false: 不唯一
     */
    boolean isPrimary();


    int getOrder();

}
