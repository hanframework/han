package org.hanframework.beans.condition;

import org.hanframework.beans.beandefinition.GenericBeanDefinition;

/**
 * @author liuxin
 * @version Id: ConditionHandler.java, v 0.1 2019-03-18 17:37
 */
public interface ConditionHandler {
  /**
   * 判断cls满足所有限定条件
   * 如果任何一个不满足都返回false
   * 当返回false以为不满足条件,不能被实例化
   *
   * @param genericBeanDefinition Bean的描述信息
   * @return
   * @throws BeanCreateConditionException 条件异常
   */
  boolean isCondition(GenericBeanDefinition genericBeanDefinition) throws BeanCreateConditionException;
}
