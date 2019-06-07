package org.hanframework.beans.condition;

import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.condition.filter.ConditionFilter;
import org.hanframework.beans.beandefinition.GenericBeanDefinition;
import org.hanframework.beans.condition.annotation.AutoConfigureAfter;
import org.hanframework.beans.condition.annotation.AutoConfigureBefore;
import org.hanframework.beans.condition.annotation.ConditionalOnBean;
import org.hanframework.beans.condition.annotation.ConditionalOnMissingBean;
import org.hanframework.beans.condition.filter.*;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.reflection.ClassTools;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 允许第三方开发者定制自己的条件性注解,通过对beanFactory的包装进行自己的条件过滤
 *
 * @author liuxin
 * @version Id: ConditionFilterRegistry.java, v 0.1 2019-03-18 14:03
 */
public class ConditionFilterRegistry implements ConditionHandler {
    private static Map<Class<? extends Annotation>, ConditionFilter> beanConditionFilterMap;

    private BeanFactory beanFactory;

    public ConditionFilterRegistry(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanConditionFilterMap == null) {
            init();
        }
    }

    private void init() {
        beanConditionFilterMap = new ConcurrentHashMap<>(6);
        beanConditionFilterMap.put(ConditionalOnMissingBean.class, new ConditionalOnMissingBeanFilter(beanFactory));
        beanConditionFilterMap.put(ConditionalOnBean.class, new ConditionalOnBeanFilter(beanFactory));
        beanConditionFilterMap.put(AutoConfigureAfter.class, new AutoConfigureAfterFilter(beanFactory));
        beanConditionFilterMap.put(AutoConfigureBefore.class, new AutoConfigureBeforeFilter(beanFactory));
    }

    private void checkAnnotation(Class as) {
        Assert.isTrue(as.isAnnotation(), ClassTools.getShortName(as) + "必须为注解,可参考:" + ConditionalOnBean.class);
    }

    /**
     * 支持用户自定义
     *
     * @param as 条件注解
     * @param cf 注解拦截条件
     */
    public void register(Class<? extends Annotation> as, AbstractConditionFilter cf) {
        checkAnnotation(as);
        beanConditionFilterMap.put(as, cf);
    }

    public ConditionFilter getConditionFilter(Class<? extends Annotation> annotationType) {
        return beanConditionFilterMap.get(annotationType);
    }

    /**
     * 判断cls满足所有限定条件
     * 如果任何一个不满足都返回false
     * 当返回false以为不满足条件,不能被实例化
     *
     * @param genericBeanDefinition bean信息
     * @return
     */
    @Override
    public boolean isCondition(GenericBeanDefinition genericBeanDefinition) throws BeanCreateConditionException {
        ConditionCheckMetaData ccm;
        Class cls = genericBeanDefinition.getOriginClass();
        boolean beanMethodInstantiation = genericBeanDefinition.isBeanMethodInstantiation();
        if (beanMethodInstantiation) {
            ccm = ConditionCheckMetaData.builder(genericBeanDefinition.getConfigurationBeanMethod().getBeanMethod());
        } else {
            ccm = ConditionCheckMetaData.builder(cls);
        }
        return doCondition(ccm);
    }


    private boolean doCondition(ConditionCheckMetaData ccm) throws BeanCreateConditionException {
        List<Annotation> annotations = ccm.getConditionAnnotations();
        for (Annotation annotation : annotations) {
            ConditionFilter conditionFilter = getConditionFilter(annotation.annotationType());
            //默认不是严格模式，即不会报错,
            Boolean strict = AnnotationTools.getAnnotationAttributeAsMap(annotation).getBoolean("strict", false);
            if (null != conditionFilter && !conditionFilter.filter(ccm, strict)) {
                if (!strict) {
                    return false;
                }
                throw new BeanCreateConditionException(ClassTools.getShortName(ccm.getOriginClass()) + ",创建失败。原因:" + ClassTools.getShortName(annotation.annotationType()) + ": 条件不允许");
            }
        }
        return true;
    }
}
