package org.hanframework.tool.annotation.type;


import java.lang.annotation.Annotation;

/**
 * 一个类的注解信息
 *
 * @author liuxin
 * @version Id: AnnotationMetadata.java, v 0.1 2019-01-10 15:00
 */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {

    /**
     * 类是否包含当前注解
     *
     * @param annotationClass 注解类
     * @return true: 包含 false: 不包含
     */
    boolean hasAnnotation(Class annotationClass);

    /**
     * 获取类注解
     *
     * @param annotationClass 注解类
     * @param <A>             注解
     * @return 注解实例
     */
    <A extends Annotation> A getAnnotation(Class<A> annotationClass);



}
