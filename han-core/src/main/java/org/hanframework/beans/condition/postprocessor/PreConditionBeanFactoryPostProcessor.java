package org.hanframework.beans.condition.postprocessor;

import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.postprocessor.BeanFactoryPostProcessor;
import org.hanframework.beans.postprocessor.annotation.BeanFactoryProcessor;


/**
 * @author liuxin
 * @version Id: PreConditionBeanFactoryPostProcessor.java, v 0.1 2019-01-29 10:42
 */
@BeanFactoryProcessor
public class PreConditionBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableBeanFactory configurableBeanFactory) {
//        ConditionFilterRegistry conditionFilterRegistry = new ConditionFilterRegistry((BeanFactory) configurableBeanFactory);
//        ConditionStrategy conditionStrategy = new ConditionStrategy(conditionFilterRegistry);
//        configurableBeanFactory.setConditionStrategy(conditionStrategy);
    }
}
