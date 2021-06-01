package com.megumi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.megumi.pojo.User;
import com.megumi.util.exception.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.message.AuthException;
import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 2021/2/27
 *
 * @author miyabi
 * @since 1.0
 */
public interface UserIdentityService {

    Map<String, Object> signIn(String account, String password) throws UserNotFoundException, AuthenticationException;

    Map<String, Object> signInByEmail(String email, String password) throws UserNotFoundException, AuthenticationException;

    /**
     * 获取用户自身信息
     *
     * @return 用户信息
     */
    User getUserInfo(Long userId);

    String signOut() throws AuthException;

    User getUserDetails() throws AuthException;

    /**
     * @param arg  用户名/账号/手机号/邮箱
     * @param type 0用户名/1账号/2手机号/3邮箱
     * @return 0不存在1存在
     */
    String exist(String arg, String type);

    /**
     * 普通注册
     *
     * @param user 用户注册详情
     * @return 0失败1成功
     */
    String addUserByInfo(User user);

    /**
     * 通过邮箱注册
     *
     * @param email 电子邮箱
     * @param pwd   密码
     * @param code  邮箱验证代码
     * @return 0失败1成功
     */
    String addUserByEmail(String code, String account, String email, String pwd) throws AuthException, UserExistException, ValidateCodeError, JsonProcessingException;

    /**
     * 通过手机号注册
     *
     * @param phone 用户手机号
     * @param pwd   密码
     * @param code  手机验证代码
     * @return 0失败1成功
     */
    String addUserByPhone(String code, String phone, String pwd) throws AuthException, JsonProcessingException;


    String modifyUser(String username) throws AuthException, IllegalRequestParameterException;

    String modifyPwd(String oldPwd, String newPwd) throws Exception;

    String addPhone(String code, String phone) throws AuthException, JsonProcessingException;

    String removePhone(String code) throws AuthException, JsonProcessingException;

    String addEmail(String code, String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError;

    String removeEmail(String code) throws AuthException, JsonProcessingException, ValidateCodeError;

    String validateOldAndSendNewEmail(String code, String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError;

    String validateOldAndSendNewPhone(String code) throws AuthException, JsonProcessingException;

    String modifyPhoneNew(String code, String phone) throws AuthException, JsonProcessingException;

    String modifyEmailNew(String code, String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError;

    String modifyPwdByPhone(String code, String phone, String pwd) throws AuthException, NoSuchAlgorithmException, UserNotFoundException, JsonProcessingException;

    String modifyPwdByEmail(String code, String email, String pwd) throws AuthException, NoSuchAlgorithmException, UserNotFoundException, JsonProcessingException, ValidateCodeError;

    Map<String, Object> getPhotos(Integer page, Integer size) throws AuthException;

    /**
     * 修改历史头像
     *
     * @param photo 修改后的头像
     * @return 提示信息 success/false
     */
    Map<String, Object> uploadPhoto(MultipartFile photo) throws IOException, AuthException, IllegalRequestParameterException;

    /**
     * 删除历史头像
     *
     * @param photoId 历史头像ID
     * @return 提示信息 success/false
     */
    String deleteOldPhoto(Long photoId) throws IOException, AuthException, IllegalRequestParameterException;

    /**
     * 根据用户名搜索用户
     *
     * @param username 用户名
     * @param page     页
     * @param size     页大小
     * @return 相关用户列表
     */
    HashMap<String, Object> getUserByUsername(String username, Integer page, Integer size);
}
