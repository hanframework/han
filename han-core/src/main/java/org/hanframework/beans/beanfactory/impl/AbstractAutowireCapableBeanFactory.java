package org.hanframework.beans.beanfactory.impl;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.BeanTools;
import org.hanframework.beans.annotation.Autowired;
import org.hanframework.beans.beandefinition.*;
import org.hanframework.beans.beanfactory.AutowireCapableBeanFactory;
import org.hanframework.beans.beanfactory.lifecycle.InitializingBean;
import org.hanframework.beans.condition.BeanCreateConditionException;
import org.hanframework.beans.exception.BeanCreationException;
import org.hanframework.beans.exception.BeanInitDestroyException;
import org.hanframework.beans.exception.BeanInjectException;
import org.hanframework.beans.parse.ConfigurationBeanMethod;
import org.hanframework.beans.postprocessor.BeanPostProcessor;
import org.hanframework.beans.postprocessor.InstantiationAwareBeanPostProcessor;
import org.hanframework.env.annotation.Profile;
import org.hanframework.env.annotation.Value;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.hanframework.tool.reflection.MethodTools;
import org.hanframework.tool.reflection.ReflectionTools;
import org.hanframework.tool.string.StringTools;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 主要操作Bean的生成
 *
 * @author liuxin
 * @version Id: AbstractAutowireCapableBeanFactory.java, v 0.1 2018/10/18 8:41 PM
 */
@Slf4j
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private static final Object NULL = null;

    /**
     * 如果实例化处理器返回了Bean,就直接返回，否则创建
     * <p>
     * 否则就用IOC实例->然后执行实例化后置处理器->填充->执行初始化前后处理器和初始化方法
     *
     * @param beanName bean名字
     * @param mbd      bean信息
     * @param args     bean实例化参数
     * @return bean实例
     */
    @Override
    protected Object createBean(String beanName, GenericBeanDefinition mbd, Object[] args, boolean checked) {
        //先从处理器中获取，一般创建代理对象会从这里面获取，主要是继承InstantiationAwareBeanPostProcessor
        //重写实例前置处理器applyBeanPostProcessorsBeforeInstantiation方法，如果获取成功就执行初始化后方法(postProcessAfterInitialization)
        Optional<Object> objectOptional = resolveBeforeInstantiation(beanName, mbd);
        return objectOptional.orElseGet(() -> doCreateBean(beanName, mbd, args, checked));
    }

    /**
     * 执行注入
     *
     * @param beanName bean名字
     * @param bean     bean实例
     */
    private void populateBean(String beanName, Object bean) {
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                if (!ibp.postProcessAfterInstantiation(bean, beanName)) {
                    break;
                }
            }
        }
        //填充
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                ibp.postProcessPropertyValues(bean, beanName);
            }
        }
    }

    private boolean conditionForBean(final String beanName, final GenericBeanDefinition mbd) {
        boolean condition;
        try {
            condition = getConditionHandler().isCondition(mbd);
        } catch (BeanCreateConditionException ce) {
            logger.error(beanName + ce.getMessage());
            condition = false;
        }
        return condition;
    }

    private Object doCreateBean(final String beanName, final GenericBeanDefinition mbd, final Object[] args, boolean checked) {
        if (conditionForBean(beanName, mbd)) {
            //判断是否是配置类,如果是配置类就自定义实例，且不用填充了
            //首先实例
            Object instanceBean;
            try {
                if (mbd.isCustomerInstantiation()) {
                    instanceBean = mbd.customerInstantiationFactory.getObject();
                } else {
                    instanceBean = createBeanInstance(beanName, mbd, args);
                    //加入到单例中
                    //实例化后处理器，然后填充
                    this.populateBean(beanName, instanceBean);
                }
                //执行初始化
                return initializeBean(instanceBean, beanName, mbd);
            } catch (Exception e) {
                if (checked) {
                    throw new BeanCreationException(beanName);
                }
                log.error(e.getMessage());
            }
        }
        return NULL;
    }

    /**
     * 实例化接口:
     * 该接口必须返回一个实例化的对象。
     * 如何进行实例化呢?
     * - 1. 空构造实例
     * - 2. 直接实例化(newInstance)
     */
    private Object createBeanInstance(final String beanName, final GenericBeanDefinition mbd, final Object[] args) {
        Object bean;
        Class beanClass = mbd.getOriginClass();
        if (Modifier.isInterface(beanClass.getModifiers())) {
            throw new BeanCreationException(beanName, "接口不能被实例化");
        }
        //如果无参构造直接实例
        List<ConstructorMetadata> constructorMetaDataList = mbd.getConstructorInfo();
        Stream<ConstructorMetadata> constructorMetadataStream = constructorMetaDataList.stream().filter(c ->
                AnnotationTools.isContainsAnnotation(c.getConstructorAnnotations(), Autowired.class));
        boolean isAutowireConstructor = constructorMetadataStream.count() > 0;
        //如果有空构造并没有构造注入就使用空构造实例
        if (mbd.isBeanMethodInstantiation()) {
            bean = autowireConfigurationBeanMethod(beanName, mbd);
        } else if (isAutowireConstructor) {
            //使用构造注入
            bean = autowireConstructor(beanName, mbd);
        } else {
            //无参构造实例
            try {
                Constructor<?> declaredConstructor = mbd.getDefaultConstructor();
                if (args != null && args.length > 0) {
                    bean = BeanTools.instantiateConstructor(declaredConstructor, args);
                } else {
                    bean = BeanTools.instantiateConstructor(declaredConstructor);
                }
            } catch (Exception e) {
                throw new BeanCreationException(e);
            }
        }
        return bean;
    }

    public String getValueForProfile(List<Annotation> annotations, Value valueAnnotation) {
        String result;
        Profile profile = AnnotationTools.findAnnotation(annotations, Profile.class);
        if (null != profile) {
            for (String pro : profile.value()) {
                result = getPropertyResolver().resolvePlaceholders(valueAnnotation.value(), pro);
                if (null != result) {
                    return result;
                }
            }
        }
        return getPropertyResolver().resolvePlaceholders(valueAnnotation.value());

    }

    /**
     * 根据配置方法实例
     *
     * @param beanName bean名字
     * @param mbd      bean信息
     * @return bean实例
     */
    private Object autowireConfigurationBeanMethod(final String beanName, final GenericBeanDefinition mbd) {
        ConfigurationBeanMethod configurationBeanMethod = mbd.getConfigurationBeanMethod();
        List<ValueHolder> valueHolders = configurationBeanMethod.getValueHolders();
        Object[] args = argsForType(valueHolders);
        conditionForBean(beanName, mbd);
        Object parentBean = getBean(configurationBeanMethod.getParentBeanCls());
        return configurationBeanMethod.invoke(parentBean, args);

    }

    /**
     * 根据构造实例化Bean。
     * 什么时候回用到?
     * <p>
     * 当一个bean有空构造时候,此时实例化可以直接进行实例化操作constructor.newInstance();
     * 但是当一个Bean要根据构造注入时候,这个时候就不能进行直接实例化,因为构造中的参数要从IOC容器中获取
     *
     * @param beanName 将要实例化的BeanName
     * @param mbd      Bean描述信息
     * @return 实例化的bean
     */
    private Object autowireConstructor(final String beanName, final GenericBeanDefinition mbd) {
        Object[] args = new Object[0];
        Optional<Constructor> constructorInvokeOpt = Optional.empty();
        //如果无参构造直接实例
        List<ConstructorMetadata> constructorInfoList = mbd.getConstructorInfo();
        for (ConstructorMetadata constructorInfo : constructorInfoList) {
            List<Annotation> declaredAnnotations = constructorInfo.getConstructorAnnotations();
            if (AnnotationTools.isContainsAnnotation(declaredAnnotations, Autowired.class)) {
                ConstructorArgumentValues constructorArguments = constructorInfo.getConstructorArguments();
                List<ValueHolder> argumentValues = constructorArguments.getConstructorArgumentValues();
                args = argsForType(argumentValues);
                constructorInvokeOpt = Optional.ofNullable(constructorInfo.getOriginalConstructor());
                break;
            }
        }
        try {
            if (constructorInvokeOpt.isPresent()) {
                Constructor constructor = constructorInvokeOpt.get();
                return constructor.newInstance(args);
            }
        } catch (Exception e) {
            log.error(beanName + "构造实例化异常: " + e.getMessage());
            throw new BeanCreationException();
        }
        return NULL;
    }


    private Object[] argsForType(List<ValueHolder> valueHolders) {
        Object[] args = new Object[valueHolders.size()];
        for (int i = 0; i < valueHolders.size(); i++) {
            ValueHolder valueHolder = valueHolders.get(i);
            args[valueHolder.getSort()] = argForType(valueHolder);
        }
        return args;
    }

    /**
     * @param valueHolder 注入值工具类
     * @return 从容器中找到的类型值
     */
    private Object argForType(ValueHolder valueHolder) {
        Class<Object> type = valueHolder.getParameterType();
        AnnotationMetadata annotationMetadata = AnnotationTools.getAnnotationMetadata(type);
        if (annotationMetadata.hasAnnotation(Value.class)) {
            List<Annotation> fieldAnnotations = valueHolder.getParameterAnnotations();
            String result = getValueForProfile(fieldAnnotations, AnnotationTools.findAnnotation(type, Value.class));
            return getTypeConverter().convertIfNecessary(result, type);
        } else if (annotationMetadata.hasAnnotation(Autowired.class)) {
            Autowired autowired = annotationMetadata.getAnnotation(Autowired.class);
            String beanName = autowired.beanName();
            Object result = getOr(StringTools.isEmpty(beanName), () -> resolveDependency(type), () -> resolveDependency(beanName));
            if (autowired.required()) {
                Optional.ofNullable(result).orElseThrow(BeanInjectException::new);
            }
            return result;
        }
        throw new BeanInjectException();

    }

    private <T> T getOr(boolean flag, Supplier<T> trueResult, Supplier<T> falseResult) {
        return flag ? trueResult.get() : falseResult.get();
    }

    /**
     * 解决实例前的问题
     *
     * @param beanName bean名字
     * @param mbd      bean信息
     * @return bean实例
     */
    private Optional<Object> resolveBeforeInstantiation(String beanName, GenericBeanDefinition mbd) {
        Optional<Object> beanOptional = Optional.empty();
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            beanOptional = applyBeanPostProcessorsBeforeInstantiation(mbd.getOriginClass(), beanName);
            if (beanOptional.isPresent()) {
                beanOptional = applyBeanPostProcessorsAfterInitialization(beanOptional.get(), beanName);
            }
            mbd.beforeInstantiationResolved = (beanOptional.isPresent());
        }
        return beanOptional;
    }

    /**
     * 应用所有的实例化处理器
     *
     * @param beanClass bean类型
     * @param beanName  bean名字
     * @return bean实例
     */
    private Optional<Object> applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {

        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                Optional<Object> result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createBean(Class<T> beanClass) throws BeanCreationException {
        String beanName = beanNameGenerator.generateBeanName(beanClass);
        return (T) createBean(beanName, (GenericBeanDefinition) getBeanDefinition(beanName), null, true);
    }


    @Override
    public Object resolveDependency(String beanName) {
        return getBean(beanName);
    }

    @Override
    public Object resolveDependency(Class<?> beanType) {
        return getBean(beanType);
    }

    @Override
    public Object initializeBean(Object existingBean, String beanName, GenericBeanDefinition mbd) {
        //执行初始化前方法
        Optional<Object> existingBeanBeforeInitOpt = applyBeanPostProcessorsBeforeInitialization(existingBean, beanName);
        //执行初始化方法
        existingBeanBeforeInitOpt.ifPresent(x -> invokeInitMethods(x, mbd));
        //执行初始化后方法
        Optional<Object> existingBeanAfterInitOpt = Optional.empty();
        if (existingBeanBeforeInitOpt.isPresent()) {
            existingBeanAfterInitOpt = applyBeanPostProcessorsAfterInitialization(existingBeanBeforeInitOpt.get(), beanName);
        }
        return existingBeanAfterInitOpt.orElseThrow(BeanInitDestroyException::new);
    }

    private void invokeInitMethods(Object bean, GenericBeanDefinition mbd) {
        boolean isInitializingBean = bean instanceof InitializingBean;
        if (isInitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
        //执行用户自定义的错误
        if (StringTools.isNotEmpty(mbd.getInitMethodName())) {
            invokeCustomInitMethod(bean, mbd);
        }
    }

    /**
     * 初始化方法从BeanDefinition中获取
     * 执行自定义的初始化方法
     */
    private void invokeCustomInitMethod(Object bean, GenericBeanDefinition mbd) {
        String initMethodName = mbd.getInitMethodName();
        Optional<Method> initMethodInvokeOpt =
                Optional.ofNullable(MethodTools.findMethodByName(mbd.getOriginClass(), initMethodName));
        initMethodInvokeOpt.ifPresent(ReflectionTools::makeAccessible);
        try {
            initMethodInvokeOpt.ifPresent(x -> MethodTools.invokeMethod(x, bean));
        } catch (Exception in) {
            log.warn(in.getMessage());
        }
    }

    /**
     * 初始化前
     *
     * @param existingBean bean实例
     * @param beanName     bean名字
     * @return bean实例
     */
    @Override
    public Optional<Object> applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
        Optional<Object> result = Optional.ofNullable(existingBean);
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            if (result.isPresent()) {
                result = beanProcessor.postProcessBeforeInitialization(result.get(), beanName);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 初始化后
     *
     * @param existingBean bean实例
     * @param beanName     bean名字
     * @return bean实例
     */
    @Override
    public Optional<Object> applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
        Optional<Object> result = Optional.ofNullable(existingBean);
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            if (result.isPresent()) {
                result = beanProcessor.postProcessAfterInitialization(result.get(), beanName);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return result;
    }


}
