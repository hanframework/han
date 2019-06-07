package org.hanframework.beans.postprocessor.impl;

import org.hanframework.beans.beanfactory.NoSuchBeanException;
import org.hanframework.beans.beanfactory.convert.TypeConverter;
import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;
import org.hanframework.env.annotation.Profile;
import org.hanframework.env.annotation.Value;
import org.hanframework.tool.annotation.AnnotationTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hanframework.tool.reflection.ReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author liuxin
 * @version Id: InjectionMetadata.java, v 0.1 2018/10/30 2:40 PM
 */
public class InjectionMetadata {

    private final Logger logger = LoggerFactory.getLogger(InjectionMetadata.class);

    private final Class<?> targetClass;

    private final Collection<InjectedElement> injectedElements;

    private volatile Set<InjectedElement> checkedElements;

    public boolean isDependency() {
        return injectedElements.isEmpty();
    }

    public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> elements) {
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }

    public void inject(Object target, String beanName) throws Throwable {
        Collection<InjectedElement> elementsToIterate =
                (this.checkedElements != null ? this.checkedElements : this.injectedElements);
        if (!elementsToIterate.isEmpty()) {
            boolean debug = logger.isDebugEnabled();
            for (InjectedElement element : elementsToIterate) {
                if (debug) {
                    logger.debug("Processing injected method of bean '" + beanName + "': " + element);
                }
                element.inject(target, beanName);
            }
        }
    }


    public static boolean needsRefresh(InjectionMetadata metadata, Class<?> clazz) {
        return (metadata == null || !metadata.targetClass.equals(clazz));
    }


    public static abstract class InjectedElement {

        private final Logger logger = LoggerFactory.getLogger(InjectionMetadata.class);

        protected final Member member;

        protected final boolean isField;

        protected volatile Boolean skip;

        protected InjectedElement(Member member) {
            this.member = member;
            this.isField = (member instanceof Field);
        }


        public abstract void setCachedValue(Object... cachedValue);

        public final Member getMember() {
            return this.member;
        }

        private Object getValueForProfile(DefaultListableBeanFactory beanFactory, Annotation[] annotations, Value value) {
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


        protected Object injectValue(DefaultListableBeanFactory beanFactory, DependencyDescriptor desc, TypeConverter typeConverter, Object bean) throws IllegalArgumentException, InvocationTargetException, IllegalAccessException {
            Value valueInstance = AnnotationTools.findAnnotation(desc.getAnnotations(), Value.class);
            Optional<Object> valueOpt = Optional.ofNullable(getValueForProfile(beanFactory, desc.getAnnotations(), valueInstance));
            if (valueOpt.isPresent()) {
                Class typeClass = isField ? desc.getField().getType() : desc.getMethodParameter().getParameterType();
                return typeConverter.convertIfNecessary(valueOpt.get(), typeClass);
            } else {
                logger.error(desc.getDeclaringBeanName() + "注入失败,缺失依赖参数名:[" + desc.getDependencyName() + "]");
                return null;
            }
        }

        protected Object injectBean(DefaultListableBeanFactory beanFactory, DependencyDescriptor desc, TypeConverter typeConverter, Object bean) throws IllegalArgumentException, InvocationTargetException, IllegalAccessException {
            Optional<Object> valueOpt;
            Class<?> beanType = isField ? desc.getField().getType() : desc.getMethodParameter().getParameterType();
            int modifiers = isField ? desc.getField().getModifiers() : desc.getMethodParameter().getMethod().getModifiers();
            if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
                String[] beanNamesForType = beanFactory.getBeanNamesForType(beanType);
                if (beanNamesForType.length == 1) {
                    valueOpt = Optional.ofNullable(beanFactory.getBean(beanNamesForType[0]));
                } else {
                    throw new NoSuchBeanException(Arrays.asList(beanNamesForType).toString());
                }
            } else {
                valueOpt = Optional.ofNullable(beanFactory.resolveDependency(desc, typeConverter));
            }
            return valueOpt.orElse(null);
        }


        protected void inject(Object target, String requestingBeanName) throws Throwable {
            if (this.isField) {
                Field field = (Field) this.member;
                ReflectionTools.makeAccessible(field);
                field.set(target, getResourceToInject(target, requestingBeanName));
            } else {
                try {
                    Method method = (Method) this.member;
                    ReflectionTools.makeAccessible(method);
                    method.invoke(target, getResourceToInject(target, requestingBeanName));
                } catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }


        protected Object getResourceToInject(Object target, String requestingBeanName) {
            return null;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof InjectedElement)) {
                return false;
            }
            InjectedElement otherElement = (InjectedElement) other;
            return this.member.equals(otherElement.member);
        }

        @Override
        public int hashCode() {
            return this.member.getClass().hashCode() * 29 + this.member.getName().hashCode();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " for " + this.member;
        }
    }

}
