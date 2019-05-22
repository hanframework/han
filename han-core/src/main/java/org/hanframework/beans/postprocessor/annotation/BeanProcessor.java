package org.hanframework.beans.postprocessor.annotation;

import org.hanframework.beans.annotation.HanComponent;
import java.lang.annotation.*;

/**
 * 被标记的前置后置处理器
 * @author liuxin
 * @version Id: PostProcessor.java, v 0.1 2019-01-28 21:53
 */
@HanComponent
@Target({ ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanProcessor {
}
