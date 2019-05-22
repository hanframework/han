package org.hanframework.core.env.resource.pathmatcher;

import java.util.regex.Pattern;

/**
 * @author liuxin
 * @version Id: AntPathMatcher.java, v 0.1 2018-12-11 16:29
 */
public class AntPathMatcher implements PathMatcher {

  public static final String DEFAULT_PATH_SEPARATOR = "/";

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");

  private String pathSeparator;

  public AntPathMatcher() {
    this.pathSeparator = DEFAULT_PATH_SEPARATOR;
  }

  /**
   * 只要路径中,包含*或?就是需要匹配的路径
   *
   * @param path 文件路径
   * @return
   */
  @Override
  public boolean isPattern(String path) {
    return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
  }

  /**
   * @param pattern 正则目录
   * @param path    完整目录
   * @return
   */
  @Override
  public boolean match(String pattern, String path) {
    String[] subPatterns = pattern.split("/");
    String[] subPath = path.split("/");
    if (subPatterns.length != subPath.length) {
      return false;
    }
    for (int i = 0; i < subPath.length; i++) {
      String currentSubPath = subPath[i];
      String currentPattern = subPatterns[i];
      if (currentPattern.equalsIgnoreCase("*") && i < subPath.length) {
        continue;
      }
      if (currentPattern.indexOf("*") != -1) {
        //通配符
        int indexOf = currentPattern.indexOf("*");
        String prefixSubPattern = currentPattern.substring(0, indexOf);
        String suffixSubPattern = currentPattern.substring(indexOf + 1);
        String suffixSubPath = currentSubPath.substring(currentSubPath.length() - suffixSubPattern.length());
        if (currentSubPath.startsWith(prefixSubPattern) && suffixSubPath.equals(suffixSubPattern)) {
          continue;
        }
      }
      if (currentPattern.equals(currentSubPath)) {
        continue;
      }
      return false;
    }
    return true;
  }



}
