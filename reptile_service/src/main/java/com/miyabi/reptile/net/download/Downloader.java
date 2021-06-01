package com.miyabi.reptile.net.download;

import com.miyabi.reptile.net.Client;
import com.miyabi.reptile.net.RequestFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * 下载
 *
 * @author miyabi
 * @date 2021-03-17-13-06
 * @since 1.0
 **/


public class Downloader {
    protected final static int WORKER_SIZE;
    private final static int DEFAULT_MAX_WORKER_SIZE = 64;
    private final static int DEFAULT_WORKER_SIZE = 4;

    static {
        var bundle = ResourceBundle.getBundle("reptile");
        if (!bundle.containsKey("download.worker.size")) {
            WORKER_SIZE = DEFAULT_WORKER_SIZE;
        } else {
            var workSize = Integer.parseInt(bundle.getString("download.worker.size"));
            if (workSize > DEFAULT_MAX_WORKER_SIZE) {
                WORKER_SIZE = DEFAULT_MAX_WORKER_SIZE;
            } else if (workSize < 1) {
                WORKER_SIZE = DEFAULT_WORKER_SIZE;
            } else {
                WORKER_SIZE = workSize;
            }
        }
    }

    final Client client;
    final int retry;
    private final String savePath;
    protected Map<String, List<String>> headers;

    public Downloader(Client client, String savePath, Map<String, List<String>> headers) throws IOException {
        this.retry = 3;
        this.client = client;
        this.savePath = savePath;
        this.headers = headers;
        Files.createDirectories(Path.of(savePath));
    }

    static String id() {
        return UUID.randomUUID().toString();
    }

    private long reqLength(String url) throws IOException, InterruptedException {
        var req = RequestFactory.getInstance().headRequest(url, headers);
        var i = 0;
        while (true) {
            try {
                var res = client.sendRetString(req);
                return res.headers().firstValueAsLong("Content-Length").orElseThrow();
            } catch (IOException | InterruptedException e) {
                if (i >= retry) {
                    throw e;
                } else {
                    i++;
                    System.out.println("超时重试");
                }
            }
        }
    }


    public Path download(String url) throws IOException, InterruptedException {
        var contentLength = reqLength(url);
//        System.out.println("开始下载： " + url);
        var downloadObj = new File(contentLength, savePath);
        var fileTask = new FileTask(url, downloadObj, this);
        return fileTask.download();
    }

}
