package org.hanframework.beans.parse;

import org.hanframework.beans.annotation.*;
import org.hanframework.beans.beandefinition.*;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.parse.exception.BeanDefinitionParserException;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.reflection.ClassTools;
import org.hanframework.tool.string.StringTools;

import java.util.Optional;
import java.util.Set;


/**
 * 解析默认的
 *
 * @author liuxin
 * @version Id: DefaultBeanDefinitionParse.java, v 0.1 2018/10/11 5:51 PM
 */
public class DefaultBeanDefinitionParser extends AnnotationBeanDefinitionParser {

    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;


    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


    public DefaultBeanDefinitionParser(Set<Class> classes) {
        super(classes);
    }

    @Override
    protected Optional<BeanDefinition> doLoadBeanDefinition(Class beanCls) throws BeanDefinitionParserException {
        Scope scope = AnnotationTools.findAnnotation(beanCls, Scope.class);
        Lazy lazy = AnnotationTools.findAnnotation(beanCls, Lazy.class);
        HanComponent smileComponent = AnnotationTools.findAnnotation(beanCls, HanComponent.class);
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setScope(null == scope ? SCOPE_SINGLETON : scope.value());
        genericBeanDefinition.setBeanClass(beanCls);
        genericBeanDefinition.setLazyInit(null == lazy ? true : lazy.value());
        genericBeanDefinition.setPrimary(AnnotationTools.isContainsAnnotation(beanCls, Primary.class));
        genericBeanDefinition.setBeanName((null == smileComponent || StringTools.isBlank(smileComponent.name())) ? StringTools.uncapitalize(ClassTools.getShortName(beanCls)) : smileComponent.name());
        //允许用户自定义实例化
        genericBeanDefinition.setBeforeInstantiationResolved(true);
        return Optional.ofNullable(genericBeanDefinition);
    }


}
