package org.hanframework.beans.postprocessor.impl;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.autoconfigure.Configuration;
import org.hanframework.beans.annotation.*;
import org.hanframework.beans.beandefinition.GenericBeanDefinition;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;
import org.hanframework.beans.beanfactory.impl.ObjectFactory;
import org.hanframework.beans.postprocessor.BeanPostProcessor;
import org.hanframework.beans.postprocessor.annotation.BeanProcessor;
import org.hanframework.context.aware.BeanFactoryAware;
import org.hanframework.env.annotation.Value;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.hanframework.tool.reflection.ClassTools;
import org.hanframework.tool.string.StringTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;


/**
 * @author liuxin
 * @version Id: ConfigurationAnnotationBeanPostProcessor.java, v 0.1 2019-01-15 19:24
 * @see Configuration
 * @see HanBean
 * 主要用来处理Configuration注解,Configuration还是当做一个正常Bean来处理,但是当里面的方法被SmileBean标记,此时就会把
 * 被标记的方法方法,封装成一个ConfigurationMethod(配置Bean方法,那么此时改Bean就不是由IOC容器来创建,而是执行开发者
 * 方法来执行创建,此时的方法参数支持是一个Bean或者是一个配置值（如果是配置值同样支持@Value注解）
 * <p>
 * 使用原理就是: 在BeanDefinition中设置一个ObjectFactory customerInstantiationFactory;当在获取Bean时候先看是否是配置类，如果是配置类
 * 就从配置类中去实例，执行顺序是
 * 1.实例前置处理器->2.执行实例化配置方法->3.实例后置处理器->4.初始化前置处理器->5.初始化后置处理器
 */
@Slf4j
@BeanProcessor
@Deprecated
public class ConfigurationAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Optional<Object> postProcessBeforeInitialization(Object bean, String beanName) {
        return Optional.ofNullable(bean);
    }

    @Override
    public Optional<Object> postProcessAfterInitialization(Object existingBean, String beanName) {
        AnnotationMetadata annotationMetadata = AnnotationTools.getAnnotationMetadata(existingBean.getClass());
        boolean isConfiguration = annotationMetadata.hasAnnotation(Configuration.class);
        if (isConfiguration) {
            Method[] declaredMethods = existingBean.getClass().getDeclaredMethods();
            for (Method method : declaredMethods) {
                //是否带有注解，然后bean解析那一套那过来
                boolean isBean = isBeanMethod(method);
                if (isBean) {
                    Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
                    Class<?> beanCls = method.getReturnType();
                    Lazy annotation = AnnotationTools.findAnnotation(Arrays.asList(declaredAnnotations), Lazy.class);
                    boolean lazy = annotation == null ? true : annotation.value();
                    Scope scope = AnnotationTools.findAnnotation(Arrays.asList(declaredAnnotations), Scope.class);
                    HanBean smileBean = AnnotationTools.findAnnotation(Arrays.asList(declaredAnnotations), HanBean.class);
                    String beanInnerName = (null == smileBean || StringTools.isBlank(smileBean.name())) ? StringTools.uncapitalize(ClassTools.getShortName(beanCls)) : smileBean.name();
                    Parameter[] parameters = method.getParameters();
                    Object[] args = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        args[i] = parameters[i].getType();
                    }
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        Annotation[] annotations = parameter.getAnnotations();
                        Value valueAnnotation = AnnotationTools.findAnnotation(annotations, Value.class);
                        if (null != valueAnnotation) {
                            String value = beanFactory.getValueForProfile(Arrays.asList(annotations), valueAnnotation);
                            args[i] = beanFactory.getTypeConverter().convertIfNecessary(value, parameter.getType());
                            if (null == args[i]) {
                                log.error(beanName + "构造实例化失败,缺失依赖参数名:[" + value + "]");
                            }
                        } else {
                            args[i] = beanFactory.getBean(parameter.getType());
                            if (null == args[i]) {
                                log.error(beanName + "构造实例化失败,缺失依赖:[" + parameter.getType() + "]");
                            }
                        }
                    }
                    //生成BeanDefinition。
                    GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
                    genericBeanDefinition.setLazyInit(lazy);
                    genericBeanDefinition.setBeanClass(beanCls);
                    genericBeanDefinition.setScope(scope == null ? "" : scope.value());
                    genericBeanDefinition.setCustomerInstantiationFactory(new ObjectFactory() {
                        @Override
                        public Object getObject() {
                            return new ConfigurationMethod(beanInnerName, method, args, existingBean).invoke();
                        }
                    });
                    beanFactory.registerBeanDefinition(beanInnerName, genericBeanDefinition);
                }
            }
        }
        return Optional.ofNullable(existingBean);
    }

    private static boolean isBeanMethod(Method method) {
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAssignableFrom(HanBean.class)) {
                return true;
            } else {
                Annotation[] parentAnnotations = annotation.annotationType().getDeclaredAnnotations();
                return AnnotationTools.isContainsAnnotation(parentAnnotations, HanBean.class);
            }
        }
        return false;
    }


    static class ConfigurationMethod {
        String beanName;
        Method beanMethod;
        Object[] args;
        Object ontologyBean;

        public ConfigurationMethod(String beanName, Method beanMethod, Object[] args, Object ontologyBean) {
            this.beanName = beanName;
            this.beanMethod = beanMethod;
            this.args = args;
            this.ontologyBean = ontologyBean;
        }

        public String getBeanName() {
            return beanName;
        }


        public Object invoke() {
            int modifiers = beanMethod.getModifiers();
            boolean aPublic = Modifier.isPublic(modifiers);
            if (!aPublic) {
                beanMethod.setAccessible(true);
            }
            try {
                return beanMethod.invoke(ontologyBean, args);
            } catch (Exception e) {
                log.error(e.getMessage());
                System.err.println(e.getMessage());
            }
            return null;
        }
    }
}
