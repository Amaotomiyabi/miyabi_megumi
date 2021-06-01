package com.megumi.repository;

import com.megumi.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface UserRepo extends JpaRepository<User, Long> {

    Integer countUserByUserAccount(String userAccount);

    Integer countUserByUsername(String username);

    Integer countUserByPhone(String phone);

    Integer countUserByEmail(String email);

    Page<User> findUsersByUsernameLike(String username, Pageable pageable);

    Page<User> findUsersByIdIn(List<Long> ids, Pageable pageable);

    @Modifying
    @Query(value = "update user set phone=?2 where id=?1", nativeQuery = true)
    void updatePhone(Long userId, String phone);

    @Modifying
    @Query(value = "update user set email=?2 where id=?1", nativeQuery = true)
    void updateEmail(Long userId, String Email);

    @Modifying
    @Query(value = "update user set pwd=?1 where phone=?2", nativeQuery = true)
    void updatePwdByPhone(String pwd, String phone);

    @Modifying
    @Query(value = "update user set pwd=?1 where email=?2", nativeQuery = true)
    void updatePwdByEmail(String pwd, String Email);

    @Modifying
    @Query(value = "update user set pwd=?1 where id=?2", nativeQuery = true)
    void updatePwd(String pwd, Long id);

    @Modifying
    @Query(value = """
            update user  set username=IFNULL(#{user.username},username)
            ,pwd=IFNULL(#{user.pwd},pwd)
            ,email=IFNULL(#{user.email},email)
            ,phone=IFNULL(#{user.phone},phone)
            ,photo=IFNULL(#{user.photo},photo)
            ,is_lock=IFNULL(#{user.isLock},photo)
            ,is_cancel=IFNULL(#{user.isCancel},is_cancel)
            ,follower_count=IFNULL(#{user.followerCount},follower_count)
            ,subscribe_count=IFNULL(#{user.subscribeCount},subscribe_count)
            ,works_count=IFNULL(#{user.worksCount},works_count)
            ,collect_img_cnt=IFNULL(#{user.collectImgCnt},collect_img_cnt)
            ,last_login_time=IFNULL(#{user.lastLoginTime},last_login_time)
            where id=#{user.id}
            """, nativeQuery = true)
    void updateUser(User user);

    @Modifying
    @Query(value = "update user set follower_count=follower_count+?1 where id=?2", nativeQuery = true)
    void incrementFollowerCnt(Integer cnt, Long id);

    @Modifying
    @Query(value = "update user set works_count=works_count+?1 where id=?2", nativeQuery = true)
    void incrementWorksCnt(Integer cnt, Long id);

    @Modifying
    @Query(value = "update user set subscribe_count=subscribe_count+?1 where id=?2", nativeQuery = true)
    void incrementSubscribeCnt(Integer cnt, Long id);

    @Modifying
    @Query(value = "update user set collect_img_cnt=collect_img_cnt+?1 where id=?2", nativeQuery = true)
    void incrementCollectImgCnt(Integer cnt, Long id);

    @Modifying
    @Query(value = "update user set photo=?1 where id=?2", nativeQuery = true)
    void updateUserPhoto(Long pId, Long uId);


}
