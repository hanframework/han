package org.hanframework.beans.postprocessor;

import org.hanframework.beans.beandefinition.BeanDefinitionRegistry;
import org.hanframework.tool.annotation.type.AnnotationMetadata;

/**
 * 读取配置类的信息,来加载逻辑
 * @author liuxin
 * @version Id: ImportBeanDefinitionRegistrar.java, v 0.1 2019-01-10 14:58
 */
public interface ImportBeanDefinitionRegistrar {
  void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry);
}
