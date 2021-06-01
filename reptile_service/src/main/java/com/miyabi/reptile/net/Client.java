package com.miyabi.reptile.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * 客户端
 *
 * @author miyabi
 * @date 2021-03-17-10-40
 * @since 1.0
 **/


public class Client {
    public static final long CONN_TIME_OUT_SECOND;

    static {
        CONN_TIME_OUT_SECOND = Long.parseLong(ResourceBundle.getBundle("reptile").getString("client.time-out"));
    }

    private final HttpClient client;

    public Client(Authenticator authenticator, ProxySelector proxySelector) {
        var builder = HttpClient.newBuilder();
        if (proxySelector != null) {
            builder.proxy(proxySelector);
        }
        if (authenticator != null) {
            builder.authenticator(authenticator);
        }
        this.client = builder.connectTimeout(Duration.ofSeconds(CONN_TIME_OUT_SECOND))
                .build();
    }

    public Client() {
        this(null, null);
    }

    public HttpResponse<InputStream> sendReqFile(HttpRequest req) throws IOException, InterruptedException {
        return client.send(req, HttpResponse.BodyHandlers.ofInputStream());
    }

    public CompletableFuture<HttpResponse<String>> sendRetStringAsync(HttpRequest req) {
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendRetString(HttpRequest req) throws IOException, InterruptedException {
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

}
