package org.hanframework.beans.parse.xml;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.w3c.dom.Element;

/**
 * @author liuxin
 * @version Id: ElementParser.java, v 0.1 2019-03-14 16:34
 */
public interface ElementParser {
    /**
     * 根据xml节点来解析生成BeanDefinition
     * @param element
     * @return
     */
    BeanDefinition parse(Element element);
}
