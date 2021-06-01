package com.megumi.service;

import com.megumi.pojo.Picture;
import com.megumi.util.exception.IllegalRequestParameterException;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.message.AuthException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface PictureService {

    Map<String, Object> getPictures(String tags, Integer x, Integer y, String isSexy, Integer page, Integer size, Integer sort);

    Map<String, Object> getPictureListByUser(Long userId, Integer page, Integer size) throws AuthException;

    Map<String, Object> getHotPictures(String tags, Integer x, Integer y, String isSexy, Integer page, Integer size);

    Picture getPictureInfo(Long pId) throws FileNotFoundException;

    String uploadPicture(List<String> tags, String src, String isSexy, MultipartFile file) throws IOException, AuthException, IllegalRequestParameterException;

    String modifyPicture(Long pId, List<String> tags, String src, String isSexy) throws AuthException;

    String removePicture(Long pId) throws IOException, AuthException, IllegalRequestParameterException;

    String removePictures(List<Long> pIds) throws IOException;


}
