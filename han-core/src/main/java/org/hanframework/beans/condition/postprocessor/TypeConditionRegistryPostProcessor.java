package org.hanframework.beans.condition.postprocessor;

import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.convert.TypeConverterRegistry;
import org.hanframework.beans.beanfactory.postprocessor.BeanFactoryPostProcessor;
import org.hanframework.beans.condition.ConditionFilterRegistry;
import org.hanframework.env.Configuration;


/**
 * @author liuxin
 * @version Id: PreConditionBeanFactoryPostProcessor.java, v 0.1 2019-01-29 10:42
 */
public abstract class TypeConditionRegistryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableBeanFactory configurableBeanFactory) {
        Configuration configuration = configurableBeanFactory.getConfiguration();
        doRegistryTypeConverter(configuration.getTypeConverterRegistry());
        doRegistryBeanConditionFilter(configuration.getConditionFilterRegistry());
    }

    /**
     * 注册用户自己的类型转换器
     *
     * @param typeConverterRegistry 类型转换注册器
     */
    abstract void doRegistryTypeConverter(TypeConverterRegistry typeConverterRegistry);

    /**
     * bean条件转换器
     *
     * @param conditionFilterRegistry 条件转换器
     */
    abstract void doRegistryBeanConditionFilter(ConditionFilterRegistry conditionFilterRegistry);
}
