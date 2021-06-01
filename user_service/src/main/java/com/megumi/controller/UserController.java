package com.megumi.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.megumi.pojo.User;
import com.megumi.service.UserIdentityService;
import com.megumi.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.message.AuthException;
import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/identity")
public class UserController {

    private final UserIdentityService userIdentityService;

    @Autowired
    public UserController(UserIdentityService userIdentityService) {
        this.userIdentityService = userIdentityService;
    }

    @PostMapping("/pwd/change")
    public String changePwd(@RequestParam String oldPassword, @RequestParam String newPassword) throws Exception {
        return userIdentityService.modifyPwd(oldPassword, newPassword);
    }

    @PostMapping("/login")
    public Map<String, Object> signIn(@RequestParam String account, @RequestParam String password) throws UserNotFoundException, AuthenticationException {
        return userIdentityService.signIn(account, password);
    }

    @PostMapping("/email/login")
    public Map<String, Object> signInByEmail(@RequestParam String email, @RequestParam String password) throws UserNotFoundException, AuthenticationException {
        return userIdentityService.signInByEmail(email, password);
    }

    @PostMapping("/register")
    public String signUp(@RequestParam String code, String account, @RequestParam String email, @RequestParam String pwd) throws UserExistException, AuthException, ValidateCodeError, JsonProcessingException {
        return userIdentityService.addUserByEmail(code, account, email, pwd);
    }

    @PostMapping("/logout")
    public String signOut() throws AuthException {
        return userIdentityService.signOut();
    }

    @PostMapping("/info/modify")
    public String modifyBaseInfo(@RequestParam String username) throws AuthException, IllegalRequestParameterException {
        return userIdentityService.modifyUser(username);
    }

    @PostMapping("/email/old/validate")
    public String validateOldEmailCode(@RequestParam String code, @RequestParam String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError {
        return userIdentityService.validateOldAndSendNewEmail(code, email);
    }

    @PostMapping("/email/new/modify")
    public String changeNewEmail(@RequestParam String code, @RequestParam String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError {
        return userIdentityService.modifyEmailNew(code, email);
    }

    @PostMapping("/email/add")
    public String addEmail(@RequestParam String code, @RequestParam String email) throws AuthException, ValidateCodeError, JsonProcessingException, EmailValidateError {
        return userIdentityService.addEmail(code, email);
    }

    @PostMapping("/email/remove")
    public String removeEmail(@RequestParam String code) throws AuthException, ValidateCodeError, JsonProcessingException {
        return userIdentityService.removeEmail(code);
    }

    @PostMapping("/email/pwd/change")
    public String changePwdByEmail(@RequestParam String email, @RequestParam String code, @RequestParam String pwd) throws UserNotFoundException, AuthException, ValidateCodeError, NoSuchAlgorithmException, JsonProcessingException {
        return userIdentityService.modifyPwdByEmail(code, email, pwd);
    }

    @PostMapping("/photo/upload")
    public Map<String, Object> uploadPhoto(MultipartFile file) throws AuthException, IOException, IllegalRequestParameterException {
        return userIdentityService.uploadPhoto(file);
    }

    @PostMapping("/photo/remove")
    public String removePhoto(Long photoId) throws AuthException, IOException, IllegalRequestParameterException {
        return userIdentityService.deleteOldPhoto(photoId);
    }

    @GetMapping("/photo/list")
    public Map<String, Object> getPhotos(Integer page, Integer size) throws AuthException {
        return userIdentityService.getPhotos(page, size);
    }

    @GetMapping("/list")
    public Map<String, Object> getUserByUsername(String username, Integer page, Integer size) {
        return userIdentityService.getUserByUsername(username, page, size);
    }

    @GetMapping("/info")
    public User getUserInfo(@RequestParam Long id) {
        return userIdentityService.getUserInfo(id);
    }

    @GetMapping("/details")
    public User getUserDetails() throws AuthException {
        return userIdentityService.getUserDetails();
    }


}
