package org.hanframework.beans.parse.xml;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.w3c.dom.Element;
import java.util.List;

/**
 * @author liuxin
 * @version Id: NamespaceHandler.java, v 0.1 2019-03-14 16:30
 */
public interface NamespaceHandler {

    void init();

    List<String> elementNames();

    BeanDefinition parse(Element element);
}
