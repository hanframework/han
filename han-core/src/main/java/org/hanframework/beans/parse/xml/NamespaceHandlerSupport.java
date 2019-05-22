package org.hanframework.beans.parse.xml;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.tool.asserts.Assert;
import org.w3c.dom.Element;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liuxin
 * @version Id: NamespaceHandlerSupport.java, v 0.1 2019-03-14 16:32
 */
public abstract class NamespaceHandlerSupport implements NamespaceHandler {
    /**
     * xml 中每个元素执行一个解析器
     */
    private final Map<String, ElementParser> elementParser =
            new HashMap<String, ElementParser>();

    @Override
    public abstract void init();

    @Override
    public List<String> elementNames() {
        return elementParser.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public BeanDefinition parse(Element element) {
        return findParserForElement(element).parse(element);
    }

    private ElementParser findParserForElement(Element element) {
        Assert.notEmpty(elementParser.keySet(), "请在实现NamespaceHandlerSupport.init(),指定元素解析器");
        return elementParser.get(element.getNodeName());
    }
}
