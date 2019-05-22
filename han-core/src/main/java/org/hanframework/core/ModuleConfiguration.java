package org.hanframework.core;


import org.hanframework.tool.asserts.Assert;

import java.util.Map;

/**
 * 所有第三方配置,需要交给核心控制需要从获取
 *
 * @author liuxin
 * @version Id: ModuleConfigurationKey.java, v 0.1 2019-05-13 21:55
 */
public abstract class ModuleConfiguration {

  /**
   * 模块名
   */
  private final String moduleName;

  /**
   * 模块版本
   */
  private final String version;

  /**
   * 原始数据
   */
  protected Map<Object, Object> source;

  public ModuleConfiguration(String moduleName, String version) {
    this.moduleName = moduleName;
    this.version = version;
  }

  public ModuleConfiguration(String moduleName, String version, Map source) {
    this.moduleName = moduleName;
    this.version = version;
    if (source == null) {
      throw new IllegalArgumentException("null source");
    }
    this.source = source;
  }

  private Object getAndCheck(Object attributeKey, boolean required) {
    Assert.notNull(attributeKey, "attributeKey not be must null");
    Object result = source.get(attributeKey);
    if (result == null && required) {
      throw new IllegalArgumentException("attributeValue not be must null");
    }
    return result;
  }


  public String getModuleName() {
    return moduleName;
  }

  public String getVersion() {
    return version;
  }

  public Object getSource() {
    return source;
  }

  public ModuleInfo getModuleInfo() {
    return new ModuleInfo(this.moduleName, this.version);
  }

  public String getString(Object attributeKey) {
    return (String) getAndCheck(attributeKey, false);
  }

  public String getRequiredString(Object attributeKey) {
    return (String) getAndCheck(attributeKey, true);
  }

  public Boolean getBoolean(Object attributeKey) {
    Object result = getAndCheck(attributeKey, false);
    return result instanceof Boolean ? (Boolean) result : null;
  }

  public Boolean getRequiredBoolean(Object attributeKey) {
    Object result = getAndCheck(attributeKey, true);
    return result instanceof Boolean ? (Boolean) result : null;
  }

  public Integer getInteger(Object attributeKey) {
    Object result = getAndCheck(attributeKey, false);
    return result instanceof Integer ? (Integer) result : null;
  }

  public Integer getRequiredInteger(Object attributeKey) {
    Object result = getAndCheck(attributeKey, true);
    return result instanceof Integer ? (Integer) result : null;
  }

  public Long getLong(Object attributeKey) {
    Object result = getAndCheck(attributeKey, false);
    return result instanceof Long ? (Long) result : null;
  }

  public Long getRequiredLong(Object attributeKey) {
    Object result = getAndCheck(attributeKey, true);
    return result instanceof Long ? (Long) result : null;
  }

  @Override
  public String toString() {
    return "ModuleConfiguration{" +
      "moduleName='" + moduleName + '\'' +
      ", version='" + version + '\'' +
      ", source=" + source +
      '}';
  }
}
