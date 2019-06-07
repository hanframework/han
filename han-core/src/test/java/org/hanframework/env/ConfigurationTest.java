package org.hanframework.env;

import org.hanframework.context.AnnotationConfigApplicationContext;
import org.junit.Test;
import java.util.Map;


/**
 * @author liuxin
 * @version Id: ConfigurationTest.java, v 0.1 2019-06-04 14:45
 */
public class ConfigurationTest {

  @Test
  public void contest() {
    AnnotationConfigApplicationContext aca = new AnnotationConfigApplicationContext();
    aca.run();
    Configuration configuration = aca.getConfiguration();
    Environment configurableEnvironment = aca.getEnvironment();
    Map<String, Object> systemEnvironment = configurableEnvironment.getSystemEnvironment();
    Map<String, Object> systemProperties = configurableEnvironment.getJvmProperties();
    print(systemEnvironment);
    print(systemProperties);
  }

  public void print(Map<String, Object> map) {
    System.err.println("---------------");
    for (Map.Entry<String, Object> en : map.entrySet()) {
      System.out.println(en.getKey() + " = " + en.getValue());
    }
  }
}
