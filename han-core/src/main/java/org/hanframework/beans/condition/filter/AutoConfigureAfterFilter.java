package org.hanframework.beans.condition.filter;

import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.condition.BeanCreateConditionException;
import org.hanframework.beans.condition.ConditionCheckMetaData;
import org.hanframework.beans.condition.annotation.AutoConfigureAfter;
import org.hanframework.tool.annotation.AnnotationTools;

/**
 * 在指定的Class之后加载。
 *
 * @author liuxin
 * @version Id: AutoConfigureAfterFilter.java, v 0.1 2019-01-29 10:30
 */
public class AutoConfigureAfterFilter extends AbstractConditionFilter {

    public AutoConfigureAfterFilter(BeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public boolean filter(ConditionCheckMetaData ccm, boolean strict) throws BeanCreateConditionException {
        AutoConfigureAfter autoConfigureAfter = AnnotationTools.findAnnotation(ccm.getConditionAnnotations(), AutoConfigureAfter.class);
        if (autoConfigureAfter == null) {
            return false;
        }
        for (Class beforeAutoConfigureClass : autoConfigureAfter.values()) {
            Object bean = getBeanFactory().getBean(beforeAutoConfigureClass);
            if (bean == null) {
                return false;
            }
        }
        for (String beforeAutoConfigureName : autoConfigureAfter.names()) {
            Object bean = getBeanFactory().getBean(beforeAutoConfigureName);
            if (bean == null) {
                return false;
            }
        }
        return true;
    }
}
