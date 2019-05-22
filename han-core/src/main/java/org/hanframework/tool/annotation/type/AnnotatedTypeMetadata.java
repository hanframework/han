package org.hanframework.tool.annotation.type;

import org.hanframework.tool.annotation.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 注解信息
 *
 * @author liuxin
 * @version Id: AnnotatedTypeMetadata.java, v 0.1 2019-01-10 15:02
 */
public interface AnnotatedTypeMetadata {

    /**
     * 判断当前类是否是一个注解
     *
     * @param annotationType 注解类型
     * @return true: 是当前注解,false: 不是
     */
    boolean isAnnotated(Class<Annotation> annotationType);

    /**
     * 获取当前注解的属性信息
     *
     * @param annotation 注解实例
     * @return 注解实例属性
     */
    Map<String, Object> getAnnotationAttributes(Annotation annotation);

    /**
     * 获取当前注解类型的属性信息
     *
     * @param annotationType
     * @return
     */
    List<AnnotationAttributes> getAnnotationAttributes(Class<? extends Annotation> annotationType);
}
