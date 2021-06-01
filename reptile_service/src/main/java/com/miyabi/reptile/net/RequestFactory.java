package com.miyabi.reptile.net;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 请求工厂类
 *
 * @author miyabi
 * @date 2021-03-17-12-40
 * @since 1.0
 **/


public class RequestFactory {

    public static final long REQ_TIME_OUT_SECOND;
    private static RequestFactory factory = new RequestFactory();

    static {
        REQ_TIME_OUT_SECOND = Long.parseLong(ResourceBundle.getBundle("reptile").getString("req.time-out"));
    }

    private RequestFactory() {
    }

    public static RequestFactory getInstance() {
        return factory;
    }

    public HttpRequest headRequest(String url) {
        return this.headRequest(url, null);
    }

    public HttpRequest getRequest(String url) {
        return this.getRequest(url, null);
    }

    public HttpRequest headRequest(String url, Map<String, List<String>> headers) {
        var reqBuilder = HttpRequest.newBuilder(URI.create(url));
        reqBuilder
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(REQ_TIME_OUT_SECOND));
        if (headers != null) {
            headers.forEach((k, v) -> {
                if (v != null && !v.isEmpty()) {
                    v.forEach(h -> reqBuilder.header(k, h));
                }
            });
        }
        return reqBuilder.build();
    }

    public HttpRequest getRequest(String url, Map<String, List<String>> headers) {
        var reqBuilder = HttpRequest.newBuilder(URI.create(url));
        reqBuilder
                .GET()
                .timeout(Duration.ofSeconds(REQ_TIME_OUT_SECOND));
        if (headers != null) {
            headers.forEach((k, v) -> {
                if (v != null && !v.isEmpty()) {
                    v.forEach(h -> reqBuilder.header(k, h));
                }
            });
        }
        return reqBuilder.build();
    }
}
