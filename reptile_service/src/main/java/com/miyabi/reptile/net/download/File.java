package com.miyabi.reptile.net.download;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 下载文件对象
 *
 * @author miyabi
 * @date 2021-03-18-09-13
 * @since 1.0
 **/


public class File {
    private final long fileLength;
    private final Path tempFilePath;
    private final Path logFilePath;

    public File(long fileLength, String savePath) throws IOException {
        var name = String.valueOf(Downloader.id());
        tempFilePath = Path.of(savePath, name + ".temp");
        logFilePath = Path.of(savePath, name + "_temp.log");
        Files.createFile(tempFilePath);
        Files.createFile(logFilePath);
        this.fileLength = fileLength;
    }

    public long getFileLength() {
        return fileLength;
    }

    public Path getTempFilePath() {
        return tempFilePath;
    }

    public Path getLogFilePath() {
        return logFilePath;
    }
}
