package org.hanframework.core.env.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author liuxin
 * @version Id: LocalFileResource.java, v 0.1 2018-12-11 16:49
 */
public class LocalFileResource extends AbstractResource {
    private URL url;

    public LocalFileResource(URL url) {
        this.url = url;
    }

    @Override
    public URL getURL() throws IOException {
        return this.url;
    }

    @Override
    public long contentLength() throws IOException {
        return url.openConnection().getContentLengthLong();
    }

    @Override
    public String getFilename() {
        int indexOf = this.url.getFile().lastIndexOf("/");
        return this.url.getFile().substring(indexOf + 1);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }

    @Override
    public File getFile() throws IOException {
        return new File(url.getFile());
    }

    /**
     * 获取文件后缀
     *
     * @return
     */
    @Override
    public String getFileSuffixName() {
        return getFilename().substring(getFilename().lastIndexOf(".") + 1);
    }
}

