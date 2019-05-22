package org.hanframework.core;

import java.util.Objects;

/**
 * @author liuxin
 * @version Id: ModuleInfo.java, v 0.1 2019-05-13 22:15
 */
public class ModuleInfo {
  /**
   * 模块名
   */
  private String moduleName;

  /**
   * 模块版本
   */
  private String version;

  public ModuleInfo(String moduleName, String version) {
    this.moduleName = moduleName;
    this.version = version;
  }

  public String getModuleName() {
    return moduleName;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ModuleInfo that = (ModuleInfo) o;
    return moduleName.equals(that.moduleName) &&
      version.equals(that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(moduleName, version);
  }

  @Override
  public String toString() {
    return "ModuleInfo{" +
      "moduleName='" + moduleName + '\'' +
      ", version='" + version + '\'' +
      '}';
  }
}
