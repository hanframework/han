package org.hanframework.beans.beandefinition;

import org.hanframework.tool.annotation.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * 元信息
 *
 * @author liuxin
 * @version Id: MetaClassDefinition.java, v 0.1 2019-05-21 15:45
 */
public interface MetaClassDefinition {

    boolean isAbstract();

    boolean isPublic();

    boolean isPrivate();

    boolean isProtected();

    boolean isStatic();

    boolean isFinal();

    Class getOriginClass();

    Constructor[] getConstructors();

    Annotation[] getAnnotations();

    Constructor getDefaultConstructor() throws NoSuchMethodException;
}
