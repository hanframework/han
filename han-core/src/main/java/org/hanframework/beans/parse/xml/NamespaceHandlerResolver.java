package org.hanframework.beans.parse.xml;

/**
 * @author liuxin
 * @version Id: NamespaceHandlerResolver.java, v 0.1 2019-03-14 16:41
 */
public interface NamespaceHandlerResolver {
    /**
     * 读取命名空间
     * @param namespaceUri
     * @return
     */
    NamespaceHandler resolve(String namespaceUri);
}
