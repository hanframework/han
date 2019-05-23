package org.hanframework.env.resource;

import org.hanframework.tool.reflection.ClassTools;

import java.net.URL;

/**
 * @author liuxin
 * @version Id: DefaultResourceLoader.java, v 0.1 2018-12-11 19:15
 */
public class DefaultResourceLoader implements ResourceLoader{
  private ClassLoader classLoader;

  public DefaultResourceLoader() {
    this.classLoader = ClassTools.getDefaultClassLoader();
  }

  @Override
  public Resource getResource(String location) {
    URL resource = this.classLoader.getResource(location);
    return new LocalFileResource(resource);
  }

  @Override
  public ClassLoader getClassLoader() {
    return this.classLoader;
  }
}
