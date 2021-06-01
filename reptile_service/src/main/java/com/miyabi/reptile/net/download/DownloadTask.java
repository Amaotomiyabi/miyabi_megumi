package com.miyabi.reptile.net.download;

import com.miyabi.reptile.net.RequestFactory;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 下载任务
 *
 * @author miyabi
 * @date 2021-03-18-10-43
 * @since 1.0
 **/


public class DownloadTask implements Runnable {
    private final static int bufSize = 8192;
    private final static int logEntrySize = 160;
    private final String url;
    private final long start;
    private final long end;
    private final Downloader downloader;
    private final int taskId;
    private final CountDownLatch latch;
    private final File dFile;
    private final AtomicBoolean flag;
    private Map<String, List<String>> headers;

    public DownloadTask(FileTask fileTask, long start, long end, int taskId) {
        this.url = fileTask.url;
        this.headers = fileTask.downloader.headers;
        this.dFile = fileTask.file;
        this.start = start;
        this.end = end;
        this.downloader = fileTask.downloader;
        this.taskId = taskId;
        this.latch = fileTask.latch;
        this.flag = fileTask.flag;
    }

    @Override
    public void run() {
        try {
            download();
        } catch (IOException | InterruptedException e) {
            System.out.println("请求资源文件错误:" + e.getMessage());
            flag.set(false);
        } finally {
            latch.countDown();
        }
    }

    public void download() throws IOException, InterruptedException {
        var req = getReq();
        var position = start;
        try (var is = downloader.client.sendReqFile(req).body();
             var inC = Channels.newChannel(is);
             var downloadFileC = FileChannel.open(dFile.getTempFilePath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
             var logFileC = FileChannel.open(dFile.getLogFilePath(), StandardOpenOption.READ, StandardOpenOption.WRITE);) {
            var buf = ByteBuffer.allocate(logEntrySize);
            var buf1 = ByteBuffer.allocateDirect(bufSize);
            while (position <= end) {
                if (!flag.get()) {
                    return;
                }
                var readSize = inC.read(buf1);
                buf1.flip();
                downloadFileC.write(buf1, position);
                buf1.clear();
                position += readSize;
                buf.putInt(taskId);
                buf.putLong(position);
                buf.putLong(end);
                buf.flip();
                logFileC.write(buf, (taskId - 1) * logEntrySize);
                buf.clear();
            }
            logFileC.read(buf, (taskId - 1) * logEntrySize);
            buf.flip();
        }
    }

    public HttpRequest getReq() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put("Range", Collections.singletonList("bytes=" + start + "-" + end));
        return RequestFactory.getInstance().getRequest(url, headers);
    }
}
