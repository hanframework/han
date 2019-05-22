package org.hanframework.beans.beandefinition;

/**
 * 关于Bean生命周期的定义只读
 *
 * @author liuxin
 * @version Id: LifeCycleDefinition.java, v 0.1 2019-01-31 14:26
 */
public interface LifeCycleOnReadyDefinition extends MetaClassDefinition {


    /**
     * 返回初始化方法
     *
     * @return 返回初始化方法
     */
    String getInitMethodName();


    /**
     * 返回销毁方法
     *
     * @return
     */
    String getDestroyMethodName();



}
