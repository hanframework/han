package org.hanframework.tool.annotation.type;

/**
 * 类信息
 *
 * @author liuxin
 * @version Id: ClassMetadata.java, v 0.1 2019-01-10 15:00
 */
public interface ClassMetadata {

    String getFullClassName();


    boolean isInterface();


    boolean isAnnotation();


    boolean isAbstract();


    boolean isFinal();


    boolean isStatic();

    boolean hasSuperClass();


    String getSuperClassName();


    String[] getInterfaceNames();


}
