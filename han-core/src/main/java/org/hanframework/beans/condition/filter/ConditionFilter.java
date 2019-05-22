package org.hanframework.beans.condition.filter;

import org.hanframework.beans.condition.BeanCreateConditionException;
import org.hanframework.beans.condition.ConditionCheckMetaData;

/**
 * @author liuxin
 * @version Id: ConditionFilter.java, v 0.1 2018/10/11 6:08 PM
 */
public interface ConditionFilter {
    /**
     * 条件判断
     *
     * @param ccm 条件判断所需要的信息
     * @param strict 严格模式，true:直接报错,false,不报错
     * @return
     * @throws org.hanframework.beans.condition.BeanCreateConditionException
     */
    boolean filter(ConditionCheckMetaData ccm, boolean strict) throws BeanCreateConditionException;
}
