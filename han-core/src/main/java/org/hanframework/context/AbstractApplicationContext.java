package org.hanframework.context;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.env.ConfigurableEnvironment;
import org.hanframework.env.StandardEnvironment;
import org.hanframework.tool.app.ApplicationPid;
import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;
import org.hanframework.beans.beanfactory.postprocessor.BeanFactoryPostProcessor;
import org.hanframework.beans.postprocessor.BeanDefinitionRegistryPostProcessor;
import org.hanframework.beans.postprocessor.BeanPostProcessor;
import org.hanframework.beans.postprocessor.CommonAnnotationBeanPostProcessor;
import org.hanframework.beans.postprocessor.impl.ApplicationContextAwareProcessor;
import org.hanframework.beans.postprocessor.impl.AutowiredAnnotationBeanPostProcessor;
import org.hanframework.context.listener.ApplicationListener;
import org.hanframework.context.listener.DefaultEnvironmentLoadListener;
import org.hanframework.context.listener.event.EnvironmentLoadEvent;
import org.hanframework.env.Configuration;
import org.hanframework.env.postprocessor.EnvironmentPostProcessor;
import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.date.StopWatch;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liuxin
 * @version Id: AbstractSmileApplicationContext.java, v 0.1 2018/10/10 5:48 PM
 */
@Slf4j
public abstract class AbstractApplicationContext implements ApplicationContext {

    /**
     * 启动时间
     */
    private long startTime;
    /**
     * 系统启动标识
     */
    private final AtomicBoolean active = new AtomicBoolean();
    /**
     * 系统关闭标识
     */
    private final AtomicBoolean closed = new AtomicBoolean();
    /**
     * 对ConfigurableBeanFactory接口,进行配置，提供给开发者使用
     * 使用方法:
     * 从BeanDefinition中读取BeanFactoryPostProcessor类型的类进行注入
     */
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    /**
     * 环境信息
     */
    private ConfigurableEnvironment configurableEnvironment;
    /**
     * 监听器
     */
    private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();
    /**
     * 构造程序,在程序运行结束后执行
     */
    private Thread shutdownHook;


    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        Assert.notNull(postProcessor, "BeanFactoryPostProcessor 不能为null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    /**
     * 运行扩展类获取bean的副本信息
     *
     * @return
     */
    @Override
    public Map<String, BeanDefinition> getBeanBeanDefinitionMap() {
        return getBeanFactory().getBeanDefinition();
    }

    /**
     * 处理可配置的信息
     *
     * @param args
     * @return
     */
    public void prepareEnvironment(String[] args) {
        //生层一个环境加载时间
        EnvironmentLoadEvent loadEvent = new EnvironmentLoadEvent(getConfigurableEnvironment(), args);
        new DefaultEnvironmentLoadListener().onApplicationEvent(loadEvent);
    }


    @Override
    public ConfigurableEnvironment getConfigurableEnvironment() {
        if (this.configurableEnvironment == null) {
            this.configurableEnvironment = this.createEnvironment();
        }

        return this.configurableEnvironment;
    }

    @Override
    public abstract ConfigurableBeanFactory getBeanFactory();


    /**
     * 对BeanFactory做一些预处理操作，设置需要的解析器等
     *
     * @param beanFactory
     */
    protected void prepareBeanFactory(ConfigurableBeanFactory beanFactory) {
        //注入环境配置
        beanFactory.setPropertyResolver(getConfigurableEnvironment());
        //注入全局的配置信息
        Configuration configuration = getConfiguration();
        beanFactory.setConfiguration(configuration);
    }


    protected void invokerEnvironmentPostProcessors(ConfigurableBeanFactory configurableBeanFactory, ConfigurableEnvironment configurableEnvironment) {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) configurableBeanFactory;
        //从IOC容器中根据类型获取到的处理器
        //留给用户对配置文件修改的机会
        String[] environmentPostProcessorNames = listableBeanFactory.getBeanNamesForType(EnvironmentPostProcessor.class);
        for (String environmentPostProcessorName : environmentPostProcessorNames) {
            EnvironmentPostProcessor environmentPostProcessor = (EnvironmentPostProcessor) listableBeanFactory.getBean(environmentPostProcessorName);
            environmentPostProcessor.postProcessEnvironment(configurableEnvironment);
        }
    }

    /**
     * 给开发者最后一次修改BeanFactory的机会
     *
     * @param configurableBeanFactory
     */
    protected void invokeBeanFactoryPostProcessors(ConfigurableBeanFactory configurableBeanFactory) {
        //容器初始化就配置好的处理器
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = getBeanFactoryPostProcessors();
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) getBeanFactory();
        //从IOC容器中根据类型获取到的处理器
        String[] beanFactoryPostProcessorForType = listableBeanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class);
        for (String beanFactoryPostProcessor : beanFactoryPostProcessorForType) {
            beanFactoryPostProcessors.add((BeanFactoryPostProcessor) listableBeanFactory.getBean(beanFactoryPostProcessor));
        }
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            //外部加载BeanDefinition放到IOC容器
            if (beanFactoryPostProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                ((BeanDefinitionRegistryPostProcessor) beanFactoryPostProcessor).postProcessBeanDefinitionRegistry(listableBeanFactory);
            }
            beanFactoryPostProcessor.postProcessBeanFactory(configurableBeanFactory);
        }

    }


    /**
     * 注册Bean的前后处理器
     * 从当前的BeanDefinition中找到BeanPostProcessor的名字，然后添加到
     * 内置的处理器
     *
     * @param configurableBeanFactory
     */
    protected void registerBeanPostProcessors(ConfigurableBeanFactory configurableBeanFactory) {
        //---------------------内置处理器-----------------------
        //内置自动注入转换器
        configurableBeanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this, configurableBeanFactory));
        //处理@PostConstruct,@PreDestroy
        configurableBeanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        //处理@Autowire注解
        AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
        autowiredAnnotationBeanPostProcessor.setBeanFactory((BeanFactory) configurableBeanFactory);
        configurableBeanFactory.addBeanPostProcessor(autowiredAnnotationBeanPostProcessor);
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableBeanFactory;
        //---------------------外部处理器-----------------------
        //外部的处理器,要被@PostProcessor注解,原理@PostProcessor注解被@SmileComponent修饰
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class);
        for (String postProcessorName : postProcessorNames) {
            Object bean = beanFactory.getBean(postProcessorName);
            configurableBeanFactory.addBeanPostProcessor((BeanPostProcessor) bean);
        }
    }

    /**
     * 注册监听器
     */
    protected void registerListeners() {
        String[] listenerNames = ((DefaultListableBeanFactory) getBeanFactory()).getBeanNamesForType(ApplicationListener.class);
        for (String listenerName : listenerNames) {
            getApplicationListeners().add((ApplicationListener) getBean(listenerName));
        }
    }


    /**
     * 容器启动后要做的事情
     */
    protected void finishRefresh() {
        registerShutdownHook();
        //添加事件通知->发送启动时间
    }


    /**
     * 保存系统的当前运行状态和启动时间
     */
    protected void prepareRefresh() {
        //记录容器加载的开始时间,最后可统计是否时间差
        this.startTime = System.currentTimeMillis();
        //保存当前上下文的运行状态
        this.closed.set(false);
        this.active.set(true);
        if (log.isInfoEnabled()) {
            log.info("Refreshing " + this);
        }
    }

    private ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }


    /**
     * 扫描所有的类,并装载
     * 当调用该方法spring就开始启动工作
     */
    protected void refresh() {
        StopWatch stopWatch = new StopWatch("Application耗时统计");
        //保存系统启动时间和当前运行状态
        stopWatch.start("Prepare Refresh Application");
        prepareRefresh();
        stopWatch.stop();
        log.info("Current system system pid:[ " + new ApplicationPid() + " ]");
        //创建一个BeanFactory以后的操作都围绕这个来。
        stopWatch.start("Create BeanFactory");
        ConfigurableBeanFactory beanFactory = getBeanFactory();
        stopWatch.stop();
        //预处理设置某些解析器等等
        stopWatch.start("Prepare BeanFactory");
        prepareBeanFactory(beanFactory);
        stopWatch.stop();
        //加载bean信息生成BeanDefinition。
        stopWatch.start("Load BeanDefinition");
        loadBeanDefinition(beanFactory);
        stopWatch.stop();
        //执行配置环境处理器
        stopWatch.start("Invoker EnvironmentPostProcessors");
        invokerEnvironmentPostProcessors(beanFactory, configurableEnvironment);
        stopWatch.stop();
        //给开发者最后一次修改BeanFactory的机会,可以在此种添加些addBeanPostProcessor
        stopWatch.start("Invoker BeanFactoryPostProcessors");
        invokeBeanFactoryPostProcessors(beanFactory);
        stopWatch.stop();
        //注册Bean 前后处理器,这些解析器对最终生成Bean有很大作用
        stopWatch.start("Register BeanPostProcessors");
        registerBeanPostProcessors(beanFactory);
        stopWatch.stop();
        //注册监听器
        stopWatch.start("Register Listeners");
        registerListeners();
        stopWatch.stop();
        //BeanFactory初始化，对单例进行提前处理
        stopWatch.start("Finish No-Lazy Bean Initialization");
        finishBeanFactoryInitialization(beanFactory);
        stopWatch.stop();
        //最后要做的事情
        stopWatch.start("Finish Refresh Application");
        finishRefresh();
        stopWatch.stop();
        log.debug(stopWatch.prettyPrint());

    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(this);
    }

    private void doClose() {
        log.info("The container begins to close ...");
        destroyBeans();
    }

    private void destroyBeans() {
        getBeanFactory().destroySingletons();
    }


    @Override
    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread(this::doClose);
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    /**
     * BeanFactory初始化操作，可以在此方法中最后对BeanFactory进行处理,并初始化单例
     *
     * @param beanFactory BeanFactory
     */
    protected void finishBeanFactoryInitialization(ConfigurableBeanFactory beanFactory) {
        beanFactory.preInstantiateSingletons();
    }


    /**
     * 加载BeanDefinition的入口
     *
     * @param configurableBeanFactory BeanFactory实例
     */
    protected abstract void loadBeanDefinition(ConfigurableBeanFactory configurableBeanFactory);

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, "ApplicationListener 不能为null");
        this.applicationListeners.add(listener);
    }

    public Set<ApplicationListener<?>> getApplicationListeners() {
        return applicationListeners;
    }


    private void setStartupDate(long startupDate) {
        this.startTime = startupDate;
    }


    private long getStartupDate() {
        return startTime;
    }


    @Override
    public Object getBean(String beanName) {
        return ((DefaultListableBeanFactory) getBeanFactory()).getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requiredType) {
        return ((DefaultListableBeanFactory) getBeanFactory()).getBean(beanName, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        return ((DefaultListableBeanFactory) getBeanFactory()).getBean(beanClass);
    }

    @Override
    public boolean containsBean(String var1) {
        return ((DefaultListableBeanFactory) getBeanFactory()).containsBean(var1);
    }


}
