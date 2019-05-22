package org.hanframework.web.handler;

import org.hanframework.tool.annotation.AnnotationAttributes;
import org.hanframework.tool.annotation.AnnotationTools;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author liuxin
 * @version Id: HttpRequestAnnotationMetaData.java, v 0.1 2019-04-11 11:29
 */
public class HttpRequestAnnotationMetaData {
  List<AnnotationAttributes> annotationAttributes;

  public HttpRequestAnnotationMetaData(Annotation annotation) {
    annotationAttributes = AnnotationTools.getAnnotationAttributes(annotation.annotationType());
  }
}
