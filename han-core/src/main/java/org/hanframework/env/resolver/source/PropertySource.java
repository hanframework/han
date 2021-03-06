package org.hanframework.env.resolver.source;

import org.hanframework.tool.asserts.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置信息源
 * @author liuxin
 * @version Id: PropertySource.java, v 0.1 2018/10/17 3:41 PM
 */
public abstract class PropertySource<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String profile;

    protected final String name;

    protected final T source;


    public PropertySource(String name, T source) {
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
        this.profile = "default";
    }

    public PropertySource(String profile, String name, T source) {
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
        this.profile = profile;
    }


    public String getName() {
        return this.name;
    }

    public String getProfile() {
        return profile;
    }

    public T getSource() {
        return source;
    }


    public boolean containsProperty(String name) {
        return this.getProperty(name) != null;
    }


    public abstract Object getProperty(String name);


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PropertySource)) {
            return false;
        }
        PropertySource<?> other = (PropertySource<?>) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        } else if (this.profile == null) {
            if (other.profile != null) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        if (logger.isDebugEnabled()) {
            return String.format("%s@%s [name='%s', properties=%s]",
                    this.getClass().getSimpleName(), System.identityHashCode(this), this.name, this.source);
        }

        return String.format("%s [name='%s']",
                this.getClass().getSimpleName(), this.name);
    }
}
