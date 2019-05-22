package org.hanframework.beans.postprocessor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hanframework.tool.reflection.ReflectionTools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

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

        protected final Member member;

        protected final boolean isField;

        protected volatile Boolean skip;

        protected InjectedElement(Member member) {
            this.member = member;
            this.isField = (member instanceof Field);
        }

        public final Member getMember() {
            return this.member;
        }


        /**
         * Either this or {@link #getResourceToInject} needs to be overridden.
         */
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


        /**
         * Either this or {@link #inject} needs to be overridden.
         */
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
