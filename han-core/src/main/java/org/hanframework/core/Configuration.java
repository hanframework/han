package org.hanframework.core;

import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.beanfactory.ConfigurableBeanFactory;
import org.hanframework.beans.beanfactory.convert.TypeConverterRegistry;
import org.hanframework.beans.condition.ConditionFilterRegistry;
import org.hanframework.context.ApplicationContext;
import org.hanframework.core.env.Environment;
import org.hanframework.core.env.resolver.MultiPropertyResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置信息
 * <p>
 * 设计风格参考MyBatis,全局配置都放在这里,简单容易理解,可扩展。无论是框架内配置或者第三方配置都包含在这里面
 * <p>
 * 但是这里不包含环境信息的配置,环境信息的配置保存在.
 * <p>
 * 所有的属性为final防止,被修改
 *
 * @author liuxin
 * @version Id: Configuration.java, v 0.1 2019-03-18 15:16
 * @see Environment
 */
public final class Configuration {

    private final TypeConverterRegistry typeConverterRegistry;

    private final ConditionFilterRegistry conditionFilterRegistry;

    private final MultiPropertyResolver multiPropertyResolver;

    /**
     * 集中化配置,所有第三方都可以拿到自己的培新信息
     */
    private final Map<ModuleInfo, ModuleConfiguration> modulesConfiguration = new ConcurrentHashMap<>();

    public Configuration(ApplicationContext applicationContext) {
        ConfigurableBeanFactory beanFactory = applicationContext.getBeanFactory();
        conditionFilterRegistry = new ConditionFilterRegistry((BeanFactory) beanFactory);
        beanFactory.setConfiguration(this);
        //类型转换器工厂创建
        typeConverterRegistry = new TypeConverterRegistry();
        this.multiPropertyResolver = applicationContext.getConfigurableEnvironment();
    }


    public TypeConverterRegistry getTypeConverterRegistry() {
        return typeConverterRegistry;
    }


    public ConditionFilterRegistry getConditionFilterRegistry() {
        return conditionFilterRegistry;
    }


    public MultiPropertyResolver getMultiPropertyResolver() {
        return multiPropertyResolver;
    }

    public void addModuleConfiguration(ModuleConfiguration moduleConfiguration) {
        ModuleInfo moduleInfo = moduleConfiguration.getModuleInfo();
        if (containModule(moduleInfo)) {
            throw new DumpModuleConfigurationException("[" + moduleInfo + "]already exists");
        }
        modulesConfiguration.put(moduleInfo, moduleConfiguration);
    }

    public boolean containModule(ModuleInfo moduleInfo) {
        return modulesConfiguration.containsKey(moduleInfo);
    }

    /**
     * 根据模块名获取配置信息,当存在多个版本则报错,存在一个直接返回 ?
     *
     * @param moduleInfo 第三方模块信息
     * @return 第三方配置
     */
    public ModuleConfiguration getModuleConfiguration(ModuleInfo moduleInfo) {
        return modulesConfiguration.get(moduleInfo);
    }

    /**
     * @param moduleName 模块名
     * @param version    版本号
     * @return 第三方配置
     */
    public ModuleConfiguration getModuleConfiguration(String moduleName, String version) {
        return getModuleConfiguration(new ModuleInfo(moduleName, version));
    }


    public void refreshModuleConfiguration(ModuleConfiguration moduleConfiguration) {
        ModuleInfo moduleInfo = moduleConfiguration.getModuleInfo();
        if (containModule(moduleInfo)) {
            throw new DumpModuleConfigurationException("[" + moduleInfo + "]already exists");
        }
        modulesConfiguration.put(moduleInfo, moduleConfiguration);
    }

    /**
     * 根据模块名获取配置信息,当存在多个版本则报错,存在一个直接返回
     *
     * @param moduleName 模块名
     * @return 第三方配置
     */
    public ModuleConfiguration getModuleConfiguration(String moduleName) {
        List<ModuleConfiguration> moduleConfigurations = new ArrayList<>();
        for (Map.Entry<ModuleInfo, ModuleConfiguration> mis : modulesConfiguration.entrySet()) {
            if (mis.getKey().getModuleName().equalsIgnoreCase(moduleName)) {
                moduleConfigurations.add(mis.getValue());
            }
        }
        if (moduleConfigurations.size() > 1) {
            //存在多个版本
            List<ModuleInfo> moduleInfos = new ArrayList<>();
            moduleConfigurations.forEach(x -> moduleInfos.add(x.getModuleInfo()));
            throw new RuntimeException("Multiple module version information exists:" + moduleInfos);
        }
        return moduleConfigurations.size() > 0 ? moduleConfigurations.get(0) : null;
    }


}
