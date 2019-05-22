package org.hanframework.beans.condition.filter;

import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.condition.BeanCreateConditionException;
import org.hanframework.beans.condition.ConditionCheckMetaData;
import org.hanframework.beans.condition.annotation.ConditionalOnBean;
import org.hanframework.tool.annotation.AnnotationTools;

/**
 * @author liuxin
 * @version Id: ConditionalOnBeanFilter.java, v 0.1 2019-01-14 16:24
 */
public class ConditionalOnBeanFilter extends AbstractConditionFilter {

    public ConditionalOnBeanFilter(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public boolean filter(ConditionCheckMetaData ccm, boolean strict) throws BeanCreateConditionException {
        ConditionalOnBean onBean = AnnotationTools.findAnnotation(ccm.getConditionAnnotations(), ConditionalOnBean.class);
        if (onBean == null) {
            return false;
        }
        for (Class missBeanClass : onBean.values()) {
            Object bean = getBeanFactory().getBean(missBeanClass);
            if (bean == null) {
                return false;
            }
        }
        for (String missBeanName : onBean.names()) {
            Object bean = getBeanFactory().getBean(missBeanName);
            if (bean == null) {
                return false;
            }
        }
        return true;
    }
}
