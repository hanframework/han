package org.hanframework.env.resource.impl;

import org.hanframework.env.resource.*;
import org.hanframework.env.resource.pathmatcher.PathMatcher;
import org.hanframework.env.resource.pathmatcher.RegularPathMatcher;
import org.hanframework.tool.asserts.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * classpath: 优先从本项目的class路径中加载,没有的话在从其他的Jar包中查询,找到第一个就返回
 * classpath*: 加载所有的class路径的配置文件
 * 举个栗子：
 * A项目中resources中存在system.properties，B项目中resources中也存在system.properties。
 * A项目以jar包形式引入B项目。
 * 此时A项目spring配置classpath:system.properties加载到的是A项目自己的system.properties。
 * 但是如果A项目resources中没有system.properties则最终结果加载到的是B.jar中的system.properties！
 * <p>
 * 在加入spring中配置的是classpath*: 那么最终加载到的是A和B项目中的配置
 * <p>
 * 如果A和B的配置文件中都有一样的配置值,那么spring会选择最后加载到的。
 * <p>
 * Spring是如何匹配的
 * /config/*\application-*.yml
 * 先获取:
 * 通配符前面的目录:/config/  ,然后在该目录中递归遍历,能匹配到的文件。如果匹配到了就加载。
 *
 * @author liuxin
 * @version Id: PathMatchingResourcePatternResolver.java, v 0.1 2018-12-11 16:19
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    private ResourceLoader resourceLoader;

    private PathMatcher pathMatcher = new RegularPathMatcher();

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public PathMatchingResourcePatternResolver() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        //如果是带*号,则用正则的方式去处理,否则就是按照路径处理
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            //以classpath*: 开头并且文件路径中也包含有通配符的
            if (getPathMatcher().isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
                return findPathMatchingResources(locationPattern);
            } else {
                //没有通配符的直接加载路径找，如果路径也没有指定,就是加载所有的配置文件，那么从Jar包中都寻找
                return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        } else {
            int prefixEnd = (locationPattern.startsWith("war:") ? locationPattern.indexOf("*/") + 1 :
                    locationPattern.indexOf(':') + 1);
            if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
                //根据匹配来
                return findPathMatchingResources(locationPattern);
            } else {
                //获取子目录
                String subPattern = locationPattern.substring(prefixEnd + 1);
                return new Resource[]{getResourceLoader().getResource(subPattern)};
            }

        }
    }

    /**
     * 通配符中加载资源
     *
     * @param locationPattern
     * @return
     * @throws IOException
     */
    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        //获取根目录
        String rootDirPath = determineRootDir(locationPattern);
        //获取子目录
        String subPattern = locationPattern.substring(rootDirPath.length());
        //获取匹配模式下,所有Jar包中的配置信息
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource rootDirResource : rootDirResources) {
            URL url = rootDirResource.getURL();
            if (url != null) {
                String protocol = url.getProtocol();
                if ("jar".equalsIgnoreCase(protocol) || "war".equalsIgnoreCase(protocol)) {
                    //从jar中加载
                } else {
                    result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
                }
            }
        }
        return result.toArray(new Resource[0]);
    }

    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern)
            throws IOException {

        File rootDir;
        try {
            rootDir = rootDirResource.getFile().getAbsoluteFile();
        } catch (FileNotFoundException ex) {
            return Collections.emptySet();
        } catch (Exception ex) {
            return Collections.emptySet();
        }
        return doFindMatchingFileSystemResources(rootDir, subPattern);
    }


    protected void doRetrieveMatchingFiles(File rootDir, String fullPath, Set<Resource> result) throws IOException {
        if (rootDir.isDirectory()) {
            File[] files = rootDir.listFiles();
            for (File file : files) {
                doRetrieveMatchingFiles(file, fullPath, result);
            }
        } else {
            if (getPathMatcher().match(fullPath, rootDir.getPath())) {
                result.add(new LocalFileResource(rootDir.toURL()));
            }
        }
    }

    /**
     * 当前目录中是否有匹配到的文件
     *
     * @param rootDir
     * @param subPattern
     * @return
     * @throws IOException
     */
    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException {
        String absolutePath = rootDir.getAbsolutePath();
        String fullPath = absolutePath + "/" + subPattern;
        Set<Resource> result = new LinkedHashSet();
        doRetrieveMatchingFiles(rootDir, fullPath, result);
        return result;
    }

    /**
     * 文件路径中不包含通配符的
     *
     * @param location
     * @return
     * @throws IOException
     */
    protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Set<Resource> result = doFindAllClassPathResources(path);

        return result.toArray(new Resource[0]);
    }

    protected Set<Resource> doFindAllClassPathResources(String path) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = getClassLoader();
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(new LocalFileResource(url));
        }
        if ("".equalsIgnoreCase(path)) {
            //如果路径等于空,就从jar包中找
            addAllClassLoaderJarRoots(cl, result);
        }
        return result;
    }

    protected void addAllClassLoaderJarRoots(ClassLoader classLoader, Set<Resource> result) {
        if (classLoader instanceof URLClassLoader) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        LocalFileResource jarResource = new LocalFileResource(new URL("jar:" + url + "!/"));
                        if (jarResource.exists()) {
                            result.add(jarResource);
                        }
                    } catch (Exception e) {

                    }

                }
            } catch (Exception e) {

            }

        }


        if (classLoader != null) {
            try {
                // Hierarchy traversal...
                addAllClassLoaderJarRoots(classLoader.getParent(), result);
            } catch (Exception ex) {
            }
        }
    }


    @Override
    public Resource getResource(String location) {
        return getResourceLoader().getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return getResourceLoader().getClassLoader();
    }

    protected String determineRootDir(String location) {
        int prefixEnd = location.indexOf(':') + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }


}
