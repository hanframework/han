package org.hanframework.beans.parse;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beanfactory.BeanNameGenerator;
import org.hanframework.beans.beanfactory.impl.DefaultBeanNameGenerator;
import org.hanframework.beans.beanfactory.impl.DefaultListableBeanFactory;
import org.hanframework.beans.parse.xml.DefaultNamespaceHandlerResolver;
import org.hanframework.beans.parse.xml.NamespaceHandler;
import org.hanframework.beans.parse.xml.NamespaceHandlerResolver;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据xml的namespaceUri,找到要解析的标签的,处理器
 *
 * @author liuxin
 * @version Id: AbstractDocumentBeanDefinitionParser.java, v 0.1 2019-03-14 16:25
 */
public class DocumentBeanDefinitionParser implements BeanDefinitionParser {

    private NamespaceHandlerResolver namespaceHandlerResolver = new DefaultNamespaceHandlerResolver();

    private BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

    private List<Document> documents = new ArrayList();

    public DocumentBeanDefinitionParser(Document document) {
        documents.add(document);
    }

    public DocumentBeanDefinitionParser(List<Document> documents) {
        documents.addAll(documents);
    }

    @Override
    public void loadBeanDefinitionParse(DefaultListableBeanFactory beanFactory) {
        for (Document document : documents) {
            //获取命名空间的解析器
            NamespaceHandler namespaceHandler = namespaceHandlerResolver.resolve("");
            namespaceHandler.init();
            List<String> elementNames = namespaceHandler.elementNames();
            for (String elementName : elementNames) {
                BeanDefinition beanDefinition = namespaceHandler.parse(document.getElementById(elementName));
                beanFactory.registerBeanDefinition(beanNameGenerator.generateBeanName(beanDefinition), beanDefinition);
            }
        }
    }


}
