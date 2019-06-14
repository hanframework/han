package org.hanframework.beans.postprocessor.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.exception.BeanCurrentlyInCreationException;
import org.hanframework.beans.annotation.Autowired;
import org.hanframework.beans.annotation.InsertBean;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.beanfactory.convert.TypeConverter;
import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;
import org.hanframework.beans.exception.BeanCreationException;
import org.hanframework.beans.postprocessor.InstantiationAwareBeanPostProcessor;
import org.hanframework.beans.postprocessor.annotation.BeanProcessor;
import org.hanframework.context.aware.BeanFactoryAware;
import org.hanframework.env.annotation.Value;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.reflection.ReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现Autowired注入和Value属性值注入
 *
 * @author liuxin
 * @version Id: AutowiredAnnotationBeanPostProcessor.java, v 0.1 2018/10/30 2:11 PM
 */
@Slf4j
@BeanProcessor
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {


    private DefaultListableBeanFactory beanFactory;
    /**
     * 标识将要注入的注解
     * eg: Autowired
     * Value
     */
    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
            new ConcurrentHashSet<>();


    private final Map<String, InjectionMetadata> injectionMetadataCache =
            new ConcurrentHashMap<>();

    public final static int BEAN_TYPE = 0;

    public final static int VALUE_TYPE = 1;


    public AutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);
    }

    public int injectType(DependencyDescriptor dependencyDescriptor) {
        if (AnnotationTools.isContainsAnnotation(dependencyDescriptor.getAnnotations(), Value.class)) {
            return VALUE_TYPE;
        }
        if (AnnotationTools.isContainsAnnotation(dependencyDescriptor.getAnnotations(), Autowired.class) || AnnotationTools.isContainsAnnotation(dependencyDescriptor.getAnnotations(), InsertBean.class)) {
            return BEAN_TYPE;
        }
        return -1;
    }

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
        return Optional.ofNullable(existingBean);
    }

    @Override
    public Optional<Object> postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return Optional.empty();
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return false;
    }

    @Override
    public void postProcessPropertyValues(Object bean, String beanName) {
        InjectionMetadata autowiringMetadata = findAutowiringMetadata(beanName, bean.getClass());
        if (autowiringMetadata.isDependency()) {
            return;
        }
        try {
            autowiringMetadata.inject(bean, beanName);
        } catch (BeanCreationException var7) {
            throw var7;
        } catch (Throwable var8) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", var8);
        }
    }

    /**
     * 解析Bean中带有指定的#{autowiredAnnotationTypes}的属性
     *
     * @param beanName bean名字
     * @param clazz    类型
     * @return 注入
     */
    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz) {
        String cacheKey = (null != beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    metadata = buildAutowiringMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }


    /**
     * 判断是否必须注入主要判断
     * required字段
     *
     * @param memberObj 要注入类的属性
     * @return 是否需要注入
     */
    private boolean determineRequiredStatus(Object memberObj) {
        if (memberObj instanceof Field) {
            boolean containsAutowiredAnnotation = AnnotationTools.isContainsAnnotationFromField((Field) memberObj, Autowired.class);
            if (containsAutowiredAnnotation) {
                return ((Field) memberObj).getDeclaredAnnotation(Autowired.class).required();
            }
            boolean containsInsertBeanAnnotation = AnnotationTools.isContainsAnnotationFromField((Field) memberObj, InsertBean.class);
            if (containsInsertBeanAnnotation) {
                return ((Field) memberObj).getDeclaredAnnotation(InsertBean.class).required();
            }
        }
        if (memberObj instanceof Method) {
            boolean containsAutowiredAnnotation = AnnotationTools.isContainsAnnotationFromMethod((Method) memberObj, Autowired.class);
            if (containsAutowiredAnnotation) {
                return ((Method) memberObj).getDeclaredAnnotation(Autowired.class).required();
            }
            boolean containsInsertBeanAnnotation = AnnotationTools.isContainsAnnotationFromMethod((Method) memberObj, InsertBean.class);
            if (containsInsertBeanAnnotation) {
                return ((Method) memberObj).getDeclaredAnnotation(InsertBean.class).required();
            }
        }
        //默认必须注入
        return true;
    }


    /**
     * 构建注入的对象
     * 注意点: 依次向上去查询,通过循环#code{ targetClass = targetClass.getSuperclass();}
     *
     * @param clazz
     * @return
     */
    private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<>();
        Class<?> targetClass = clazz;

        do {
            LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<>();
            for (Field field : targetClass.getDeclaredFields()) {
                //首先判断Field上是否有注入标志
                if (AnnotationTools.isContainsAnnotationFromField(field, autowiredAnnotationTypes)) {
                    //带有static的不支持注入
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (log.isWarnEnabled()) {
                            log.warn("@Autowired 注解不支持静态字段: " + field);
                        }
                        continue;
                    }
                    boolean required = determineRequiredStatus(field);
                    currElements.add(new AutowiredFieldElement(field, required));
                }
            }
            for (Method method : targetClass.getDeclaredMethods()) {
                if (AnnotationTools.isContainsAnnotationFromMethod(method, autowiredAnnotationTypes)) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (log.isWarnEnabled()) {
                            log.warn("@Autowired 注解不支持静态方法: " + method);
                        }
                        continue;
                    }
                    if (method.getParameterTypes().length == 0) {
                        if (log.isWarnEnabled()) {
                            log.warn("@Autowired 应该修饰具有入参的方法: " + method);
                        }
                    }
                    boolean required = determineRequiredStatus(method);
                    currElements.add(new AutowiredMethodElement(method, required));
                }
            }
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
        return new InjectionMetadata(clazz, elements);
    }


    /**
     * 自动注入字段元素
     */
    private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

        private final boolean required;

        private volatile boolean cached = false;

        private volatile Object cachedFieldValue;

        public AutowiredFieldElement(Field field, boolean required) {
            super(field);
            this.required = required;
        }

        @Override
        public void setCachedValue(Object... cachedValue) {
            this.cachedFieldValue = cachedValue;
            cached = cachedValue != null && cachedValue.length > 0;
        }

        @Override
        protected void inject(Object bean, String beanName) throws Throwable {
            Field field = (Field) this.member;
            try {
                if (cached) {
                    ReflectionTools.makeAccessible(field);
                    field.set(bean, cachedFieldValue);
                } else {
                    //将一个beanName依赖的bean给记录下来，给BeanFactory维护，当前先不实现。
                    TypeConverter typeConverter = beanFactory.getTypeConverter();
                    DependencyDescriptor desc = new DependencyDescriptor(beanName, field, this.required);
                    desc.setContainingClass(bean.getClass());
                    switch (injectType(desc)) {
                        case AutowiredAnnotationBeanPostProcessor.BEAN_TYPE:
                            setCachedValue(injectBean(beanFactory, desc, typeConverter, bean));
                            break;
                        case AutowiredAnnotationBeanPostProcessor.VALUE_TYPE:
                            setCachedValue(injectValue(beanFactory, desc, typeConverter, bean));
                            break;
                        default:
                            log.warn("no ject");
                    }
                    ReflectionTools.makeAccessible(desc.getField());
                    desc.getField().set(bean, cachedFieldValue);
                }
            } catch (BeanCurrentlyInCreationException bie) {
                throw bie;
            } catch (Throwable ex) {
                throw new BeanCreationException("不能自动注入字段: " + field, ex);
            }
        }
    }


    /**
     * Class representing injection information about an annotated method.
     */
    private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

        private final boolean required;

        private volatile boolean cached = false;

        private volatile Object[] cachedMethodArguments;

        public AutowiredMethodElement(Method method, boolean required) {
            super(method);
            this.required = required;
        }

        @Override
        public void setCachedValue(Object... cachedValue) {
            this.cachedMethodArguments = cachedValue;
            this.cached = cachedValue != null && cachedValue.length > 0;
        }

        @Override
        protected void inject(Object bean, String beanName) throws Throwable {
            Method method = (Method) this.member;
            try {
                Object[] arguments;
                if (this.cached) {
                    ReflectionTools.makeAccessible(method);
                    method.invoke(bean, cachedMethodArguments);
                } else {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    arguments = new Object[paramTypes.length];
                    TypeConverter typeConverter = beanFactory.getTypeConverter();
                    for (int i = 0; i < arguments.length; i++) {
                        MethodParameter methodParameter = new MethodParameter(method, i);
                        DependencyDescriptor desc = new DependencyDescriptor(beanName, methodParameter, this.required);
                        desc.setContainingClass(bean.getClass());
                        switch (injectType(desc)) {
                            case AutowiredAnnotationBeanPostProcessor.BEAN_TYPE:
                                arguments[i] = injectBean(beanFactory, desc, typeConverter, bean);
                                break;
                            case AutowiredAnnotationBeanPostProcessor.VALUE_TYPE:
                                arguments[i] = injectValue(beanFactory, desc, typeConverter, bean);
                                break;
                            default:
                                log.warn("不执行注入");
                        }
                    }
                    setCachedValue(arguments);
                }
                if (cachedMethodArguments != null) {
                    ReflectionTools.makeAccessible(method);
                    method.invoke(bean, cachedMethodArguments);
                }
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } catch (Throwable ex) {
                throw new BeanCreationException("不能自动注入方法: " + method, ex);
            }
        }
    }
}
