package com.megumi.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.megumi.aspect.annotation.RequireValid;
import com.megumi.common.Encryption;
import com.megumi.common.FileUtil;
import com.megumi.common.IdGenerator;
import com.megumi.common.StringUtils;
import com.megumi.config.CommonConfig;
import com.megumi.config.CustomMvcConfig;
import com.megumi.pojo.Photo;
import com.megumi.pojo.Role;
import com.megumi.pojo.User;
import com.megumi.repository.PhotoRepo;
import com.megumi.repository.UserRepo;
import com.megumi.service.AuthorizationManageService;
import com.megumi.service.MsgService;
import com.megumi.service.UserIdentityService;
import com.megumi.util.RequestHolderUtil;
import com.megumi.util.exception.*;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.message.AuthException;
import javax.security.sasl.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

import static com.megumi.common.Const.*;
import static com.megumi.common.StateCode.TRUE;
import static com.megumi.dict.MessageType.*;

/**
 * 2021/2/27
 *
 * @author miyabi
 * @since 1.0
 */
@Service
@Transactional(rollbackOn = {Exception.class})
public class UserIdentityServiceImpl implements UserIdentityService {

    private final UserRepo userRepo;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final MsgService msgService;
    private final IdGenerator idGenerator;
    private final AuthorizationManageService authorizationManageService;
    private final PasswordEncoder passwordEncoder;
    private final PhotoRepo photoRepo;
    private final FileUtil fileUtil;

    @Autowired
    public UserIdentityServiceImpl(UserRepo userRepo, RedisTemplate<Object, Object> redisTemplate, MsgService msgService, IdGenerator idGenerator, AuthorizationManageService authorizationManageService, PasswordEncoder passwordEncoder, PhotoRepo photoRepo, FileUtil fileUtil) {
        this.userRepo = userRepo;
        this.redisTemplate = redisTemplate;
        this.msgService = msgService;
        this.idGenerator = idGenerator;
        this.authorizationManageService = authorizationManageService;
        this.passwordEncoder = passwordEncoder;
        this.photoRepo = photoRepo;
        this.fileUtil = fileUtil;
    }

    @Override
    @RequireValid
    public Map<String, Object> signIn(String account, String password) throws UserNotFoundException, AuthenticationException {
        var user = userRepo.findUserByUserAccount(account).orElseThrow(() -> new UserNotFoundException("该账号不存在"));
        if (!passwordEncoder.matches(password, user.getPwd())) {
            throw new AuthenticationException("密码错误");
        }
        return saveLoginInfoAndGetToken(user);
    }

    @Override
    @RequireValid
    public Map<String, Object> signInByEmail(String email, String password) throws UserNotFoundException, AuthenticationException {
        var user = userRepo.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("该邮箱未绑定账号"));
        if (!passwordEncoder.matches(password, user.getPwd())) {
            throw new AuthenticationException("密码错误");
        }
        return saveLoginInfoAndGetToken(user);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> saveLoginInfoAndGetToken(User user) {
        var map = new HashMap<String, Object>(1);
        map.put("alg", "HS256");
        var token = JWT.create().withHeader(map)
                .withClaim("id", user.getId())
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET));
        var val = new HashMap<String, Object>();
        var grantedAuthorities = new ArrayList<GrantedAuthority>();
        user.getRoles().forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role.getName())));
        val.put("id", user.getId());
        val.put("role", grantedAuthorities);
        var tokens = redisTemplate.opsForHash().get(user.getId(), "tokens");
        if (tokens != null) {
            ((List<String>) tokens).add(token);
            val.put("tokens", tokens);
        } else {
            var tokenList = new ArrayList<String>();
            tokenList.add(token);
            val.put("tokens", tokenList);
        }
        Objects.requireNonNull(((ServletRequestAttributes) (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getResponse()).addCookie(new Cookie("token", token));
        userRepo.updateUserLoginTime(LocalDateTime.now(), user.getId());
        redisTemplate.opsForHash().putAll(user.getId(), val);
        var result = new HashMap<String, Object>();
        result.put("token", token);
        var tarUser = new User();
        BeanUtils.copyProperties(user, tarUser, "roles", "phone", "lastLoginTime", "collectImgCnt", "pwd", "isLock", "isCancel", "worksCount", "subscribeCount", "followerCount", "createTime");
        var roles = new ArrayList<Role>();
        tarUser.setRoles(roles);
        user.getRoles().forEach(role -> {
            var roleTar = new Role();
            BeanUtils.copyProperties(role, roleTar, "modules", "id", "desc");
            roles.add(roleTar);
        });
        dealPhoto(user, tarUser);
        result.put("user", tarUser);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String signOut() throws AuthException {
        var token = RequestHolderUtil.getToken();
        var id = RequestHolderUtil.getUserId(token);
        var tokens = (List<String>) redisTemplate.opsForHash().get(id, "tokens");
        Objects.requireNonNull(tokens).remove(token);
        if (tokens.isEmpty()) {
            redisTemplate.delete(id);
        } else {
            redisTemplate.opsForHash().put(id, "tokens", tokens);
        }
        return TRUE;
    }

    @Override
    @RequireValid
    public String addUserByInfo(User user) {
        var account = String.valueOf(idGenerator.getNextId());
        user.setUserAccount(account);
        if (!StringUtils.argsIsValid(user.getUsername())) {
            user.setUsername(account);
        }
        user.setPwd(passwordEncoder.encode(user.getPwd()));
        user.setPhone(null);
        user.setEmail(null);
        user.init();
        userRepo.saveAndFlush(user);
        authorizationManageService.setUserDefaultRole(user.getId());
        return TRUE;
    }

    @Override
    @RequireValid
    public String addUserByEmail(String code, String account, String email, String pwd) throws UserExistException, ValidateCodeError, JsonProcessingException {
        if (!msgService.validateMsg(code, email, MAIL_SIGN_UP)) {
            throw new ValidateCodeError();
        }
        var user = new User();
        if (StringUtils.isValid(account)) {
            if (userRepo.countUserByUserAccount(account) != 0) {
                throw new UserExistException("该用户已存在");
            }
        } else {
            account = String.valueOf(idGenerator.getNextId());
        }
        user.setUserAccount(account);
        user.setUsername(account);
        user.setPwd(passwordEncoder.encode(pwd));
        user.setEmail(email);
        user.init();
        userRepo.saveAndFlush(user);
        authorizationManageService.setUserDefaultRole(user.getId());
        return TRUE;
    }

    @Override
    public String modifyUser(String username) throws AuthException, IllegalRequestParameterException {
        if (!StringUtils.isValid(username)) {
            throw new IllegalRequestParameterException("用户名不能为空");
        }
        var tar = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow();
        tar.setUsername(username);
        userRepo.save(tar);
        return TRUE;
    }

    @Override
    @RequireValid
    public String addUserByPhone(String code, String phone, String pwd) throws JsonProcessingException {
        msgService.validateMsg(code, phone, PHONE_SIGN_UP);
        var user = new User();
        var account = String.valueOf(idGenerator.getNextId());
        user.setUserAccount(account);
        user.setUsername(account);
        user.setPwd(passwordEncoder.encode(user.getPwd()));
        user.setPhone(phone);
        user.init();
        userRepo.saveAndFlush(user);
        authorizationManageService.setUserDefaultRole(user.getId());
        return TRUE;
    }

    @Override
    public String addPhone(String code, String phone) throws AuthException, JsonProcessingException {
        msgService.validateMsg(code, phone, PHONE_ADD);
        userRepo.updatePhone(RequestHolderUtil.getUserId(), phone);
        return TRUE;
    }

    @Override
    public String removePhone(String code) throws AuthException, JsonProcessingException {
        var user = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow();
        if (!msgService.validateMsg(code, user.getPhone(), PHONE_REMOVE)) {
            return "验证码错误或已经失效";
        }
        userRepo.updatePhone(user.getId(), null);
        return TRUE;
    }

    @Override
    public String addEmail(String code, String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError {
        if (userRepo.countUserByEmail(email) != 0) {
            throw new EmailValidateError("该邮箱已绑定其他账号");
        }
        if (!msgService.validateMsg(code, email, EMAIL_ADD)) {
            throw new ValidateCodeError();
        }
        userRepo.updateEmail(RequestHolderUtil.getUserId(), email);
        return TRUE;
    }

    @Override
    @RequireValid
    public String removeEmail(String code) throws AuthException, JsonProcessingException, ValidateCodeError {
        var user = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow();
        if (!msgService.validateMsg(code, user.getEmail(), EMAIL_REMOVE)) {
            throw new ValidateCodeError();
        }
        userRepo.updateEmail(user.getId(), null);
        return TRUE;
    }


    @Override
    @RequireValid
    public String validateOldAndSendNewEmail(String code, String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError {
        var user = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow();
        if (!msgService.validateMsg(code, user.getEmail(), MAIL_CHANGE_EMAIL_OLD)) {
            throw new ValidateCodeError();
        }
        msgService.sendChangeEmailCodeToNew(email);
        return TRUE;
    }

    @Override
    @RequireValid
    public String validateOldAndSendNewPhone(String code) throws AuthException, JsonProcessingException {
        var user = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow();
        if (!msgService.validateMsg(code, user.getPhone(), PHONE_CHANGE_PHONE_OLD)) {
            return "验证码错误或已经失效";
        }
        msgService.sendCodeMsg(user.getPhone(), PHONE_CHANGE_PHONE_NEW);
        return TRUE;
    }

    @Override
    @RequireValid
    public String modifyPhoneNew(String code, String phone) throws AuthException, JsonProcessingException {
        if (!msgService.validateMsg(code, phone, PHONE_CHANGE_PHONE_NEW)) {
            return "验证码错误或已经失效";
        }
        userRepo.updatePhone(RequestHolderUtil.getUserId(), phone);
        return TRUE;
    }

    @Override
    @RequireValid
    public String modifyEmailNew(String code, String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError {
        if (userRepo.countUserByEmail(email) != 0) {
            throw new EmailValidateError("该邮箱已绑定其他账号");
        }
        if (!msgService.validateMsg(code, email, MAIL_CHANGE_EMAIL_NEW)) {
            throw new ValidateCodeError();
        }
        userRepo.updateEmail(RequestHolderUtil.getUserId(), email);
        return TRUE;
    }

    @Override
    @RequireValid
    public String modifyPwdByPhone(String code, String phone, String pwd) throws NoSuchAlgorithmException, UserNotFoundException, JsonProcessingException {
        if (!msgService.validateMsg(code, phone, PHONE_CHANGE_PWD)) {
            return "验证码错误或已经失效";
        }
        if (userRepo.countUserByPhone(phone) != 1) {
            throw new UserNotFoundException("该用户不存在");
        }
        pwd = Encryption.sha256(pwd);
        userRepo.updatePwdByPhone(pwd, phone);
        return TRUE;
    }

    @Override
    @RequireValid
    public String modifyPwdByEmail(String code, String email, String pwd) throws NoSuchAlgorithmException, UserNotFoundException, JsonProcessingException, ValidateCodeError {
        if (!msgService.validateMsg(code, email, MAIL_CHANGE_PWD)) {
            throw new ValidateCodeError();
        }
        if (userRepo.countUserByEmail(email) == 0) {
            throw new UserNotFoundException("该用户不存在");
        }
        pwd = passwordEncoder.encode(pwd);
        userRepo.updatePwdByEmail(pwd, email);
        return TRUE;
    }

    @Override
    @RequireValid
    public String modifyPwd(String oldPwd, String newPwd) throws Exception {
        Long id = RequestHolderUtil.getUserId();
        var user = userRepo.findById(id).orElseThrow();
        if (!passwordEncoder.matches(oldPwd, user.getPwd())) {
            return "旧密码错误";
        }
        userRepo.updatePwd(passwordEncoder.encode(newPwd), id);
        redisTemplate.delete(id);
        return TRUE;
    }

    @Override
    public Map<String, Object> getPhotos(Integer page, Integer size) throws AuthException {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        var photos = photoRepo.findPhotosByUserId(RequestHolderUtil.getUserId(), PageRequest.of(page - 1, size));
        var content = new ArrayList<Photo>();
        photos.getContent().forEach(photoOriginal -> {
            var photo = new Photo();
            BeanUtils.copyProperties(photoOriginal, photo);
            photo.setPath(CustomMvcConfig.PHOTO_REQUEST_PATH + photo.getPath());
            photo.setSmallPath(CustomMvcConfig.PHOTO_REQUEST_PATH + photo.getSmallPath());
            content.add(photo);
        });
        var result = new HashMap<String, Object>();
        result.put("data", content);
        result.put("total", photos.getTotalElements());
        result.put("pages", photos.getTotalPages());
        return result;
    }

    @Override
    public Map<String, Object> uploadPhoto(MultipartFile photo) throws IOException, AuthException, IllegalRequestParameterException {
        if (photo.isEmpty()) {
            throw new IllegalRequestParameterException("文件为空");
        }
        var path = fileUtil.save(photo, CommonConfig.PHOTO_SAVE_PATH);
        var name = fileUtil.getName(path);
        var smallPath = name;
        if (photo.getSize() > 5242880) {
            var smallFile = fileUtil.createFile("jpeg", CommonConfig.PHOTO_SAVE_PATH);
            Thumbnails.of(photo.getInputStream()).scale(1f).outputQuality(0.25f).toFile(smallFile);
            smallPath = smallFile.getName();
        }
        var photoTemp = new Photo();
        photoTemp.setUserId(RequestHolderUtil.getUserId());
        photoTemp.setPath(name);
        photoTemp.setSmallPath(smallPath);
        photoRepo.save(photoTemp);
        userRepo.updateUserPhoto(photoTemp.getId(), photoTemp.getUserId());
        var result = new HashMap<String, Object>();
        result.put("path", CustomMvcConfig.PHOTO_REQUEST_PATH + name);
        result.put("smallPath", CustomMvcConfig.PHOTO_REQUEST_PATH + smallPath);
        return result;
    }

    @Override
    public String deleteOldPhoto(Long photoId) throws IOException, AuthException, IllegalRequestParameterException {
        var user = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow();
        if (user.getPhoto().getId().equals(photoId)) {
            throw new IllegalRequestParameterException("不能删除正在使用的头像");
        }
        var photo = photoRepo.findById(photoId).orElseThrow();
        Files.deleteIfExists(Path.of(CommonConfig.PHOTO_SAVE_PATH, photo.getPath()));
        Files.deleteIfExists(Path.of(CommonConfig.PHOTO_SAVE_PATH, photo.getSmallPath()));
        photoRepo.deleteById(photoId);
        return TRUE;
    }

    @Override
    public HashMap<String, Object> getUserByUsername(String username, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        Page<User> users;
        if (StringUtils.isValid(username)) {
            users = userRepo.findUsersByUsernameContains(username, PageRequest.of(page - 1, size));
        } else {
            users = userRepo.findAll(PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        var content = new ArrayList<User>();
        users.getContent().forEach(user -> {
            var tarUser = new User();
            BeanUtils.copyProperties(user, tarUser, "phone", "lastLoginTime", "collectImgCnt", "pwd", "isLock", "isCancel", "photo", "createTime");
            dealPhoto(user, tarUser);
            content.add(tarUser);
        });
        var result = new HashMap<String, Object>();
        result.put("data", content);
        result.put("total", users.getTotalElements());
        result.put("pages", users.getTotalPages());
        return result;
    }

    @Override
    @RequireValid
    public User getUserInfo(Long userId) {
        var user = userRepo.findById(userId).orElseThrow();
        var tarUser = new User();
        BeanUtils.copyProperties(user, tarUser, "phone", "lastLoginTime", "collectImgCnt", "pwd", "isLock", "isCancel", "photo");
        dealPhoto(user, tarUser);
        return tarUser;
    }

    @Override
    public User getUserDetails() throws AuthException {
        var user = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow();
        var tarUser = new User();
        BeanUtils.copyProperties(user, tarUser, "pwd", "isLock", "isCancel", "photo");
        dealPhoto(user, tarUser);
        return tarUser;
    }

    private void dealPhoto(User source, User tar) {
        if (source.getPhoto() != null) {
            var photo = new Photo();
            BeanUtils.copyProperties(source.getPhoto(), photo);
            photo.setPath(CustomMvcConfig.PHOTO_REQUEST_PATH + photo.getPath());
            photo.setSmallPath(CustomMvcConfig.PHOTO_REQUEST_PATH + photo.getSmallPath());
            tar.setPhoto(photo);
        }
    }

    @Override
    public String exist(String arg, String type) {
        StringUtils.argsRequireValid(arg, type);
        var count = switch (type) {
            case PHONE -> userRepo.countUserByPhone(arg);
            case EMAIL -> userRepo.countUserByEmail(arg);
            case USERNAME -> userRepo.countUserByUsername(arg);
            case USER_ACCOUNT -> userRepo.countUserByUserAccount(arg);
            default -> 0;
        };
        if (count > 0) {
            return TRUE;
        }
        return "该用户不存在";
    }
}
