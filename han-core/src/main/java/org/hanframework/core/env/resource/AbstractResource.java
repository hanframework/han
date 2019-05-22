package org.hanframework.core.env.resource;

import java.io.File;
import java.io.IOException;


/**
 * 公用的方法抽象出来
 *
 * @author liuxin
 * @version Id: AbstractResource.java, v 0.1 2018-12-11 16:45
 */
public abstract class AbstractResource implements Resource {

    @Override
    public boolean exists() {
        try {
            return getFile().exists();
        } catch (IOException ex) {
            try {
                getInputStream().close();
                return true;
            } catch (Throwable isEx) {
                return false;
            }
        }
    }

    @Override
    public boolean isReadable() {
        return exists();
    }

    @Override
    public boolean isOpen() {
        return exists();
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public abstract File getFile() throws IOException;


}
