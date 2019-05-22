package org.hanframework.core.env.resolver;

/**
 * 提供从多配置文件中指定使用其中某一个配置文件的能力
 *
 * @author liuxin
 * @version Id: MultiPropertyResolver.java, v 0.1 2019-03-21 13:47
 */
public interface MultiPropertyResolver extends PropertyResolver {
  /**
   * 表达式: ${...}
   * 从配置文件中读取
   *
   * @param text
   * @param profile 指定的环境
   * @return
   */
  String resolvePlaceholders(String text, String profile);

  /**
   * 从配置文件中读取,当读取不到就报错
   *
   * @param text
   * @param profile 指定的环境
   * @return
   * @throws IllegalArgumentException
   */
  String resolveRequiredPlaceholders(String text, String profile) throws IllegalArgumentException;
}
