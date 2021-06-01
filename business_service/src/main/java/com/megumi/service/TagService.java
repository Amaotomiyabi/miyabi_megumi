package com.megumi.service;

import com.megumi.pojo.PTag;

import javax.security.auth.message.AuthException;
import java.util.List;
import java.util.Map;

/**
 * 2021/2/27
 *
 * @author miyabi
 * @since 1.0
 */
public interface TagService {

    List<PTag> saveTags(List<String> tags) throws AuthException;

    String saveTag(String tag) throws AuthException;

    Map<String, Object> getTags(String name, Integer page, Integer size, Integer sort);

    Map<String, Object> getAssociationTags(String name, Integer page, Integer size, Integer sort);

    Map<String, Object> getHotTags(String name, Integer page, Integer size);
}
