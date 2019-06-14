package org.hanframework.beans.beandefinition;

import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.impl.ObjectFactory;
import org.hanframework.beans.factorybean.FactoryBean;
import org.hanframework.beans.parse.ConfigurationBeanMethod;
import org.hanframework.beans.ConstructorBuilder;
import org.hanframework.beans.sort.Order;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.reflection.ClassTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

/**
 * @author liuxin
 * @version Id: AbstractBeanDefinition.java, v 0.1 2019-01-31 17:08
 */
public abstract class AbstractBeanDefinition implements ConfigurableBeanDefinition, BeanDefinition {

    private static final String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    private String scope = SCOPE_SINGLETON;

    private boolean primary = false;

    private boolean lazyInit = false;

    private volatile Class beanClass;

    private String initMethodName;

    private String destroyMethodName;

    private String beanName;

    private ObjectFactory customerInstantiationFactory;

    private ConfigurationBeanMethod configurationBeanMethod;

    public boolean beforeInstantiationResolved = true;

    private List<ConstructorMetadata> constructorInfo;

    public ObjectFactory getCustomerInstantiationFactory() {
        return customerInstantiationFactory;
    }

    public void setBeforeInstantiationResolved(boolean beforeInstantiationResolved) {
        this.beforeInstantiationResolved = beforeInstantiationResolved;
    }

    @Override
    public boolean isBeforeInstantiationResolved() {
        return beforeInstantiationResolved;
    }


    public boolean isCustomerInstantiation() {
        return customerInstantiationFactory != null;
    }

    @Override
    public void setCustomerInstantiationFactory(ObjectFactory customerInstantiationFactory) {
        this.customerInstantiationFactory = customerInstantiationFactory;
    }


    @Override
    public int getOrder() {
        if (getAnnotationMetadata().hasAnnotation(Order.class)) {
            return getAnnotationMetadata().getAnnotation(Order.class).value();
        }
        return 0;
    }

    @Override
    public String getInitMethodName() {
        return initMethodName;
    }

    @Override
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    @Override
    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    @Override
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    @Override
    public void setBeanClass(Class beanClass) {
        Assert.isTrue(beanClass != null, "BeanDefinition原始字节码不能为null");
        this.beanClass = beanClass;
        buildConstructorInfo();
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean isLazy() {
        return this.lazyInit;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }

    @Override
    public boolean isPrototype() {
        return !isSingleton();
    }

    @Override
    public boolean isAbstract() {
        return AnnotationTools.getAnnotationMetadata(beanClass).isAbstract();
    }


    @Override
    public void setConfigurationBeanMethod(ConfigurationBeanMethod configurationBeanMethod) {
        this.configurationBeanMethod = configurationBeanMethod;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public Class getOriginClass() {
        return beanClass;
    }

    public ConfigurationBeanMethod getConfigurationBeanMethod() {
        return this.configurationBeanMethod;
    }

    @Override
    public String getOriginClassShortName() {
        return ClassTools.getShortName(getOriginClass());
    }

    @Override
    public boolean isFactoryBean() {
        return FactoryBean.class.isAssignableFrom(getOriginClass());
    }

    @Override
    public Constructor[] getConstructors() {
        return getOriginClass().getDeclaredConstructors();
    }

    @Override
    public Annotation[] getAnnotations() {
        return getOriginClass().getDeclaredAnnotations();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Constructor<?> getDefaultConstructor() throws NoSuchMethodException {
        return getOriginClass().getDeclaredConstructor();
    }

    private void buildConstructorInfo() {
        Optional<List<ConstructorMetadata>> constructorInfoOptional = ConstructorBuilder.buildConstructors(beanClass);
        if (constructorInfoOptional.isPresent()) {
            this.constructorInfo = constructorInfoOptional.get();
        }
    }

    @Override
    public List<ConstructorMetadata> getConstructorInfo() {
        return this.constructorInfo;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return AnnotationTools.getAnnotationMetadata(getOriginClass());
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(getOriginClass().getModifiers());
    }

    @Override
    public boolean isPrivate() {
        return Modifier.isPrivate(getOriginClass().getModifiers());
    }

    @Override
    public boolean isProtected() {
        return Modifier.isPublic(getOriginClass().getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(getOriginClass().getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(getOriginClass().getModifiers());
    }
}
