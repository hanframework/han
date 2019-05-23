package org.hanframework.env.resolver.source;

import java.util.Map;

/**
 * @author liuxin
 * @version Id: CommonProperty.java, v 0.1 2019-05-23 17:13
 */
public class CommonProperty extends PropertySource {

  Map<String, Object> properties;

  public CommonProperty(String name, Object source) {
    super(name, source);
    properties = ( Map<String, Object>)source;
  }

  @Override
  public Object getProperty(String name) {
    return properties.get(name);
  }
}
