package org.hanframework.core.env.resource.pathmatcher;

/**
 * @author liuxin
 * @version Id: PathMatcher.java, v 0.1 2018-12-11 16:28
 */
public interface PathMatcher {

    boolean isPattern(String path);

    boolean match(String pattern, String path);

}
