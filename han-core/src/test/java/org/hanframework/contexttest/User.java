package org.hanframework.contexttest;

import org.hanframework.beans.annotation.HanComponent;
import org.hanframework.beans.annotation.PostConstruct;
import org.hanframework.beans.annotation.PreDestroy;

/**
 * @author liuxin
 * @version Id: User.java, v 0.1 2019-05-20 13:56
 */
@HanComponent
public class User {

    @PostConstruct
    public void init() {
        System.out.println("init...");
    }

    @PreDestroy
    public void destory(){
        System.out.println("destory...");
    }
}
