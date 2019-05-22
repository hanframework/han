package org.hanframework.web.server.netty;

import org.hanframework.web.annotation.GetMapping;
import org.hanframework.web.annotation.PostMapping;
import org.hanframework.web.annotation.RestController;
import org.hanframework.web.http.HttpHeaders;

/**
 * @author liuxin
 * @version Id: HttpControllerTest.java, v 0.1 2019-04-12 17:30
 */
@RestController
public class HttpControllerTest {

  @GetMapping(value = "/zoo/api")
  public String get() {
    return "Hello World";
  }

  @PostMapping(value = "/zoo/api/${name}/animal")
  public String post(String name, HttpHeaders httpHeaders) {
    return "Hello World"+name + httpHeaders.toString();
  }
}
