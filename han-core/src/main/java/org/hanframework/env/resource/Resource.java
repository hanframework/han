package org.hanframework.env.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author liuxin
 * @version Id: Resource.java, v 0.1 2018-12-11 16:09
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    default boolean isReadable() {
        return this.exists();
    }

    default boolean isOpen() {
        return false;
    }

    default boolean isFile() {
        return false;
    }

    /**
     * @return URL
     * @throws IOException IO异常
     */
    URL getURL() throws IOException;


    /**
     * @return 文件
     * @throws IOException IO异常
     */
    File getFile() throws IOException;


    /**
     * 已nio的方式读取
     *
     * @return 读取通道
     * @throws IOException IO异常
     */
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(this.getInputStream());
    }

    /**
     * 文件长度
     *
     * @return long
     * @throws IOException IO异常
     */
    long contentLength() throws IOException;

    /**
     * 文件描述
     *
     * @return String
     */
    String getDescription();

    /**
     * 文件名
     *
     * @return String
     */
    String getFilename();

    /**
     * @return 文件后缀
     */
    String getFileSuffixName();

}
