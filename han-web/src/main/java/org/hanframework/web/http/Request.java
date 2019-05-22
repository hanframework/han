package org.hanframework.web.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hanframework.web.annotation.HttpMethod;
import org.hanframework.web.http.HttpHeaders;
import java.util.Map;

/**
 * @author liuxin
 * @version Id: Request.java, v 0.1 2019-05-08 19:24
 */
@Builder
@Getter
@AllArgsConstructor
public final class Request {

  final String httpVersion;

  final String url;

  final HttpMethod httpMethod;

  final HttpHeaders httpHeaders;

  final Map<String, Object> paramMap;

}
