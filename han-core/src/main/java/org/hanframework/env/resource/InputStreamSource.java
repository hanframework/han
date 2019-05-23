package org.hanframework.env.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author liuxin
 * @version Id: InputStreamSource.java, v 0.1 2018-12-11 16:09
 */
public interface InputStreamSource {
    /**
     * 输入流都要有获取数据流的方法
     *
     * @return InputStream
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;
}
