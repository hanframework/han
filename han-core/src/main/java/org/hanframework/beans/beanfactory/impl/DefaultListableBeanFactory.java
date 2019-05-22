package org.hanframework.beans.beanfactory.impl;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.beandefinition.BeanDefinitionRegistry;
import org.hanframework.beans.beanfactory.ListableBeanFactory;
import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beandefinition.BeanDefinitionRegistry;
import org.hanframework.beans.beandefinition.GenericBeanDefinition;
import org.hanframework.beans.beanfactory.ListableBeanFactory;
import org.hanframework.beans.beanfactory.NoSuchBeanException;
import org.hanframework.beans.beanfactory.convert.TypeConverter;
import org.hanframework.beans.factorybean.FactoryBean;
import org.hanframework.beans.postprocessor.impl.DependencyDescriptor;
import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.reflection.ClassTools;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractAutowireCapableBeanFactory提供了创建Bean和注入的方法
 * BeanDefinitionRegistry 提供了操作BeanDefinitionMap的方法
 * 维护和BeanDefinitionMap的关系
 *
 * @author liuxin
 * @version Id: DefaultListableBeanFactory.java, v 0.1 2018/10/11 11:21 AM
 */
@Slf4j
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ListableBeanFactory, BeanDefinitionRegistry {

    /**
     * 保存Bean的描述信息
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(64);

    /**
     * 实现BeanDefinitionRegistry相关方法
     */
    //---------------------------------------------------------------------
    // Implementation of BeanDefinitionRegistry interface
    //---------------------------------------------------------------------
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        log.debug("registerBeanDefinition:{}", beanName);
        this.beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void refreshBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinitionMap.containsKey(beanName)) {
            System.err.println("refresh " + beanName);
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        this.beanDefinitionMap.remove(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        String[] strings = this.beanDefinitionMap.keySet().toArray(new String[]{});
        return strings;
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }


    /**
     * 实现Bean工厂相关的接口方法
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        Assert.notNull(requiredType, "Required type must not be null");
        //根据类型注入要先去找FactoryBean,判断是否是抽象和接口，如果是
        if (requiredType.getGenericSuperclass() instanceof FactoryBean) {

        }
        if (!ClassTools.isInterface(requiredType)) {
            String beanName = getBeanDefinition(beanNameGenerator.generateBeanName(requiredType)).getBeanName();
            return (T) getBean(beanName);
        }
        String[] beanNamesForType = getBeanNamesForType(requiredType);
        if (beanNamesForType.length == 1) {
            return (T) getBean(beanNamesForType[0]);
        }
        //如果等于多个则获取每个的字节码,如果里面只有一个呗Pri修饰,则使用其中的
        List<String> primaryBeanNames = new ArrayList<>();
        for (String beanName : beanNamesForType) {
            if (getBeanDefinition(beanName).isPrimary()) {
                primaryBeanNames.add(beanName);
            }
        }
        if (primaryBeanNames.size() == 1) {
            return (T) getBean(primaryBeanNames.get(0));
        } else {
            throw new NoSuchBeanException("NoSuchBean:" + Arrays.asList(primaryBeanNames).toString());
        }
    }

    @Override
    public Class getType(String name) {
        return containsBean(name) ? getBeanDefinition(name).getOriginClass() : null;
    }

    @Override
    public boolean isFactoryBean(String name) {
        if (containsBeanDefinition(name)) {
            BeanDefinition beanDefinition = getBeanDefinition(name);
            return beanDefinition.isFactoryBean();
        }
        return false;
    }

    /**
     * @param descriptor
     * @param requestBeanName    可以根据请求的BeanName找到BeanDefinition
     * @param autowiredBeanNames 自动注入的beanName
     * @param typeConverter
     * @return
     */
    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestBeanName,
                                    Set<String> autowiredBeanNames, TypeConverter typeConverter) {
        Class<?> dependencyType = descriptor.getDependencyType();
        return getBean(beanNameGenerator.generateBeanName(dependencyType));
    }


    /**
     * 根据类型找到相同类型的BeanName。
     * 什么时候会使用这个呢?
     * 当一个bean需要注入一个接口或者是抽象类时候,众所周知接口和抽象类是不能够实力化的,这个时候我们就需要
     * 根据接口或者抽象类的类型找到，相同类型的beanName从而来执行注入,当只找到一个时候就可以实现注入
     * 但是当一个类型的接口或者抽象类，找到了多个BeanName,此时就不能正常注入,因为IOC容器并不知道你要注入那个
     * 这个时候你就要根据BeanName来指定注入了
     *
     * @param beanCls
     * @return
     */
    @Override
    public String[] getBeanNamesForType(Class<?> beanCls) {
        List<String> beanNameList = new ArrayList<>();
        Iterator<BeanDefinition> iterator = beanDefinitionMap.values().iterator();
        while (iterator.hasNext()) {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) iterator.next();
            Class beanClass = beanDefinition.getOriginClass();
            //是否匹配
            if (!beanClass.equals(beanCls) && beanCls.isAssignableFrom(beanClass)) {
                beanNameList.add(beanDefinition.getBeanName());
            }
        }
        //然后寻找被Configuration标记的bean，然后实例。
        return beanNameList.toArray(new String[beanNameList.size()]);
    }

    @Override
    public boolean containsBean(String name) {
        return beanDefinitionMap.keySet().contains(name);
    }

    /**
     * 返回一个副本,防止被外包恶意修改
     *
     * @return
     */
    @Override
    public Map<String, BeanDefinition> getBeanDefinition() {
        return new ConcurrentHashMap<>(beanDefinitionMap);
    }

}


