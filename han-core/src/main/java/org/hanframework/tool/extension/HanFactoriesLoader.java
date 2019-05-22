package org.hanframework.tool.extension;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.core.classpathscan.AutoConfigureException;
import org.hanframework.core.env.resource.Resource;
import org.hanframework.core.env.resource.impl.PathMatchingResourcePatternResolver;
import org.hanframework.tool.asserts.Assert;
import org.hanframework.tool.string.StreamTools;
import org.hanframework.tool.yaml.YamlTools;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 当写该类的时候，有点犹豫了.犹豫什么呢?
 * 是像Spring一样去加载指定类,还是像Dubbo中SPI机制那样加载呢?
 * 在该项目中IOC容器完全是根据Spring来做的,但是小编觉得Dubbo的SPI机制更好写。
 * 好在哪里呢?
 * spring中spring.factories文件中key是字节码的完整名,value是实现类或者是要需要配合使用的类
 * 而dubbo中配置文件是字节码的完整名,文件也是有key和value不过，是key和value是对应的,不存在一对多的情况。主要是根据key来找到激活的实现类。
 * 当上面的东西写完，突然清晰了，他们的用处是不一样的。我现在做的功能是实现自动配置
 *
 * @author liuxin
 * @version Id: HanFactoriesLoader.java, v 0.1 2018-12-12 16:15
 */
@Slf4j
public class HanFactoriesLoader {

    /**
     * 仅仅提供读操作不提供写操作
     */
    private final static List<Resource> RESOURCES = new ArrayList<>();

    private final static Map<Class, Set<Class>> FACTORIES_CLASS_CACHE = new ConcurrentHashMap<>();

    static {
        PathMatchingResourcePatternResolver prpr = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = prpr.getResources("classpath*:autoconfigure.yml");
            HanFactoriesLoader.RESOURCES.addAll(Arrays.asList(resources));
            init(resources);
        } catch (Exception e) {
            throw new AutoConfigureException(e);
        }

    }

    /**
     * 获取原始资源
     *
     * @return
     */
    public static List<Resource> getResources() {
        return Collections.unmodifiableCollection(new ArrayList<>(RESOURCES)).stream().collect(Collectors.toList());
    }

    /**
     * 获取自动配置类
     *
     * @param cls key
     * @return 适配的实例
     */
    public static Set<Class> getAutoConfigure(Class cls) {
        Set<Class> classes = FACTORIES_CLASS_CACHE.get(cls);
        return classes == null ? Collections.EMPTY_SET : classes;
    }

    public static Class getActiveConfigure(Class cls, String activeName) {
        return null;
    }

    public static Set<Class> loadFactories(Class factoryClass) {
        Assert.notNull(factoryClass, "'factoryClass' must not be null");
        return FACTORIES_CLASS_CACHE.get(factoryClass);
    }


    private static void init(Resource[] resources) throws Exception {
        for (Resource r : Arrays.asList(resources)) {
            InputStream inputStream = r.getInputStream();
            String utf8 = StreamTools.convertStreamToString(inputStream, "utf8");
            Properties properties = YamlTools.readYaml(utf8);
            Enumeration keys = properties.keys();
            while (keys.hasMoreElements()) {
                String classKeyPath = (String) keys.nextElement();
                Class<?> interfaceCls = Class.forName(classKeyPath);
                Set<Class> implsCls = FACTORIES_CLASS_CACHE.get(interfaceCls);
                if (null == implsCls) {
                    implsCls = new HashSet<>();
                    FACTORIES_CLASS_CACHE.put(interfaceCls, implsCls);
                }
                List<String> autoConfigure = new ArrayList<>();
                try {
                    autoConfigure = (List<String>) properties.get(classKeyPath);
                } catch (ClassCastException e) {
                    log.error("autoconfigure.yml only supports list collection types");
                }
                for (String impl : autoConfigure) {
                    implsCls.add(Class.forName(impl));
                }
            }
        }
    }

}
