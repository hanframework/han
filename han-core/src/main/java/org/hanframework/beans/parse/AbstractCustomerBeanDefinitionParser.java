package org.hanframework.beans.parse;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beandefinition.ConfigurableBeanDefinition;
import org.hanframework.beans.parse.exception.BeanDefinitionParserException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 无状态类,保证线程安全
 * 当继承该类,则会通过该类的方式,生成BeanDefinition
 *
 * @author liuxin
 * @version Id: CustomerBeanDefinitionParse.java, v 0.1 2018/10/11 5:25 PM
 */
@Slf4j
public abstract class AbstractCustomerBeanDefinitionParser {


    protected List<BeanDefinition> customerBeanDefinitionParse(Class beanCls) {
        List<ConfigurableBeanDefinition> configurableBeanDefinitions = new ArrayList<>();
        if (allowParse(beanCls)) {
            //在这里判断是否需要从Method上去读Bean的声明注解
            doCustomerBeanDefinitionParser(beanCls, configurableBeanDefinitions);
        }
        return adapterBeanDefinition(configurableBeanDefinitions);
    }

    /**
     * 转换器将可配置的ConfigurableBeanDefinition转成IOC可以使用的BeanDefinition
     *
     * @param configurableBeanDefinitions 可配置的BeanDefinition
     * @return IOC容器可接受的BeanDefinition
     */
    private List<BeanDefinition> adapterBeanDefinition(List<ConfigurableBeanDefinition> configurableBeanDefinitions) {
        return configurableBeanDefinitions.stream().filter(x -> x != null).map(x -> (BeanDefinition) x).collect(Collectors.toList());
    }

    /**
     * 必须要重写
     * <p>
     * 如果返回true:则代表这个类由自己的解析器。
     *
     * @param cls 当前处理的类
     * @return boolean 是否运行解析
     */
    public abstract boolean allowParse(Class cls);


    /**
     * 处理用户自定义的标记注解
     * 默认是将SmileComponent 和SmileService等注解做成BeanDefinition
     * 但是也提供了接口，允许外部根据自己的注解生成BeanDefinition。
     *
     * @param beanCls                     当前类字节码
     * @param configurableBeanDefinitions bean的可配置接口
     * @throws BeanDefinitionParserException 解析异常
     */
    public abstract void doCustomerBeanDefinitionParser(Class beanCls, final List<ConfigurableBeanDefinition> configurableBeanDefinitions) throws BeanDefinitionParserException;
}
