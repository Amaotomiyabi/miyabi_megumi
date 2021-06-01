package com.megumi.controller;

import com.megumi.common.StateCode;
import com.megumi.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.message.AuthException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @PostMapping("/save")
    public String saveTag(@RequestParam String tag) throws AuthException {
        return tagService.saveTag(tag);
    }

    @PostMapping("/save/list")
    public String saveTags(@RequestParam List<String> tags) throws AuthException {
        tagService.saveTags(tags);
        return StateCode.TRUE;
    }

    @GetMapping("/list")
    public Map<String, Object> getTags(String name, Integer page, Integer size, Integer sort) {
        return tagService.getTags(name, page, size, sort);
    }

    @GetMapping("/association/list")
    public Map<String, Object> getAssociationTags(String name, Integer page, Integer size, Integer sort) {
        return tagService.getAssociationTags(name, page, size, sort);
    }

    @GetMapping("/index/hot")
    public Map<String, Object> getIndexHotTags() {
        var result = tagService.getHotTags(null, 1, 36);
        if (((Long) result.get("total")) == 0 || result.get("data") == null || ((List<?>) result.get("data")).isEmpty()) {
            result = tagService.getTags(null, 1, 36, 1);
        }
        return result;
    }

    @GetMapping("/hot")
    public Map<String, Object> getHotTags(String name, Integer size, Integer page) {
        return tagService.getHotTags(name, size, page);
    }


}
