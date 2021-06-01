package com.megumi.service;

import com.megumi.aspect.annotation.RequireValid;
import com.megumi.pojo.middle.Follower;
import com.megumi.pojo.middle.ImgUserApproval;
import com.megumi.pojo.middle.TagUserHistory;
import com.megumi.util.exception.IllegalRequestParameterException;
import org.springframework.data.domain.Page;

import javax.naming.OperationNotSupportedException;
import javax.security.auth.message.AuthException;
import java.util.Map;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface UserService {

    /**
     * 获取粉丝
     *
     * @param id   被订阅者ID
     * @param page 页
     * @param size 页大小
     * @return 粉丝列表
     */
    Page<Follower> getFollower(Long id, Integer page, Integer size) throws AuthException, IllegalRequestParameterException;

    /**
     * 获取关注的人
     *
     * @param id   关注人ID
     * @param page 页
     * @param size 页大小
     * @return 关注者列表
     */
    Page<Follower> getSubscribeUser(Long id, Integer page, Integer size) throws AuthException, IllegalRequestParameterException;

    @RequireValid
    boolean getSubscribeState(Long id) throws AuthException;

    /**
     * @param subscribeId 被关注用户ID
     * @return 提示信息 success/false
     */
    String subscribe(Long subscribeId) throws AuthException, OperationNotSupportedException, IllegalRequestParameterException;

    /**
     * @param subscribeId 被取消关注的用户ID
     * @return 提示信息 success/false
     */
    String unsubscribe(Long subscribeId) throws AuthException, OperationNotSupportedException, IllegalRequestParameterException;

    @RequireValid
    String deleteSubscribe(Long id) throws AuthException;

    /**
     * @param userId 用户ID
     * @param page   当前页
     * @param size   页大小
     * @return 点赞图片集合
     * @throws AuthException 身份验证
     */
    Page<ImgUserApproval> getApprovalPictures(Long userId, Integer page, Integer size) throws AuthException;

    @RequireValid
    Map<String, Object> getPictureUserState(Long pId) throws AuthException;

    /**
     * 点赞图片
     *
     * @param pictureId 被点赞的图片ID
     * @return
     */
    String approvalPicture(Long pictureId) throws AuthException;


    /**
     * @param pIds 取消点赞
     * @return 提示信息
     * @throws AuthException
     */
    String unApprovalPictures(Long[] pIds) throws AuthException;

    /**
     * 取消图片点赞
     *
     * @param pictureId 被收藏的图片ID
     * @return 提示信息 success/false
     */
    String unApprovalPicture(Long pictureId) throws AuthException;

    Map<String, Object> getCollectPictures(Long userId, Integer page, Integer size) throws AuthException;

    /**
     * 收藏图片
     *
     * @param pictureId 被收藏的图片ID
     * @return 提示信息 success/false
     */
    String collectPicture(Long pictureId) throws AuthException;


    /**
     * 取消图片收藏
     *
     * @param pictureId 被收藏的图片ID
     * @return 提示信息 success/false
     */
    String unCollectPicture(Long pictureId) throws AuthException;


    String deleteCollectPicture(Long id) throws AuthException, IllegalRequestParameterException;

    String unCollectPictures(Long[] pIds) throws AuthException;


    Map<String, Object> getHistoryPictures(Integer page, Integer size) throws AuthException;


    String addPictureHistory(Long pictureId) throws AuthException;


    String removePictureHistory(Long pictureId) throws AuthException;


    @RequireValid
    String deletePictureHistory(Long id) throws AuthException, IllegalRequestParameterException;

    String removePictureHistories(Long[] pIds) throws AuthException;


    Page<TagUserHistory> getHistoryTags(Long userId, Integer page, Integer size) throws AuthException;


    String addTagHistory(Long tagId) throws AuthException;


    String removeTagHistory(Long tagId) throws AuthException;


    String removeTagHistories(Long[] tIds) throws AuthException;

    Map<String, Object> getSimpleRecommendedPictures(Integer page, Integer size) throws AuthException;
}
