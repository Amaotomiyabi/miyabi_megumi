package com.megumi.controller;

import com.megumi.service.PictureService;
import com.megumi.service.UserService;
import com.megumi.util.exception.IllegalRequestParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;
import javax.security.auth.message.AuthException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PictureService pictureService;

    @Autowired
    public UserController(UserService userService, PictureService pictureService) {
        this.userService = userService;
        this.pictureService = pictureService;
    }

    @GetMapping("/picture/list/collect")
    public Map<String, Object> getUserCollectPictures(Long id, Integer page, Integer size) throws AuthException {
        return userService.getCollectPictures(id, page, size);
    }

    @GetMapping("/picture/list/history")
    public Map<String, Object> getUserHistoryPictures(Integer page, Integer size) throws AuthException {
        return userService.getHistoryPictures(page, size);
    }

    @GetMapping("/picture/list/works")
    public Map<String, Object> getUserWorks(Long id, Integer page, Integer size) throws AuthException {
        return pictureService.getPictureListByUser(id, page, size);
    }

    @PostMapping("/picture/approval")
    public String approvalPicture(@RequestParam Long pId) throws AuthException {
        return userService.approvalPicture(pId);
    }

    @PostMapping("/picture/approval/remove")
    public String removeApprovalPicture(@RequestParam Long pId) throws AuthException {
        return userService.unApprovalPicture(pId);
    }

    @PostMapping("/picture/collect")
    public String collectPicture(@RequestParam Long pId) throws AuthException {
        return userService.collectPicture(pId);
    }

    @PostMapping("/picture/collect/remove")
    public String removeCollectPicture(@RequestParam Long pId) throws AuthException {
        return userService.unCollectPicture(pId);
    }

    @PostMapping("/picture/collect/delete")
    public String deleteCollectPicture(@RequestParam Long id) throws AuthException, IllegalRequestParameterException {
        return userService.deleteCollectPicture(id);
    }

    @PostMapping("/picture/history")
    public String addUserHistoryPicture(@RequestParam Long pId) throws AuthException {
        return userService.addPictureHistory(pId);
    }

    @PostMapping("/picture/history/remove")
    public String removeUserHistoryPicture(@RequestParam Long pId) throws AuthException {
        return userService.removePictureHistory(pId);
    }

    @PostMapping("/picture/history/delete")
    public String deleteUserHistoryPicture(@RequestParam Long id) throws AuthException, IllegalRequestParameterException {
        return userService.deletePictureHistory(id);
    }

    @GetMapping("/picture/list/recommend")
    public Map<String, Object> getSimpleRecommendedPicture(Integer page, Integer size) throws AuthException {
        return userService.getSimpleRecommendedPictures(page, size);
    }

    @GetMapping("/picture/user/state")
    public Map<String, Object> getPictureUserState(@RequestParam Long pId) throws AuthException {
        return userService.getPictureUserState(pId);
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam Long id) throws OperationNotSupportedException, IllegalRequestParameterException, AuthException {
        return userService.subscribe(id);
    }

    @PostMapping("/unSubscribe")
    public String unSubscribe(@RequestParam Long id) throws OperationNotSupportedException, IllegalRequestParameterException, AuthException {
        return userService.unsubscribe(id);
    }

    @GetMapping("/subscribe/state")
    public boolean getSubscribeState(@RequestParam Long id) throws AuthException {
        return userService.getSubscribeState(id);
    }
}
