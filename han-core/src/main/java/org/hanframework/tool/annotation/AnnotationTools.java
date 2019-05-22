package org.hanframework.tool.annotation;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.hanframework.tool.annotation.type.StandardAnnotationMetadata;
import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.reflection.ClassTools;
import org.hanframework.tool.string.ObjectTools;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Package: org.smileframework.tool.annotation
 * @Description: 注解工具
 * @author: liuxin
 * @date: 2018/1/3 上午10:55
 */
@NoArgsConstructor
public class AnnotationTools {
    private static final Map<Class<? extends Annotation>, List<AnnotationAttributes>> attributeAliasesCache = new ConcurrentHashMap<>(256);

    private static final Map<AnnotationCacheKey, Annotation> findAnnotationCache =
            new ConcurrentHashMap<>(256);

    public static List<Method> findMethods(Class cls, Class annotationType) {
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (isContainsAnnotation(method.getDeclaredAnnotations(), annotationType)) {
                methods.add(method);
            }
        }
        return methods;
    }


    public static AnnotationMetadata getAnnotationMetadata(Class annotationClass) {
        return new StandardAnnotationMetadata(annotationClass);
    }

    public static List<Annotation> getAnnotationFromMethod(Method method) {
        return Arrays.asList(method.getDeclaredAnnotations());
    }

    public static List<Annotation> getAnnotationFromClass(Class cls) {
        return Arrays.asList(cls.getDeclaredAnnotations());
    }

    public static boolean isContainsAnnotation(Class cls, Class annotationType) {
        return isContainsAnnotation(getAnnotationFromClass(cls), annotationType);
    }

    public static boolean isContainsAnnotation(Annotation[] annotations, Class annotationType) {
        return isContainsAnnotation(Arrays.asList(annotations), annotationType);
    }

    public static boolean isContainsAnnotation(List<Annotation> annotations, Class annotationType) {
        Assert.isTrue(annotationType.isAnnotation(), "the annotationClass must be annotation");

        //先判断本身是否包含
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAssignableFrom(annotationType) || isCyclicContainsAnnotation(annotation, annotationType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param obj         将要判断的类
     * @param annotations 注解集合
     * @return 是否将要判断的类上，是否包含注解集合任意注解
     */
    public static boolean isContainsAnnotation(Object obj, Class... annotations) {
        return isContainsAnnotation(obj, Arrays.asList(annotations));
    }

    public static boolean isContainsAnnotationFromField(Field field, List<Class<? extends Annotation>> annotations) {
        if (null == field) {
            return false;
        }
        for (Class<? extends Annotation> annotation : annotations) {
            if (null != field.getAnnotation(annotation)) {
                return true;
            }

        }
        return false;
    }

    public static boolean isContainsAnnotationFromField(Field field, Class<? extends Annotation>... annotations) {
        return isContainsAnnotationFromField(field, Arrays.asList(annotations));
    }

    public static boolean isContainsAnnotationFromField(Field field, Set<Class<? extends Annotation>> annotations) {
        if (null == field) {
            return false;
        }
        for (Class<? extends Annotation> annotation : annotations) {
            if (null != field.getDeclaredAnnotation(annotation)) {
                return true;
            }

        }
        return false;
    }

    public static boolean isContainsAnnotationFromMethod(Method method, Set<Class<? extends Annotation>> annotations) {
        if (null == method) {
            return false;
        }
        for (Class<? extends Annotation> annotation : annotations) {
            if (null != method.getDeclaredAnnotation(annotation)) {
                return true;
            }

        }
        return false;
    }

    public static boolean isContainsAnnotationFromMethod(Method method, Class<? extends Annotation>... annotations) {
        if (null == method) {
            return false;
        }
        for (Class<? extends Annotation> annotation : annotations) {
            if (null != method.getDeclaredAnnotation(annotation)) {
                return true;
            }

        }
        return false;
    }

    /**
     * @param obj         将要判断的类
     * @param annotations 注解集合
     * @return 是否将要判断的类上，是否包含注解集合任意注解
     */
    public static boolean isContainsAnnotation(Object obj, Collection<Class<? extends Annotation>> annotations) {

        for (Class<? extends Annotation> annotation : annotations) {
            if (obj instanceof Class) {
                if (null != ((Class) obj).getAnnotation(annotation)) {
                    return true;
                }
            }
            if (obj instanceof Field) {
                if (null != ((Field) obj).getAnnotation(annotation)) {
                    return true;
                }
            }
            if (obj instanceof Method) {
                if (null != ((Method) obj).getAnnotation(annotation)) {
                    return true;
                }
            }
            if (obj instanceof Constructor) {
                if (null != ((Constructor) obj).getAnnotation(annotation)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取注解对象参数值
     *
     * @param annotation
     * @return
     */
    public static AnnotationMap<String, Object> getAnnotationAttributeAsMap(Annotation annotation) {
        List<AnnotationAttributes> annotationAttributes = getAnnotationAttributes(annotation.annotationType());
        Map<String, Object> annotationValue = new ConcurrentHashMap<>();
        for (AnnotationAttributes annotationAttr : annotationAttributes) {
            String annotationKey = annotationAttr.getAnnotationName();
            Object o = null;
            try {
                Method declaredMethod = annotation.annotationType().getDeclaredMethod(annotationKey);
                o = declaredMethod.invoke(annotation, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            annotationValue.put(annotationKey, o);
        }
        return new AnnotationMap<>(annotationValue);
    }


    /**
     * 返回当前注解的所有字段
     *
     * @param annotationType
     * @return
     */
    public static List<AnnotationAttributes> getAnnotationAttributes(Class<? extends Annotation> annotationType) {
        List<AnnotationAttributes> annotationAttributes = attributeAliasesCache.get(annotationType);
        if (ObjectTools.isNotEmpty(annotationAttributes)) {
            return annotationAttributes;
        }
        List<Method> methods = getAttributeMethods(annotationType);
        List<AnnotationAttributes> annotationAttributesList = new ArrayList<>();
        methods.stream().forEach(method -> {
            annotationAttributesList.add(new AnnotationAttributes(method.getName(), method.getReturnType()));
        });
        attributeAliasesCache.put(annotationType, annotationAttributesList);
        return annotationAttributesList;
    }

    /**
     * 获取注解中所有的字段名
     * 因为注解就是接口，里面的都是方法
     *
     * @param annotationType
     * @return
     */
    static List<Method> getAttributeMethods(Class<? extends Annotation> annotationType) {
        Method[] declaredMethods = annotationType.getDeclaredMethods();
        return ClassTools.castByArray(declaredMethods, Method.class);
    }

    public static <A extends Annotation> A findAnnotation(Class beanCls, Class<A> annotationType) {
        return findAnnotation(beanCls, annotationType, true);
    }

    /**
     * 查询字节码上面的注解
     *
     * @param beanCls
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotation(Class beanCls, Class<A> annotationType, boolean synthesize) {
        if (isContainsAnnotation(beanCls, annotationType)) {
            // 缓存 class+annotationType
            AnnotationCacheKey cacheKey = new AnnotationCacheKey(beanCls, annotationType);
            A result = (A) findAnnotationCache.get(cacheKey);
            if (result == null) {
                result = findAnnotation(beanCls, annotationType, new HashSet<Annotation>());
                //创建代理,兼容AliasFor
                synthesize = !isInJavaLangAnnotationPackage(result);
                if (result != null && synthesize) {
                    result = synthesizeAnnotation(result, beanCls);
                    findAnnotationCache.put(cacheKey, result);
                }
            }
            return result;
        }
        return (A) beanCls.getDeclaredAnnotation(annotationType);
    }

    static <A extends Annotation> A synthesizeAnnotation(A annotation, Object annotatedElement) {
        if (annotation == null) {
            return null;
        }
        Class<? extends Annotation> annotationType = annotation.annotationType();
        InvocationHandler handler = new SynthesizedAnnotationInvocationHandler(annotation);
        Class<?>[] exposedInterfaces = new Class<?>[]{annotationType, SynthesizedAnnotation.class};
        return (A) Proxy.newProxyInstance(annotation.getClass().getClassLoader(), exposedInterfaces, handler);
    }

    private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType, Set<Annotation> visited) {
        try {
            Annotation[] anns = clazz.getDeclaredAnnotations();
            for (Annotation ann : anns) {
                if (ann.annotationType() == annotationType) {
                    return (A) ann;
                }
            }
            for (Annotation ann : anns) {
                if (!isInJavaLangAnnotationPackage(ann) && visited.add(ann)) {
                    A annotation = findAnnotation(ann.annotationType(), annotationType, visited);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
        } catch (Throwable ex) {
            return null;
        }

        for (Class<?> ifc : clazz.getInterfaces()) {
            A annotation = findAnnotation(ifc, annotationType, visited);
            if (annotation != null) {
                return annotation;
            }
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null || Object.class == superclass) {
            return null;
        }
        return findAnnotation(superclass, annotationType, visited);
    }

    public static boolean isInJavaLangAnnotationPackage(Annotation annotation) {
        return (annotation != null && isInJavaLangAnnotationPackage(annotation.annotationType()));
    }

    static boolean isInJavaLangAnnotationPackage(Class<? extends Annotation> annotationType) {
        return (annotationType != null && isInJavaLangAnnotationPackage(annotationType.getName()));
    }

    public static boolean isInJavaLangAnnotationPackage(String annotationType) {
        return (annotationType != null && annotationType.startsWith("java.lang.annotation"));
    }

    /**
     * 当前注解集合是否包含指定的注解
     *
     * @param annotations
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A> A findAnnotation(List<Annotation> annotations, Class<A> annotationType) {
        for (Annotation annotation : annotations) {
            if (annotationType.isAssignableFrom(annotation.annotationType())) {
                return (A) annotation;
            }
        }
        return null;
    }

    public static <A> A findAnnotation(Annotation[] annotations, Class<A> annotationType) {
        return findAnnotation(Arrays.asList(annotations), annotationType);
    }


    /**
     * 获取所有的注解
     *
     * @param beanCls
     * @return
     */
    public static List<? extends Annotation> getClassStatementAnnotations(Class beanCls) {
        Annotation[] declaredAnnotations = beanCls.getDeclaredAnnotations();
        return Arrays.asList(declaredAnnotations);
    }

    /**
     * 依次向上查找
     * 凡是继承了@SmileComponent 都是组件
     *
     * @return true: 包含 false: 不包含
     */
    public static boolean isCyclicContainsAnnotation(List<? extends Annotation> annotations, Class annotationType) {
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.isAssignableFrom(annotationType)) {
                return true;
            } else {
                //获取每个注解上面是否包含
                Annotation[] parentAnnotations = annotation.annotationType().getDeclaredAnnotations();
                if (AnnotationTools.isContainsAnnotation(parentAnnotations, annotationType)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 依次向上查找
     * 凡是继承了@SmileComponent 都是组件
     *
     * @return true: 包含 false: 不包含
     */
    public static boolean isCyclicContainsAnnotation(Annotation annotation, Class annotationType) {
        Class<? extends Annotation> aClass = annotation.annotationType();
        if (aClass.isAssignableFrom(annotationType)) {
            return true;
        } else {
            //获取每个注解上面是否包含
            Annotation[] parentAnnotations = annotation.annotationType().getDeclaredAnnotations();
            List<Annotation> collect = Arrays.stream(parentAnnotations).filter(x -> !ClassTools.getPackageName(x.annotationType()).equalsIgnoreCase("java.lang.annotation")).collect(Collectors.toList());
            if (AnnotationTools.isContainsAnnotation(collect, annotationType)) {
                return true;
            }
        }
        return false;
    }


    public static RetentionPolicy getRetentionPolicy(Class<? extends Annotation> annotationType) {
        Retention retention = (Retention) annotationType.getAnnotation(Retention.class);
        return null == retention ? RetentionPolicy.CLASS : retention.value();
    }

    public static ElementType[] getTargetType(Class<? extends Annotation> annotationType) {
        Target target = (Target) annotationType.getAnnotation(Target.class);
        return null == target ? new ElementType[]{ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE, ElementType.PACKAGE} : target.value();
    }

    public static boolean isDocumented(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Documented.class);
    }

    public static boolean isInherited(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Inherited.class);
    }


    @RequiredArgsConstructor
    final static class AnnotationCacheKey implements Comparable<AnnotationCacheKey> {
        final Class element;
        final Class<? extends Annotation> annotationType;

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationCacheKey)) {
                return false;
            }
            AnnotationCacheKey otherKey = (AnnotationCacheKey) other;
            return (this.element.equals(otherKey.element) && this.annotationType.equals(otherKey.annotationType));
        }

        @Override
        public int hashCode() {
            return (this.element.hashCode() * 29 + this.annotationType.hashCode());
        }

        @Override
        public String toString() {
            return "@" + this.annotationType + " on " + this.element;
        }

        @Override
        public int compareTo(AnnotationCacheKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0) {
                result = this.annotationType.getName().compareTo(other.annotationType.getName());
            }
            return result;
        }
    }


}
