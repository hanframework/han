package org.hanframework.beans.beanfactory;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.condition.ConditionHandler;
import org.hanframework.beans.postprocessor.BeanPostProcessor;
import org.hanframework.env.Configuration;
import org.hanframework.env.resolver.MultiPropertyResolver;
import java.util.List;
import java.util.Map;

/**
 * @author liuxin
 * @version Id: ConfigurableBeanFactory.java, v 0.1 2018/10/12 6:29 PM
 */
public interface ConfigurableBeanFactory extends SingletonBeanRegistry {
    /**
     * Scope identifier for the standard singleton scope: "singleton".
     * Custom scopes can be added via {@code registerScope}.
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * Scope identifier for the standard prototype scope: "prototype".
     * Custom scopes can be added via {@code registerScope}.
     */
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 添加前后处理器
     *
     * @param beanPostProcessor
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);


    /**
     * 获取所有的Bean解析器
     *
     * @return
     */
    List<BeanPostProcessor> getBeanPostProcessors();

    /**
     * 获取处理器个数
     *
     * @return
     */
    int getBeanPostProcessorCount();


    /**
     * 判断是否是FactoryBean
     *
     * @param name
     * @return
     */
    boolean isFactoryBean(String name);

    /**
     * 判断是否循环依赖
     *
     * @param beanName
     * @return
     */
    boolean isCurrentlyInCreation(String beanName);

    /**
     * 可配置的配置信息
     *
     * @param multiPropertyResolver 配置信息
     */
    void setPropertyResolver(MultiPropertyResolver multiPropertyResolver);

    /**
     * 配置类
     *
     * @param configuration
     */
    void setConfiguration(final Configuration configuration);

    Configuration getConfiguration();

    Map<String, BeanDefinition> getBeanDefinition();
    /**
     * 单例提前处理
     */
    void preInstantiateSingletons();
    /**
     * 销毁单例的对象
     */
    @Override
    void destroySingletons();
    /**
     * Bean条件处理器
     *
     * @return
     */
    ConditionHandler getConditionHandler();

}
