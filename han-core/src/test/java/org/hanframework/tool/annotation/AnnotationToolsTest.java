package org.hanframework.tool.annotation;

import org.junit.Test;


/**
 * @author liuxin
 * @version Id: AnnotationToolsTest.java, v 0.1 2019-04-18 14:17
 */
@A1(a2 = "test")
public class AnnotationToolsTest {

  /**
   * 别名共享
   */
  @Test
  public void syntaxTest() {

    A1 a1 = AnnotationTools.findAnnotation(AnnotationToolsTest.class, A1.class);
    System.out.println(a1.a1());
    System.out.println(a1.a2());


//    System.out.println(annotation.value());
//
//    Retention annotation1 = AnnotationTools.findAnnotation(AnnotationToolsTest.class, Retention.class);
//    System.out.println(annotation1);
//
//
//    boolean containsAnnotation = AnnotationTools.isContainsAnnotation(AnnotationToolsTest.class, ComponentScan.class);
//    System.out.println(containsAnnotation);
//
//    boolean containsAnnotation1 = AnnotationTools.isContainsAnnotation(AnnotationToolsTest.class, Retention.class);
//    System.out.println(containsAnnotation1);


  }
}
