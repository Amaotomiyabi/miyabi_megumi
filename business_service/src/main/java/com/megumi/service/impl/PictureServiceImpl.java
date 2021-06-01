package com.megumi.service.impl;

import com.megumi.common.FileUtil;
import com.megumi.common.IdGenerator;
import com.megumi.common.StateCode;
import com.megumi.common.StringUtils;
import com.megumi.config.CommonConfig;
import com.megumi.config.CustomMvcConfig;
import com.megumi.pojo.PTag;
import com.megumi.pojo.Photo;
import com.megumi.pojo.Picture;
import com.megumi.pojo.User;
import com.megumi.pojo.middle.ImgUserHistory;
import com.megumi.pojo.middle.TagPicture;
import com.megumi.pojo.middle.TagUserHistory;
import com.megumi.repository.*;
import com.megumi.service.PictureService;
import com.megumi.service.TagService;
import com.megumi.util.RequestHolderUtil;
import com.megumi.util.exception.IllegalRequestParameterException;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.criteria.Predicate;
import javax.security.auth.message.AuthException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 2021/2/27
 *
 * @author miyabi
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PictureServiceImpl implements PictureService {
    private final PictureRepo pictureRepo;
    private final UserRepo userRepo;
    private final PTagRepo tagRepo;
    private final ImgUserHistoryRepo imgUserHistoryRepo;
    private final ImgUserCollectRepo imgUserCollectRepo;
    private final ImgUserAppRepo imgUserAppRepo;
    private final TagService tagService;
    private final FileUtil fileUtil;
    private final ImgTagRepo imgTagRepo;
    private final IdGenerator idGenerator;
    private final TagUserHistoryRepo tagUserHistoryRepo;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public PictureServiceImpl(PictureRepo pictureRepo, UserRepo userRepo, PTagRepo tagRepo, ImgUserHistoryRepo imgUserHistoryRepo, ImgUserCollectRepo imgUserCollectRepo, ImgUserAppRepo imgUserAppRepo, TagService tagService, FileUtil fileUtil, ImgTagRepo imgTagRepo, IdGenerator idGenerator, TagUserHistoryRepo tagUserHistoryRepo, RedisTemplate<Object, Object> redisTemplate) {
        this.pictureRepo = pictureRepo;
        this.userRepo = userRepo;
        this.tagRepo = tagRepo;
        this.imgUserHistoryRepo = imgUserHistoryRepo;
        this.imgUserCollectRepo = imgUserCollectRepo;
        this.imgUserAppRepo = imgUserAppRepo;
        this.tagService = tagService;
        this.fileUtil = fileUtil;
        this.imgTagRepo = imgTagRepo;
        this.idGenerator = idGenerator;
        this.tagUserHistoryRepo = tagUserHistoryRepo;
        this.redisTemplate = redisTemplate;
    }


    private Specification<Picture> getSpecification(String tags, Integer x, Integer y, String isSexy) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (StringUtils.isValid(tags)) {
                var path = root.join("tags").get("name");
                var in = criteriaBuilder.in(path);
                for (String tag : tags.split(" ")) {
                    in.value(tag);
                }
                predicates.add(in);
            }
            if (x != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("x"), x));
            }
            if (y != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("y"), y));
            }
            if (StringUtils.isValid(isSexy)) {
                predicates.add(criteriaBuilder.equal(root.get("isSexy"), isSexy));
            }
            criteriaQuery.where(predicates.toArray(new Predicate[0])).groupBy(root.get("id"));
            return null;
        };
    }

    @Override
    public Map<String, Object> getPictures(String tags, Integer x, Integer y, String isSexy, Integer page, Integer size, Integer sort) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        if (sort == null) {
            sort = 0;
        }
        var sortTemp = switch (sort) {
            case 1 -> Sort.by(Sort.Direction.DESC, "views");
            case 2 -> Sort.by(Sort.Direction.DESC, "approval");
            case 3 -> Sort.by(Sort.Direction.DESC, "collect");
            default -> Sort.by(Sort.Direction.DESC, "upTime");
        };
        var pictures = pictureRepo.findAll(getSpecification(tags, x, y, isSexy), PageRequest.of(page - 1, size, sortTemp));
        return getPicturesResult(size, pictures);
    }

    public Map<String, Object> getPicturesResult(Integer size, Page<Picture> pictures) {
        var result = new HashMap<String, Object>(size);
        var content = new ArrayList<Picture>();
        pictures.forEach(pictureOriginal -> {
            var picture = new Picture();
            content.add(picture);
            BeanUtils.copyProperties(pictureOriginal, picture, "uploader", "tags");
            if (Objects.equals(picture.getPath(), picture.getSmallPath())) {
                picture.setSmallPath(CustomMvcConfig.IMG_REQUEST_PATH + picture.getSmallPath());
                picture.setPath(picture.getSmallPath());
            } else {
                picture.setPath(CustomMvcConfig.IMG_REQUEST_PATH + picture.getPath());
                picture.setSmallPath(CustomMvcConfig.SMALL_IMG_REQUEST_PATH + picture.getSmallPath());
            }
        });
        result.put("data", content);
        result.put("total", pictures.getTotalElements());
        result.put("pages", pictures.getTotalPages());
        return result;
    }

    public Map<String, Object> getPicturesResult(Integer size, Long total, Integer pages, List<Picture> pictures) {
        var result = new HashMap<String, Object>(size);
        var content = new ArrayList<Picture>();
        pictures.forEach(pictureOriginal -> {
            var picture = new Picture();
            content.add(picture);
            BeanUtils.copyProperties(pictureOriginal, picture, "uploader", "tags");
            if (Objects.equals(picture.getPath(), picture.getSmallPath())) {
                picture.setSmallPath(CustomMvcConfig.IMG_REQUEST_PATH + picture.getSmallPath());
                picture.setPath(picture.getSmallPath());
            } else {
                picture.setPath(CustomMvcConfig.IMG_REQUEST_PATH + picture.getPath());
                picture.setSmallPath(CustomMvcConfig.SMALL_IMG_REQUEST_PATH + picture.getSmallPath());
            }
        });
        result.put("data", content);
        result.put("total", total);
        result.put("pages", pages);
        return result;
    }


    @Override
    public Map<String, Object> getPictureListByUser(Long userId, Integer page, Integer size) throws AuthException {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        if (userId == null) {
            userId = RequestHolderUtil.getUserId();
        }
        var pictures = pictureRepo.findPicturesByUploaderAndIsSexy(new User(userId), "0", PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "upTime")));
        return getPicturesResult(size, pictures);
    }

    @Override
    public Map<String, Object> getHotPictures(String tags, Integer x, Integer y, String isSexy, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        var pictures = pictureRepo.findHotImg(StringUtils.isValid(tags) ? tags.split(" ") : null, x, y, isSexy, LocalDateTime.now().minusMonths(1), PageRequest.of(page - 1, size));
        return getPicturesResult(size, pictures);
    }

    /**
     * @param pId
     * @return
     * @throws FileNotFoundException
     */
    @Override
    public Picture getPictureInfo(Long pId) throws FileNotFoundException {
        var pictureOriginal = redisTemplate.opsForValue().get(pId);
        Picture picture;
        if (pictureOriginal == null) {
            pictureOriginal = pictureRepo.findById(pId).orElseThrow(() -> new FileNotFoundException("没有找到该图片"));
            picture = new Picture();
            BeanUtils.copyProperties(pictureOriginal, picture);
            if (Objects.equals(picture.getPath(), picture.getSmallPath())) {
                picture.setSmallPath(CustomMvcConfig.IMG_REQUEST_PATH + picture.getSmallPath());
                picture.setPath(picture.getSmallPath());
            } else {
                picture.setPath(CustomMvcConfig.IMG_REQUEST_PATH + picture.getPath());
                picture.setSmallPath(CustomMvcConfig.SMALL_IMG_REQUEST_PATH + picture.getSmallPath());
            }
            var user = picture.getUploader();
            if (user != null) {
                var tarUser = new User();
                BeanUtils.copyProperties(user, tarUser, "phone", "lastLoginTime", "collectImgCnt", "pwd", "isLock", "isCancel", "photo");
                if (user.getPhoto() != null) {
                    var photo = new Photo();
                    BeanUtils.copyProperties(user.getPhoto(), photo);
                    photo.setPath(CustomMvcConfig.PHOTO_REQUEST_PATH + photo.getPath());
                    photo.setSmallPath(CustomMvcConfig.PHOTO_REQUEST_PATH + photo.getSmallPath());
                    tarUser.setPhoto(photo);
                }
                picture.setUploader(tarUser);
            }
            if (picture.getTags() != null && !picture.getTags().isEmpty()) {
                var tags = new ArrayList<PTag>();
                var tempTags = picture.getTags();
                for (PTag tempTag : tempTags) {
                    var t1 = new PTag();
                    BeanUtils.copyProperties(tempTag, t1, "uploader");
                    tags.add(t1);
                }
                picture.setTags(tags);
            }
        } else {
            picture = (Picture) pictureOriginal;
            picture.setViews(picture.getViews() + 1);
        }
        redisTemplate.opsForValue().set(pId, picture, 3, TimeUnit.MINUTES);
        Long userId = null;
        try {
            userId = RequestHolderUtil.getUserId();
        } catch (AuthException ignored) {

        }
        pictureRepo.incrementViews(1, pId);
        var imgUserHistory = new ImgUserHistory(null, userId, new Picture(pId), null);
        var iUHO = imgUserHistoryRepo.findOne(Example.of(imgUserHistory));
        if (iUHO.isPresent()) {
            var iUH = iUHO.get();
            iUH.setBrowseTime(LocalDateTime.now());
            imgUserHistoryRepo.save(iUH);
        } else {
            imgUserHistoryRepo.save(imgUserHistory);
        }
        var tagUserHistoryList = new ArrayList<TagUserHistory>();
        for (PTag tag : picture.getTags()) {
            var tagUserHistory = new TagUserHistory();
            tagUserHistory.setTag(tag);
            tagUserHistory.setUserId(userId);
            tagUserHistoryList.add(tagUserHistory);
        }
        tagUserHistoryRepo.saveAll(tagUserHistoryList);
        return picture;
    }

    @Override
    public String uploadPicture(List<String> tags, String src, String isSexy, MultipartFile file) throws IOException, AuthException, IllegalRequestParameterException {
        if (file.isEmpty()) {
            throw new IllegalRequestParameterException("文件为空");
        }
        var path = fileUtil.save(file, CommonConfig.IMG_SAVE_PATH);
        var smallPath = path;
        if (file.getSize() > 5242880) {
            var smallFile = fileUtil.createFile("jpeg", CommonConfig.IMG_SMALL_SAVE_PATH);
            Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.25f).toFile(smallFile);
            smallPath = smallFile.getName();
        }
        var picture = new Picture();
        picture.setSrc(src);
        picture.setUploader(new User(RequestHolderUtil.getUserId()));
        picture.setSize(file.getSize());
        picture.setPath(fileUtil.getName(path));
        picture.setSmallPath(fileUtil.getName(smallPath));
        var bi = ImageIO.read(file.getInputStream());
        picture.setX(bi.getWidth());
        picture.setY(bi.getHeight());
        if (isSexy == null) {
            isSexy = "0";
        }
        if (!isSexy.equals("0") && !isSexy.equals("1")) {
            throw new IllegalArgumentException("参数错误");
        }
        picture.setIsSexy(isSexy);
        picture.init();
        pictureRepo.saveAndFlush(picture);
        if (tags != null && !tags.isEmpty()) {
            var tagList = tagService.saveTags(tags);
            var tagImgList = tagList.stream().map(tag -> {
                var tagImg = new TagPicture();
                tagImg.setPicture(picture);
                tagImg.setPTag(tag);
                return tagImg;
            }).toList();
            imgTagRepo.saveAll(tagImgList);
        }
        return StateCode.TRUE;
    }

    @Override
    public String modifyPicture(Long pId, List<String> tags, String src, String isSexy) throws AuthException {
        var picture = pictureRepo.findById(pId).orElseThrow();
        var pictureTemp = new Picture();
        BeanUtils.copyProperties(picture, pictureTemp);
        pictureTemp.setSrc(src);
        pictureTemp.setIsSexy(isSexy);
        var deleteTags = picture.getTags().stream().filter(tag -> {
            for (String s : tags) {
                if (s.equalsIgnoreCase(tag.getName())) {
                    return false;
                }
            }
            return true;
        }).toList();
        var newTags = tags.stream().filter(tag -> {
            for (PTag pictureTag : picture.getTags()) {
                if (pictureTag.getName().equalsIgnoreCase(tag)) {
                    return false;
                }
            }
            return true;
        }).toList();
        var newPTags = tagService.saveTags(newTags).stream().map(tag -> {
            var imgTag = new TagPicture();
            imgTag.setPTag(tag);
            imgTag.setPicture(picture);
            return imgTag;
        }).toList();
        imgTagRepo.deleteBypTagInAndPicture(deleteTags, picture);
        imgTagRepo.saveAll(newPTags);
        pictureRepo.save(pictureTemp);
        return StateCode.TRUE;
    }

    @Override
    public String removePicture(Long pId) throws IOException, AuthException, IllegalRequestParameterException {
        var img = pictureRepo.findById(pId).orElseThrow();
        if (!img.getUploader().getId().equals(RequestHolderUtil.getUserId())) {
            throw new IllegalRequestParameterException("您无权进行此操作");
        }
        if (StringUtils.isValid(img.getPath())) {
            Files.deleteIfExists(Paths.get(CommonConfig.IMG_SAVE_PATH, img.getPath()));
        }
        if (StringUtils.isValid(img.getSmallPath())) {
            Files.deleteIfExists(Paths.get(CommonConfig.IMG_SMALL_SAVE_PATH, img.getSmallPath()));
        }
        imgUserAppRepo.updateImgTo0(pId);
        imgUserCollectRepo.updateImgTo0(pId);
        imgUserHistoryRepo.updateImgTo0(pId);
        pictureRepo.deleteById(pId);
        return StateCode.TRUE;
    }

    @Override
    public String removePictures(List<Long> pIds) throws IOException {
        var imgs = pictureRepo.findAllById(pIds);
        for (Picture img : imgs) {
            if (img.getPath() != null && !img.getPath().isBlank()) {
                Files.deleteIfExists(Paths.get(CommonConfig.IMG_SAVE_PATH, img.getPath()));
            }
            if (img.getSmallPath() != null && !img.getSmallPath().isBlank()) {
                Files.deleteIfExists(Paths.get(CommonConfig.IMG_SMALL_SAVE_PATH, img.getSmallPath()));
            }
        }
        imgUserAppRepo.updateImgTo0(pIds);
        imgUserCollectRepo.updateImgTo0(pIds);
        imgUserHistoryRepo.updateImgTo0(pIds);
        pictureRepo.deleteInBatch(imgs);
        return StateCode.TRUE;
    }
}
