package org.hanframework.tool.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author liuxin
 * @version Id: MethodTools.java, v 0.1 2018-12-04 15:44
 */
public class MethodTools {

    /**
     * An empty class array
     */
    private static final Class[] EMPTY_CLASS_PARAMETERS = new Class[0];
    /**
     * An empty object array
     */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static Method findMethodByName(Class cls, String methodName, Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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


    public static Object invokeStaticMethod(
            Class objectClass,
            String methodName,
            Object[] args,
            Class[] parameterTypes)
            throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException {

        if (parameterTypes == null) {
            parameterTypes = EMPTY_CLASS_PARAMETERS;
        }
        if (args == null) {
            args = EMPTY_OBJECT_ARRAY;
        }

        Method method = objectClass.getDeclaredMethod(
                methodName,
                parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: " +
                    methodName + "() on class: " + objectClass.getName());
        }
        return method.invoke(null, args);
    }


}
