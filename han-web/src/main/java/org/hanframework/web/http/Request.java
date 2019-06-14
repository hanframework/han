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

    private final String id;

    private final String httpVersion;

    private final String url;

    private final HttpMethod httpMethod;

    private final HttpHeaders httpHeaders;

    private final Map<String, Object> paramMap;

    private HttpSession session;

}
