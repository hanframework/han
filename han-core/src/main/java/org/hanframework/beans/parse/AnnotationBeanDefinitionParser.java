package org.hanframework.beans.parse;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.annotation.Lazy;
import org.hanframework.beans.annotation.Primary;
import org.hanframework.beans.annotation.Scope;
import org.hanframework.beans.annotation.HanComponent;
import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beandefinition.GenericBeanDefinition;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;
import org.hanframework.beans.parse.exception.BeanDefinitionParserException;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.reflection.ClassTools;
import org.hanframework.tool.string.StringTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author liuxin
 * @version Id: DefaultBeanDefinitionParse.java, v 0.1 2018/10/11 5:08 PM
 */
@Slf4j
public class AnnotationBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * 当被该注解标注过的类才会被加入到IOC容器中
     */
    private static final Class SMILE_COMPONENT = HanComponent.class;
    /**
     * 扫描到的字节码集合
     */
    private Set<Class> classes;

    /**
     * 提供给第三方可自行扩展的集合
     */
    private List<AbstractCustomerBeanDefinitionParser> configurableBeanDefinitionParsers = new ArrayList<>(3);


    public AnnotationBeanDefinitionParser(Set<Class> classes) {
        this(classes, true);
    }

    /**
     * 是否允许自定义解析器
     * 自定义解决器可能会出现多个解析器对同一个Bean进行了多次解析
     * 1. 这个时候要保证beanName不能一样
     * 2. 根据类型注入时候要保证有一个类型是已经被
     *
     * @param classes        被容器扫描到的字节码
     * @param customerParser 是否允许扩展
     * @see org.hanframework.beans.annotation.Primary 声明过的
     */
    public AnnotationBeanDefinitionParser(Set<Class> classes, boolean customerParser) {
        this.classes = classes;
        if (customerParser) {
            initConfigurableBeanDefinitionParserForType();
        }
    }


    /**
     * 依次向上查找
     * 凡是继承了@HanComponent 都是组件
     *
     * @return true是组件
     */
    protected static boolean isComponent(Class cls) {
        return AnnotationTools.getAnnotationMetadata(cls).hasAnnotation(SMILE_COMPONENT);
    }

    private void initConfigurableBeanDefinitionParserForType() {
        classes.stream().forEach(cls -> {
            if (AbstractCustomerBeanDefinitionParser.class.isAssignableFrom(cls) && !ClassTools.isAbstract(cls) && !ClassTools.isInterface(cls)) {
                try {
                    AbstractCustomerBeanDefinitionParser configurableBeanDefinitionParser = (AbstractCustomerBeanDefinitionParser) cls.newInstance();
                    configurableBeanDefinitionParsers.add(configurableBeanDefinitionParser);
                } catch (Exception e) {
                    log.error(cls + ": 自定义解析器异常" + e.getMessage());
                }
            }
        });
    }


    /**
     * 系统默认的组件
     *
     * @param cls
     * @return
     */
    protected Optional<BeanDefinition> componentParser(Class cls) {
        if (isComponent(cls)) {
            return doLoadBeanDefinition(cls);
        }
        return Optional.empty();
    }

    /**
     * 处理用户自定义的和系统默认的
     * 系统模式的是凡是被SmileComponent标记的类或者是被标记的注解
     * <p>
     * 凡是被@Configuration标记的类,会在初始化时候创建见下类
     *
     * @param beanFactory
     * @see org.hanframework.beans.postprocessor.impl.ConfigurationAnnotationBeanPostProcessor
     * <p>
     * 自定义的从配置类中获取
     */
    @Override
    public void loadBeanDefinitionParse(DefaultListableBeanFactory beanFactory) {
        for (Class cls : classes) {
            //正常情况一个Cls只能生成一个BeanDefinition，但是配置类中可以包含很多，
            List<BeanDefinition> result = new ArrayList<>();
            //根据系统定义的注解生成BeanDefinition
            Optional<BeanDefinition> definitionOptional = componentParser(cls);
            //正常情况,一个插件,只会有一个配置类,当存在了多个,要求先判断是否是当前类,先生成BeanDefinition,然后实例时候在处理。
            for (AbstractCustomerBeanDefinitionParser c : configurableBeanDefinitionParsers) {
                List<BeanDefinition> beanDefinitions = c.customerBeanDefinitionParse(cls);
                if (null != beanDefinitions) {
                    result = beanDefinitions;
                }
            }
            //获取方法中所有被Bean注解标识（不能再这里做,要放在PostProcessor中去实现,配置类都是单例的，当实例时候去解析Method中的Bean）
            //如果是配置类
            if (definitionOptional.isPresent()) {
                result.add(definitionOptional.get());
                for (BeanDefinition beanDefinition : result) {
                    beanFactory.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
                }
            }
        }
    }


    /**
     * 容器默认生成BeanDefinition的规则
     *
     * @param beanCls
     * @return
     * @throws BeanDefinitionParserException 解析异常
     */
    protected Optional<BeanDefinition> doLoadBeanDefinition(Class beanCls) throws BeanDefinitionParserException {
        Scope scope = AnnotationTools.findAnnotation(beanCls, Scope.class);
        Lazy lazy = AnnotationTools.findAnnotation(beanCls, Lazy.class);
        HanComponent smileComponent = AnnotationTools.findAnnotation(beanCls, HanComponent.class);
        GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setScope(null == scope ? ConfigurableBeanFactory.SCOPE_SINGLETON : scope.value());
        genericBeanDefinition.setBeanClass(beanCls);
        genericBeanDefinition.setLazyInit(null == lazy ? true : lazy.value());
        genericBeanDefinition.setPrimary(AnnotationTools.isContainsAnnotation(beanCls, Primary.class));
        genericBeanDefinition.setBeanName((null == smileComponent || StringTools.isBlank(smileComponent.name())) ? StringTools.uncapitalize(ClassTools.getShortName(beanCls)) : smileComponent.name());
        //允许用户自定义实例化
        genericBeanDefinition.setBeforeInstantiationResolved(true);
        return Optional.ofNullable(genericBeanDefinition);
    }

}
