package org.hanframework.env.resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuxin
 * @version Id: AbstractPropertyResolver.java, v 0.1 2018/10/16 7:56 PM
 */
public abstract class AbstractPropertyResolver implements MultiConfigurablePropertyResolver {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String placeholderPrefix = "${";

    private String placeholderSuffix = "}";

    private String valueSeparator = ":";

    private boolean ignoreUnresolvableNestedPlaceholders = false;

    private PropertyPlaceholderHelper nonStrictHelper;

    private PropertyPlaceholderHelper strictHelper;

    @Override
    public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
        this.ignoreUnresolvableNestedPlaceholders=ignoreUnresolvableNestedPlaceholders;
    }

    /**
     * 创建一个解析@Value的语法
     *
     * @param ignoreUnresolvablePlaceholders true:安全模式，找不到报错 false: 非安全模式,找不到打印key
     * @return
     */
    private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix,
                this.valueSeparator, ignoreUnresolvablePlaceholders);
    }

    @Override
    public void setValueSeparator(String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    @Override
    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    @Override
    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null ? value : defaultValue);
    }


    @Override
    public String resolvePlaceholders(String text) {
        if (this.nonStrictHelper == null) {
            this.nonStrictHelper = createPlaceholderHelper(true);
        }
        return doResolvePlaceholders(text, this.nonStrictHelper);
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = createPlaceholderHelper(false);
        }
        return doResolvePlaceholders(text, this.strictHelper);
    }

    @Override
    public String resolvePlaceholders(String text, String profile) {
        if (this.nonStrictHelper == null) {
            this.nonStrictHelper = createPlaceholderHelper(true);
        }
        return doResolvePlaceholders(text, profile, this.nonStrictHelper);
    }

    @Override
    public String resolveRequiredPlaceholders(String text, String profile) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = createPlaceholderHelper(false);
        }
        return doResolvePlaceholders(text, profile, this.strictHelper);
    }

    protected String resolveNestedPlaceholders(String value) {
        return this.ignoreUnresolvableNestedPlaceholders ?
                resolvePlaceholders(value) : resolveRequiredPlaceholders(value);
    }

    private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, new PropertyPlaceholderHelper.PlaceholderResolver() {
            @Override
            public String resolvePlaceholder(String placeholderName) {
                return getPropertyAsRawString(placeholderName);
            }
        });
    }

    private String doResolvePlaceholders(String text, String profile, PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, new PropertyPlaceholderHelper.PlaceholderResolver() {
            @Override
            public String resolvePlaceholder(String placeholderName) {
                return getPropertyAsRawString(placeholderName, profile);
            }
        });
    }


    /**
     * 转换成什么类型，由子类实现
     *
     * @param key
     * @return
     */
    protected abstract String getPropertyAsRawString(String key);


    protected abstract String getPropertyAsRawString(String key, String profile);


}
