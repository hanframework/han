package org.hanframework.beans.condition;

import org.hanframework.tool.annotation.AnnotationTools;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 条件检查数据
 *
 * @author liuxin
 * @version Id: ConditionCheckMetaData.java, v 0.1 2019-03-18 11:17
 */
public class ConditionCheckMetaData {
  private List<Annotation> annotations;

  private Class originClass;

  private Method originMethod;

  public ConditionCheckMetaData(Class originClass) {
    this.annotations =  AnnotationTools.getAnnotationFromClass(originClass);
    this.originClass = originClass;
  }

  public ConditionCheckMetaData(Method originMethod) {
    this.annotations =  AnnotationTools.getAnnotationFromMethod(originMethod);
    this.originMethod = originMethod;
  }

  public Class getOriginClass() {
    return originClass;
  }

  public List<Annotation> getConditionAnnotations() {
    return annotations;
  }

  protected static ConditionCheckMetaData builder(Method originMethod){
    return new ConditionCheckMetaData(originMethod);
  }

  protected static ConditionCheckMetaData builder(Class originClass){
    return new ConditionCheckMetaData(originClass);
  }
}
