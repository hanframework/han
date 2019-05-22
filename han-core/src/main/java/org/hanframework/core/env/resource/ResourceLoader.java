package org.hanframework.core.env.resource;

/**
 * @author liuxin
 * @version Id: ResourceLoader.java, v 0.1 2018-12-11 16:08
 */
public interface ResourceLoader {

  String CLASSPATH_URL_PREFIX = "classpath:";

  Resource getResource(String location);

  ClassLoader getClassLoader();
}
