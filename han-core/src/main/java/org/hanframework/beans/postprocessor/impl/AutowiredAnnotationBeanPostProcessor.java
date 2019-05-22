package org.hanframework.beans.postprocessor.impl;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.exception.BeanCurrentlyInCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hanframework.beans.annotation.Autowired;
import org.hanframework.beans.annotation.InsertBean;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.beanfactory.convert.TypeConverter;
import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;
import org.hanframework.beans.exception.BeanCreationException;
import org.hanframework.beans.postprocessor.InstantiationAwareBeanPostProcessor;
import org.hanframework.beans.postprocessor.annotation.BeanProcessor;
import org.hanframework.context.aware.BeanFactoryAware;
import org.hanframework.core.env.annotation.Profile;
import org.hanframework.core.env.annotation.Value;
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
            new LinkedHashSet();


    private final Map<String, InjectionMetadata> injectionMetadataCache =
            new ConcurrentHashMap(10);


    public AutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);
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
        return  Optional.ofNullable(existingBean);
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
     * @param beanName
     * @param clazz
     * @return
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
     * @param memberObj
     * @return
     */
    protected boolean determineRequiredStatus(Object memberObj) {
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
        LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList();
        Class<?> targetClass = clazz;

        do {
            LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList();
            for (Field field : targetClass.getDeclaredFields()) {
                //首先判断Field上是否有注入标志
                boolean containsAnnotation = AnnotationTools.isContainsAnnotationFromField(field, autowiredAnnotationTypes);
                if (containsAnnotation) {
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
                boolean containsAnnotation = AnnotationTools.isContainsAnnotationFromMethod(method, autowiredAnnotationTypes);
                if (containsAnnotation) {
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


    private Object resolvedCachedArgument(String beanName, Object cachedArgument) {
        if (cachedArgument instanceof DependencyDescriptor) {
            DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
            return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
        } else {
            return cachedArgument;
        }
    }


    private Object getValueForProfile(Annotation[] annotations, Value value) {
        Object result;
        Profile profile = AnnotationTools.findAnnotation(Arrays.asList(annotations), Profile.class);
        if (null != profile) {
            for (String pro : profile.value()) {
                result = beanFactory.getPropertyResolver().resolvePlaceholders(value.value(), pro);
                if (null != result) {
                    return result;
                }
            }
        }
        return beanFactory.getPropertyResolver().resolvePlaceholders(value.value());

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
        protected void inject(Object bean, String beanName) throws Throwable {
            Field field = (Field) this.member;
            try {
                Object value = null;
                if (this.cached) {
                    value = resolvedCachedArgument(beanName, this.cachedFieldValue);
                } else {
                    //将一个beanName依赖的bean给记录下来，给BeanFactory维护，当前先不实现。
                    Set<String> autowiredBeanNames = new LinkedHashSet(1);
                    TypeConverter typeConverter = beanFactory.getTypeConverter();
                    DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
                    desc.setContainingClass(bean.getClass());
                    Annotation[] annotations = desc.getAnnotations();
                    Value valueAnnotation = AnnotationTools.findAnnotation(Arrays.asList(annotations), Value.class);
                    if (null != valueAnnotation) {
                        value = getValueForProfile(annotations, valueAnnotation);
                        value = typeConverter.convertIfNecessary(value, field.getType());
                        if (null == value) {
                            log.error(beanName + "注入失败,缺失依赖参数名:[" + valueAnnotation.value() + "]");
                        }
                    } else if (Modifier.isInterface(field.getType().getModifiers()) || Modifier.isAbstract(field.getType().getModifiers())) {
                        String[] beanNamesForType = beanFactory.getBeanNamesForType(field.getType());
                        if (beanNamesForType.length == 1) {
                            value = beanFactory.getBean(beanNamesForType[0]);
                        }
                        if (null == value) {
                            log.error(beanName + "注入失败,缺失依赖:[" + beanNamesForType[0] + "]");
                        }
                    } else {
                        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
                        if (null == value) {
                            log.error(beanName + "注入失败,缺失依赖:[" + desc.getDependencyName() + "]");
                        }
                    }
                    //当发现value是空，但是
                    if (value == null && !this.required) {
                        value = null;
                    }
                }
                if (value != null) {
                    ReflectionTools.makeAccessible(field);
                    field.set(bean, value);
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
        protected void inject(Object bean, String beanName) throws Throwable {
            Method method = (Method) this.member;
            try {
                Object[] arguments;
                if (this.cached) {
                    // Shortcut for avoiding synchronization...
                    arguments = resolveCachedArguments(beanName);
                } else {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    arguments = new Object[paramTypes.length];
                    DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
                    Set<String> autowiredBeanNames = new LinkedHashSet<String>(paramTypes.length);
                    TypeConverter typeConverter = beanFactory.getTypeConverter();
                    for (int i = 0; i < arguments.length; i++) {
                        MethodParameter methodParam = new MethodParameter(method, i);
                        DependencyDescriptor desc = new DependencyDescriptor(methodParam, this.required);
                        desc.setContainingClass(bean.getClass());
                        descriptors[i] = desc;
                        Annotation[] annotations = desc.getAnnotations();
                        Value valueAnnotation = AnnotationTools.findAnnotation(Arrays.asList(annotations), Value.class);
                        Object arg = null;
                        if (null != valueAnnotation) {
                            arg = getValueForProfile(annotations, valueAnnotation);
                            arg = typeConverter.convertIfNecessary(arg, paramTypes[i]);
                        } else if (Modifier.isInterface(paramTypes[i].getModifiers()) || Modifier.isAbstract(paramTypes[i].getModifiers())) {
                            String[] beanNamesForType = beanFactory.getBeanNamesForType(paramTypes[i]);
                            if (beanNamesForType.length == 1) {
                                arg = beanFactory.getBean(beanNamesForType[0]);
                            }
                        } else {
                            arg = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
                        }
                        if (arg == null && !this.required) {
                            arguments = null;
                            break;
                        }
                        arguments[i] = arg;
                    }
                }
                if (arguments != null) {
                    ReflectionTools.makeAccessible(method);
                    method.invoke(bean, arguments);
                }
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } catch (Throwable ex) {
                throw new BeanCreationException("不能自动注入方法: " + method, ex);
            }
        }

        private Object[] resolveCachedArguments(String beanName) {
            if (this.cachedMethodArguments == null) {
                return null;
            }
            Object[] arguments = new Object[this.cachedMethodArguments.length];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = resolvedCachedArgument(beanName, this.cachedMethodArguments[i]);
            }
            return arguments;
        }
    }
}
