package com.megumi.controller;


import com.megumi.pojo.Picture;
import com.megumi.service.PictureService;
import com.megumi.util.exception.IllegalRequestParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.message.AuthException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/picture")
public class PictureMainController {

    private final PictureService pictureService;

    @Autowired
    public PictureMainController(PictureService pictureService) {
        this.pictureService = pictureService;
    }


    @PostMapping("/upload")
    public String uploadPicture(@RequestParam(value = "tags", required = false) List<String> tags, String src, String isSexy, MultipartFile file) throws AuthException, IOException, IllegalRequestParameterException {
        return pictureService.uploadPicture(tags, src, isSexy, file);
    }

    @PostMapping("/modify")
    public String modifyPicture(@RequestParam Long pId, @RequestParam(value = "tags", required = false) List<String> tags, String src, String isSexy) throws AuthException {
        return pictureService.modifyPicture(pId, tags, src, isSexy);
    }

    @PostMapping("/remove")
    public String removePicture(@RequestParam Long id) throws IOException, IllegalRequestParameterException, AuthException {
        return pictureService.removePicture(id);
    }

    @PostMapping("/remove/list")
    public String removePictures(@RequestParam List<Long> pIds) throws IOException {
        return pictureService.removePictures(pIds);
    }

    @GetMapping("/hot")
    public Map<String, Object> getHotPictures(String tags, Integer x, Integer y, String isSexy, Integer page, Integer size) {
        return pictureService.getHotPictures(tags, x, y, "0", page, size);
    }

    @GetMapping("/list")
    public Map<String, Object> getPictures(String tags, Integer x, Integer y, String isSexy, Integer page, Integer size, Integer sort) {
        return pictureService.getPictures(tags, x, y, "0", page, size, sort);
    }

    @GetMapping("/up")
    public Map<String, Object> getPicturesByUploader(Long userId, Integer page, Integer size) throws AuthException {
        return pictureService.getPictureListByUser(userId, page, size);
    }

    @GetMapping("/info/{id}")
    public Picture getPictureById(@PathVariable("id") Long pId) throws FileNotFoundException {
        return pictureService.getPictureInfo(pId);
    }

    @GetMapping("/index/hot")
    public Map<String, Object> getIndexHotPictures() {
        var result = pictureService.getHotPictures(null, null, null, "0", 1, 10);
        if (((Long) result.get("total")) == 0 || result.get("data") == null || ((List<?>) result.get("data")).isEmpty()) {
            result = getPictures(null, null, null, "0", 1, 10, 1);
        }
        return result;
    }
}
