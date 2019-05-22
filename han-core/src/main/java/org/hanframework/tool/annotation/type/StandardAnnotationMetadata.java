package org.hanframework.tool.annotation.type;

import org.hanframework.tool.annotation.AnnotationAttributes;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.asserts.Assert;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * 标准类信息
 *
 * @author liuxin
 * @version Id: StandardAnnotationMetadata.java, v 0.1 2019-01-10 15:25
 */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {

    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        super(introspectedClass);
    }

    @Override
    public boolean hasAnnotation(Class annotationType) {
        Assert.isTrue(annotationType.isAnnotation(), "annotationType must be annotation");
        Class<?> introspectedClass = getIntrospectedClass();
        //注解没有继承的概念,只有派生包含概念
        return AnnotationTools.isContainsAnnotation(introspectedClass, annotationType);
    }

    @Override
    public boolean isAnnotated(Class annotationType) {
        Assert.isTrue(annotationType.isAnnotation(), "annotationType must be annotation");
        return getIntrospectedClass().equals(annotationType);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(Annotation annotation) {
        return AnnotationTools.getAnnotationAttributeAsMap(annotation);
    }

    @Override
    public List<AnnotationAttributes> getAnnotationAttributes(Class<? extends Annotation> annotationType) {
        return AnnotationTools.getAnnotationAttributes(annotationType);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return AnnotationTools.findAnnotation(getIntrospectedClass(), annotationClass);
    }
}
