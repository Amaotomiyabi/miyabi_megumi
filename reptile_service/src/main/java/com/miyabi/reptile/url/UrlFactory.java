package com.miyabi.reptile.url;

import java.io.IOException;

/**
 * URL工厂接口
 *
 * @author miyabi
 * @date 2021-03-31-13-30
 * @since 1.0
 **/


public interface UrlFactory {

    String getNextObjUrl();

    String getPageUrl(int page, int limit);

    long getId();

    void setId(long id);

    default String getObjDocUrl() {
        throw new UnsupportedOperationException("不支持的类型");
    }

    default String getPageDocUrl() {
        throw new UnsupportedOperationException("不支持的类型");
    }

    long getMaxId() throws IOException, InterruptedException;
}
