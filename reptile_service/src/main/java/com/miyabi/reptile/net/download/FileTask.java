package com.miyabi.reptile.net.download;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实际下载工作者
 *
 * @author miyabi
 * @date 2021-03-17-13-05
 * @since 1.0
 **/


public class FileTask {
    private final static long SMALL_SPLIT_SIZE = 2097152;
    private final int workerSize;
    private final long contentLength;
    protected CountDownLatch latch;
    protected AtomicBoolean flag = new AtomicBoolean(true);
    protected String url;
    protected File file;
    protected Downloader downloader;

    public FileTask(String url, File file, Downloader downloader) {
        this.url = url;
        this.workerSize = Downloader.WORKER_SIZE;
        this.contentLength = file.getFileLength();
        this.file = file;
        this.downloader = downloader;
    }

    public Path download() throws IOException {
        if (contentLength < SMALL_SPLIT_SIZE) {
            latch = new CountDownLatch(1);
            DWManager.getInstance().execute(new DownloadTask(this, 0, contentLength - 1, 1));
        } else {
            latch = new CountDownLatch(workerSize);
            for (int i = 0; i < workerSize; i++) {
                var dwManager = DWManager.getInstance();
                long splitSize = contentLength / workerSize;
                if (i == workerSize - 1) {
                    dwManager.execute(new DownloadTask(this, i * splitSize + 1, contentLength - 1, i + 1));
                } else {
                    dwManager.execute(new DownloadTask(this, i * splitSize, (i + 1) * splitSize - 1, i + 1));
                }
            }
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var tPath = file.getTempFilePath();
        var pStr = tPath.getFileName().toString();
        var newPath = Path.of(tPath.getParent().toString(), pStr.substring(0, pStr.lastIndexOf('.')) + ".jpg");
        if (!flag.get()) {
            Files.deleteIfExists(tPath);
            Files.deleteIfExists(file.getLogFilePath());
            throw new IOException("下载文件资源失败");
        } else {
            Files.move(tPath, newPath);
            Files.deleteIfExists(file.getLogFilePath());
        }

        return newPath;
    }
}
