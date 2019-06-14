package org.hanframework.web.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author liuxin
 * @version Id: URL.java, v 0.1 2019-04-01 11:04
 */
public class URL {

    private static final String REGEX_PREFIX = "${";
    private static final String REGEX_SUFFIX = "}";
    private static final String REGEX_CONTEXT = ".*";
    private static final String SEPARATOR = "/";

    private Pattern compile;
    /**
     * http、https
     */
    @Setter
    @Getter
    private String protocol;

    @Getter
    private String path;

    @Getter
    private boolean variable;

    /**
     * 记录路径中的变量和在路径所在的位置
     * eg:
     * api/zoo/${age}/${name}
     * age = 2
     * name = 3
     */
    private Map<String, Integer> variableIndexMap = new ConcurrentHashMap<>(8);


    public URL(String path) {
        this.path = path;
        this.variable = path.contains(REGEX_PREFIX);
        if (variable) {
            //进行替换将${}转换成.*
            compile = Pattern.compile(cleanPath(buildRegex(path)));
        } else {
            compile = Pattern.compile(cleanPath(path));
        }
        this.protocol = Protocol.HTTP.getProtocol();
    }

    public URL(String path,Protocol protocol) {
        this.path = path;
        this.variable = path.contains(REGEX_PREFIX);
        if (variable) {
            //进行替换将${}转换成.*
            compile = Pattern.compile(cleanPath(buildRegex(path)));
        } else {
            compile = Pattern.compile(cleanPath(path));
        }
        this.protocol = protocol.getProtocol();
    }

    public static URL valueOf(String url) {
        return new URL(url);
    }

    /**
     * 用户请求过来的路径,是否能匹配当前URL
     *
     * @param path 用户请求路径
     * @return TRUE:匹配,FALSE: 不匹配
     */
    public boolean matcher(String path) {
        return compile.matcher(cleanPath(path)).matches();
    }


    /**
     * 根据用户请求路径获取路径变量参数
     *
     * @param path 用户请求路径
     * @return
     */
    public Map<String, String> getUrlVariable(String path) {
        cleanPath(path);
        if (!matcher(path)) {
            throw new RuntimeException("path:" + path + ",不能匹配");
        }
        String[] pathArr = path.split(SEPARATOR);
        Map<String, String> urlMap = new ConcurrentHashMap<>(10);
        for (Map.Entry<String, Integer> variableContext : variableIndexMap.entrySet()) {
            String key = variableContext.getKey();
            Integer indexValue = variableContext.getValue();
            String value = pathArr[indexValue];
            urlMap.put(key, value);
        }
        return Collections.unmodifiableMap(urlMap);
    }

    /**
     * 清楚路径前后的 / 符号
     *
     * @param path /name/age/
     * @return name/age
     */
    public String cleanPath(String path) {
        int length = path.length();
        if (path.startsWith(SEPARATOR)) {
            path = path.substring(1, length);
        }
        if (path.endsWith(SEPARATOR)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 将可变参数生成正则表达式
     * <p>
     * eg:
     * /api/zoo/${age}/name
     * 替换为/api/zoo/.+/name
     *
     * @param variablePath 可变参数
     * @return 构建完成的正则表达式
     */
    private String buildRegex(String variablePath) {
        String[] split = variablePath.split(SEPARATOR);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (split[i].startsWith(REGEX_PREFIX) && split[i].endsWith(REGEX_SUFFIX)) {
                int start = split[i].indexOf(REGEX_PREFIX) + 2;
                int end = split[i].indexOf(REGEX_SUFFIX);
                String varContext = split[i].substring(start, end);
                variableIndexMap.put(varContext, i);
                split[i] = REGEX_CONTEXT;
            }
            sb.append(split[i]).append(SEPARATOR);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "URL{" +
                "compile=" + compile +
                ", protocol='" + protocol + '\'' +
                ", path='" + path + '\'' +
                ", variable=" + variable +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URL url = (URL) o;
        return variable == url.variable &&
                Objects.equals(compile, url.compile) &&
                protocol.equals(url.protocol) &&
                path.equals(url.path) &&
                variableIndexMap.equals(url.variableIndexMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, path, variable, variableIndexMap);
    }
}