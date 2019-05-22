package org.hanframework.beans.postprocessor;


import org.hanframework.beans.annotation.PostConstruct;
import org.hanframework.beans.annotation.PreDestroy;
import org.hanframework.beans.postprocessor.annotation.BeanProcessor;

/**
 * @author liuxin
 * @version Id: CommonAnnotationBeanPostProcessor.java, v 0.1 2018/10/18 10:50 PM
 */
@BeanProcessor
public class CommonAnnotationBeanPostProcessor extends InitDestroyAnnotationBeanPostProcessor {

    public CommonAnnotationBeanPostProcessor(){
        setInitAnnotationType(PostConstruct.class);
        setDestroyAnnotationType(PreDestroy.class);
    }
}
