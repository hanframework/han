package org.hanframework.tool.annotation.type;

import org.hanframework.tool.asserts.Assert;
import java.lang.reflect.Modifier;

/**
 * @author liuxin
 * @version Id: StandardClassMetadata.java, v 0.1 2019-01-10 15:04
 */
public class StandardClassMetadata implements ClassMetadata {

    private final Class<?> introspectedClass;


    public StandardClassMetadata(Class<?> introspectedClass) {
        Assert.notNull(introspectedClass, "Class must not be null");
        this.introspectedClass = introspectedClass;
    }

    public Class<?> getIntrospectedClass() {
        return introspectedClass;
    }

    @Override
    public String getFullClassName() {
        return this.introspectedClass.getName();
    }

    @Override
    public boolean isInterface() {
        return this.introspectedClass.isInterface();
    }

    @Override
    public boolean isAnnotation() {
        return this.introspectedClass.isAnnotation();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedClass.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedClass.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return ((this.introspectedClass.getDeclaringClass() != null &&
                        Modifier.isStatic(this.introspectedClass.getModifiers())));
    }


    @Override
    public boolean hasSuperClass() {
        return (this.introspectedClass.getSuperclass() != null);
    }

    @Override
    public String getSuperClassName() {
        Class<?> superClass = this.introspectedClass.getSuperclass();
        return (superClass != null ? superClass.getName() : null);
    }

    @Override
    public String[] getInterfaceNames() {
        Class<?>[] ifcs = this.introspectedClass.getInterfaces();
        String[] ifcNames = new String[ifcs.length];
        for (int i = 0; i < ifcs.length; i++) {
            ifcNames[i] = ifcs[i].getName();
        }
        return ifcNames;
    }


}
