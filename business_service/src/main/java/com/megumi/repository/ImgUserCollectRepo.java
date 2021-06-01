package com.megumi.repository;

import com.megumi.pojo.User;
import com.megumi.pojo.middle.ImgUserApproval;
import com.megumi.pojo.middle.ImgUserCollect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public interface ImgUserCollectRepo extends JpaRepository<ImgUserCollect, Long> {
    @Modifying
    @Query(value = """
            insert into pictures_users_collection values (?1,?2,?3,?4)
            """, nativeQuery = true)
    void insertCollectImg(Long id, Long userId, Long pId, String time);

    @Modifying
    @Query(value = """
            delete from pictures_users_collection where picture_id=?1 and user_id=?2
            """, nativeQuery = true)
    void deleteCollectImg(Long pId, Long userId);

    @Modifying
    @Query(value = """
            delete from pictures_users_collection where picture_id in (:pIds) and user_id=:userId
            """, nativeQuery = true)
    void deleteCollectImgs(@Param("pIds") List<Long> pIds, @Param("userId") Long userId);

    Page<ImgUserCollect> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);

    @Modifying
    @Query(value = "update pictures_users_collection set picture_id=null where picture_id in (?1)", nativeQuery = true)
    void updateImgTo0(Collection<Long> pIds);

    @Modifying
    @Query(value = "update pictures_users_collection set picture_id=null where picture_id=?1", nativeQuery = true)
    void updateImgTo0(Long pId);

}
