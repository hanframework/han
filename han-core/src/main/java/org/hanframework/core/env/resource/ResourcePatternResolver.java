package org.hanframework.core.env.resource;

import java.io.IOException;

/**
 * 根据正则匹配
 * @author liuxin
 * @version Id: ResourcePatternResolver.java, v 0.1 2018-12-11 16:17
 */
public interface ResourcePatternResolver extends ResourceLoader {

  String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

  Resource[] getResources(String locationPattern) throws IOException;
}
