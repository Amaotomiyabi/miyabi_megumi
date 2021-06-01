package com.megumi.service.impl;

import com.megumi.aspect.annotation.RequireValid;
import com.megumi.common.StateCode;
import com.megumi.common.StringUtils;
import com.megumi.pojo.PTag;
import com.megumi.pojo.User;
import com.megumi.repository.PTagRepo;
import com.megumi.service.TagService;
import com.megumi.util.RequestHolderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.message.AuthException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2021/2/27
 *
 * @author miyabi
 * @since 1.0
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class TagServiceImpl implements TagService {

    private final PTagRepo pTagRepo;

    @Autowired
    public TagServiceImpl(PTagRepo pTagRepo) {
        this.pTagRepo = pTagRepo;
    }


    @Override
    @RequireValid
    public List<PTag> saveTags(List<String> tags) throws AuthException {
        var userId = RequestHolderUtil.getUserId();
        var tagList = pTagRepo.findByNameIn(tags);
        var pTags = tags.stream().distinct().map(tag -> {
            var pTag = new PTag(tag);
            pTag.setUploader(new User(userId));
            pTag.setViews(0);
            return pTag;
        }).filter(tag -> !tagList.contains(tag)).collect(Collectors.toList());
        var result = pTagRepo.saveAll(pTags);
        result.addAll(tagList);
        return result;
    }

    @Override
    @RequireValid
    public String saveTag(String tag) throws AuthException {
        var existsTag = pTagRepo.findByName(tag);
        if (existsTag != null) {
            return "该标签已存在";
        }
        var pTag = new PTag(tag);
        pTag.setViews(0);
        pTag.setUploader(new User(RequestHolderUtil.getUserId()));
        pTagRepo.save(new PTag(tag));
        return StateCode.TRUE;
    }

    @Override
    public Map<String, Object> getTags(String name, Integer page, Integer size, Integer sort) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        Sort sortTemp;
        if (sort == null || sort != 1) {
            sortTemp = Sort.by(Sort.Direction.DESC, "createTime");
        } else {
            sortTemp = Sort.by(Sort.Direction.DESC, "views");
        }
        Page<PTag> pTags;
        if (StringUtils.isValid(name)) {
            pTags = pTagRepo.findByNameContains(name, PageRequest.of(page - 1, size, sortTemp));
        } else {
            pTags = pTagRepo.findAll(PageRequest.of(page - 1, size, sortTemp));
        }
        var result = new HashMap<String, Object>();
        result.put("data", pTags.getContent());
        result.put("total", pTags.getTotalElements());
        result.put("pages", pTags.getTotalPages());
        return result;
    }

    @Override
    public Map<String, Object> getAssociationTags(String name, Integer page, Integer size, Integer sort) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        if (!StringUtils.isValid(name)) {
            return getTags(name, page, size, sort);
        } else {
            var searchTags = pTagRepo.findByNameIn(Arrays.asList(name.split(" ")));
            if (!searchTags.isEmpty()) {
                var associationList = pTagRepo.findByPredicateTag(searchTags.stream().map(PTag::getId).toList());
                searchTags.addAll(associationList);
            }
            var result = new HashMap<String, Object>();
            result.put("data", searchTags);
            return result;
        }
    }

    @Override
    public Map<String, Object> getHotTags(String name, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        name = StringUtils.isValid(name) ? name : null;
        var pTags = pTagRepo.findHotTag(LocalDateTime.now().minusMonths(1), name, PageRequest.of(page - 1, size));
        var result = new HashMap<String, Object>();
        result.put("data", pTags.getContent());
        result.put("total", pTags.getTotalElements());
        result.put("pages", pTags.getTotalPages());
        return result;
    }
}
