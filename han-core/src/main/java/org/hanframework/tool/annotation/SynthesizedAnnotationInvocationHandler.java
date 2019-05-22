package org.hanframework.tool.annotation;

import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.reflection.ReflectionTools;
import org.hanframework.tool.string.StringTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuxin
 * @version Id: SynthesizedAnnotationInvocationHandler.java, v 0.1 2019-04-18 15:30
 */
public class SynthesizedAnnotationInvocationHandler implements InvocationHandler {

  private Annotation annotation;

  private final Map<String, Object> valueCache = new ConcurrentHashMap<String, Object>(8);

  public SynthesizedAnnotationInvocationHandler(Annotation annotation) {
    this.annotation = annotation;
    getAttributeAliasMap(annotation.annotationType());
    initValueCache(annotation.annotationType());
  }

  private static final Map<Class<? extends Annotation>, Map<String, List<String>>> attributeAliasesCache =
    new ConcurrentHashMap<Class<? extends Annotation>, Map<String, List<String>>>(256);


  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (ReflectionTools.isEqualsMethod(method)) {
      return annotationEquals(args[0]);
    }
    if (ReflectionTools.isHashCodeMethod(method)) {
      return annotationHashCode();
    }
    if (ReflectionTools.isToStringMethod(method)) {
      return annotationToString();
    }
    if (ReflectionTools.isAnnotationTypeMethod(method)) {
      return annotationType();
    }
    if (!ReflectionTools.isAttributeMethod(method)) {
      throw new AnnotationConfigurationException(String.format(
        "Method [%s] is unsupported for synthesized annotation type [%s]", method, annotationType()));
    }
    return getAttributeValue(method);
  }


  /**
   * 初始化所有的值
   */
  private void initValueCache(Class<? extends Annotation> annotationType) {
    Method[] declaredMethods = annotationType.getDeclaredMethods();
    for (Method method : declaredMethods) {
      String name = method.getName();
      Object invoke;
      try {
        invoke = method.invoke(annotation);
        boolean array = invoke.getClass().isArray();
        if (array) {
          Object[] obj = (Object[]) invoke;
          array = obj.length <= 0;
        }
        if (null != invoke && !array) {
          if ((invoke instanceof String) && StringTools.isNullOrEmpty((String) invoke)) {
            continue;
          }
          valueCache.put(name, invoke);
        }
      } catch (Exception e) {
      }
    }
  }


  /**
   * 注解不为空,检查别名是否为空。如果注解有值,别名也有值,则报错
   *
   * @param result
   * @param attributeMethod
   * @return
   */
  public Object processor(Object result, Method attributeMethod) {
    String attributeName = attributeMethod.getName();
    boolean isAliasFlag = AnnotationTools.isContainsAnnotation(attributeMethod.getDeclaredAnnotations(), AliasFor.class);
    if (result != null && isAliasFlag) {
      //根据别名到map中那别名的信息
      List<String> attributeAliasNames = getAttributeAliasNames(attributeMethod);
      for (String alias : attributeAliasNames) {
        if (null != valueCache.get(alias)) {
          throw new RuntimeException("AliasFor.class 互为别名,不能同时存在");
        }
      }
    }
    if (result == null && isAliasFlag) {
      List<String> attributeAliasNames = getAttributeAliasNames(attributeMethod);
      for (String aliasName : attributeAliasNames) {
        result = valueCache.get(aliasName);
        if (null != result) {
          if (!result.getClass().isAssignableFrom(attributeMethod.getReturnType())) {
            String format = String.format("属性:%s,类型:%s与属性:%s,类型:%s,类型不匹配,不能互为别名,请检查:%s设置",
              attributeName, attributeMethod.getReturnType()
              , aliasName, result.getClass(), annotation.annotationType());
            throw new RuntimeException(format);
          } else {
            return result;
          }
        }
      }
    }
    return result;
  }

  /**
   * 真正的处理逻辑,处理别名
   *
   * @param attributeMethod 注解的方法
   * @return
   */
  private Object getAttributeValue(Method attributeMethod) {
    //方法名
    String attributeName = attributeMethod.getName();
    //基础类型不计算别名，直接报错。
    Object result = processor(valueCache.get(attributeName), attributeMethod);
    return result == null ? attributeMethod.getDefaultValue() : result;
  }

  /**
   * 获取当前注解，方法和对应的别名
   *
   * @param annotationType 注解
   * @return
   */
  static Map<String, List<String>> getAttributeAliasMap(Class<? extends Annotation> annotationType) {
    if (annotationType == null) {
      return Collections.emptyMap();
    }
    Map<String, List<String>> map = attributeAliasesCache.get(annotationType);
    if (map != null && !map.isEmpty()) {
      return map;
    }
    map = new LinkedHashMap<>();
    for (Method attribute : AnnotationTools.getAttributeMethods(annotationType)) {
      List<String> aliasNames = getAttributeAliasNames(attribute);
      if (!aliasNames.isEmpty()) {
        map.put(attribute.getName(), aliasNames);
      }
    }
    attributeAliasesCache.put(annotationType, map);
    return map;
  }


  /**
   * 拿到当前方法的所有的别名
   *
   * @param attribute 注解方法
   * @return 当前方法的所有别名列表
   */
  static List<String> getAttributeAliasNames(Method attribute) {
    Assert.notNull(attribute, "attribute must not be null");
    Annotation[] declaredAnnotations = attribute.getDeclaredAnnotations();
    List<String> aliasForList = new ArrayList<>();
    for (Annotation annotation : declaredAnnotations) {
      if (annotation.annotationType().isAssignableFrom(AliasFor.class)) {
        AliasFor aliasFor = (AliasFor) annotation;
        aliasForList.add(aliasFor.value());
      }
    }
    return aliasForList;
  }


  private Class<? extends Annotation> annotationType() {
    return this.annotation.annotationType();
  }

  private boolean annotationEquals(Object other) {
    if (this == other) {
      return true;
    }
    if (!annotationType().isInstance(other)) {
      return false;
    }

    for (Method attributeMethod : AnnotationTools.getAttributeMethods(annotationType())) {
      Object thisValue = getAttributeValue(attributeMethod);
      Object otherValue = ReflectionTools.invokeMethod(attributeMethod, other);
      if (null == otherValue) {

      }
    }
    return true;
  }


  private int annotationHashCode() {
    int result = 0;

    for (Method attributeMethod : AnnotationTools.getAttributeMethods(annotationType())) {
      Object value = getAttributeValue(attributeMethod);
      int hashCode;
      if (value.getClass().isArray()) {
        hashCode = hashCodeForArray(value);
      } else {
        hashCode = value.hashCode();
      }
      result += (127 * attributeMethod.getName().hashCode()) ^ hashCode;
    }

    return result;
  }


  private int hashCodeForArray(Object array) {
    if (array instanceof boolean[]) {
      return Arrays.hashCode((boolean[]) array);
    }
    if (array instanceof byte[]) {
      return Arrays.hashCode((byte[]) array);
    }
    if (array instanceof char[]) {
      return Arrays.hashCode((char[]) array);
    }
    if (array instanceof double[]) {
      return Arrays.hashCode((double[]) array);
    }
    if (array instanceof float[]) {
      return Arrays.hashCode((float[]) array);
    }
    if (array instanceof int[]) {
      return Arrays.hashCode((int[]) array);
    }
    if (array instanceof long[]) {
      return Arrays.hashCode((long[]) array);
    }
    if (array instanceof short[]) {
      return Arrays.hashCode((short[]) array);
    }

    // else
    return Arrays.hashCode((Object[]) array);
  }

  private String annotationToString() {
    StringBuilder sb = new StringBuilder("@").append(annotationType().getName()).append("(");

    Iterator<Method> iterator = AnnotationTools.getAttributeMethods(annotationType()).iterator();
    while (iterator.hasNext()) {
      Method attributeMethod = iterator.next();
      sb.append(attributeMethod.getName());
      sb.append('=');
      sb.append(attributeValueToString(getAttributeValue(attributeMethod)));
      sb.append(iterator.hasNext() ? ", " : "");
    }

    return sb.append(")").toString();
  }

  private String attributeValueToString(Object value) {
    if (value instanceof Object[]) {
      return "[" + StringTools.arrayToDelimitedString((Object[]) value, ", ") + "]";
    }
    return String.valueOf(value);
  }
}
