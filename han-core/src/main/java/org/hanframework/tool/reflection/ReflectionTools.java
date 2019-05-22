package org.hanframework.tool.reflection;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.hanframework.tool.string.StringTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Package: org.smileframework.tool.clazz
 * @Description: 反射工具
 * @author: liuxin
 * @date: 2017/12/12 下午7:19
 */
public class ReflectionTools {


    public static List<Method> getMethod(Object obj) {
        return Arrays.asList(obj.getClass().getDeclaredMethods());
    }


    public static boolean isAnnotationTypeMethod(Method method) {
        return (method != null && method.getName().equals("annotationType") && method.getParameterTypes().length == 0);
    }

    public static boolean isAttributeMethod(Method method) {
        return (method != null && method.getParameterTypes().length == 0 && method.getReturnType() != void.class);
    }

    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, new Object[0]);
    }


    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }


    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }


    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }


    public static boolean isEqualsMethod(Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        return (paramTypes.length == 1 && paramTypes[0] == Object.class);
    }


    public static boolean isHashCodeMethod(Method method) {
        return (method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0);
    }


    public static boolean isToStringMethod(Method method) {
        return (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0);
    }


    public static boolean isObjectMethod(Method method) {
        if (method == null) {
            return false;
        }
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }

    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }

    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }

    }


    public static List<Method> getMethod(Object obj, String methodName) {
        Object[] objects = getMethod(obj).stream().filter(m -> {
            if (StringTools.isNotEmpty(methodName)) {
                return m.getName().contains(methodName);
            } else {
                return true;
            }
        }).toArray();
        return ClassTools.castByArray(objects, Method.class);
    }



    public static String[] getParameterNames(Method method) {
        //declar不局限与修饰符
        Class clazz = method.getDeclaringClass();
        if (clazz.getSimpleName().equalsIgnoreCase("Object")) {
            throw new IllegalArgumentException(clazz.getSimpleName() + ":类不能被解析");
        }
        String methodName = method.getName();
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(clazz));
        CtMethod cm = null;
        try {
            CtClass cc = pool.get(clazz.getName());
            cm = cc.getDeclaredMethod(methodName);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr =
                (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            return null;
        }
        String[] paramNames = new String[0];
        try {
            paramNames = new String[cm.getParameterTypes().length];
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }


    public static List<ParamDefinition> getParameterDefinitions(Method method, Class<? extends Annotation> annotation) {
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = getParameterNames(method);
        List<ParamDefinition> paramDefinitions = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Annotation annotationDefinition = parameter.getAnnotation(annotation);
            if (annotationDefinition == null) {
                continue;
            }
            Class<?> type = parameter.getType();
            String argIndex = getArgIndex(parameter.getName());
            String parameterName = parameterNames[Integer.parseInt(argIndex)];
            Annotation[] annotations = new Annotation[1];
            annotations[0] = annotationDefinition;
            paramDefinitions.add(new ParamDefinition(argIndex, parameterName, annotations, type));
        }
        return paramDefinitions;
    }


    public static List<ParamDefinition> getParameterDefinitions(Method method) {
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = getParameterNames(method);
        List<ParamDefinition> paramDefinitions = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Annotation[] declaredAnnotations = parameter.getDeclaredAnnotations();
            Class<?> type = parameter.getType();
            String argIndex = getArgIndex(parameter.getName());
            String parameterName = parameterNames[Integer.parseInt(argIndex)];
            paramDefinitions.add(new ParamDefinition(argIndex, parameterName, declaredAnnotations, type));
        }
        return paramDefinitions;
    }


    private static String getArgIndex(String arg) {
        String argIndex = arg.replaceAll("arg", "");
        return argIndex;
    }


    public static Annotation getAnnotation(Annotation[] annotations, Class<? extends Annotation> annotation) {
        for (int i = 0; i < annotations.length; i++) {
            Annotation srcAnnotation = annotations[i];
            if (srcAnnotation.annotationType().isAssignableFrom(annotation)) {
                return srcAnnotation;
            }
        }
        return null;
    }


    public static class ParamDefinition {

        private String argIndex;

        private String paramName;

        private Annotation[] declaredAnnotations;

        private Class paramType;

        public ParamDefinition(String argIndex, String paramName, Annotation[] declaredAnnotations, Class paramType) {
            this.argIndex = argIndex;
            this.paramName = paramName;
            this.declaredAnnotations = declaredAnnotations;
            this.paramType = paramType;
        }

        public String getArgIndex() {
            return argIndex;
        }

        public void setArgIndex(String argIndex) {
            this.argIndex = argIndex;
        }

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public Annotation[] getDeclaredAnnotations() {
            return declaredAnnotations;
        }

        public void setDeclaredAnnotations(Annotation[] declaredAnnotations) {
            this.declaredAnnotations = declaredAnnotations;
        }

        public Class getParamType() {
            return paramType;
        }

        public void setParamType(Class paramType) {
            this.paramType = paramType;
        }

        @Override
        public String toString() {
            return "ParamDefinition{" +
                    "argIndex='" + argIndex + '\'' +
                    ", paramName='" + paramName + '\'' +
                    ", declaredAnnotations=" + Arrays.toString(declaredAnnotations) +
                    ", paramType=" + paramType +
                    '}';
        }
    }
}
