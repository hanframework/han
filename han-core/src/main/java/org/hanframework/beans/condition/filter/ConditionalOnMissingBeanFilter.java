package org.hanframework.beans.condition.filter;

import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.condition.BeanCreateConditionException;
import org.hanframework.beans.condition.ConditionCheckMetaData;
import org.hanframework.beans.condition.annotation.ConditionalOnMissingBean;
import org.hanframework.tool.annotation.AnnotationTools;

/**
 * @author liuxin
 * @version Id: ConditionalOnMissingBeanFilter.java, v 0.1 2018/10/11 6:14 PM
 */
public class ConditionalOnMissingBeanFilter extends AbstractConditionFilter {

    public ConditionalOnMissingBeanFilter(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public boolean filter(ConditionCheckMetaData ccm, boolean strict) throws BeanCreateConditionException {
        ConditionalOnMissingBean onMissingBean = AnnotationTools.findAnnotation(ccm.getConditionAnnotations(), ConditionalOnMissingBean.class);
        if (onMissingBean == null) {
            return false;
        }
        for (Class missBeanClass : onMissingBean.values()) {
            Object bean = getBeanFactory().getBean(missBeanClass);
            if (bean != null) {
                return false;
            }
        }
        for (String missBeanName : onMissingBean.names()) {
            Object bean = getBeanFactory().getBean(missBeanName);
            if (bean != null) {
                return false;
            }
        }
        return true;
    }

}
