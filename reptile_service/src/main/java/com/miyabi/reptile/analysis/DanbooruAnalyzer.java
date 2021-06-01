package com.miyabi.reptile.analysis;

import com.miyabi.reptile.url.DanbooruUrlFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * danbooru
 *
 * @author miyabi
 * @date 2021-03-31-14-17
 * @since 1.0
 **/


public class DanbooruAnalyzer implements ResultAnalyzer<Map<String, Object>> {

    @Override
    public List<String> getArtist(Map<String, Object> result) {
        return null;
    }

    @Override
    public List<String> getCharacter(Map<String, Object> result) {
        return null;
    }

    @Override
    public List<String> getGeneral(Map<String, Object> result) {
        return Arrays.asList(((String) result.get("tag_string")).split(" "));
    }

    @Override
    public String getSource(Map<String, Object> result) {
        return (String) result.get("source");
    }

    @Override
    public Integer width(Map<String, Object> result) {
        return (Integer) result.get("image_width");
    }

    @Override
    public Long getSize(Map<String, Object> result) {
        return Long.parseLong(String.valueOf(result.get("file_size")));
    }

    @Override
    public Integer height(Map<String, Object> result) {
        return (Integer) result.get("image_height");
    }

    @Override
    public String getBSource(Map<String, Object> result) {
        return DanbooruUrlFactory.baseUrl + "/" + result.get("id");
    }

    @Override
    public String smallUrl(Map<String, Object> result) {
        return (String) result.get("file_url");
    }

    @Override
    public String getDownloadUrl(Map<String, Object> result) {
        var url = result.get("large_file_url");
        if (url == null) {
            url = result.get("file_url");
        }
        return (String) url;
    }
}
