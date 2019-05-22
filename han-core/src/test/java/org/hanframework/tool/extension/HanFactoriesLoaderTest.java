package org.hanframework.tool.extension;

import org.hanframework.beans.parse.annotation.BeanDefinitionParsers;
import org.hanframework.tool.string.StreamTools;
import org.hanframework.tool.yaml.YamlTools;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author liuxin
 * @version Id: HanFactoriesLoaderTest.java, v 0.1 2019-05-13 23:22
 */
public class HanFactoriesLoaderTest {

  /**
   * class 为key
   */
  @Test
  public void test() {
    Set<Class> autoConfigure = HanFactoriesLoader.getAutoConfigure(BeanDefinitionParsers.class);
    System.out.println(autoConfigure);
  }

  @Test
  public void yaml(){
    InputStream inputStream = ClassLoader.getSystemResourceAsStream("autoconfigure.yml");
    String utf8 = StreamTools.convertStreamToString(inputStream, "utf8");
    Properties properties = YamlTools.readYaml(utf8);
    System.out.println(properties);
  }
}
