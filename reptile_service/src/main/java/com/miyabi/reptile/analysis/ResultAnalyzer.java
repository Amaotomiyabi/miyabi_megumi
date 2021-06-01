package com.miyabi.reptile.analysis;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * 结果解析
 *
 * @author miyabi
 * @date 2021-03-31-14-09
 * @since 1.0
 **/


public interface ResultAnalyzer<T> {

    List<String> getArtist(T result) throws JsonProcessingException;

    List<String> getCharacter(T result);

    List<String> getGeneral(T result);

    String getSource(T result);

    Integer width(T result);

    Integer height(T result);

    String getBSource(T result);

    String getDownloadUrl(T result) throws JsonProcessingException;

    String smallUrl(T result);

    Long getSize(T result);
}
