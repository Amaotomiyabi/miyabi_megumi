package com.miyabi.reptile.persistence;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 持久化
 *
 * @author miyabi
 * @date 2021-03-31-14-41
 * @since 1.0
 **/


public interface Persistence {

    String saveBaseInfo(String result) throws IOException, InterruptedException;

    default Path downloadImg(String url) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("不支持的操作");
    }
}
