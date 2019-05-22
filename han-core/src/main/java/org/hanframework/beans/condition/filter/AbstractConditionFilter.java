package org.hanframework.beans.condition.filter;

import org.hanframework.beans.beanfactory.BeanFactory;

/**
 * @author liuxin
 * @version Id: AbstractConditionFilter.java, v 0.1 2019-01-14 15:15
 */
public abstract class AbstractConditionFilter implements ConditionFilter {

  private BeanFactory beanFactory;

  public AbstractConditionFilter(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  protected BeanFactory getBeanFactory() {
    return beanFactory;
  }
}
