package org.hanframework.beans.postprocessor;

import org.hanframework.beans.beandefinition.BeanDefinitionRegistry;
import org.hanframework.beans.beanfactory.postprocessor.BeanFactoryPostProcessor;

/**
 * 运行外部生成BeanDefinition并加入到IOC容器
 * 扩展:
 * 如果是扩展dubbo类型的,只需要将被标记的类生成代理，指定自己的实例化方法即可。
 *
 * @author liuxin
 * @version Id: BeanDefinitionRegistryPostProcessor.java, v 0.1 2019-01-10 11:09
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

    /**
     * 注册器的后置处理接口
     *
     * @param beanDefinitionRegistry BeanDefinition注册器
     */
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry);
}
