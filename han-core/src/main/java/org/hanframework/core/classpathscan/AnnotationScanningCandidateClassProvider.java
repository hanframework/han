package org.hanframework.core.classpathscan;

import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.BeanTools;
import org.hanframework.tool.extension.HanFactoriesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hanframework.autoconfigure.EnableAutoConfiguration;
import org.hanframework.beans.parse.annotation.BeanDefinitionParsers;
import org.hanframework.beans.postprocessor.annotation.BeanFactoryProcessor;
import org.hanframework.beans.postprocessor.annotation.BeanProcessor;
import org.hanframework.context.annotation.ComponentScan;
import org.hanframework.context.annotation.FilterType;
import org.hanframework.context.annotation.Import;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.hanframework.tool.reflection.ClassTools;

import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.*;

/**
 * 扫描应用环境中将要被IOC容器扫描的Class字节码
 *
 * @author liuxin
 * @version Id: ClassPathScanningCandidateComponentProvider.java, v 0.1 2018/10/12 9:18 AM
 */
@Slf4j
public final class AnnotationScanningCandidateClassProvider {

    /**
     * 当一个类上指定了@Import注解,将从@Import注解中读取需要添加的Class
     *
     * @param cls 任意注解
     * @return 获取导入的类
     */
    protected Set<Class> importConfigurationClass(Class<?> cls) {
        HashSet<Class> importConfigClasses = new HashSet<>();
        if (!AnnotationTools.isContainsAnnotation(cls, Import.class)) {
            return importConfigClasses;
        }
        Import annotation = cls.getAnnotation(Import.class);
        importConfigClasses.addAll(Arrays.asList(annotation.value()));
        return importConfigClasses;
    }

    /**
     * @param primarySources 将要被扫描的字节码,注意该字节码一定要包含@ComponentScan注解
     * @return 扫描到的字节码
     */
    public Set<Class> scan(Class<?> primarySources) {
        Set<Class> classesByPackages = new ConcurrentHashSet<>();
        //当被PostProcessor标记则从配置中读取,处理器
        classesByPackages.addAll(HanFactoriesLoader.getAutoConfigure(BeanProcessor.class));
        classesByPackages.addAll(HanFactoriesLoader.getAutoConfigure(BeanFactoryProcessor.class));
        classesByPackages.addAll(HanFactoriesLoader.getAutoConfigure(BeanDefinitionParsers.class));
        if (primarySources == null || !AnnotationTools.isContainsAnnotation(primarySources, ComponentScan.class)) {
            log.warn("bootstrap class should contain ComponentScan.class: cls=[" + primarySources + "] not contain ComponentScan.class");
            return classesByPackages;
        }
        AnnotationMetadata annotationMetadata = AnnotationTools.getAnnotationMetadata(primarySources);
        //读取自动配置
        if (annotationMetadata.hasAnnotation(EnableAutoConfiguration.class)) {
            classesByPackages.addAll(HanFactoriesLoader.getAutoConfigure(EnableAutoConfiguration.class));
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ComponentScan componentScan = annotationMetadata.getAnnotation(ComponentScan.class);
        String[] scanPackages = componentScan.scanPackages();

        //当没有指定扫描包,则从当前目录向下扫描
        if (scanPackages.length == 0) {
            scanPackages = new String[1];
            scanPackages[0] = ClassTools.getPackageName(primarySources);
        }
        for (String packagePath : scanPackages) {
            Set<Class<?>> classesByPackage;
            try {
                classesByPackage = ClassTools.getClassesByPackageName(contextClassLoader,
                        packagePath, true, true);
            } catch (Exception e) {
                throw new ScanClassException(e);
            }
            classesByPackages.addAll(classesByPackage);
        }
        //如果被过滤器匹配到
        scanFilter(classesByPackages, typeFiltersFor(componentScan));
        List<Class> imports = new ArrayList<>();
        //从所有的字节码上,读取Import的类
        for (Class c : classesByPackages) {
            imports.addAll(importConfigurationClass(c));
        }
        classesByPackages.addAll(imports);
        //打印所有的被扫描到的字节码文件
        log.info("打印已扫描到的文件名开始");
        for (Class beanCls : classesByPackages) {
            log.debug(ClassTools.getQualifiedName(beanCls));
        }
        log.info("打印已扫描到的文件名结束");
        return classesByPackages;
    }

    private void scanFilter(Set<Class> scannedClass, List<TypeFilter> typeFilters) {
        for (TypeFilter typeFilter : typeFilters) {
            for (Class prepareCls : scannedClass) {
                if (!typeFilter.match(prepareCls)) {
                    scannedClass.remove(prepareCls);
                }
            }
        }
    }


    private List<TypeFilter> typeFiltersFor(ComponentScan componentScan) {
        List<TypeFilter> typeFilters = new ArrayList<>();
        ComponentScan.Filter[] filters = componentScan.excludeFilters();
        for (ComponentScan.Filter filter : filters) {
            FilterType filterType = filter.type();
            switch (filterType) {
                case REGEX:
                    log.error("正则匹配现在不支持");
                    break;
                case CUSTOM:
                    Arrays.stream(filter.value()).map(BeanTools::newInstance).forEach(typeFilters::add);
                    break;
                default:
                    //自定义
                    break;
            }
        }
        return typeFilters;
    }

}
