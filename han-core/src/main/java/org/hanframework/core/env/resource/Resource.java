package org.hanframework.core.env.resource;

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

  URL getURL() throws IOException;

  File getFile() throws IOException;

  default ReadableByteChannel readableChannel() throws IOException {
    return Channels.newChannel(this.getInputStream());
  }

  long contentLength() throws IOException;


  String getDescription();

  String getFilename();

  String getFileSuffixName();

}
