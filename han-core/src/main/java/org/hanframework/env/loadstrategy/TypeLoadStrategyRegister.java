package org.hanframework.env.loadstrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuxin
 * @version Id: TypeLoadStrategyRegistory.java, v 0.1 2019-05-24 11:18
 */
public class TypeLoadStrategyRegister {
  private Map<String, TypeLoadStrategy> typeLoadStrategyMap = new ConcurrentHashMap<>(10);

  public TypeLoadStrategyRegister() {
    init();
  }

  public void init() {
    typeLoadStrategyMap.put("yaml", new YamlLoadStrategy());
    typeLoadStrategyMap.put("yml", new YamlLoadStrategy());
    typeLoadStrategyMap.put("properties", new PropertiesLoadStrategy());
  }

  public void registryTypeLoadStrategy(String fileSuffixName, TypeLoadStrategy typeLoadStrategy) {
    typeLoadStrategyMap.put(fileSuffixName, typeLoadStrategy);
  }

  public TypeLoadStrategy getLoadStrategy(String fileSuffixName) {
    return typeLoadStrategyMap.get(fileSuffixName);
  }
}
