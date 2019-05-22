package org.hanframework.beans.parse.xml;

/**
 * 首先先将xml转换成docment文档
 * @author liuxin
 * @version Id: DefaultNamespaceHandlerResolver.java, v 0.1 2019-03-14 16:42
 */
public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver {

    /**
     * 根据命名空间获取指定的命名解析器
     * @param namespaceUri
     * @return
     */
    @Override
    public NamespaceHandler resolve(String namespaceUri) {
        //从namespaceUri中获取指定的的命名空间处理器
        return null;
    }
}
