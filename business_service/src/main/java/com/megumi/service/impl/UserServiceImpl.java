package com.megumi.service.impl;

import com.megumi.aspect.annotation.RequireValid;
import com.megumi.common.FileUtil;
import com.megumi.config.CustomMvcConfig;
import com.megumi.pojo.PTag;
import com.megumi.pojo.Picture;
import com.megumi.pojo.User;
import com.megumi.pojo.middle.*;
import com.megumi.repository.*;
import com.megumi.service.UserService;
import com.megumi.util.RequestHolderUtil;
import com.megumi.util.exception.IllegalRequestParameterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.message.AuthException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.megumi.common.StateCode.TRUE;


/**
 * 2021/2/22
 *
 * @author miyabi
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = {Throwable.class})
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final RedisTemplate<?, ?> redisTemplate;
    private final PictureRepo pictureRepo;
    private final PTagRepo pTagRepo;
    private final FollowerRepo followerRepo;
    private final PictureServiceImpl pictureServiceImpl;
    private ImgUserAppRepo imgUserAppRepo;
    private ImgUserCollectRepo imgUserCollectRepo;
    private ImgUserHistoryRepo imgUserHistoryRepo;
    private TagUserHistoryRepo tagUserHistoryRepo;
    private FileUtil fileUtil;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, RedisTemplate<Object, Object> redisTemplate, PictureRepo pictureRepo, PTagRepo pTagRepo, FollowerRepo followerRepo, ImgUserAppRepo imgUserAppRepo, ImgUserCollectRepo imgUserCollectRepo, ImgUserHistoryRepo imgUserHistoryRepo, TagUserHistoryRepo tagUserHistoryRepo, FileUtil fileUtil, PictureServiceImpl pictureServiceImpl) {
        this.userRepo = userRepo;
        this.redisTemplate = redisTemplate;
        this.pictureRepo = pictureRepo;
        this.pTagRepo = pTagRepo;
        this.followerRepo = followerRepo;
        this.imgUserAppRepo = imgUserAppRepo;
        this.imgUserCollectRepo = imgUserCollectRepo;
        this.imgUserHistoryRepo = imgUserHistoryRepo;
        this.tagUserHistoryRepo = tagUserHistoryRepo;
        this.fileUtil = fileUtil;
        this.pictureServiceImpl = pictureServiceImpl;
    }

    @Override
    public Page<Follower> getFollower(Long id, Integer page, Integer size) throws AuthException {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        if (id == null) {
            id = RequestHolderUtil.getUserId();
        }
        return followerRepo.findFollowersByUser(new User(id), PageRequest.of(page, size));
    }

    @Override
    public Page<Follower> getSubscribeUser(Long id, Integer page, Integer size) throws AuthException, IllegalRequestParameterException {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        if (id == null) {
            id = RequestHolderUtil.getUserId();
        }
        return followerRepo.findFollowersByFollower(new User(id), PageRequest.of(page, size));
    }

    @Override
    @RequireValid
    public boolean getSubscribeState(Long id) throws AuthException {
        return followerRepo.countFollowerByUserAndFollower(new User(id), new User(RequestHolderUtil.getUserId())) != 0;
    }

    @Override
    @RequireValid
    public String subscribe(Long subscribeId) throws AuthException {
        var followerId = RequestHolderUtil.getUserId();
        if (subscribeId.equals(followerId)) {
            return "不能订阅自己";
        }
        if (followerRepo.countFollowerByUserAndFollower(new User(subscribeId), new User(followerId)) != 0) {
            return "不可重复订阅";
        }
        followerRepo.save(new Follower(subscribeId, followerId));
        userRepo.incrementSubscribeCnt(1, followerId);
        userRepo.incrementFollowerCnt(1, subscribeId);
        return TRUE;
    }

    @Override
    @RequireValid
    public String unsubscribe(Long subscribeId) throws AuthException {
        var followerId = RequestHolderUtil.getUserId();
        if (subscribeId.equals(followerId)) {
            return "错误的操作";
        }
        followerRepo.deleteSubscribe(followerId, subscribeId);
        userRepo.incrementSubscribeCnt(-1, followerId);
        userRepo.incrementFollowerCnt(-1, subscribeId);
        return TRUE;
    }

    @Override
    @RequireValid
    public String deleteSubscribe(Long id) throws AuthException {
        var record = followerRepo.findById(id).orElseThrow();
        if (!record.getFollower().getId().equals(RequestHolderUtil.getUserId())) {
            throw new AuthException("没有权限执行此操作");
        }
        followerRepo.deleteById(id);
        userRepo.incrementSubscribeCnt(-1, record.getFollower().getId());
        userRepo.incrementFollowerCnt(-1, record.getUser().getId());
        return TRUE;
    }


    @Override
    @RequireValid
    public Page<ImgUserApproval> getApprovalPictures(Long userId, Integer page, Integer size) throws AuthException {
        if (userId == null) {
            userId = RequestHolderUtil.getUserId();
        }
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        return imgUserAppRepo.findByUserOrderByCreateTimeDesc(new User(userId), PageRequest.of(page, size));
    }

    @Override
    @RequireValid
    public Map<String, Object> getPictureUserState(Long pId) throws AuthException {
        var uId = RequestHolderUtil.getUserId();
        var approvalState = imgUserAppRepo.exists(Example.of(new ImgUserApproval(null, new User(uId), new Picture(pId), null)));
        var collectState = imgUserCollectRepo.exists(Example.of(new ImgUserCollect(null, new User(uId), new Picture(pId), null)));
        var result = new HashMap<String, Object>();
        result.put("approvalState", approvalState);
        result.put("collectState", collectState);
        return result;
    }

    @Override
    @RequireValid
    public String approvalPicture(Long pictureId) throws AuthException {
        var userId = RequestHolderUtil.getUserId();
        if (imgUserAppRepo.exists(Example.of(new ImgUserApproval(null, new User(userId), new Picture(pictureId), null)))) {
            return "已点赞";
        }
        var user = new User(userId);
        var img = new Picture(pictureId);
        var imgUserApp = new ImgUserApproval();
        imgUserApp.setUser(user);
        imgUserApp.setPictureId(img);
        imgUserAppRepo.save(imgUserApp);
        pictureRepo.incrementApproval(1, pictureId);
        return TRUE;
    }

    @Override
    @RequireValid
    public String unApprovalPicture(Long pictureId) throws AuthException {
        var userId = RequestHolderUtil.getUserId();
        if (!imgUserAppRepo.exists(Example.of(new ImgUserApproval(null, new User(userId), new Picture(pictureId), null)))) {
            return TRUE;
        }
        imgUserAppRepo.deleteApprovalImg(pictureId, userId);
        pictureRepo.incrementApproval(-1, pictureId);
        return TRUE;
    }

    @Override
    @RequireValid
    public String unApprovalPictures(Long[] pIds) throws AuthException {
        imgUserAppRepo.deleteApprovalImgs(Arrays.asList(pIds), RequestHolderUtil.getUserId());
        pictureRepo.incrementApprovals(-1, Arrays.asList(pIds));
        return TRUE;
    }

    @Override
    @RequireValid
    public Map<String, Object> getCollectPictures(Long userId, Integer page, Integer size) throws AuthException {
        if (userId == null) {
            userId = RequestHolderUtil.getUserId();
        }
        if (page == null || page < 0) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        var pictures = imgUserCollectRepo.findByUserOrderByCreateTimeDesc(new User(userId), PageRequest.of(page - 1, size));
        var iUC = new ArrayList<ImgUserCollect>();
        pictures.forEach(imgUserHistory -> {
            var tarImgUser = new ImgUserCollect();
            iUC.add(tarImgUser);
            BeanUtils.copyProperties(imgUserHistory, tarImgUser, "picture");
            var tarImg = new Picture();
            var picture = imgUserHistory.getPicture();
            if (picture != null) {
                BeanUtils.copyProperties(picture, tarImg, "uploader", "tags");
                if (Objects.equals(tarImg.getPath(), tarImg.getSmallPath())) {
                    tarImg.setSmallPath(CustomMvcConfig.IMG_REQUEST_PATH + tarImg.getSmallPath());
                    tarImg.setPath(tarImg.getSmallPath());
                } else {
                    tarImg.setPath(CustomMvcConfig.IMG_REQUEST_PATH + tarImg.getPath());
                    tarImg.setSmallPath(CustomMvcConfig.SMALL_IMG_REQUEST_PATH + tarImg.getSmallPath());
                }
                tarImgUser.setPicture(tarImg);
            }
        });
        var result = new HashMap<String, Object>(size);
        result.put("data", iUC);
        result.put("total", pictures.getTotalElements());
        result.put("pages", pictures.getTotalPages());
        return result;
    }

    @Override
    @RequireValid
    public String collectPicture(Long pictureId) throws AuthException {
        var userId = RequestHolderUtil.getUserId();
        if (imgUserCollectRepo.exists(Example.of(new ImgUserCollect(null, new User(userId), new Picture(pictureId), null)))) {
            return "已收藏";
        }
        var user = new User(userId);
        var img = new Picture(pictureId);
        var imgUserCollect = new ImgUserCollect();
        imgUserCollect.setUser(user);
        imgUserCollect.setPicture(img);
        imgUserCollectRepo.save(imgUserCollect);
        pictureRepo.incrementCollect(1, pictureId);
        userRepo.incrementCollectImgCnt(1, userId);
        return TRUE;
    }


    @Override
    public String unCollectPicture(Long pictureId) throws AuthException {
        var userId = RequestHolderUtil.getUserId();
        if (!imgUserCollectRepo.exists(Example.of(new ImgUserCollect(null, new User(userId), new Picture(pictureId), null)))) {
            return TRUE;
        }
        imgUserCollectRepo.deleteCollectImg(pictureId, userId);
        pictureRepo.incrementCollect(-1, pictureId);
        userRepo.incrementCollectImgCnt(-1, RequestHolderUtil.getUserId());
        return TRUE;
    }

    @Override
    @RequireValid
    public String deleteCollectPicture(Long id) throws AuthException, IllegalRequestParameterException {
        var userId = RequestHolderUtil.getUserId();
        var recordO = imgUserCollectRepo.findById(id);
        if (recordO.isEmpty()) {
            return TRUE;
        }
        var record = recordO.get();
        if (!record.getUser().getId().equals(userId)) {
            throw new IllegalRequestParameterException("您无权进行此操作");
        }
        imgUserCollectRepo.deleteById(id);
        if (record.getPicture() != null) {
            pictureRepo.incrementCollect(-1, record.getPicture().getId());
        }
        userRepo.incrementCollectImgCnt(-1, RequestHolderUtil.getUserId());
        return TRUE;
    }

    @Override
    @RequireValid
    public String unCollectPictures(Long[] pIds) throws AuthException {
        imgUserCollectRepo.deleteCollectImgs(Arrays.asList(pIds), RequestHolderUtil.getUserId());
        pictureRepo.incrementCollects(-1, Arrays.asList(pIds));
        userRepo.incrementCollectImgCnt(-pIds.length, RequestHolderUtil.getUserId());
        return TRUE;
    }

    @Override
    @RequireValid
    public Map<String, Object> getHistoryPictures(Integer page, Integer size) throws AuthException {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        var pictures = imgUserHistoryRepo.findByUserIdOrderByBrowseTimeDesc(RequestHolderUtil.getUserId(), PageRequest.of(page - 1, size));
        var iUH = new ArrayList<ImgUserHistory>();
        pictures.forEach(imgUserHistory -> {
            var tarImgUserHistory = new ImgUserHistory();
            iUH.add(tarImgUserHistory);
            BeanUtils.copyProperties(imgUserHistory, tarImgUserHistory, "picture");
            var tarImg = new Picture();
            var picture = imgUserHistory.getPicture();
            if (picture != null) {
                BeanUtils.copyProperties(picture, tarImg, "uploader", "tags");
                if (Objects.equals(tarImg.getPath(), tarImg.getSmallPath())) {
                    tarImg.setSmallPath(CustomMvcConfig.IMG_REQUEST_PATH + tarImg.getSmallPath());
                    tarImg.setPath(tarImg.getSmallPath());
                } else {
                    tarImg.setPath(CustomMvcConfig.IMG_REQUEST_PATH + tarImg.getPath());
                    tarImg.setSmallPath(CustomMvcConfig.SMALL_IMG_REQUEST_PATH + tarImg.getSmallPath());
                }
                tarImgUserHistory.setPicture(tarImg);
            }
        });
        var result = new HashMap<String, Object>(size);
        result.put("data", iUH);
        result.put("total", pictures.getTotalElements());
        result.put("pages", pictures.getTotalPages());
        return result;
    }

    @Override
    @RequireValid
    public String addPictureHistory(Long pictureId) throws AuthException {
        var userId = RequestHolderUtil.getUserId();
        var img = new Picture(pictureId);
        var imgUserHistory = new ImgUserHistory();
        imgUserHistory.setUserId(userId);
        imgUserHistory.setPicture(img);
        imgUserHistoryRepo.save(imgUserHistory);
        return TRUE;
    }

    @Override
    @RequireValid
    public String removePictureHistory(Long pictureId) throws AuthException {
        imgUserHistoryRepo.deleteLookImg(pictureId, RequestHolderUtil.getUserId());
        return TRUE;
    }

    @Override
    @RequireValid
    public String deletePictureHistory(Long id) throws AuthException, IllegalRequestParameterException {
        var userId = RequestHolderUtil.getUserId();
        var recordO = imgUserHistoryRepo.findById(id);
        if (recordO.isEmpty()) {
            return TRUE;
        }
        var record = recordO.get();
        if (!record.getUserId().equals(userId)) {
            throw new IllegalRequestParameterException("您无权进行此操作");
        }
        imgUserHistoryRepo.deleteById(id);
        return TRUE;
    }

    @Override
    @RequireValid
    public String removePictureHistories(Long[] pIds) throws AuthException {
        imgUserHistoryRepo.deleteLookImgs(Arrays.asList(pIds), RequestHolderUtil.getUserId());
        return TRUE;
    }

    @Override
    @RequireValid
    public Page<TagUserHistory> getHistoryTags(Long userId, Integer page, Integer size) throws AuthException {
        if (userId == null) {
            userId = RequestHolderUtil.getUserId();
        }
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        return tagUserHistoryRepo.findByUserIdOrderByBrowseTimeDesc(userId, PageRequest.of(page, size));
    }

    @Override
    @RequireValid
    public String addTagHistory(Long tagId) throws AuthException {
        var userId = RequestHolderUtil.getUserId();
        var tag = new PTag(tagId);
        var tagUserHistory = new TagUserHistory();
        tagUserHistory.setUserId(userId);
        tagUserHistory.setTag(tag);
        tagUserHistoryRepo.save(tagUserHistory);
        return TRUE;
    }

    @Override
    @RequireValid
    public String removeTagHistory(Long tagId) throws AuthException {
        tagUserHistoryRepo.deleteLookTag(tagId, RequestHolderUtil.getUserId());
        return TRUE;
    }

    @Override
    @RequireValid
    public String removeTagHistories(Long[] tIds) throws AuthException {
        tagUserHistoryRepo.deleteLookTags(Arrays.asList(tIds), RequestHolderUtil.getUserId());
        return TRUE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSimpleRecommendedPictures(Integer page, Integer size) throws AuthException {
        var redisT = (RedisTemplate<String, PTag>) redisTemplate;
        var key = RequestHolderUtil.getUserId() + "_rt";
        List<PTag> tarTags;
        if (Objects.equals(redisT.hasKey(key), Boolean.TRUE)) {
            var ops = redisT.boundListOps(key);
            tarTags = ops.range(0, Objects.requireNonNull(ops.size()) - 1);
        } else {
            var pictures = imgUserHistoryRepo.findDistinctByUserIdOrderByBrowseTimeDesc(RequestHolderUtil.getUserId(), PageRequest.of(0, 100));
            var content = pictures.getContent();
            var tags = new HashMap<PTag, Integer>();
            content.forEach(imgUserHistory -> imgUserHistory.getPicture().getTags().forEach(tag -> {
                var num = tags.get(tag);
                if (num == null) {
                    tags.put(tag, 0);
                } else {
                    tags.put(tag, num + 1);
                }
            }));
            tarTags = tags.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).limit(20).map(Map.Entry::getKey).toList();
            if (!tarTags.isEmpty()) {
                redisT.opsForList().leftPushAll(key, tarTags);
                redisT.expire(key, 5, TimeUnit.SECONDS);
            }
        }
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 20;
        }
        var tarPictures = pictureRepo.findRecommendedPictures(tarTags, PageRequest.of(page - 1, size));
        return pictureServiceImpl.getPicturesResult(size, tarPictures);
    }

}
