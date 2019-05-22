package org.hanframework.cycle;


import org.hanframework.context.AnnotationConfigApplicationContext;
import org.hanframework.context.annotation.ComponentScan;
import org.junit.Test;

/**
 * 为什么不解决循环依赖问题?
 * 1. 循环依赖本来就是一种错误的编程导致的,与其在使用时候发现,不如在服务启动就提前发现。
 * 2. 循环依赖创建的对象,就跟错误的递归调用一样,极容易造成内存溢出,等无法自动修复的系统级异常。
 * <p>
 * 如何解决循环依赖的问题?
 * 1. 发现有循环依赖,就先实例化对象(前提1.Bean有无参构造;前提2.单例模式,不支持原型模式)
 * 2. 实例化对象(不进行属性注入),因为都是单例,所以到单例里面分别获取这两个循环依赖的对象,进行互相注入依赖对象
 * <p>
 * 伪代码实现
 * <pre>
 * class A{
 *     B b;
 * }
 * class B{
 *     A a;
 * }
 *
 * A a = new A();
 * B b = new B();
 * a.setB(a);
 * b.setA(b);
 *
 * </pre>
 *
 * @author liuxin
 * @version Id: SingletonBeanRegistry.java, v 0.1 2018/10/29 2:21 PM
 */
@ComponentScan
public class CycleTest {

    /**
     * 循环依赖在系统中不允许存在!
     * <p>
     * 为什么不解决循环依赖问题?
     * 1. 循环依赖本来就是一种错误的编程导致的,与其在使用时候发现,不如在服务启动就提前发现。
     * 2. 循环依赖创建的对象,就跟错误的递归调用一样,极容易造成内存溢出,等无法自动修复的系统级异常。
     */
    @Test
    public void appTest() {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(CycleTest.class);
        app.run();
        System.out.println(app.getBean("a"));
        System.out.println(app.getBean("b"));
    }
}
