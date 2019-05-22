package org.hanframework.beans.beandefinition;

import org.hanframework.beans.beanfactory.impl.ObjectFactory;
import org.hanframework.beans.parse.ConfigurationBeanMethod;

/**
 * 可配置的
 *
 * @author liuxin
 * @version Id: ConfigurableBeanDefinition.java, v 0.1 2019-01-31 15:07
 */
public interface ConfigurableBeanDefinition extends LifeCycleOnReadyDefinition {

    /**
     * 设置自定义的实例工厂
     *
     * @param customerInstantiationFactory 自定义bean的实例化工厂
     */
    void setCustomerInstantiationFactory(ObjectFactory customerInstantiationFactory);

    /**
     * 主要处理@Configuration注解下，标记在方法上的Bean。
     * 当解析到这种方法,会创建一个ConfigurationBeanMethod对象。
     * 实例化时候根据ConfigurationBeanMethod进行实例
     *
     * @param configurationBeanMethod 配置类的方式获取
     */
    void setConfigurationBeanMethod(ConfigurationBeanMethod configurationBeanMethod);

    /**
     * 设置初始化方法,用户自定义的
     *
     * @param initMethodName 初始化方法
     */
    void setInitMethodName(String initMethodName);

    /**
     * 设置销毁方法
     *
     * @param destroyMethodName 销毁方法
     */
    void setDestroyMethodName(String destroyMethodName);

    /**
     * 设置bean类型
     *
     * @param beanClass 原始字节码
     */
    void setBeanClass(Class beanClass);

    /**
     * Bean的名字
     *
     * @param beanName bean名字
     */
    void setBeanName(String beanName);

    /**
     * 设置当前bean声明的域对象即单例还是原型
     *
     * @param scope 域名
     */
    void setScope(String scope);

    /**
     * 设置是否默认注入
     * 当注入的bean在IOC容器中是存在两个或者多个,此时会选择注入Primary为True的注入
     * 相同类型又相同名字的Bean的Primary都为True则异常
     *
     * @param primary 布尔类型
     */
    void setPrimary(boolean primary);

    /**
     * 设置是否懒加载
     * 默认是懒加载
     *
     * @param lazyInit 布尔类型
     */
    void setLazyInit(boolean lazyInit);

    /**
     * 是否只支持实例化后,直接返回。
     * 默认为false,及实例化后还要经过前后处理器处理后返回
     *
     * @return true
     */
    boolean isBeforeInstantiationResolved();
}
