package org.hanframework.beans.beanfactory.impl;

import org.hanframework.beans.factorybean.FactoryBean;
import org.hanframework.tool.reflection.MethodTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beandefinition.GenericBeanDefinition;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.beanfactory.BeanFactoryTools;
import org.hanframework.beans.beanfactory.BeanNameGenerator;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.convert.TypeConverter;
import org.hanframework.beans.beanfactory.convert.TypeConverterRegistry;
import org.hanframework.beans.beanfactory.lifecycle.DisposableBean;
import org.hanframework.beans.condition.ConditionHandler;
import org.hanframework.beans.exception.BeanCurrentlyInCreationException;
import org.hanframework.beans.postprocessor.BeanPostProcessor;
import org.hanframework.beans.postprocessor.DestructionAwareBeanPostProcessor;
import org.hanframework.beans.postprocessor.InstantiationAwareBeanPostProcessor;
import org.hanframework.env.Configuration;
import org.hanframework.env.resolver.MultiPropertyResolver;
import org.hanframework.tool.asserts.Assert;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用方法
 *
 * @author liuxin
 * @version Id: AbstractBeanFactory.java, v 0.1 2018/10/29 11:39 AM
 */
public abstract class AbstractBeanFactory implements ConfigurableBeanFactory, BeanFactory {

    protected Logger logger = LoggerFactory.getLogger(AbstractBeanFactory.class);

    /**
     *
     */
    private final Set<BeanPostProcessor> beanPostProcessors = new HashSet<>();

    protected static final Object NULL_OBJECT = new Object();

    /**
     * 循环依赖标记
     * 如果当前类A正在创建,依赖的类B也在创建,当创建B时候又创建A，此时就构成循环依赖
     * A--->B--->A 循环依赖
     */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));

    private final Set<String> inCreationCheckExclusions =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));


    /**
     * 单例的缓存: bean name --> bean instance
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(64);

    /**
     * Cache of singleton factories: bean name --> ObjectFactory
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>(16);

    /**
     * 缓存FactoryBean的接口
     */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>(16);


    protected final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    /**
     * 全局配置信息
     */
    private Configuration configuration;

    /**
     * Configuration 已经包含MultiPropertyResolver
     */
    private MultiPropertyResolver multiPropertyResolver;

    /**
     * 如果实现了实例化前后处理器在改标识为true
     * 如果为true就以为这有部分的Bean会是由处理器来直接生成实例，而不是都依赖于Spring来构建
     */
    private boolean hasInstantiationAwareBeanPostProcessors;

    /**
     * Indicates whether any DestructionAwareBeanPostProcessors have been registered
     */
    private boolean hasDestructionAwareBeanPostProcessors;


    @Override
    public void setPropertyResolver(MultiPropertyResolver multiPropertyResolver) {
        this.multiPropertyResolver = multiPropertyResolver;
    }

    public MultiPropertyResolver getPropertyResolver() {
        return this.multiPropertyResolver;
    }


    @Override
    public ConditionHandler getConditionHandler() {
        return getConfiguration().getConditionFilterRegistry();
    }

    /**
     * 返回所有的处理器
     *
     * @return
     */
    @Override
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return new ArrayList<>(this.beanPostProcessors);
    }


    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        Assert.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
        this.beanPostProcessors.add(beanPostProcessor);
        if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
            this.hasInstantiationAwareBeanPostProcessors = true;
        }
        if (beanPostProcessor instanceof DestructionAwareBeanPostProcessor) {
            this.hasDestructionAwareBeanPostProcessors = true;
        }
    }

    @Override
    public int getBeanPostProcessorCount() {
        return beanPostProcessors.size();
    }


    @Override
    public boolean isCurrentlyInCreation(String beanName) {
        return false;
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        this.singletonObjects.put(beanName, singletonObject);
    }


    @Override
    public Object getSingleton(String beanName) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject instanceof FactoryBean) {
            return ((FactoryBean) singletonObject).getObject();
        }
        return singletonObject;
    }

    /**
     * 对名字先进行处理,如果是FactoryBean的名字就把名字转换成Bean的名字
     * 先从单例缓存中
     *
     * @param name          Bean名字
     * @param requiredType  将要获取的Bean类型
     * @param args          参数
     * @param typeCheckOnly 是否检查类型
     * @param <T>
     * @return
     */
    protected <T> T doGetBean(
            final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly) {
        //获取转换后的BeanName,如果是FactoryBean就把前缀&去掉
        final String beanName = transformedBeanName(name);
        Object bean;

        //从单例中获取实例
        Object sharedInstance = getSingleton(beanName);

        if (sharedInstance != null && args == null) {
            if (logger.isDebugEnabled()) {
                if (isSingletonCurrentlyInCreation(beanName)) {
                    logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
                            "' that is not fully initialized yet - a consequence of a circular reference");
                } else {
                    logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
                }
            }
            //先判断是否是FactoryBean,如果是调用getObject()
            bean = getObjectForBeanInstance(sharedInstance, beanName);
        } else {
            //创建Bean，如果是单例，添加到单例中
            if (!containsBean(beanName)) {
                return null;
            }
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) getBeanDefinition(beanName);
            if (beanDefinition.isSingleton()) {
                sharedInstance = getSingleton(beanName);
                //将当前正在创建的类添加到set集合中
                beforeSingletonCreation(beanName);
                if (sharedInstance == null) {
                    sharedInstance = createBean(beanName, beanDefinition, null, false);
                    if (sharedInstance != null) {
                        this.singletonObjects.put(beanName, sharedInstance);
                    }
                    //创建成功就移除
                    afterSingletonCreation(beanName);
                }
            } else {
                sharedInstance = createBean(beanName, beanDefinition, null, false);
            }
            bean = getObjectForBeanInstance(sharedInstance, beanName);
        }

        //如果类型不一样,就转换。否则直接返回
        if (requiredType != null && bean != null && !requiredType.isInstance(bean)) {
            return getTypeConverter().convertIfNecessary(bean, requiredType);
        }
        return (T) bean;
    }


    public abstract BeanDefinition getBeanDefinition(String beanName);

    /**
     * 创建Bean,因为有很多的依赖所有智能有
     *
     * @param beanName bean名字
     * @param mbd      bean创建信息
     * @param args     bean创建参数
     * @return
     */
    protected abstract Object createBean(String beanName, GenericBeanDefinition mbd, Object[] args, boolean checked);


    /**
     * 从FactoryBean中getObject获取实例
     *
     * @param beanInstance bean工厂实例
     * @param beanName     bean名字
     * @return bean实例
     */
    protected Object getObjectForBeanInstance(
            Object beanInstance, String beanName) {
        if (beanInstance == null) {
            return null;
        }
        //如果不是FactoryBean,但是还是以&开头,就报错.
        if (BeanFactoryTools.isFactoryDereference(beanName) && !(beanInstance instanceof FactoryBean)) {
            System.err.println("以&开头,但是不是FactoryBean的实例,直接报错");
        }
        //如果是&开头,但是不是FactoryBean就直接返回
        if (!(beanInstance instanceof FactoryBean) || BeanFactoryTools.isFactoryDereference(beanName)) {
            return beanInstance;
        }
        Object object = getCachedObjectForFactoryBean(beanName);
        if (object == null) {
            FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
            /**
             * 如果是单例就把他放到缓存中
             */
            if (factory.isSingleton()) {
                object = factory.getObject();
                this.factoryBeanObjectCache.put(beanName, (object != null ? object : NULL_OBJECT));
            }
        }
        return object;
    }

    /**
     * 先从缓存中拿到FactoryBean的Bean
     *
     * @param beanName bean名字
     * @return bean实例
     */
    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }

    /**
     * Return whether the specified singleton bean is currently in creation
     * (within the entire factory).
     *
     * @param beanName the name of the bean
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /**
     * 当一个类正在创建,那么此时集合中只会出现一个当前创建的类型
     * 当第二次调用该方法,发现已经存在，则直接报错。容器不允许创建循环依赖的类
     *
     * @param beanName bean名字
     */
    protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) &&
                !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }
    }


    protected void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) &&
                !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }


    /**
     * 转换后的BeanName
     *
     * @param name bean名字
     * @return
     */
    protected String transformedBeanName(String name) {
        return (BeanFactoryTools.transformedBeanName(name));
    }

    @Override
    public Object getBean(String name) {
        return doGetBean(name, null, null, false);
    }


    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return doGetBean(name, requiredType, null, false);
    }

    /**
     * 是否包含当期Bean
     *
     * @param name bean名字
     * @return true包含，false不包含
     */
    @Override
    public abstract boolean containsBean(String name);

    @Override
    public boolean isSingleton(String name) {
        return getBeanDefinition(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return !isSingleton(name);
    }

    @Override
    public Class getType(String name) {
        return getBeanDefinition(name).getOriginClass();
    }

    /**
     * 类型转换器,当使用注入依赖Bean时候,可以根据转换器转换成想要的类型。
     * eg:
     * 1. 一个bean中,int类型使用了@Value注入环境参数,TypeConverterSupport会根据类型(int)找到指定的int属性解析器将从环境中读取到的
     * String类型的参数,转换成int类型
     * 2. 一个bean中,一个List或者Map的参数,也使用了@Value注入环境参数,TypeConverterSupport会根据类型(List或者Map)找到指定的属性
     * 解析器将String转换成List或者Map
     * <p>
     * List.class ==> new ListEditor()
     * Map.class  ==> new MapEditor()
     *
     * @return
     * @see TypeConverterRegistry#convertIfNecessary(Object, Class)
     */
    public TypeConverter getTypeConverter() {
        return getConfiguration().getTypeConverterRegistry();
    }


    /**
     * 单例初始化,当是单例
     */
    @Override
    public void preInstantiateSingletons() {
        Iterator<Map.Entry<String, BeanDefinition>> iterator = getBeanDefinition().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BeanDefinition> next = iterator.next();
            String beanName = next.getKey();
            BeanDefinition bd =  next.getValue();
            if (bd.isSingleton() && bd.isLazy() && !bd.isAbstract()) {
                getBean(beanName);
            }
        }
    }


    @Override
    public void destroySingletons() {
        Iterator<Map.Entry<String, BeanDefinition>> iterator = getBeanDefinition().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BeanDefinition> next = iterator.next();
            String beanName = next.getKey();
            BeanDefinition bd =  next.getValue();
            String destroyMethodName = bd.getDestroyMethodName();
            if (bd.isSingleton() && bd.isLazy() && !bd.isAbstract()) {
                Object singleton = getSingleton(beanName);
                if (singleton == null) {
                    continue;
                }
                List<BeanPostProcessor> beanPostProcessors = getBeanPostProcessors();
                for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                    if (beanPostProcessor instanceof DestructionAwareBeanPostProcessor) {
                        ((DestructionAwareBeanPostProcessor) beanPostProcessor).postProcessBeforeDestruction(singleton, beanName);
                    }
                }
                if (singleton instanceof DisposableBean) {
                    ((DisposableBean) singleton).destroy();
                }
                //执行xml中配置的
                try {
                    if (null != destroyMethodName) {
                        MethodTools.findMethodByName(singleton.getClass(), destroyMethodName).invoke(singleton);
                    }
                } catch (Exception i) {
                    logger.error("自定义销毁方法[" + destroyMethodName + "]执行异常");
                }
            }
        }
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }
}
