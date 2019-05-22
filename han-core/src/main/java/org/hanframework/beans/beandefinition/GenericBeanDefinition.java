package org.hanframework.beans.beandefinition;

/**
 * @author liuxin
 * @version Id: AbstractBeanDefinition.java, v 0.1 2018/10/18 4:15 PM
 */
public class GenericBeanDefinition extends AbstractBeanDefinition {

    /**
     * 是否是根据方法实例
     *
     * @return true:根据配置类生成
     */
    public boolean isBeanMethodInstantiation() {
        return super.configurationBeanMethod != null;
    }

    /**
     * 是否有空构造
     *
     * @return true: 空构造
     */
    public boolean isEmptyConstructorFlag() {
        return getConstructorInfo() == null;
    }
}
